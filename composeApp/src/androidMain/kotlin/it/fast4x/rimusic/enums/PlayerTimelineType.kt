package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

enum class PlayerTimelineType {
    Default,
    Wavy,
    PinBar,
    BodiedBar,
    FakeAudioBar,
    ThinBar,
    ColoredBar;


    val textName: String
        @Composable
        get() = when (this) {
            Default -> stringResource(R.string._default)
            Wavy -> stringResource(R.string.wavy_timeline)
            BodiedBar -> stringResource(R.string.bodied_bar)
            PinBar -> stringResource(R.string.pin_bar)
            FakeAudioBar -> stringResource(R.string.fake_audio_bar)
            ThinBar -> stringResource(R.string.thin_bar)
            ColoredBar -> stringResource(R.string.colored_bar)
        }

}