package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.utils.colorPaletteModeKey
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.rememberPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { WindowInsets.ime },
    content: @Composable ColumnScope.() -> Unit,
) {
    val bottomPadding = if(isLandscape) 0.dp else WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    //NEW SHEET
//    val mbSheetState: ModalBottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
//        initialValue = androidx.compose.material.ModalBottomSheetValue.Expanded,
//        skipHalfExpanded = true,
//        confirmValueChange = {
//            if (it == androidx.compose.material.ModalBottomSheetValue.Hidden) {
//                onDismissRequest()
//            }
//            true
//        }
//    )

    if (showSheet) {

        //NEW SHEET
//        val scope = rememberCoroutineScope()
//        SideEffect {
//            scope.launch {
//                mbSheetState.show()
//            }
//        }
//        ModalBottomSheetLayout(
//            sheetState =  mbSheetState,
//            sheetContentColor = colorPalette().background0,
//            sheetBackgroundColor = colorPalette().background0,
//            sheetShape = shape,
//            sheetElevation = tonalElevation,
//            scrimColor = scrimColor,
//            sheetContent = {
//                Column(Modifier.padding(bottom = bottomPadding)) {
//                    content()
//                }
//            },
//            content = {}
//        )
        // NEW SHEET

        //PREVIOUS SHEET
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets
        ) {
            val colorPaletteMode by rememberPreference(colorPaletteModeKey, ColorPaletteMode.Dark)
            val isPicthBlack = colorPaletteMode == ColorPaletteMode.PitchBlack
            val isDark =
                colorPaletteMode == ColorPaletteMode.Dark || isPicthBlack || (colorPaletteMode == ColorPaletteMode.System && isSystemInDarkTheme())

            Column(modifier = Modifier.padding(bottom = bottomPadding)) {

                val view = LocalView.current
                (view.parent as? DialogWindowProvider)?.window?.let { window ->
                    SideEffect {
                        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                    }
                }


                content()
            }
        }
        //PREVIOUS SHEET
    }
}