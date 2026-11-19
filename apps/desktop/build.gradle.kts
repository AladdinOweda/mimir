plugins {
    kotlin("jvm")
    application
}
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}
application {
    mainClass.set("com.mimir.desktop.MainKt")
}
dependencies {
    implementation(project(":shared:kernel"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    testImplementation(kotlin("test"))
}
