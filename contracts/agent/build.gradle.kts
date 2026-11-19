plugins { kotlin("jvm") }
base { archivesName.set("contracts-agent") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies { testImplementation(kotlin("test")) }
tasks.test { useJUnitPlatform() }
