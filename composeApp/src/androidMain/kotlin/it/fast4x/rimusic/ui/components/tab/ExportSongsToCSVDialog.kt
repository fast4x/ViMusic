package it.fast4x.rimusic.ui.components.tab

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.ui.components.themed.IDialog
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportSongsToCSVDialog private constructor(
    private val valueState: MutableState<String>,
    private val activeState: MutableState<Boolean>,
    private val launcher: ManagedActivityResultLauncher<String, Uri?>
): IDialog, Descriptive, MenuIcon {
    
    companion object {
        @UnstableApi
        private fun writeToFile(
            uri: Uri,
            browseId: () -> String = { "" },
            playlistName: () -> String,
            songs: () -> List<MediaItem>
        ) {
            appContext().applicationContext
                        .contentResolver
                        .openOutputStream( uri )
                        ?.use { outputStream ->
                            csvWriter().open(outputStream) {
                                writeRow(       // Write down needed sections
                                    "PlaylistBrowseId",
                                    "PlaylistName",
                                    "MediaId",
                                    "Title",
                                    "Artists",
                                    "Duration",
                                    "ThumbnailUrl"
                                )
                                songs().forEach { song ->
                                    val duration = song.mediaMetadata.durationMs?.div( 1000f )

                                    writeRow(
                                        browseId(),
                                        playlistName(),
                                        song.mediaId,
                                        song.mediaMetadata.title,
                                        song.mediaMetadata.artist,
                                        duration?.let { "%.0f".format(it) } ?: "",
                                        song.mediaMetadata.artworkUri
                                    )
                                }
                            }
                        }
        }

        @UnstableApi
        @JvmStatic
        @Composable
        fun init(
            valueState: MutableState<String>,
            songs: () -> List<MediaItem>
        ) = ExportSongsToCSVDialog(
            valueState,
            rememberSaveable { mutableStateOf(false) },
            rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("text/csv")
            ) { uri ->
                writeToFile(
                    uri ?: return@rememberLauncherForActivityResult,
                    { "" },
                    { valueState.value },
                    songs
                )
            }
        )
    }

    override val messageId: Int = R.string.export_playlist
    override val iconId: Int = R.drawable.export
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.enter_the_playlist_name )
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override var value: String = valueState.value
        set(value) {
            valueState.value = value
            field = value
        }
    override var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }

    override fun onShortClick() = super.onShortClick()

    override fun onSet( newValue: String ) {
        var fileName = newValue

        // If user didn't indicate a name, apply date as replacement
        if( newValue.isBlank() ) {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault() )
            fileName = "RMPlaylist_${dateFormat.format( Date() )}"
        }

        // Add extension
        fileName += ".csv"

        try {
            launcher.launch( fileName )
        } catch ( _: ActivityNotFoundException) {
            SmartMessage(
                "Couldn't find an application to create documents",
                type = PopupType.Warning,
                context = appContext()
            )
        } finally { onDismiss() }
    }
}