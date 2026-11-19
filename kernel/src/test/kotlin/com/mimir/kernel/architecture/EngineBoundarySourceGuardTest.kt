package com.mimir.kernel.architecture

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Static guard: no private engine->engine imports in production sources, and no engine->engine
 * Gradle edges outside test scope.
 *
 * Portable: finds the repo root by walking up from the working directory to settings.gradle.kts
 * (Gradle runs tests with cwd = module dir). Fails if it cannot find the root or scans nothing,
 * so it can never pass vacuously.
 */
class EngineBoundarySourceGuardTest {

    private fun repoRoot(): File {
        var d: File? = File(System.getProperty("user.dir")).absoluteFile
        while (d != null) {
            if (File(d, "settings.gradle.kts").isFile) return d
            d = d.parentFile
        }
        error("settings.gradle.kts not found above ${System.getProperty("user.dir")}")
    }

    private val importLine = Regex("""^\s*import\s+com\.mimir\.engine\.(\w+)""")

    @Test
    fun no_engine_imports_another_engine_in_main_sources() {
        val root = repoRoot()
        val engines = File(root, "engines").listFiles { f -> f.isDirectory }.orEmpty()
        var scanned = 0
        val violations = mutableListOf<String>()
        for (eng in engines) {
            val main = File(eng, "src/main")
            if (!main.isDirectory) continue
            main.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { f ->
                scanned++
                val ownPkg = Regex("""^\s*package\s+com\.mimir\.engine\.(\w+)""")
                    .find(f.readText().lineSequence().firstOrNull { it.trimStart().startsWith("package ") } ?: "")
                    ?.groupValues?.get(1)
                f.readLines().forEach { line ->
                    val other = importLine.find(line)?.groupValues?.get(1)
                    if (other != null && other != ownPkg) {
                        violations += "${f.relativeTo(root).invariantSeparatorsPath}: $line"
                    }
                }
            }
        }
        assertTrue(scanned > 0, "guard scanned no engine sources (wrong root?)")
        assertTrue(violations.isEmpty(), "engine->engine imports:\n" + violations.joinToString("\n"))
    }

    @Test
    fun no_engine_to_engine_gradle_edges_outside_tests() {
        val root = repoRoot()
        val edge = Regex("""^\s*(implementation|api|compileOnly|runtimeOnly)\(project\(":shared:engine:""")
        val violations = mutableListOf<String>()
        var scripts = 0
        File(root, "engines").listFiles { f -> f.isDirectory }.orEmpty().forEach { eng ->
            val b = File(eng, "build.gradle.kts")
            if (!b.isFile) return@forEach
            scripts++
            b.readLines().filter { edge.containsMatchIn(it) }.forEach {
                violations += "${eng.name}: ${it.trim()}"
            }
        }
        assertTrue(scripts > 0, "guard found no engine build scripts (wrong root?)")
        assertTrue(violations.isEmpty(), "engine->engine Gradle edges:\n" + violations.joinToString("\n"))
    }
}
