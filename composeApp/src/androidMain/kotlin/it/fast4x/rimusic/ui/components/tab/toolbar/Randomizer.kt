package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.navigation.header.TabToolBar
import kotlin.random.Random

interface Randomizer<T>: Button {

    companion object {
        /**
         * To ensure true randomness, Random must not
         * be a constant variable
         */
        fun nextInt( until: Int ) =
            Random( System.currentTimeMillis() ).nextInt( until )
    }

    fun getItems(): List<T>

    fun onClick( index: Int )

    @Composable
    override fun ToolBarButton() {
        TabToolBar.Icon(
            iconId = R.drawable.dice,
            enabled = getItems().isNotEmpty()
        ) {
            onClick( nextInt( getItems().size ) )
        }
    }
}