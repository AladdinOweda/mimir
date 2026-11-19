package com.mimir.composition

import com.mimir.adapters.browser.WebViewDrivers
import com.mimir.adapters.browser.WebViewPort
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.computer.ComputerUseEngine
import com.mimir.engine.computer.UiDrivers

/**
 * Product composition only — not part of computer or browser engine modules.
 * Avoids computer→browser Gradle edge (H-02).
 */
class SharedBrowserSession(
    driverKind: String = "noop",
    val port: WebViewPort = WebViewDrivers.create(driverKind),
) {
    val computer: ComputerUseEngine =
        ComputerUseEngine(uiKind = driverKind, ui = UiDrivers.shared(port))

    val browser: BrowserEngine =
        BrowserEngine(driverKind = driverKind, driverFactory = { port })

    suspend fun start() {
        browser.start()
        computer.start()
    }
}
