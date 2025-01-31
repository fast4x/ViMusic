package it.fast4x.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class GridRenderer(
    val items: List<Item>?,
    val header: Header?,
    val continuations: List<Continuation>?,
) {
    @Serializable
    data class Item(
        val musicTwoRowItemRenderer: MusicTwoRowItemRenderer?
    )

    @Serializable
    data class Header(
        val gridHeaderRenderer: GridHeaderRenderer?
    )

    @Serializable
    data class GridHeaderRenderer(
        val title: Runs?
    )
}
