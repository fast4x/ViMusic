package it.fast4x.rimusic.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.enums.MaxSongs
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
fun PlayShuffledSongs(songsList: List<Song>?, context: Context, binder: PlayerServiceModern.Binder?) {
    if (songsList == null || binder == null) return
    playShuffledSongs( songsList.map( Song::asMediaItem ), context, binder )
}

@UnstableApi
fun playShuffledSongs( mediaItems: List<MediaItem>, context: Context, binder: PlayerServiceModern.Binder? ) {

    if ( binder == null ) return

    // Send message saying that there's no song to play
    if( mediaItems.isEmpty() ) {
        // TODO: add this string to R.string
        SmartMessage(
            message = "There's no song to play",
            context = context
        )
        return
    }

    val maxSongsInQueue = context.preferences
        .getEnum( maxSongsInQueueKey, MaxSongs.`500` )
        .number
        .toInt()

    mediaItems.let { songs ->

        // Return whole list if its size is less than queue size
        val songsInQueue = songs.shuffled().take( maxSongsInQueue )
        CoroutineScope( Dispatchers.Main ).launch {
            binder.stopRadio()
            binder.player.forcePlayFromBeginning( songsInQueue )
        }
    }
}