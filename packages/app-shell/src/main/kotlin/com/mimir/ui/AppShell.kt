package com.mimir.ui

data class FeatureFlags(
    val chat: Boolean = true,
    val forge: Boolean = true,
    val browser: Boolean = true,
    val agents: Boolean = true,
    val terminal: Boolean = true,
    val vault: Boolean = true,
)

data class StringCatalog(val locale: String = "en") {
    fun t(en: String, ar: String) = if (locale.startsWith("ar")) ar else en
}

sealed class UiNode {
    data class Screen(val destination: AppDestination, val title: String) : UiNode()
    data class BottomNav(val items: List<NavItem>, val selected: BottomDestination) : UiNode()
    data class Drawer(val items: List<NavItem>, val selected: AppDestination) : UiNode()
    data class Composer(val snapshot: ComposerSnapshot) : UiNode()
    data class Layout(val mode: LayoutMode) : UiNode()
}

class AppShell(
    private val flags: FeatureFlags = FeatureFlags(),
    private val catalog: StringCatalog = StringCatalog(),
    val composer: GlobalComposer = GlobalComposer(),
) {
    fun visibleDrawer(): List<NavItem> = AppNavCatalog.drawer.filter {
        when (it.id) {
            AppDestination.Chat -> flags.chat
            AppDestination.Forge -> flags.forge
            AppDestination.Browser -> flags.browser
            AppDestination.Agents -> flags.agents
            AppDestination.Terminal -> flags.terminal
            AppDestination.Vault -> flags.vault
            else -> true
        }
    }

    fun bottomSelected(destination: AppDestination): BottomDestination = when (destination) {
        AppDestination.Home -> BottomDestination.Home
        AppDestination.Projects -> BottomDestination.Projects
        AppDestination.Tasks -> BottomDestination.Tasks
        else -> BottomDestination.More
    }

    fun render(destination: AppDestination, widthDp: Int): List<UiNode> {
        val mode = layoutModeForWidth(widthDp)
        composer.setContext(destination)
        val title = (AppNavCatalog.bottom + visibleDrawer()).find { it.id == destination }?.let {
            catalog.t(it.labelEn, it.labelAr)
        } ?: destination.name
        return listOf(
            UiNode.Layout(mode),
            UiNode.BottomNav(AppNavCatalog.bottom, bottomSelected(destination)),
            UiNode.Drawer(visibleDrawer(), destination),
            UiNode.Composer(composer.snapshot()),
            UiNode.Screen(destination, title),
        )
    }
}
