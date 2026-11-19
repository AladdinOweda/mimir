package com.mimir.kernel.persist

import com.mimir.kernel.audit.AuditEntry
import com.mimir.kernel.security.ApprovalToken
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * R4 — durable kernel store (audit, approvals, permission grants) via SQLite JDBC.
 * Schema mirrors KernelPersist.sq until SQLDelight codegen is verified in CI.
 */
class JdbcKernelStore(
    dbFile: File,
) : AutoCloseable {
    private val conn: Connection

    init {
        dbFile.parentFile?.mkdirs()
        Class.forName("org.sqlite.JDBC")
        conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        conn.createStatement().use { st ->
            st.execute(
                """
                CREATE TABLE IF NOT EXISTS audit_entry (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  at_epoch_ms INTEGER NOT NULL,
                  principal_id TEXT NOT NULL,
                  capability_id TEXT NOT NULL,
                  decision TEXT NOT NULL,
                  reason TEXT NOT NULL,
                  request_id TEXT NOT NULL,
                  approval_token_id TEXT
                )
                """.trimIndent(),
            )
            st.execute(
                """
                CREATE TABLE IF NOT EXISTS approval_token (
                  id TEXT PRIMARY KEY,
                  principal_id TEXT NOT NULL,
                  action_id TEXT NOT NULL,
                  target_resource TEXT NOT NULL,
                  granted_scope TEXT NOT NULL,
                  expires_at_epoch_ms INTEGER NOT NULL,
                  single_use INTEGER NOT NULL,
                  revoked INTEGER NOT NULL,
                  consumed INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            st.execute(
                """
                CREATE TABLE IF NOT EXISTS permission_grant (
                  principal_id TEXT NOT NULL,
                  permission TEXT NOT NULL,
                  PRIMARY KEY (principal_id, permission)
                )
                """.trimIndent(),
            )
        }
    }

    fun appendAudit(e: AuditEntry) {
        conn.prepareStatement(
            """
            INSERT INTO audit_entry(at_epoch_ms, principal_id, capability_id, decision, reason, request_id, approval_token_id)
            VALUES (?,?,?,?,?,?,?)
            """.trimIndent(),
        ).use { ps ->
            ps.setLong(1, e.atEpochMs)
            ps.setString(2, e.principalId)
            ps.setString(3, e.capabilityId)
            ps.setString(4, e.decision)
            ps.setString(5, e.reason)
            ps.setString(6, e.requestId)
            ps.setString(7, e.approvalTokenId)
            ps.executeUpdate()
        }
    }

    fun loadAudit(limit: Int = 1000): List<AuditEntry> {
        val out = mutableListOf<AuditEntry>()
        conn.prepareStatement(
            "SELECT at_epoch_ms, principal_id, capability_id, decision, reason, request_id, approval_token_id FROM audit_entry ORDER BY id ASC LIMIT ?",
        ).use { ps ->
            ps.setInt(1, limit)
            ps.executeQuery().use { rs ->
                while (rs.next()) {
                    out += AuditEntry(
                        atEpochMs = rs.getLong(1),
                        principalId = rs.getString(2),
                        capabilityId = rs.getString(3),
                        decision = rs.getString(4),
                        reason = rs.getString(5),
                        requestId = rs.getString(6),
                        approvalTokenId = rs.getString(7),
                    )
                }
            }
        }
        return out
    }

    fun saveToken(t: ApprovalToken) {
        conn.prepareStatement(
            """
            INSERT OR REPLACE INTO approval_token(
              id, principal_id, action_id, target_resource, granted_scope,
              expires_at_epoch_ms, single_use, revoked, consumed
            ) VALUES (?,?,?,?,?,?,?,?,?)
            """.trimIndent(),
        ).use { ps ->
            ps.setString(1, t.id)
            ps.setString(2, t.principalId)
            ps.setString(3, t.actionId)
            ps.setString(4, t.targetResource)
            ps.setString(5, t.grantedScope)
            ps.setLong(6, t.expiresAtEpochMs)
            ps.setInt(7, if (t.singleUse) 1 else 0)
            ps.setInt(8, if (t.revoked) 1 else 0)
            ps.setInt(9, if (t.consumed) 1 else 0)
            ps.executeUpdate()
        }
    }

    fun loadToken(id: String): ApprovalToken? {
        conn.prepareStatement("SELECT * FROM approval_token WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { rs ->
                if (!rs.next()) return null
                return ApprovalToken(
                    id = rs.getString("id"),
                    principalId = rs.getString("principal_id"),
                    actionId = rs.getString("action_id"),
                    targetResource = rs.getString("target_resource"),
                    grantedScope = rs.getString("granted_scope"),
                    expiresAtEpochMs = rs.getLong("expires_at_epoch_ms"),
                    singleUse = rs.getInt("single_use") == 1,
                    revoked = rs.getInt("revoked") == 1,
                    consumed = rs.getInt("consumed") == 1,
                )
            }
        }
    }

    fun grantPermission(principalId: String, permission: String) {
        conn.prepareStatement(
            "INSERT OR IGNORE INTO permission_grant(principal_id, permission) VALUES (?,?)",
        ).use { ps ->
            ps.setString(1, principalId)
            ps.setString(2, permission)
            ps.executeUpdate()
        }
    }

    fun loadPermissions(principalId: String): Set<String> {
        val out = mutableSetOf<String>()
        conn.prepareStatement("SELECT permission FROM permission_grant WHERE principal_id = ?").use { ps ->
            ps.setString(1, principalId)
            ps.executeQuery().use { rs ->
                while (rs.next()) out += rs.getString(1)
            }
        }
        return out
    }

    fun auditCount(): Int {
        conn.createStatement().use { st ->
            st.executeQuery("SELECT COUNT(*) FROM audit_entry").use { rs ->
                return if (rs.next()) rs.getInt(1) else 0
            }
        }
    }

    override fun close() {
        conn.close()
    }
}
