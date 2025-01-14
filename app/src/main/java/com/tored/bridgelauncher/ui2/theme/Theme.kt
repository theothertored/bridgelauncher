package com.tored.bridgelauncher.ui2.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.services.settings2.BridgeThemeOptions
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.rememberBridgeSettingState
import com.tored.bridgelauncher.utils.ComposableContent

private val DarkColorPalette = darkColors(
    primary = GreenA200,
    primaryVariant = GreenA200,
    secondary = GreenA200,
    background = Color(0xff212121),
    surface = Color(0xff000000),
)

private val LightColorPalette = lightColors(
    primary = GreenA700,
    primaryVariant = GreenA700,
    secondary = GreenA700,
    background = Color(0xfff7f7f7),
    surface = Color(0xffffffff),
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun BridgeLauncherTheme(content: ComposableContent)
{
    val theme by rememberBridgeSettingState(BridgeSettings.theme)

    val useDarkTheme = theme == BridgeThemeOptions.Dark || (theme == BridgeThemeOptions.System && isSystemInDarkTheme())

    BridgeLauncherThemeStateless(
        useDarkTheme = useDarkTheme,
        content = content,
    )
}

@Composable
fun BridgeLauncherThemeStateless(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: ComposableContent
)
{
    MaterialTheme(
        colors = if (useDarkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
    )
    {
        CompositionLocalProvider(
            LocalElevationOverlay provides null,
            content = content
        )
    }
}

data class Borders(val soft: BorderStroke)

val MaterialTheme.borders: Borders
    @Composable
    get() = Borders(
        soft = BorderStroke(width = 1.dp, color = colors.borderLight)
    )

val Colors.textSec: Color
    get() = if (isLight) Color(0x8C000000) else Color(0x8CFFFFFF)

val Colors.textPlaceholder: Color
    get() = if (isLight) Color(0x65000000) else Color(0x66FFFFFF)

val Colors.checkedItemBg: Color
    get() = if (isLight) Color(0x26000000) else Color(0x26FFFFFF)

val Colors.scrim: Color
    get() = if (isLight) BridgeColorsLightScrim else BridgeColorsDarkScrim

val BridgeColorsLightScrim = Color(0xD9FFFFFF)
val BridgeColorsDarkScrim = Color(0x80000000)

val Colors.info: Color
    get() = if (isLight) Color(0xFF1A4FB6) else Color(0xFF729BEC)

val Colors.warning: Color
    get() = if (isLight) Color(0xFFC88D1C) else Color(0xFFEFC779)

val Colors.borderLight: Color
    get() = if(isLight) Color(0x26000000) else Color(0x26ffffff)

val Colors.inputFieldBg: Color
    get () = if (isLight) Color(0x10000000) else Color(0x33FFFFFF)









