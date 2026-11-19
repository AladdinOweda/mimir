package com.mimir.engine.process
import com.mimir.contracts.process.ProcessExecResult

import com.mimir.contracts.process.ProcessExecutorPort
import com.mimir.contracts.process.UnavailableProcessExecutor
import com.mimir.contracts.process.ProcessExecRequest
import java.io.File

data class MergeAttempt(
    val ok: Boolean,
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val reason: String,
)

/**
 * R6 — never mark Accepted/Done as merged unless git reports success.
 * forceMerge without clean tree → Failed, not Succeeded.
 */
class GitMergeGate(
    private val executor: ProcessExecutorPort = UnavailableProcessExecutor,
) {
    fun mergeBranch(
        repoDir: File,
        sourceBranch: String,
        targetBranch: String,
        forceDirty: Boolean = false,
    ): MergeAttempt {
        if (!repoDir.isDirectory || !File(repoDir, ".git").exists()) {
            return MergeAttempt(false, -1, "", "not_a_git_repo", "not_a_git_repo")
        }
        val status = executor.execute(
            ProcessExecRequest(
                argv = listOf("git", "status", "--porcelain"),
                cwd = repoDir.absolutePath,
                workspaceRoot = repoDir.absolutePath,
                shell = false,
                timeoutMs = 10_000,
            ),
        )
        if (status.exitCode != 0) {
            return MergeAttempt(false, status.exitCode, status.stdout, status.stderr, "status_failed")
        }
        if (status.stdout.isNotBlank() && !forceDirty) {
            return MergeAttempt(false, 1, status.stdout, "", "dirty_worktree")
        }
        if (status.stdout.isNotBlank() && forceDirty) {
            return MergeAttempt(false, 1, status.stdout, "", "force_dirty_rejected")
        }
        val checkout = executor.execute(
            ProcessExecRequest(
                argv = listOf("git", "checkout", targetBranch),
                cwd = repoDir.absolutePath,
                workspaceRoot = repoDir.absolutePath,
                shell = false,
            ),
        )
        if (checkout.exitCode != 0) {
            return MergeAttempt(false, checkout.exitCode, checkout.stdout, checkout.stderr, "checkout_failed")
        }
        val merge = executor.execute(
            ProcessExecRequest(
                argv = listOf("git", "merge", "--no-ff", sourceBranch, "-m", "mimir: merge $sourceBranch"),
                cwd = repoDir.absolutePath,
                workspaceRoot = repoDir.absolutePath,
                shell = false,
                timeoutMs = 30_000,
            ),
        )
        return if (merge.exitCode == 0) {
            MergeAttempt(true, 0, merge.stdout, merge.stderr, "merged")
        } else {
            MergeAttempt(false, merge.exitCode, merge.stdout, merge.stderr, "merge_failed")
        }
    }
}
