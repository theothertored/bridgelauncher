package com.tored.bridgelauncher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.shared.SetSystemBarsForBotBarActivity
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui.theme.botBar
import com.tored.bridgelauncher.ui.theme.textSec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


@AndroidEntryPoint
class AppDrawerActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                AppDrawerScreen()
            }
        }
    }
}

@Composable
fun AppDrawerScreen()
{
    SetSystemBarsForBotBarActivity()

    val context = LocalContext.current
    val appContext = context.applicationContext as BridgeLauncherApp
    val haptics = LocalHapticFeedback.current

    var searchString by remember { mutableStateOf("") }

    var dropdownOpenFor by remember { mutableStateOf<InstalledApp?>(null) }
    var dropdownItemInLazyColOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var dropdownTouchOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var dropdownParentSize by remember { mutableStateOf(IntSize(0, 0)) }

    var dropdownFinalOffset = (dropdownItemInLazyColOffset + dropdownTouchOffset).toIntOffset()
    val dropToLeft = dropdownFinalOffset.x > dropdownParentSize.width / 2
    val dropUp = dropdownFinalOffset.y > dropdownParentSize.height / 2

    val dropdownAlignment = if (dropToLeft)
    {
        if (dropUp)
            Alignment.BottomEnd
        else
            Alignment.TopEnd
    }
    else
    {
        if (dropUp)
            Alignment.BottomStart
        else
            Alignment.TopStart
    }

    if (dropToLeft)
        dropdownFinalOffset = dropdownFinalOffset.copy(x = dropdownFinalOffset.x - dropdownParentSize.width)

    if (dropUp)
        dropdownFinalOffset = dropdownFinalOffset.copy(y = dropdownFinalOffset.y - dropdownParentSize.height)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    )
    {
        Column()
        {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned {
                        dropdownParentSize = it.size
                    }
            )
            {
                AppContextMenu(
                    showForApp = dropdownOpenFor,
                    offset = dropdownFinalOffset,
                    alignment = dropdownAlignment,
                    onDismissRequest = {
                        dropdownOpenFor = null
                    }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(0.dp, 8.dp),
                )
                {
                    items(appContext.installedAppsHolder.installedApps)
                    { app ->

                        val interactionSource = remember { MutableInteractionSource() }
                        var positionInParent by remember { mutableStateOf(Offset(0f, 0f)) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .indication(interactionSource, LocalIndication.current)
                                .onGloballyPositioned {
                                    positionInParent = it.positionInParent()
                                }
                                .pointerInput(Unit)
                                {
                                    detectTapGestures(
                                        onPress = { offset ->
                                            val press = PressInteraction.Press(offset)

                                            val pressAfterDelayJob = CoroutineScope(coroutineContext).launch {
                                                delay(100)
                                                interactionSource.emit(press)
                                            }

                                            val gotCancelled = !tryAwaitRelease()
                                            val wasNotEmitted = !pressAfterDelayJob.isCompleted

                                            if (wasNotEmitted && gotCancelled)
                                            {
                                                pressAfterDelayJob.cancel()
                                            }
                                            else
                                            {
                                                if (wasNotEmitted)
                                                    interactionSource.emit(press)

                                                interactionSource.emit(
                                                    if (gotCancelled)
                                                        PressInteraction.Release(press)
                                                    else
                                                        PressInteraction.Cancel(press)
                                                )
                                            }
                                        },

                                        onTap = {
                                            context.startActivity(app.launchIntent)
                                        },

                                        onLongPress = { offset ->
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            dropdownItemInLazyColOffset = positionInParent
                                            dropdownTouchOffset = offset
                                            dropdownOpenFor = app
                                        }
                                    )
                                }
                                .padding(8.dp, 0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        )
                        {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            )
                            {
                                Image(
                                    painter = rememberDrawablePainter(app.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.FillBounds,
                                )
                            }
                            Column()
                            {
                                Text(app.label)
                                Text(app.packageName, color = MaterialTheme.colors.textSec, style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                }
            }

            SearchBotBar(searchString) { searchString = it }
        }
    }
}

private fun Offset.toIntOffset(): IntOffset
{
    return IntOffset(this.x.toInt(), this.y.toInt())
}

@Composable
fun AppContextMenu(
    showForApp: InstalledApp?,
    offset: IntOffset,
    alignment: Alignment,
    onDismissRequest: () -> Unit
)
{
    val context = LocalContext.current
    val clipman = LocalClipboardManager.current

    data class Action(
        val iconResId: Int,
        val label: String,
        val onClick: InstalledApp.() -> Unit,
    )

    val items = remember {
        arrayOf(
            Action(R.drawable.ic_copy, "Copy label")
            { clipman.setText(AnnotatedString(label)) },

            Action(R.drawable.ic_copy, "Copy package name")
            { clipman.setText(AnnotatedString(packageName)) },

            Action(R.drawable.ic_info, "App info")
            {
                try
                {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${packageName}")
                        )
                    )
                }
                catch (_: Exception)
                {
                    Toast.makeText(context, "Could not open app info.", Toast.LENGTH_SHORT).show()
                }
            },

            Action(R.drawable.ic_delete, "Uninstall")
            {
                try
                {
                    val packageURI = Uri.parse("package:${packageName}")
                    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
                    context.startActivity(uninstallIntent)
                }
                catch (_: Exception)
                {
                    Toast.makeText(context, "Could not request app uninstall.", Toast.LENGTH_SHORT).show()
                }
            },
        )
    }

    @Composable
    fun DropdownItems(app: InstalledApp)
    {
        for (item in items)
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.onClick(app)
                        onDismissRequest()
                    }
                    .padding(start = 32.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    item.label,
                    modifier = Modifier.width(IntrinsicSize.Max),
                )
                Box(
                    modifier = Modifier
                        .size(48.dp),
                    contentAlignment = Alignment.Center,
                )
                {
                    ResIcon(item.iconResId)
                }
            }
        }
    }

    if (showForApp != null)
    {
        Popup(
            offset = offset,
            alignment = alignment,
            onDismissRequest = onDismissRequest,
        )
        {

            Surface(
                modifier = Modifier,
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.large,
                elevation = 8.dp,
            )
            {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .padding(0.dp, 16.dp)
                )
                {

                    DropdownItems(showForApp)
                }
            }
        }
    }
}

@Composable
fun SearchBotBar(searchString: String, onSearchStringChange: (String) -> Unit)
{
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.botBar,
        elevation = 4.dp,
    )
    {
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            val context = LocalContext.current as Activity

            IconButton(onClick = { context.finish() })
            {
                ResIcon(R.drawable.ic_arrow_left)
            }
            TextField(
                value = searchString,
                onValueChange = onSearchStringChange,
                modifier = Modifier
                    .weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                placeholder = {
                    Text("Tap to search")
                }
            )
            Spacer(modifier = Modifier.size(48.dp))
//                IconToggleButton(
//                    checked = MaterialTheme.colors.isLight,
//                    onCheckedChange = { /* TODO */ }
//                )
//                {
//                    ResIcon(iconResId = R.drawable.ic_dark_mode)
//                }
        }
    }
}

@Composable
@Preview
fun AppDrawerPreview()
{
    BridgeLauncherTheme {
        AppDrawerScreen()
    }
}