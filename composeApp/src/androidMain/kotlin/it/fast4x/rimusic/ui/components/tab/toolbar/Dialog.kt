package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable

interface Dialog {

    var isActive: Boolean
    @get:Composable
    val dialogTitle: String

    @Composable
    fun Render()

    /**
     * What happens when user taps on icon.
     *
     * By default, this action enables dialog
     * by assigning `true` to [isActive]
     */
    fun onShortClick() { isActive = true }
}