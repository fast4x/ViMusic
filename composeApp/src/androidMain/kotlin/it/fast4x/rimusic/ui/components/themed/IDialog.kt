package it.fast4x.rimusic.ui.components.themed

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.ui.components.tab.toolbar.Dialog

interface IDialog: Dialog {

    var value: String
    val placeholder: String
        @Composable
        get() = ""

    /**
     * What happens when user hits "Confirm" button
     */
    fun onSet( newValue: String )

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

        InputTextDialog(
            onDismiss = ::onDismiss,
            title = dialogTitle,
            value = value,
            placeholder = placeholder,
            setValue = ::onSet
        )
    }
}