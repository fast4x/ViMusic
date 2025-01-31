package it.fast4x.rimusic.ui.components.themed

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import it.fast4x.rimusic.R

@UnstableApi
@Composable
fun DownloadStateIconButton(
    onClick: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    @DrawableRes icon: Int,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indication: Indication? = null,
    downloadState: Int
) {

    if (downloadState == Download.STATE_DOWNLOADING
                || downloadState == Download.STATE_QUEUED
                || downloadState == Download.STATE_RESTARTING
                ){
        Image(
            painter = painterResource(R.drawable.download_progress),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier
                .clickable(
                    indication = indication ?: ripple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled,
                    onClick = onCancelButtonClicked
                )
                .then(modifier)
        )
        /*
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            color = colorPalette().text,
            modifier = Modifier
                .size(16.dp)

        )

         */
    } else {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier
                .clickable(
                    indication = indication ?: ripple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled,
                    onClick = onClick
                )
                .then(modifier)
        )
    }
}