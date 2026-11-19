plugins { kotlin("jvm") }
base { archivesName.set("engine-forge") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    implementation(project(":contracts:forge"))
    implementation(project(":shared:kernel"))
    implementation(project(":contracts:process"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
}
