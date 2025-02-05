package it.fast4x.rimusic

import coil3.Uri
import coil3.toUri

const val PINNED_PREFIX = "pinned:"
const val MODIFIED_PREFIX = "modified:"
const val MONTHLY_PREFIX = "monthly:"
const val PIPED_PREFIX = "piped:"
const val EXPLICIT_PREFIX = "e:"
const val LOCAL_KEY_PREFIX = "local:"
const val YTP_PREFIX = "account:"
const val YTEDITABLEPLAYLIST_PREFIX = "editable:"

fun cleanPrefix(text: String): String {
    val cleanText = text.replace(PINNED_PREFIX, "", true)
        .replace(MONTHLY_PREFIX, "", true)
        .replace(PIPED_PREFIX, "", true)
        .replace(YTEDITABLEPLAYLIST_PREFIX, "", true)
        .replace(EXPLICIT_PREFIX, "", true)
        .replace(MODIFIED_PREFIX, "", true)
        .replace(YTP_PREFIX, "", true)


    return cleanText
}

fun cleanString(text: String): String {
    var cleanText = text.replace("/", "", true)
    cleanText = cleanText.replace("#", "", true)
    return cleanText
}

fun String?.thumbnail(size: Int): String? {
    return when {
        this?.startsWith("https://lh3.googleusercontent.com") == true -> "$this-w$size-h$size"
        this?.startsWith("https://yt3.ggpht.com") == true -> "$this-w$size-h$size-s$size"
        else -> this
    }
}
fun String?.thumbnail(): String? {
    return this
}
fun Uri?.thumbnail(size: Int): Uri? {
    return toString().thumbnail(size)?.toUri()
}