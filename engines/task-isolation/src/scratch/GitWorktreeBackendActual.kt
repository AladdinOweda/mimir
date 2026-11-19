package com.mimir.engine.task.workspace

import com.mimir.engine.task.IsolationKind
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Real Git worktree backend for **software tasks only** (Desktop/JVM).
 *
 * Requires:
 * - `git` on PATH
 * - [basePath] pointing at an existing git repository
 *
 * Does not run on Android common paths — TaskIsolationEngine selects backend by kind.
 */
class GitWorktreeBackendActual(
    private val worktreesDirName: String = ".mimir-worktrees",
    private val timeoutSec: Long = 60,
) : IsolationBackend {

    override val kind: IsolationKind = IsolationKind.GitWorktree

    override fun prepare(taskKey: String, basePath: String?): WorkspaceHandle? {
        val repo = File(basePath ?: return null)
        if (!repo.isDirectory) return null
        if (!File(repo, ".git").exists() && !isGitRepo(repo)) return null

        val safeKey = taskKey.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val branch = "mimir/task-$safeKey"
        val wtRoot = File(repo, worktreesDirName)
        if (!wtRoot.exists()) wtRoot.mkdirs()
        val wtPath = File(wtRoot, safeKey)

        if (wtPath.exists()) {
            // recover existing worktree
            return WorkspaceHandle(path = wtPath.absolutePath, kind = IsolationKind.GitWorktree, dirty = isDirty(wtPath))
        }

        // git worktree add -b branch path
        val add = runGit(
            repo,
            listOf("worktree", "add", "-b", branch, wtPath.absolutePath),
        )
        if (!add.ok) {
            // branch may exist — try without -b
            val add2 = runGit(
                repo,
                listOf("worktree", "add", wtPath.absolutePath, branch),
            )
            if (!add2.ok) {
                // last resort: new branch from HEAD with unique name
                val branch2 = "$branch-${System.currentTimeMillis() % 100000}"
                val add3 = runGit(
                    repo,
                    listOf("worktree", "add", "-b", branch2, wtPath.absolutePath),
                )
                if (!add3.ok) return null
            }
        }
        return WorkspaceHandle(path = wtPath.absolutePath, kind = IsolationKind.GitWorktree, dirty = false)
    }

    override fun markDirty(handle: WorkspaceHandle): WorkspaceHandle =
        handle.copy(dirty = true)

    override fun isCleanForMerge(handle: WorkspaceHandle): Boolean {
        val dir = File(handle.path)
        if (!dir.isDirectory) return false
        return !isDirty(dir)
    }

    fun removeWorktree(handle: WorkspaceHandle, repoPath: String, force: Boolean = false): Boolean {
        val repo = File(repoPath)
        val args = mutableListOf("worktree", "remove")
        if (force) args += "--force"
        args += handle.path
        return runGit(repo, args).ok
    }

    private fun isGitRepo(dir: File): Boolean {
        val r = runGit(dir, listOf("rev-parse", "--is-inside-work-tree"))
        return r.ok && r.stdout.trim() == "true"
    }

    private fun isDirty(dir: File): Boolean {
        val r = runGit(dir, listOf("status", "--porcelain"))
        return !r.ok || r.stdout.isNotBlank()
    }

    data class GitResult(val ok: Boolean, val exitCode: Int, val stdout: String, val stderr: String)

    private fun runGit(cwd: File, args: List<String>): GitResult {
        return try {
            val pb = ProcessBuilder(listOf("git") + args)
                .directory(cwd)
                .redirectErrorStream(false)
            val p = pb.start()
            val stdout = p.inputStream.bufferedReader().readText()
            val stderr = p.errorStream.bufferedReader().readText()
            val finished = p.waitFor(timeoutSec, TimeUnit.SECONDS)
            if (!finished) {
                p.destroyForcibly()
                return GitResult(false, -1, stdout, stderr + "\ntimeout")
            }
            GitResult(p.exitValue() == 0, p.exitValue(), stdout, stderr)
        } catch (t: Throwable) {
            GitResult(false, -1, "", t.message ?: "git_fail")
        }
    }
}
