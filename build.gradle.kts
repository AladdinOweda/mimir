plugins {
    base
    kotlin("jvm") version "2.0.21" apply false
    kotlin("android") version "2.0.21" apply false
    id("com.android.application") version "8.7.2" apply false
}

allprojects {
    version = providers.gradleProperty("mimir.version").getOrElse("0.5.0")
    // Disambiguate same directory names (terminal, process, forge, …)
    // under contracts/ vs engines/ — prevents module identity collision.
    group = when {
        path.startsWith(":contracts") -> "com.mimir.contracts"
        path.startsWith(":shared:engine") || path.startsWith(":engines") -> "com.mimir.engines"
        path.startsWith(":services") -> "com.mimir.services"
        path.startsWith(":adapters") -> "com.mimir.adapters"
        path.startsWith(":packages") -> "com.mimir.packages"
        path.startsWith(":shared") -> "com.mimir.shared"
        path.startsWith(":apps") -> "com.mimir.apps"
        else -> "com.mimir"
    }
    if (project != rootProject) {
        val pathName = path.removePrefix(":").replace(':', '-')
        plugins.withId("java") {
            base.archivesName.set(pathName)
        }
        plugins.withId("org.jetbrains.kotlin.jvm") {
            base.archivesName.set(pathName)
        }
    }
}

tasks.register("printModules") {
    doLast {
        println(rootProject.subprojects.map { it.path }.sorted().joinToString("\n"))
    }
}

tasks.register("verifyModuleBuilds") {
    doLast {
        val intermediate = setOf(":shared", ":shared:engine", ":apps")
        val missing = rootProject.subprojects
            .filter { it.path !in intermediate }
            .filter {
                !File(it.projectDir, "build.gradle.kts").exists() &&
                    !File(it.projectDir, "build.gradle").exists()
            }
        if (missing.isNotEmpty()) {
            throw GradleException("Missing build.gradle.kts for: " + missing.map { it.path })
        }
        println("All product subprojects have build scripts (${rootProject.subprojects.size - intermediate.size} leaves)")
    }
}
