plugins { kotlin("jvm") }
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":shared:events"))
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
