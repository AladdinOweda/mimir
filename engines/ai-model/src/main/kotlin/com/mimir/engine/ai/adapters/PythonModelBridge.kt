package com.mimir.engine.ai.adapters

import com.mimir.engine.ai.contract.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/** Optional external python process — disabled by default. */
class PythonModelBridgeAdapter(
    private val pythonBin: String = "python3",
    private val scriptPath: String? = null,
    private val enabled: Boolean = false,
) : ModelRuntimeContract {
    override val runtimeId = RuntimeId("python-bridge")
    override val displayName = "Python Model Bridge"
    private val loaded = ConcurrentHashMap.newKeySet<String>()
    override fun capabilities() = ModelRuntimeCapabilities(generate = enabled, stream = false, train = false)
    override suspend fun load(model: ModelManifest, options: Map<String, String>): RuntimeLoadResult {
        if (!enabled) return RuntimeLoadResult(false, model.id, errorCode = "disabled")
        if (scriptPath == null || !File(scriptPath).isFile) return RuntimeLoadResult(false, model.id, errorCode = "script_missing")
        if (!model.licenseApproved) return RuntimeLoadResult(false, model.id, errorCode = "license_blocked")
        loaded += model.id.value
        return RuntimeLoadResult(true, model.id, "python_bridge")
    }
    override suspend fun unload(modelId: ModelId) = loaded.remove(modelId.value)
    override suspend fun generate(request: GenerateRequest): GenerateResult {
        if (!enabled) return GenerateResult(false, errorCode = "disabled")
        if (request.modelId.value !in loaded) return GenerateResult(false, errorCode = "not_loaded")
        val script = scriptPath ?: return GenerateResult(false, errorCode = "script_missing")
        return try {
            val p = ProcessBuilder(pythonBin, script, "--prompt", request.prompt).start()
            if (!p.waitFor(20, TimeUnit.SECONDS)) {
                p.destroyForcibly(); GenerateResult(false, errorCode = "timeout")
            } else GenerateResult(true, text = p.inputStream.bufferedReader().readText().take(4000), stub = false)
        } catch (t: Throwable) {
            GenerateResult(false, errorCode = "python_exec_failed")
        }
    }
    override suspend fun health() = RuntimeHealth(runtimeId, enabled, loaded.toList(), if (enabled) "enabled" else "disabled")
}
