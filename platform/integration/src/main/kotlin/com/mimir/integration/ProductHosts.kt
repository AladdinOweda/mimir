package com.mimir.integration

import com.mimir.engine.process.JdbcTaskStore
import com.mimir.engine.process.TaskBoardEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.kernel.host.KernelHost
import java.io.File

/**
 * Product-path durable hosts (SQLite files under [root]).
 */
object ProductHosts {
    fun kernel(root: File): KernelHost = KernelHost.durable(File(root, "kernel"))

    fun taskBoard(root: File): TaskBoardEngine =
        TaskBoardEngine(store = JdbcTaskStore(File(root, "tasks/tasks.db")))

    fun vault(root: File): VaultEngine =
        VaultEngine(durableDb = File(root, "vault/vault.db"))
}
