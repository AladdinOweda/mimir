plugins { kotlin("jvm") }
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
