package it.fast4x.rimusic.ui.components.themed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.thumbnailRoundnessKey
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.typography

class Search private constructor(
    private val inputState: MutableState<String>,
    private val visibleState: MutableState<Boolean>,
    private val focusState: MutableState<Boolean>,
): MenuIcon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init() = Search(
            rememberSaveable { mutableStateOf( "" ) },
            rememberSaveable { mutableStateOf( false ) },
            rememberSaveable { mutableStateOf( false ) }
        )
    }

    override val iconId: Int = R.drawable.search_circle
    override val messageId: Int = R.string.search
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    var isVisible: Boolean = visibleState.value
        set(value) {
            visibleState.value = value
            field = value
        }
    var isFocused: Boolean = focusState.value
        set(value) {
            focusState.value = value
            field = value
        }
    var input: String = inputState.value
        set(value) {
            inputState.value = value
            field = value
        }

    @Composable
    private fun ColumnScope.DecorationBox(
        innerTextField: @Composable () -> Unit,
        onBackClick: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding( horizontal = 10.dp )
        ) {
            IconButton(
                onClick = {},
                icon = R.drawable.search,
                color = colorPalette().favoritesIcon,
                modifier = Modifier.align( Alignment.CenterStart )
                    .size(16.dp)
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(1f)
                .padding(horizontal = 30.dp)
        ) {
            // Search hint
            androidx.compose.animation.AnimatedVisibility(
                visible = input.isBlank(),
                enter = fadeIn(tween(100)),
                exit = fadeOut(tween(100)),
            ) {
                BasicText(
                    text = stringResource(R.string.search),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typography().xs
                        .semiBold
                        .secondary
                        .copy(
                            color = colorPalette().textDisabled
                        )
                )
            }

            // Actual text from user
            innerTextField()
        }
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.padding( start = 30.dp, end = 15.dp )
        ) {
            IconButton(
                onClick = onBackClick,
                icon = R.drawable.backspace_outline,
                color = colorPalette().text.copy( alpha = .8f ), // A little dimmer to prevent eye-candy
                modifier = Modifier.align( Alignment.CenterEnd )
                                   .size( 16.dp )
            )
        }
    }

    fun onItemSelected() {
        if ( isVisible )
            if ( input.isBlank() )
                isVisible = false
            else
                isFocused = false
    }

    @Composable
    fun SearchBar( columnScope: ColumnScope ) {
        val thumbnailRoundness by rememberPreference(
            thumbnailRoundnessKey,
            ThumbnailRoundness.Heavy
        )

        val focusRequester = remember { FocusRequester() }

        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier.padding(all = 10.dp)
                .fillMaxWidth()
        ) {
            // Auto focus on search bar when it's visible
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect( isVisible, isFocused ) {
                if( !isVisible ) return@LaunchedEffect

                if( isFocused )
                    focusRequester.requestFocus()
                else {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }

            /**
             * [TextFieldValue] gives control over cursor.
             *
             * This prevents the cursor from being placed
             * at the beginning of search term.
             */
            var searchTerm by remember { mutableStateOf(
                TextFieldValue( input, TextRange( input.length ) )
            )}
            BasicTextField(
                value = searchTerm,
                onValueChange = {
                    searchTerm = it.copy(
                        text = it.text,
                        selection = it.selection
                    )
                    input = it.text
                },
                textStyle = typography().xs.semiBold,
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done ),
                keyboardActions = KeyboardActions(onDone = {
                    isVisible = input.isNotBlank()
                    isFocused = false
                    keyboardController?.hide()
                }),
                cursorBrush = SolidColor(colorPalette().text),
                decorationBox = {
                    columnScope.DecorationBox( it ) {
                        searchTerm = TextFieldValue( "" )
                        input = ""

                        // Regain focus in case keyboard is hidden
                        isFocused = true
                    }
                },
                modifier = Modifier.height( 30.dp )
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .background(
                        colorPalette().background4,
                        thumbnailRoundness.shape()
                    )
            )
        }
    }

    override fun onShortClick() {
        isVisible = !isVisible
        isFocused = isVisible
    }
}