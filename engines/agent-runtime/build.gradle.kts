plugins { kotlin("jvm") }
base { archivesName.set("engine-agent-runtime") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":contracts:agent"))
    implementation(project(":shared:kernel"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
}
