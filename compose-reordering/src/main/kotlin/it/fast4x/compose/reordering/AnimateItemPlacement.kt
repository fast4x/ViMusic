package it.fast4x.compose.reordering

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.ui.Modifier

context(LazyItemScope)
@ExperimentalFoundationApi
fun Modifier.localAnimateItemPlacement(reorderingState: ReorderingState) =
    if (reorderingState.draggingIndex == -1) Modifier.animateItem() else this
