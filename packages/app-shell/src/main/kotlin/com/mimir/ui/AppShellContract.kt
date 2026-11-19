package com.mimir.ui

/**
 * Application Shell destinations — MIMIR-UX-SHELL-001.
 * Bottom nav is product destinations only; engines are workspaces via drawer.
 */

/** Mobile bottom navigation (exactly these four). */
enum class BottomDestination {
    Home,
    Projects,
    Tasks,
    More,
}

/**
 * Global destinations including drawer workspaces and secondary areas.
 * Engine workspaces (Chat/Forge/Browser) are not bottom-nav peers.
 */
enum class AppDestination {
    Home,
    Projects,
    Tasks,
    More,
    // Workspaces (drawer)
    Chat,
    Forge,
    Browser,
    // Organization / intelligence / management
    Agents,
    Activity,
    Approvals,
    History,
    Files,
    Settings,
    Help,
    // Technical surfaces reachable from More / Settings — not primary nav
    Terminal,
    Vault,
}

data class NavItem(
    val id: AppDestination,
    val labelEn: String,
    val labelAr: String,
    val group: NavGroup,
)

enum class NavGroup {
    Bottom,
    Workspace,
    Organization,
    Intelligence,
    Management,
    Knowledge,
    System,
    Technical,
}

object AppNavCatalog {
    val bottom: List<NavItem> = listOf(
        NavItem(AppDestination.Home, "Home", "الرئيسية", NavGroup.Bottom),
        NavItem(AppDestination.Projects, "Projects", "المشاريع", NavGroup.Bottom),
        NavItem(AppDestination.Tasks, "Tasks", "المهام", NavGroup.Bottom),
        NavItem(AppDestination.More, "More", "المزيد", NavGroup.Bottom),
    )

    val drawer: List<NavItem> = listOf(
        NavItem(AppDestination.Chat, "Chat", "المحادثة", NavGroup.Workspace),
        NavItem(AppDestination.Forge, "Forge", "Forge", NavGroup.Workspace),
        NavItem(AppDestination.Browser, "Browser", "المتصفح", NavGroup.Workspace),
        NavItem(AppDestination.Projects, "Projects", "المشاريع", NavGroup.Organization),
        NavItem(AppDestination.Tasks, "Tasks", "المهام", NavGroup.Organization),
        NavItem(AppDestination.Agents, "Agents", "الوكلاء", NavGroup.Intelligence),
        NavItem(AppDestination.Activity, "Activity", "النشاط", NavGroup.Intelligence),
        NavItem(AppDestination.Approvals, "Approvals", "الموافقات", NavGroup.Management),
        NavItem(AppDestination.History, "History", "السجل", NavGroup.Management),
        NavItem(AppDestination.Files, "Files & Knowledge", "الملفات والمعرفة", NavGroup.Knowledge),
        NavItem(AppDestination.Settings, "Settings", "الإعدادات", NavGroup.System),
        NavItem(AppDestination.Help, "Help", "المساعدة", NavGroup.System),
        NavItem(AppDestination.Terminal, "Terminal", "الطرفية", NavGroup.Technical),
        NavItem(AppDestination.Vault, "Vault", "الخزنة", NavGroup.Technical),
    )

    fun bottomIds(): Set<AppDestination> = bottom.map { it.id }.toSet()
}

enum class LayoutMode { PhoneBottomBar, TabletRail, DesktopDrawer }

fun layoutModeForWidth(widthDp: Int): LayoutMode = when {
    widthDp < 600 -> LayoutMode.PhoneBottomBar
    widthDp < 1024 -> LayoutMode.TabletRail
    else -> LayoutMode.DesktopDrawer
}

/** Workspace contribution descriptor (shell consumes; does not hard-code engine logic). */
data class WorkspaceDescriptor(
    val key: String,
    val titleEn: String,
    val titleAr: String,
    val destination: AppDestination,
    val tabs: List<String> = listOf("overview"),
    val supportsSplit: Boolean = false,
)

object DefaultWorkspaces {
    val all = listOf(
        WorkspaceDescriptor("chat", "Chat", "المحادثة", AppDestination.Chat),
        WorkspaceDescriptor("forge", "Forge", "Forge", AppDestination.Forge, tabs = listOf("overview", "scaffold"), supportsSplit = true),
        WorkspaceDescriptor("browser", "Browser", "المتصفح", AppDestination.Browser, tabs = listOf("tabs", "policy")),
    )
}
