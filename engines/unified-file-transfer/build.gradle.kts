plugins { kotlin("jvm") }
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":contracts:uft"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}
tasks.test { useJUnitPlatform() }
