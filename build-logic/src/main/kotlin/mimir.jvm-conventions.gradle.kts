plugins {
    kotlin("jvm")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
