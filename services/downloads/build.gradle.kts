plugins { kotlin("jvm") }
// events optional for DownloadEventBridge
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:events"))
    implementation(project(":shared:kernel"))
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
