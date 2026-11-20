rootProject.name = "mimir"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

// Historic coordinate names preserved for dependency stability;
// projectDir points at MIMIR-REPO-002 physical layout.

include(":shared:kernel")
include(":shared:ui")
include(":shared:design-tokens")
include(":shared:integration")

include(":shared:engine:terminal")
include(":shared:engine:forge")
include(":shared:engine:browser")
include(":shared:engine:computer")
include(":shared:engine:orchestration")
include(":shared:engine:ie")
include(":shared:engine:vault")
include(":shared:engine:agent-connectivity")
include(":shared:engine:agent-session")
include(":shared:engine:agent-runtime")
include(":shared:engine:bot")
include(":shared:engine:task-isolation")
include(":shared:engine:spe")
include(":shared:engine:process")
include(":shared:engine:provider")
include(":shared:engine:verification")
include(":shared:engine:ai-model")
include(":shared:engine:workflow")
include(":shared:engine:tools")

include(":apps:android")
include(":apps:desktop")

project(":shared:kernel").projectDir = file("kernel")
project(":shared:ui").projectDir = file("packages/app-shell")
project(":shared:design-tokens").projectDir = file("packages/design-tokens")
project(":shared:integration").projectDir = file("platform/integration")

project(":shared:engine:terminal").projectDir = file("engines/terminal")
project(":shared:engine:forge").projectDir = file("engines/forge")
project(":shared:engine:browser").projectDir = file("engines/browser")
project(":shared:engine:computer").projectDir = file("engines/computer")
project(":shared:engine:orchestration").projectDir = file("engines/orchestration")
project(":shared:engine:ie").projectDir = file("engines/intelligent-execution")
project(":shared:engine:vault").projectDir = file("engines/vault")
project(":shared:engine:agent-connectivity").projectDir = file("engines/agent-connectivity")
project(":shared:engine:agent-session").projectDir = file("engines/agent-session")
project(":shared:engine:agent-runtime").projectDir = file("engines/agent-runtime")
project(":shared:engine:bot").projectDir = file("engines/bot-profiles")
project(":shared:engine:task-isolation").projectDir = file("engines/task-isolation")
project(":shared:engine:spe").projectDir = file("engines/spe")
project(":shared:engine:process").projectDir = file("engines/process")
project(":shared:engine:provider").projectDir = file("engines/provider")
project(":shared:engine:verification").projectDir = file("engines/verification")
project(":shared:engine:ai-model").projectDir = file("engines/ai-model")
project(":shared:engine:workflow").projectDir = file("engines/workflow-runtime")
project(":shared:engine:tools").projectDir = file("engines/tools")

project(":apps:android").projectDir = file("apps/android")
project(":apps:desktop").projectDir = file("apps/desktop")

val localProps = java.util.Properties()
val localFile = file("local.properties")
val hasAndroidSdk = localFile.exists().also {
    if (it) localFile.inputStream().use { s -> localProps.load(s) }
} && (localProps.getProperty("sdk.dir") != null || System.getenv("ANDROID_HOME") != null)
if (hasAndroidSdk) {
include(":apps:androidApp")
project(":apps:androidApp").projectDir = file("apps/androidApp")
}

include(":adapters:browser")
include(":adapters:process")
project(":adapters:process").projectDir = file("adapters/process")
project(":adapters:browser").projectDir = file("adapters/browser")

include(":shared:memory")
project(":shared:memory").projectDir = file("platform/memory")

include(":shared:secrets")
project(":shared:secrets").projectDir = file("platform/secrets")

include(":services:approvals")
project(":services:approvals").projectDir = file("services/approvals")
include(":services:activity")
project(":services:activity").projectDir = file("services/activity")
include(":services:files")
project(":services:files").projectDir = file("services/files")
include(":engines:loop-runtime")
project(":engines:loop-runtime").projectDir = file("engines/loop-runtime")

include(":services:notifications")
project(":services:notifications").projectDir = file("services/notifications")
include(":packages:app-composition")
project(":packages:app-composition").projectDir = file("packages/app-composition")
include(":services:projects")
project(":services:projects").projectDir = file("services/projects")
include(":services:tasks")
project(":services:tasks").projectDir = file("services/tasks")

include(":shared:events")
project(":shared:events").projectDir = file("platform/events")
include(":shared:configuration")
project(":shared:configuration").projectDir = file("platform/configuration")


include(":contracts:capability")
project(":contracts:capability").projectDir = file("contracts/capability")

include(":services:downloads")
project(":services:downloads").projectDir = file("services/downloads")
include(":contracts:knowledge")
project(":contracts:knowledge").projectDir = file("contracts/knowledge")

include(":services:retention")
project(":services:retention").projectDir = file("services/retention")
include(":services:portability")
project(":services:portability").projectDir = file("services/portability")

include(":contracts:terminal")
project(":contracts:terminal").projectDir = file("contracts/terminal")
include(":contracts:forge")
project(":contracts:forge").projectDir = file("contracts/forge")
include(":contracts:browser")
project(":contracts:browser").projectDir = file("contracts/browser")

include(":contracts:agent")
project(":contracts:agent").projectDir = file("contracts/agent")
include(":contracts:workflow")
project(":contracts:workflow").projectDir = file("contracts/workflow")
include(":contracts:events")
project(":contracts:events").projectDir = file("contracts/events")

include(":engines:knowledge-workspace")
project(":engines:knowledge-workspace").projectDir = file("engines/knowledge-workspace")

include(":engines:unified-file-transfer")
project(":engines:unified-file-transfer").projectDir = file("engines/unified-file-transfer")

include(":engines:ai-hub")
project(":engines:ai-hub").projectDir = file("engines/ai-hub")

include(":engines:collaboration")
project(":engines:collaboration").projectDir = file("engines/collaboration")

include(":engines:chat")
project(":engines:chat").projectDir = file("engines/chat")
include(":contracts:chat")
project(":contracts:chat").projectDir = file("contracts/chat")
include(":contracts:process")
project(":contracts:process").projectDir = file("contracts/process")
include(":contracts:aihub")
project(":contracts:aihub").projectDir = file("contracts/aihub")
include(":contracts:collab")
project(":contracts:collab").projectDir = file("contracts/collab")
include(":contracts:uft")
project(":contracts:uft").projectDir = file("contracts/uft")
include(":contracts:vault")
project(":contracts:vault").projectDir = file("contracts/vault")
include(":contracts:verify")
project(":contracts:verify").projectDir = file("contracts/verify")
include(":contracts:spe")
project(":contracts:spe").projectDir = file("contracts/spe")
include(":contracts:bot")
project(":contracts:bot").projectDir = file("contracts/bot")
include(":contracts:loop")
project(":contracts:loop").projectDir = file("contracts/loop")
include(":contracts:computer")
project(":contracts:computer").projectDir = file("contracts/computer")
include(":contracts:ie")
project(":contracts:ie").projectDir = file("contracts/ie")

include(":engines:integration-tests")
project(":engines:integration-tests").projectDir = file("engines/integration-tests")
