package it.fast4x.rimusic.enums

enum class WallpaperType {
    Home,
    Lockscreen,
    Both;

    val displayName: String
    get() = when (this) {
        Home -> "Home"
        Lockscreen -> "Lockscreen"
        Both -> "Both"
    }
}