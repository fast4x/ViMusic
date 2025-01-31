package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog

interface ConfirmDialog: Dialog {


    /**
     * What happens when user hits "Confirm" button.
     */
    fun onConfirm()

    /**
     * Triggered when user interacts with back button
     * or with something outside of this menu's scope.
     *
     * By default, this will turn off the dialog
     */
    fun onDismiss() { isActive = false }

    @Composable
    override fun Render() {
        if( !isActive ) return

        ConfirmationDialog(
            text = dialogTitle,
            onDismiss = ::onDismiss,
            onConfirm = ::onConfirm
        )
    }
}