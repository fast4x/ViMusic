package it.fast4x.rimusic.ui.components.tab

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.utils.formatAsDuration

class ImportSongsFromCSV private constructor(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>
): Descriptive, MenuIcon {

    companion object {
        private fun openFile(
            uri: Uri,
            beforeTransaction: (Int, Map<String, String>) -> Unit = { _,_ -> },
            afterTransaction: ( Int, Song, Album, List<Artist> ) -> Unit = { _,_,_,_ -> }
        ) {
            appContext().applicationContext
                        .contentResolver
                        .openInputStream(uri)
                        ?.use { inputStream ->
                            csvReader().open(inputStream) {
                                readAllWithHeaderAsSequence().forEachIndexed { index, row: Map<String, String> ->
                                    println("mediaItem index song $index")

                                    Database.asyncTransaction {
                                        beforeTransaction( index, row )
                                        /**/
                                        val explicitPrefix = if (row["Explicit"] == "true") "e:" else ""
                                        val pseudoMediaId = (row["Track Name"]+row["Artist Name(s)"]).filter { it.isLetterOrDigit() }
                                        val title = row["Title"] ?: row["Track Name"] ?: return@asyncTransaction
                                        val mediaId = row["MediaId"] ?: pseudoMediaId
                                        val artistsText = row["Artists"] ?: row["Artist Name(s)"] ?: ""
                                        val durationText = row["Duration"] ?: formatAsDuration(row["Track Duration (ms)"]?.toLong() ?: 0L)

                                        val song = Song(
                                            id = mediaId,
                                            title = explicitPrefix+title,
                                            artistsText = artistsText,
                                            durationText = durationText,
                                            thumbnailUrl = row["ThumbnailUrl"] ?: "",
                                            totalPlayTimeMs = 1L
                                        )

                                         val albumId = row["AlbumId"] ?: ""
                                         val albumTitle = row["AlbumTitle"]
                                         val album = Album(
                                            id = albumId,
                                            title = albumTitle
                                         )

                                         val artistNames = row["Artists"]?.split(",")
                                         val artistIds = row["ArtistIds"]?.split(",")
                                         val artists = mutableListOf<Artist>()
                                         if (artistIds != null && (artistNames?.size == artistIds.size)) {
                                            for(idx in artistIds.indices){
                                                val artistName = artistNames.getOrNull(idx)
                                                val artistId = artistIds.getOrNull(idx)
                                                if(artistId!=null){
                                                    val artist = Artist(
                                                    id = artistId,
                                                    name = artistName
                                                    )
                                                    artists.add(artist)
                                                }
                                            }
                                         }

                                afterTransaction( index, song, album, artists )
                            }
                        }
                    }
                }
        }

        @JvmStatic
        @Composable
        fun init(
            beforeTransaction: (Int, Map<String, String>) -> Unit = { _,_ -> },
            afterTransaction: ( Int, Song, Album, List<Artist> ) -> Unit = { _,_,_,_ -> }
        ) = ImportSongsFromCSV(
            rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                if( uri == null ) return@rememberLauncherForActivityResult

                openFile( uri, beforeTransaction, afterTransaction )
            }
        )
    }

    override val messageId: Int = R.string.import_playlist
    override val iconId: Int = R.drawable.resource_import
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        try {
            launcher.launch( arrayOf("text/csv", "text/comma-separated-values") )
        } catch (_: ActivityNotFoundException) {
            SmartMessage(
                appContext().resources.getString( R.string.info_not_find_app_open_doc ),
                type = PopupType.Warning, context = appContext()
            )
        }
    }
}