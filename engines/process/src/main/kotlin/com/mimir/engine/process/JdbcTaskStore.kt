package com.mimir.engine.process

import com.mimir.contracts.process.TaskItem
import com.mimir.contracts.process.TaskItemStatus

import java.io.File
import java.sql.DriverManager

/**
 * R6 — durable TaskItem rows (SQLite). Activity log is append-only JSON-ish lines.
 */
class JdbcTaskStore(dbFile: File) : AutoCloseable {
    private val conn = run {
        dbFile.parentFile?.mkdirs()
        Class.forName("org.sqlite.JDBC")
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").also { c ->
            c.createStatement().use { st ->
                st.execute(
                    """
                    CREATE TABLE IF NOT EXISTS task_item (
                      id TEXT PRIMARY KEY,
                      title TEXT NOT NULL,
                      criterion TEXT NOT NULL,
                      requirement_ids TEXT NOT NULL,
                      lane TEXT NOT NULL,
                      test_command TEXT NOT NULL,
                      depends_on TEXT NOT NULL,
                      status TEXT NOT NULL,
                      claim_agent_id TEXT,
                      lease_until_epoch_ms INTEGER NOT NULL,
                      commit_ref TEXT,
                      frozen_requirement_fingerprint TEXT,
                      activity TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
                st.execute(
                    """
                    CREATE TABLE IF NOT EXISTS evidence (
                      id TEXT PRIMARY KEY,
                      task_id TEXT NOT NULL,
                      kind TEXT NOT NULL,
                      body TEXT NOT NULL,
                      commit_ref TEXT,
                      at_epoch_ms INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }
    }

    fun save(item: TaskItem) {
        conn.prepareStatement(
            """
            INSERT OR REPLACE INTO task_item(
              id,title,criterion,requirement_ids,lane,test_command,depends_on,status,
              claim_agent_id,lease_until_epoch_ms,commit_ref,frozen_requirement_fingerprint,activity
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """.trimIndent(),
        ).use { ps ->
            ps.setString(1, item.id)
            ps.setString(2, item.title)
            ps.setString(3, item.criterion)
            ps.setString(4, item.requirementIds.joinToString(","))
            ps.setString(5, item.lane)
            ps.setString(6, item.testCommand)
            ps.setString(7, item.dependsOn.joinToString(","))
            ps.setString(8, item.status.name)
            ps.setString(9, item.claimAgentId)
            ps.setLong(10, item.leaseUntilEpochMs)
            ps.setString(11, item.commitRef)
            ps.setString(12, item.frozenRequirementFingerprint)
            ps.setString(13, item.activity.joinToString("\n"))
            ps.executeUpdate()
        }
    }

    fun loadAll(): List<TaskItem> {
        val out = mutableListOf<TaskItem>()
        conn.createStatement().use { st ->
            st.executeQuery("SELECT * FROM task_item").use { rs ->
                while (rs.next()) {
                    out += TaskItem(
                        id = rs.getString("id"),
                        title = rs.getString("title"),
                        criterion = rs.getString("criterion"),
                        requirementIds = rs.getString("requirement_ids").split(',').filter { it.isNotBlank() },
                        lane = rs.getString("lane"),
                        testCommand = rs.getString("test_command"),
                        dependsOn = rs.getString("depends_on").split(',').filter { it.isNotBlank() },
                        status = TaskItemStatus.valueOf(rs.getString("status")),
                        claimAgentId = rs.getString("claim_agent_id"),
                        leaseUntilEpochMs = rs.getLong("lease_until_epoch_ms"),
                        commitRef = rs.getString("commit_ref"),
                        frozenRequirementFingerprint = rs.getString("frozen_requirement_fingerprint"),
                        activity = rs.getString("activity").split('\n').filter { it.isNotBlank() }.toMutableList(),
                    )
                }
            }
        }
        return out
    }

    fun appendEvidence(id: String, taskId: String, kind: String, body: String, commitRef: String?, at: Long) {
        conn.prepareStatement(
            "INSERT OR REPLACE INTO evidence(id,task_id,kind,body,commit_ref,at_epoch_ms) VALUES (?,?,?,?,?,?)",
        ).use { ps ->
            ps.setString(1, id)
            ps.setString(2, taskId)
            ps.setString(3, kind)
            ps.setString(4, body)
            ps.setString(5, commitRef)
            ps.setLong(6, at)
            ps.executeUpdate()
        }
    }

    fun evidenceFor(taskId: String): List<Triple<String, String, String>> {
        val out = mutableListOf<Triple<String, String, String>>()
        conn.prepareStatement("SELECT id,kind,body FROM evidence WHERE task_id=? ORDER BY at_epoch_ms").use { ps ->
            ps.setString(1, taskId)
            ps.executeQuery().use { rs ->
                while (rs.next()) out += Triple(rs.getString(1), rs.getString(2), rs.getString(3))
            }
        }
        return out
    }

    override fun close() {
        conn.close()
    }
}
