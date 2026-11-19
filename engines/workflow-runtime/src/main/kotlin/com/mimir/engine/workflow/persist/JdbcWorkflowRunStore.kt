package com.mimir.engine.workflow.persist

import com.mimir.engine.workflow.Checkpoint
import com.mimir.engine.workflow.RunStatus
import com.mimir.engine.workflow.WorkflowRun
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * SQLite JDBC store aligned with WorkflowRun.sq.
 * Used until SQLDelight generated queries are applied in CI builds.
 */
class JdbcWorkflowRunStore(
    dbFile: File,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : WorkflowRunStore, AutoCloseable {
    private val conn: Connection

    init {
        dbFile.parentFile?.mkdirs()
        Class.forName("org.sqlite.JDBC")
        conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        conn.createStatement().use { st ->
            st.execute(
                """
                CREATE TABLE IF NOT EXISTS workflow_run (
                  run_id TEXT NOT NULL PRIMARY KEY,
                  workflow_id TEXT NOT NULL,
                  workflow_version TEXT NOT NULL,
                  status TEXT NOT NULL,
                  current_node_id TEXT,
                  node_order_csv TEXT NOT NULL,
                  index_pos INTEGER NOT NULL DEFAULT 0,
                  completed_csv TEXT NOT NULL DEFAULT '',
                  checkpoints_json TEXT NOT NULL DEFAULT '[]',
                  idempotency_key TEXT NOT NULL DEFAULT '',
                  updated_at_epoch_ms INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            st.execute("CREATE INDEX IF NOT EXISTS workflow_run_workflow_id ON workflow_run(workflow_id)")
        }
    }

    override fun save(run: WorkflowRun) {
        val cps = run.checkpoints.joinToString(";") { "${it.id}|${it.atEpochMs}|${it.nodeId}|${it.stateRef}" }
        conn.prepareStatement(
            """
            INSERT OR REPLACE INTO workflow_run(
              run_id, workflow_id, workflow_version, status, current_node_id,
              node_order_csv, index_pos, completed_csv, checkpoints_json, idempotency_key, updated_at_epoch_ms
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """.trimIndent(),
        ).use { ps ->
            ps.setString(1, run.runId)
            ps.setString(2, run.workflowId)
            ps.setString(3, run.workflowVersion)
            ps.setString(4, run.status.name)
            ps.setString(5, run.currentNodeId)
            ps.setString(6, run.nodeOrder.joinToString(","))
            ps.setInt(7, run.index)
            ps.setString(8, run.completedNodes.joinToString(","))
            ps.setString(9, cps)
            ps.setString(10, run.idempotencyKey)
            ps.setLong(11, clockMs())
            ps.executeUpdate()
        }
    }

    override fun load(runId: String): WorkflowRun? {
        conn.prepareStatement("SELECT * FROM workflow_run WHERE run_id = ?").use { ps ->
            ps.setString(1, runId)
            ps.executeQuery().use { rs ->
                if (!rs.next()) return null
                return fromRow(rs)
            }
        }
    }

    override fun listByWorkflow(workflowId: String): List<WorkflowRun> {
        val out = mutableListOf<WorkflowRun>()
        conn.prepareStatement("SELECT * FROM workflow_run WHERE workflow_id = ?").use { ps ->
            ps.setString(1, workflowId)
            ps.executeQuery().use { rs ->
                while (rs.next()) out += fromRow(rs)
            }
        }
        return out
    }

    private fun fromRow(rs: java.sql.ResultSet): WorkflowRun {
        val cpsRaw = rs.getString("checkpoints_json") ?: ""
        val checkpoints = if (cpsRaw.isBlank()) mutableListOf() else cpsRaw.split(";").mapNotNull { part ->
            val p = part.split("|")
            if (p.size < 4) null else Checkpoint(p[0], p[1].toLongOrNull() ?: 0L, p[2], p[3])
        }.toMutableList()
        val completed = rs.getString("completed_csv")?.orEmpty().orEmpty()
        val order = rs.getString("node_order_csv")?.orEmpty().orEmpty()
        return WorkflowRun(
            runId = rs.getString("run_id"),
            workflowId = rs.getString("workflow_id"),
            workflowVersion = rs.getString("workflow_version"),
            status = RunStatus.valueOf(rs.getString("status")),
            currentNodeId = rs.getString("current_node_id"),
            nodeOrder = if (order.isBlank()) emptyList() else order.split(","),
            index = rs.getInt("index_pos"),
            checkpoints = checkpoints,
            completedNodes = if (completed.isBlank()) mutableSetOf() else completed.split(",").toMutableSet(),
            idempotencyKey = rs.getString("idempotency_key") ?: "",
        )
    }

    override fun close() {
        conn.close()
    }
}
