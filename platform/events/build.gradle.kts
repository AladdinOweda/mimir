plugins { kotlin("jvm") }
base { archivesName.set("platform-events") }
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":contracts:events"))
    testImplementation(kotlin("test"))
}
