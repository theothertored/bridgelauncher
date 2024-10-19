package com.tored.bridgelauncher.ui2.settings.sections.project

data class SettingsScreen2ProjectSectionActions(
    val changeProject: () -> Unit,
    val onStoragePermsStateChanged: (areGranted: Map<String, Boolean>) -> Unit,
    val changeAllowProjectsToTurnScreenOff: (newAllow: Boolean) -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2ProjectSectionActions({}, {}, {})
    }
}