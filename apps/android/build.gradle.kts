plugins {
    kotlin("jvm")
}
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}
dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":shared:ui"))
    implementation(project(":services:tasks"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
