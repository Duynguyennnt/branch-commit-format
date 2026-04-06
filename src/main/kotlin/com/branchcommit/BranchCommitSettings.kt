package com.branchcommit

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "BranchCommitSettings",
    storages = [Storage("BranchCommitFormatter.xml")]
)
class BranchCommitSettings : PersistentStateComponent<BranchCommitSettings.SettingsState> {

    class SettingsState {
        var enabled: Boolean = true
        var branchPattern: String = DEFAULT_PATTERN
        var commitFormat: String = DEFAULT_FORMAT
        var replaceUnderscores: Boolean = true
    }

    private var myState = SettingsState()

    override fun getState(): SettingsState = myState

    override fun loadState(state: SettingsState) {
        myState = state
    }

    companion object {
        const val DEFAULT_PATTERN = "^(?:.+/)?([A-Za-z]+-\\d+)[_-](.+)$"
        const val DEFAULT_FORMAT = "[{ticket}] {description}"

        fun getInstance(): BranchCommitSettings =
            ApplicationManager.getApplication().getService(BranchCommitSettings::class.java)
    }
}
