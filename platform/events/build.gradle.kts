plugins {
    `java-library`
    kotlin("jvm")
}

base {
    archivesName.set("platform-events")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(project(":shared:kernel"))
    api(project(":contracts:events"))
    testImplementation(kotlin("test"))
}
