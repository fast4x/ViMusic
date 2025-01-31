package it.fast4x.rimusic.ui.styling

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.ColorPaletteName


@Immutable
data class ColorPalette(
    val background0: Color,
    val background1: Color,
    val background2: Color,
    val background3: Color,
    val background4: Color,
    val accent: Color,
    val onAccent: Color,
    val red: Color = Color(0xffbf4040),
    val blue: Color = Color(0xff4472cf),
    val text: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val isDark: Boolean,
    val iconButtonPlayer: Color,
) {
    companion object : Saver<ColorPalette, List<Any>> {
        override fun restore(value: List<Any>) = when (val accent = value[0] as Int) {
            0 -> DefaultDarkColorPalette
            1 -> DefaultLightColorPalette
            2 -> PureBlackColorPalette
            3 -> ModernBlackColorPalette
            else -> dynamicColorPaletteOf(
                FloatArray(3).apply { ColorUtils.colorToHSL(accent, this) },
                value[1] as Boolean
            )
        }

        override fun SaverScope.save(value: ColorPalette) =
            listOf(
                when {
                    value === DefaultDarkColorPalette -> 0
                    value === DefaultLightColorPalette -> 1
                    value === PureBlackColorPalette -> 2
                    value === ModernBlackColorPalette -> 3
                    else -> value.accent.toArgb()
                },
                value.isDark
            )
    }
}



val DefaultDarkColorPalette = ColorPalette(
    background0 = Color(0xff16171d),
    background1 = Color(0xff1f2029),
    background2 = Color(0xff2b2d3b),
    background3 = Color(0xff495057),
    background4 = Color(0xff333333),
    text = Color(0xffe1e1e2),
    textSecondary = Color(0xffa3a4a6),
    textDisabled = Color(0xff6f6f73),
    iconButtonPlayer = Color(0xffe1e1e2),
    accent = Color(0xFF2b9348),
    onAccent = Color.White,
    isDark = true
)

val DefaultLightColorPalette = ColorPalette(
    background0 = Color(0xfffdfdfe),
    background1 = Color(0xfff8f8fc),
    background2 = Color(0xffeaeaf5),
    background3 = Color(0xffeaeafd),
    background4 = Color(0xffeaeafd),
    text = Color(0xff212121),
    textSecondary = Color(0xff656566),
    textDisabled = Color(0xff9d9d9d),
    iconButtonPlayer = Color(0xff212121),
    accent = Color(0xFF2b9348),
    onAccent = Color.White,
    isDark = false
)

val PureBlackColorPalette = DefaultDarkColorPalette.copy(
    background0 = Color.Black,
    background1 = Color.Black,
    background2 = Color.Black,
    accent = Color.White,
    onAccent = Color.DarkGray
    )

val ModernBlackColorPalette = DefaultDarkColorPalette.copy(
    background0 = Color.Black,
    background1 = Color.Black,
    //background2 = DefaultDarkColorPalette.background2, // Color.Black,
    background2 = Color.Black,
    background3 = DefaultDarkColorPalette.accent
)

fun colorPaletteOf(
    colorPaletteName: ColorPaletteName,
    colorPaletteMode: ColorPaletteMode,
    isSystemInDarkMode: Boolean
): ColorPalette {
    return when (colorPaletteName) {
        ColorPaletteName.Default, ColorPaletteName.Dynamic,
        ColorPaletteName.MaterialYou, ColorPaletteName.Customized, ColorPaletteName.CustomColor -> when (colorPaletteMode) {
            ColorPaletteMode.Light -> DefaultLightColorPalette
            ColorPaletteMode.Dark, ColorPaletteMode.PitchBlack -> DefaultDarkColorPalette
            ColorPaletteMode.System -> when (isSystemInDarkMode) {
                true -> DefaultDarkColorPalette
                false -> DefaultLightColorPalette
            }
        }
        ColorPaletteName.PureBlack -> PureBlackColorPalette
        ColorPaletteName.ModernBlack -> ModernBlackColorPalette
    }
}

fun dynamicColorPaletteOf(bitmap: Bitmap, isDark: Boolean): ColorPalette? {
    val palette = Palette
        .from(bitmap)
        .maximumColorCount(8)
        //.addFilter(if (isDark) ({ _, hsl -> hsl[0] !in 36f..100f }) else null)
        .generate()



    val hsl = if (isDark) {
        palette.dominantSwatch ?: Palette
            .from(bitmap)
            .maximumColorCount(8)
            .generate()
            .dominantSwatch
    } else {
        palette.dominantSwatch
    }?.hsl ?: return null

    return if (hsl[1] < 0.08) {
        val newHsl = palette.swatches
            .map(Palette.Swatch::getHsl)
            .sortedByDescending(FloatArray::component2)
            .find { it[1] != 0f }
            ?: hsl
        dynamicColorPaletteOf(newHsl, isDark)

    } else {
        dynamicColorPaletteOf(hsl, isDark)
    }
}

fun dynamicColorPaletteOf(hsl: FloatArray, isDark: Boolean): ColorPalette {
    return colorPaletteOf(ColorPaletteName.Dynamic, if (isDark) ColorPaletteMode.Dark else ColorPaletteMode.Light, false).copy(

        background0 = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.1f), if (isDark) 0.10f else 0.925f),
        background1 = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.3f), if (isDark) 0.15f else 0.90f),
        background2 = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.4f), if (isDark) 0.2f else 0.85f),

        accent = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.5f), 0.5f),

        text = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.02f), if (isDark) 0.88f else 0.12f),
        textSecondary = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.1f), if (isDark) 0.65f else 0.40f),
        textDisabled = Color.hsl(hsl[0], hsl[1].coerceAtMost(0.2f), if (isDark) 0.40f else 0.65f),

    )
}


fun dynamicColorPaletteOf(hsl: Hsl, isDark: Boolean) = hsl.let { (hue, saturation) ->
    val accentColor = Color.hsl(
        hue = hue,
        saturation = saturation.coerceAtMost(if (isDark) 0.4f else 0.5f),
        lightness = 0.5f
    )

    colorPaletteOf(
        ColorPaletteName.Dynamic,
        if (isDark) ColorPaletteMode.Dark else ColorPaletteMode.Light,
        isDark
    ).copy(
        background0 = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.1f),
            lightness = if (isDark) 0.10f else 0.925f
        ),
        background1 = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.3f),
            lightness = if (isDark) 0.15f else 0.90f
        ),
        background2 = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.4f),
            lightness = if (isDark) 0.2f else 0.85f
        ),
        accent = accentColor,
        text = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.02f),
            lightness = if (isDark) 0.88f else 0.12f
        ),
        textSecondary = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.1f),
            lightness = if (isDark) 0.65f else 0.40f
        ),
        textDisabled = Color.hsl(
            hue = hue,
            saturation = saturation.coerceAtMost(0.2f),
            lightness = if (isDark) 0.40f else 0.65f
        )
    )
}

fun dynamicColorPaletteOf(
    accentColor: Color,
    isDark: Boolean
) = dynamicColorPaletteOf(
    hsl = accentColor.hsl,
    isDark = isDark
)

inline val ColorPalette.collapsedPlayerProgressBar: Color
    get() = if (this === DefaultDarkColorPalette || this === DefaultLightColorPalette || this === PureBlackColorPalette) {
        text
    } else {
        accent
    }



inline val ColorPalette.favoritesIcon: Color
    get() = if (this === DefaultDarkColorPalette || this === DefaultLightColorPalette || this === PureBlackColorPalette) {
        red
    } else {
        accent
    }

inline val ColorPalette.shimmer: Color
    get() = if (this === DefaultDarkColorPalette || this === DefaultLightColorPalette || this === PureBlackColorPalette) {
        Color(0xff838383)
    } else {
        accent
    }

inline val ColorPalette.primaryButton: Color
    get() = if (this === PureBlackColorPalette || this === ModernBlackColorPalette) {
        Color(0xFF272727)
    } else {
        background2
    }


inline val ColorPalette.favoritesOverlay: Color
    get() = if (this === DefaultDarkColorPalette || this === DefaultLightColorPalette || this === PureBlackColorPalette) {
        red.copy(alpha = 0.4f)
    } else {
        accent.copy(alpha = 0.4f)
    }

inline val ColorPalette.overlay: Color
    get() = PureBlackColorPalette.background0.copy(alpha = 0.5f)

inline val ColorPalette.onOverlay: Color
    get() = PureBlackColorPalette.text

inline val ColorPalette.onOverlayShimmer: Color
    get() = PureBlackColorPalette.shimmer

inline val ColorPalette.applyPitchBlack: ColorPalette
    get() = this.copy(
        isDark = true,
        background0 = Color.Black,
        background1 = Color.Black,
        background2 = Color.Black,
        background3 = Color.Black,
        background4 = Color.Black,
        text = Color.White,
    )