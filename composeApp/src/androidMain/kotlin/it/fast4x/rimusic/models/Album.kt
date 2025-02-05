package it.fast4x.rimusic.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity
data class Album(
    @PrimaryKey val id: String,
    val title: String? = null,
    val thumbnailUrl: String? = null,
    val year: String? = null,
    val authorsText: String? = null,
    val shareUrl: String? = null,
    val timestamp: Long? = null,
    val bookmarkedAt: Long? = null,
    val isYoutubeAlbum: Boolean = false,
) {
    fun toggleBookmark(): Album {
        return copy(
            bookmarkedAt = if (bookmarkedAt == null) System.currentTimeMillis() else null
        )
    }
}
