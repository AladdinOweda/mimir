package com.mimir.engine.workflow.persist

import com.mimir.engine.workflow.Checkpoint
import com.mimir.engine.workflow.RunStatus
import com.mimir.engine.workflow.WorkflowRun
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Durable file-backed store (JSON lines). Stands in until SQLDelight codegen is wired in Gradle.
 * Schema mirror: WorkflowRun.sq
 */
class FileWorkflowRunStore(
    private val dir: File,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : WorkflowRunStore {
    private val cache = ConcurrentHashMap<String, WorkflowRun>()

    init {
        dir.mkdirs()
        dir.listFiles()?.filter { it.extension == "run" }?.forEach { f ->
            parse(f.readText())?.let { cache[it.runId] = it }
        }
    }

    override fun save(run: WorkflowRun) {
        val copy = run.copy(
            checkpoints = run.checkpoints.toMutableList(),
            completedNodes = run.completedNodes.toMutableSet(),
        )
        cache[run.runId] = copy
        fileFor(run.runId).writeText(serialize(copy))
    }

    override fun load(runId: String): WorkflowRun? = cache[runId] ?: fileFor(runId).takeIf { it.isFile }?.let {
        parse(it.readText())?.also { r -> cache[runId] = r }
    }

    override fun listByWorkflow(workflowId: String): List<WorkflowRun> =
        cache.values.filter { it.workflowId == workflowId }

    private fun fileFor(runId: String) = File(dir, "${runId.replace(Regex("[^a-zA-Z0-9._-]"), "_")}.run")

    private fun serialize(run: WorkflowRun): String {
        val cps = run.checkpoints.joinToString(";") { "${it.id}|${it.atEpochMs}|${it.nodeId}|${it.stateRef}" }
        return listOf(
            run.runId,
            run.workflowId,
            run.workflowVersion,
            run.status.name,
            run.currentNodeId ?: "",
            run.nodeOrder.joinToString(","),
            run.index.toString(),
            run.completedNodes.joinToString(","),
            cps,
            run.idempotencyKey,
            clockMs().toString(),
        ).joinToString("\n")
    }

    private fun parse(text: String): WorkflowRun? {
        val lines = text.lines()
        if (lines.size < 10) return null
        val status = runCatching { RunStatus.valueOf(lines[3]) }.getOrElse { return null }
        val checkpoints = if (lines[8].isBlank()) mutableListOf() else lines[8].split(";").mapNotNull { part ->
            val p = part.split("|")
            if (p.size < 4) null else Checkpoint(p[0], p[1].toLongOrNull() ?: 0L, p[2], p[3])
        }.toMutableList()
        return WorkflowRun(
            runId = lines[0],
            workflowId = lines[1],
            workflowVersion = lines[2],
            status = status,
            currentNodeId = lines[4].ifBlank { null },
            nodeOrder = if (lines[5].isBlank()) emptyList() else lines[5].split(","),
            index = lines[6].toIntOrNull() ?: 0,
            checkpoints = checkpoints,
            completedNodes = if (lines[7].isBlank()) mutableSetOf() else lines[7].split(",").toMutableSet(),
            idempotencyKey = lines[9],
        )
    }
}
