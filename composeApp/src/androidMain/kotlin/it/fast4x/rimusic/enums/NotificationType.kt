package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

enum class NotificationType {
    Default,
    Advanced;

    val textName: String
        @Composable
        get() = when (this) {
            Default -> stringResource(R.string.notification_type_default)
            Advanced -> stringResource(R.string.notification_type_advanced)

        }
}