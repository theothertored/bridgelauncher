package com.tored.bridgelauncher.ui2.settings.sections.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.settings.SystemBarAppearanceOptionsField
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.utils.getDisplayName

@Composable
fun SettingsScreen2OverlaysSectionContent(
    state: SettingsScreen2OverlaysSectionState,
    actions: SettingsScreen2OverlaysSectionActions,
    modifier: Modifier = Modifier
)
{
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    )
    {
        SystemBarAppearanceOptionsField(
            label = SettingsState::statusBarAppearance.getDisplayName(),
            selectedOption = state.statusBarAppearance,
            onChange = { actions.changeStatusBarAppearance(it) }
        )

        SystemBarAppearanceOptionsField(
            label = SettingsState::navigationBarAppearance.getDisplayName(),
            selectedOption = state.navigationBarAppearance,
            onChange = { actions.changeNavigationBarAppearance(it) }
        )

        CheckboxField(
            label = SettingsState::drawWebViewOverscrollEffects.getDisplayName(),
            isChecked = state.drawWebViewOverscrollEffects,
            onCheckedChange = { actions.changeDrawWebViewOverscrollEffects(it) }
        )
    }
}


// PREVIEW

@Composable
fun SettingsScreen2OverlaysSectionContentPreview(
    statusBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.Hide,
    navigationBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.Hide,
    drawWebViewOverscrollEffects: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2OverlaysSectionContent(
            state = SettingsScreen2OverlaysSectionState(
                statusBarAppearance = statusBarAppearance,
                navigationBarAppearance = navigationBarAppearance,
                drawWebViewOverscrollEffects = drawWebViewOverscrollEffects
            ),
            actions = SettingsScreen2OverlaysSectionActions.empty()
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2OverlaysSectionContentPreview01()
{
    SettingsScreen2OverlaysSectionContentPreview(
        navigationBarAppearance = SystemBarAppearanceOptions.LightIcons
    )
}

@Composable
@PreviewLightDark
fun SettingsScreen2OverlaysSectionContentPreview02()
{
    SettingsScreen2OverlaysSectionContentPreview(
        statusBarAppearance = SystemBarAppearanceOptions.LightIcons,
        navigationBarAppearance = SystemBarAppearanceOptions.DarkIcons,
        drawWebViewOverscrollEffects = true
    )
}