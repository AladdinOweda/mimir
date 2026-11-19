plugins { kotlin("jvm") }
base { archivesName.set("contracts-chat") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":shared:kernel"))
    testImplementation(kotlin("test"))
}
