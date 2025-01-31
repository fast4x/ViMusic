package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MaxTopPlaylistItems
import it.fast4x.rimusic.enums.TopPlaylistPeriod
import it.fast4x.rimusic.utils.MaxTopPlaylistItemsKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.typography


@Composable
fun PeriodMenu (
    onDismiss: (TopPlaylistPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    var height by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val maxTopPlaylistItems by rememberPreference(
        MaxTopPlaylistItemsKey,
        MaxTopPlaylistItems.`10`
    )

    Menu(
        modifier = modifier
            .onPlaced { height = with(density) { it.size.height.toDp() } }

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(end = 12.dp)
        ) {
                BasicText(
                    text = stringResource(R.string.header_view_top_of).format(maxTopPlaylistItems),
                    style = typography().m.semiBold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )
        }

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        TopPlaylistPeriod.entries.forEach {
            MenuEntry(
                icon = R.drawable.time,
                text = when (it) {
                    TopPlaylistPeriod.PastDay -> stringResource(R.string.past_day)
                    TopPlaylistPeriod.PastWeek -> stringResource(R.string.past_week)
                    TopPlaylistPeriod.PastMonth -> stringResource(R.string.past_month)
                    TopPlaylistPeriod.PastYear -> stringResource(R.string.past_year)
                    TopPlaylistPeriod.AllTime -> stringResource(R.string.all_time)
                },
                onClick = {
                    onDismiss(it)

                }
            )
        }


    }
}