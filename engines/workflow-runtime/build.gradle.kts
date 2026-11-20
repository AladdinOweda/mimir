plugins {
    `java-library`
    kotlin("jvm")
}
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}
dependencies {
    api(project(":contracts:workflow"))
    implementation(project(":shared:kernel"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
