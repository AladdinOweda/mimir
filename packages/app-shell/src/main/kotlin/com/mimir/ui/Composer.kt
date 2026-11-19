package com.mimir.ui

/**
 * Global assistant/composer — owned by Shell, not Chat (MIMIR-UX-SHELL-001 §9).
 */
enum class ComposerState {
    Collapsed,
    Expanded,
    KeyboardActive,
    Sending,
    Processing,
    ApprovalRequired,
    Error,
    Disabled,
}

data class ComposerDraft(
    val text: String = "",
    val attachmentNames: List<String> = emptyList(),
    val contextLabel: String = "",
)

data class ComposerSnapshot(
    val state: ComposerState,
    val draft: ComposerDraft,
    val contextDestination: AppDestination,
)

class GlobalComposer {
    var state: ComposerState = ComposerState.Collapsed
        private set
    var draft: ComposerDraft = ComposerDraft()
        private set
    var contextDestination: AppDestination = AppDestination.Home
        private set

    fun setContext(destination: AppDestination) {
        contextDestination = destination
        draft = draft.copy(contextLabel = destination.name)
    }

    fun expand() {
        if (state != ComposerState.Disabled) state = ComposerState.Expanded
    }

    fun collapse() {
        if (state != ComposerState.Sending && state != ComposerState.Processing) {
            state = ComposerState.Collapsed
        }
    }

    fun updateText(text: String) {
        draft = draft.copy(text = text)
        if (state == ComposerState.Collapsed && text.isNotBlank()) state = ComposerState.Expanded
    }

    /** Request send — UI only; actual dispatch is application service. Returns false if blocked. */
    fun requestSend(): Boolean {
        if (state == ComposerState.Disabled || state == ComposerState.Sending) return false
        if (draft.text.isBlank() && draft.attachmentNames.isEmpty()) return false
        state = ComposerState.Sending
        return true
    }

    fun markProcessing() {
        state = ComposerState.Processing
    }

    fun markError(preserveDraft: Boolean = true) {
        state = ComposerState.Error
        if (!preserveDraft) draft = ComposerDraft(contextLabel = draft.contextLabel)
    }

    fun markIdle() {
        state = ComposerState.Expanded
        draft = draft.copy(text = "", attachmentNames = emptyList())
    }

    fun snapshot() = ComposerSnapshot(state, draft, contextDestination)
}
