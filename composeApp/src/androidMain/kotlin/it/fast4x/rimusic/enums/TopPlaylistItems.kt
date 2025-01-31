package it.fast4x.rimusic.enums

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import it.fast4x.rimusic.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

enum class MaxTopPlaylistItems {
    `10`,
    `20`,
    `30`,
    `40`,
    `50`,
    `70`,
    `90`,
    `100`,
    `150`,
    `200`;

    val number: Long
        get() = when (this) {
            `10` -> 10
            `20` -> 20
            `30` -> 30
            `40` -> 40
            `50` -> 50
            `70` -> 70
            `90` -> 90
            `100` -> 100
            `150` -> 150
            `200` -> 200
        }

}

enum class TopPlaylistPeriod(
    @DrawableRes val iconId: Int
): Drawable {
    PastDay( R.drawable.stat_today ),
    PastWeek( R.drawable.stat_week ),
    PastMonth( R.drawable.stat_month),
    PastYear( R.drawable.stat_year ),
    AllTime( R.drawable.stat );

    val duration: Duration
        get() = when (this) {
            PastDay -> 1.days
            PastWeek -> 7.days
            PastMonth -> 30.days
            PastYear -> 365.days
            AllTime -> Duration.INFINITE
        }

    override val icon: Painter
        @Composable
        get() = painterResource( this.iconId )
}