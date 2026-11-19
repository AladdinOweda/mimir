plugins { kotlin("jvm") }
base { archivesName.set("contracts-process") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    testImplementation(kotlin("test"))
}
