plugins {
    `java-library`
    kotlin("jvm")
}
base { archivesName.set("engine-forge") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    api(project(":contracts:forge"))
    implementation(project(":shared:kernel"))
    api(project(":contracts:process"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation(project(":adapters:process"))
}
