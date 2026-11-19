package com.mimir.engine.computer

import com.mimir.kernel.contract.*

object ComputerCapabilities {
    const val OBSERVE = "computer.observe"
    const val ACT = "computer.act"
    const val SET_LEVEL = "computer.level.set"
    const val SCREENSHOT_META = "computer.screenshot.meta"
    const val NAVIGATE = "computer.navigate"
    const val METRICS = "computer.metrics"
    val ALL = listOf(OBSERVE, ACT, SET_LEVEL, SCREENSHOT_META, NAVIGATE, METRICS)
}

enum class ComputerLevel { Observe, Assist, Autonomous, FullControl }

/**
 * Computer Use layered on [UiAutomationPort].
 * observe → same a11y ref tree as browser.snapshot.a11y when browser-backed.
 * act: prefers input["ref"] (unified with browser.act.ref), else selector.
 */
class ComputerUseEngine(
    private val uiKind: String = "noop",
    private val ui: UiAutomationPort = UiDrivers.create(uiKind),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private var state = EngineState.Discovered
    private var level = ComputerLevel.Observe
    private val lastObserve = linkedMapOf<String, String>()

    private val manifest = EngineManifest(
        id = EngineId("computer-use"),
        name = "Mimir Computer Use",
        version = "0.7.0",
        provides = ComputerCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("computer.observe", "computer.act", "computer.admin"),
        dataNamespace = "engine.computer",
        priority = 72,
        platforms = listOf("desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured
        return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running
        return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped
        return state
    }
    override suspend fun health() =
        HealthReport(
            manifest.id,
            state == EngineState.Running,
            state,
            "level=$level driver=${ui.driverName} real=${ui.realDriver} a11yLinked=true",
            clockMs(),
        )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        val input = request.input - "approvalGranted"
        return when (request.capability.id.value) {
            ComputerCapabilities.OBSERVE -> {
                val interactive = input["interactive"]?.lowercase() != "false"
                val o = ui.observe(interactive)
                lastObserve.clear()
                lastObserve["url"] = o.url
                lastObserve["title"] = o.title
                lastObserve["text"] = o.text.take(500)
                lastObserve["refs"] = o.refs
                lastObserve["tree"] = o.tree.take(1_000)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "level" to level.name,
                        "url" to o.url,
                        "title" to o.title,
                        "text" to o.text.take(2_000),
                        "htmlLen" to o.htmlPrefix.length.toString(),
                        "tree" to o.tree.take(8_000),
                        "refs" to o.refs,
                        "nodeCount" to o.nodeCount.toString(),
                        "realDriver" to o.realDriver.toString(),
                        "driver" to o.driverName,
                        "parity" to "browser.snapshot.a11y",
                    ),
                )
            }
            ComputerCapabilities.NAVIGATE -> {
                val url = input["url"] ?: return InvocationResult.Err(request.requestId, "invalid", "url")
                if (level == ComputerLevel.Observe) {
                    return InvocationResult.Err(request.requestId, "level_denied", "Observe")
                }
                val o = ui.navigate(url)
                lastObserve["url"] = o.url
                lastObserve["refs"] = o.refs
                lastObserve["tree"] = o.tree.take(1_000)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "url" to o.url,
                        "title" to o.title,
                        "tree" to o.tree.take(4_000),
                        "refs" to o.refs,
                        "nodeCount" to o.nodeCount.toString(),
                        "realDriver" to o.realDriver.toString(),
                        "driver" to o.driverName,
                        "textLen" to o.text.length.toString(),
                    ),
                )
            }
            ComputerCapabilities.ACT -> {
                if (level == ComputerLevel.Observe) {
                    return InvocationResult.Err(request.requestId, "level_observe_blocks_act", level.name)
                }
                if (level == ComputerLevel.Assist) {
                    if (input["approvalToken"].isNullOrBlank()) {
                        return InvocationResult.Err(request.requestId, "approval_required", "Assist_requires_token")
                    }
                }

                val action = input["action"] ?: return InvocationResult.Err(request.requestId, "invalid", "action")
                if (level == ComputerLevel.Observe) {
                    return InvocationResult.Err(request.requestId, "level_denied", "Observe")
                }
                if (level == ComputerLevel.Autonomous || level == ComputerLevel.FullControl) {
                    if (input["approvalToken"].isNullOrBlank()) {
                        return InvocationResult.Err(request.requestId, "approval_required", level.name)
                    }
                }
                if (action in setOf("click", "type", "fill", "key") && input["approvalToken"].isNullOrBlank()) {
                    return InvocationResult.Err(request.requestId, "approval_required", action)
                }
                val ref = input["ref"]?.trim().orEmpty()
                val selector = input["selector"]?.trim().orEmpty()
                val text = input["text"] ?: ""
                val executed = when (action) {
                    "click" -> when {
                        ref.isNotBlank() -> ui.clickRef(ref)
                        selector.isNotBlank() -> ui.click(selector)
                        else -> false
                    }
                    "type", "fill" -> when {
                        ref.isNotBlank() -> ui.typeRef(ref, text)
                        selector.isNotBlank() -> ui.type(selector, text)
                        else -> false
                    }
                    else -> false
                }
                val target = when {
                    ref.isNotBlank() -> "ref:$ref"
                    selector.isNotBlank() -> "selector:$selector"
                    else -> "none"
                }
                val reason = when {
                    executed -> "ok"
                    !ui.realDriver -> "no_desktop_driver"
                    ref.isBlank() && selector.isBlank() -> "missing_ref_or_selector"
                    else -> "driver_act_failed_or_unsupported"
                }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "action" to action,
                        "target" to target,
                        "executed" to executed.toString(),
                        "reason" to reason,
                        "realDriver" to ui.realDriver.toString(),
                        "driver" to ui.driverName,
                        "level" to level.name,
                        "parity" to if (ref.isNotBlank()) "browser.act.ref" else "browser.click|type",
                    ),
                )
            }
            ComputerCapabilities.SET_LEVEL -> {
                val raw = input["level"] ?: return InvocationResult.Err(request.requestId, "invalid", "level")
                val next = try {
                    ComputerLevel.valueOf(raw)
                } catch (_: Exception) {
                    return InvocationResult.Err(request.requestId, "invalid_level", raw)
                }
                if (next == ComputerLevel.Autonomous || next == ComputerLevel.FullControl) {
                    if (input["approvalToken"].isNullOrBlank()) {
                        return InvocationResult.Err(request.requestId, "approval_required", next.name)
                    }
                }
                level = next
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "level" to level.name,
                        "realDriver" to ui.realDriver.toString(),
                        "driver" to ui.driverName,
                    ),
                )
            }
            ComputerCapabilities.SCREENSHOT_META ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "available" to ui.realDriver.toString(),
                        "driver" to ui.driverName,
                        "realDriver" to ui.realDriver.toString(),
                        "reason" to if (ui.realDriver) "dom_or_a11y_via_driver" else "no_desktop_driver",
                    ),
                )
            ComputerCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, mapOf("state" to state.name, "engine" to "computer-use"))
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
}
