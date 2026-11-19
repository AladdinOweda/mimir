plugins {
    kotlin("jvm")
}
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}
dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":shared:engine:terminal"))
    implementation(project(":shared:engine:forge"))
    implementation(project(":shared:engine:process"))
    implementation(project(":shared:engine:vault"))
    implementation(project(":shared:engine:tools"))
    implementation(project(":shared:engine:workflow"))
    implementation(project(":shared:engine:ai-model"))
    implementation(project(":shared:engine:verification"))
    implementation(project(":shared:engine:bot"))
    implementation(project(":shared:engine:agent-session"))
    implementation(project(":shared:engine:orchestration"))
    implementation(project(":shared:engine:browser"))
    implementation(project(":shared:engine:computer"))
    implementation(project(":shared:engine:agent-runtime"))
    implementation(project(":shared:engine:provider"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}
tasks.test { useJUnitPlatform() }
