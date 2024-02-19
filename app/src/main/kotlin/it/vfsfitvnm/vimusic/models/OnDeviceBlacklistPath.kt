package it.vfsfitvnm.vimusic.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("OnDeviceBlacklist")
data class OnDeviceBlacklistPath(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val path: String
) {
    fun test(relativePath: String): Boolean {
        return this.path.startsWith(relativePath)
    }
}