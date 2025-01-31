package it.fast4x.rimusic.ui.components.navigation.header

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.ui.components.themed.Button
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography

private fun appIconClickAction(
    navController: NavController,
    countToReveal: MutableIntState,
    context: Context
) {
    countToReveal.intValue++

    val message: String =
        when( countToReveal.intValue ) {
            10 -> {
                countToReveal.intValue = 0
                navController.navigate( NavRoutes.gamePacman.name )
                ""
            }
            3 -> "Do you like clicking? Then continue..."
            6 -> "Okay, you’re looking for something, keep..."
            9 -> "You are a number one, click and enjoy the surprise"
            else -> ""
        }
    if( message.isNotEmpty() )
        SmartMessage(
            message = message,
            durationLong = true,
            context = context
        )
}

private fun appIconLongClickAction(
    navController: NavController,
    context: Context
) {
    SmartMessage(
        "You are a number one, click and enjoy the surprise",
        durationLong = true,
        context = context
    )
    navController.navigate( NavRoutes.gameSnake.name )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppLogo(
    navController: NavController,
    context: Context
) {
    val countToReveal = remember { mutableIntStateOf(0) }
    val modifier = Modifier.combinedClickable(
        onClick = { appIconClickAction( navController, countToReveal, context ) },
        onLongClick = { appIconLongClickAction( navController, context ) }
    )

    Button(
        iconId = R.drawable.app_icon,
        color = colorPalette().favoritesIcon,
        padding = 0.dp,
        size = 36.dp,
        modifier = modifier
    ).Draw()
}

@Composable
private fun AppLogoText( navController: NavController ) {
    val iconTextClick: () -> Unit = {
        if ( NavRoutes.home.isNotHere( navController ) )
            navController.navigate(NavRoutes.home.name)
    }



    Button(
        iconId = R.drawable.app_logo_text,
        color = AppBar.contentColor(),
        padding = 0.dp,
        size = 36.dp,
        forceWidth = 100.dp,
        modifier = Modifier.clickable { iconTextClick() }
    ).Draw()
}

// START
@Composable
fun AppTitle(
    navController: NavController,
    context: Context
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy( 5.dp ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLogo( navController, context )
        AppLogoText( navController )

        if(Preference.parentalControl())
            Button(
                iconId = R.drawable.shield_checkmark,
                color = AppBar.contentColor(),
                padding = 0.dp,
                size = 20.dp
            ).Draw()

        if (Preference.debugLog())
            BasicText(
                text = stringResource(R.string.info_debug_mode_enabled),
                style = TextStyle(
                    fontSize = typography().xxs.semiBold.fontSize,
                    fontWeight = typography().xxs.semiBold.fontWeight,
                    fontFamily = typography().xxs.semiBold.fontFamily,
                    color = colorPalette().red
                ),
                modifier = Modifier
                    .clickable {
                        SmartMessage(context.resources.getString(R.string.info_debug_mode_is_enabled), durationLong = true, context = context)
                        navController.navigate(NavRoutes.settings.name)
                    }
            )
    }
// END
}