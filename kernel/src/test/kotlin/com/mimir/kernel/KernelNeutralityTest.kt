package com.mimir.kernel

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Kernel neutrality ratchet (Constitution §3.2).
 *
 * The kernel must not know engine names. After G-05 move, kernel must not mention engine names at all. Baseline is empty;
 * any hit fails the test.
 */
class KernelNeutralityTest {

    private val engineWord =
        Regex("\\b(terminal|forge|browser|computer|vault)\\b", RegexOption.IGNORE_CASE)

    /** path relative to com/mimir/kernel -> max allowed lines mentioning an engine name */
    // G-05: catalog + default policies + bootstrap notes moved out of kernel.
    // Baseline empty — no engine-name lines allowed in any kernel file.
    private val baseline = emptyMap<String, Int>()

    private fun mainRoot(): File =
        listOf(File("src/main/kotlin"), File("kernel/src/main/kotlin")).first { it.isDirectory }

    @Test
    fun kernel_does_not_gain_engine_names() {
        val root = mainRoot()
        val base = File(root, "com/mimir/kernel")
        val problems = mutableListOf<String>()
        root.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { f ->
            val rel = f.relativeTo(base).invariantSeparatorsPath
            val hits = f.readLines().count { engineWord.containsMatchIn(it) }
            val allowed = baseline[rel] ?: 0
            if (hits > allowed) problems += "$rel: $hits engine-name lines (allowed $allowed)"
        }
        assertTrue(problems.isEmpty(), "Kernel neutrality regressed:\n" + problems.joinToString("\n"))
    }
}
