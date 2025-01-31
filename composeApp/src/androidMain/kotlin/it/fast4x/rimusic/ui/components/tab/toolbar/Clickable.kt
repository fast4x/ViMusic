package it.fast4x.rimusic.ui.components.tab.toolbar

interface Clickable {

    /**
     * What happens when user taps on icon.
     */
    fun onShortClick()

    /**
     * What happens when user holds this icon for a while.
     */
    fun onLongClick()
}