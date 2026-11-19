package com.mimir.desktop

import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import java.io.File

/**
 * Desktop entry — durable KernelHost product path.
 * Full Compose + ProductHosts wiring lands with UX/apps packaging cycle.
 */
fun main(args: Array<String>) {
    val root = File(args.getOrNull(0) ?: "${System.getProperty("user.home")}/.mimir/data/kernel")
    val host = KernelHost.durable(root)
    host.permissions.grantAll("local", listOf("*"))
    println("mimir-desktop ready")
    println("data=${root.absolutePath}")
    println("principal=local auditSize=${host.audit.size()} canShip=false")
    println("destinations=Home,Chat,Projects,Tasks,Workspace,Library,Settings")
}
