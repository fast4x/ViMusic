package it.fast4x.rimusic.ui.components.themed

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon

class ItemSelector private constructor(
    private val menuState: MenuState,
    private val activeState: MutableState<Boolean>
): MenuIcon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init() = ItemSelector(
            LocalMenuState.current,
            // Should be reset after changing tab
            remember { mutableStateOf( false ) }
        )
    }

    var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }
    override val iconId: Int = R.drawable.checked
    override val messageId: Int = R.string.item_select
    override val menuIconTitle: String
        @Composable
        get() = "${stringResource(R.string.item_select)}/${stringResource(R.string.item_deselect)}"

    override fun onShortClick() {
        isActive = !isActive
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun PlayNext(
    onClick: () -> Unit
): MenuIcon = object: MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.play_skip_forward
    override val messageId: Int = R.string.play_next
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun Enqueue(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.enqueue
    override val messageId: Int = R.string.enqueue
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun LikeSongs(
    mediaItems: () -> List<MediaItem>
): MenuIcon = object: MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.heart
    override val messageId: Int = R.string.add_to_favorites
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        Database.asyncTransaction {
            mediaItems().forEach {
                like( it.mediaId, System.currentTimeMillis() )
            }

            runBlocking( Dispatchers.Main ) {
                SmartMessage(
                    message = appContext().resources.getString( R.string.done ),
                    context = appContext()
                )
            }
        }
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun Synchronize(
    onClick: () -> Unit
): MenuIcon = object: MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.sync
    override val messageId: Int = R.string.sync
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
        SmartMessage(
            appContext().resources.getString( R.string.done ),
            context = appContext()
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ListenOnYouTube(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.play
    override val messageId: Int = R.string.listen_on_youtube
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ThumbnailPicker(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.image
    override val messageId: Int = R.string.edit_thumbnail
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ResetThumbnail(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.image
    override val messageId: Int = R.string.reset_thumbnail
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}