package it.fast4x.rimusic.extensions.contributors.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.annotations.SerializedName
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography

data class Translator(
    @SerializedName( "username" ) val username: String,
    @SerializedName( "displayName" ) val displayName: String?,
    @SerializedName( "languages" ) val languages: String,
    @SerializedName( "profileUrl" ) val profileUrl: String?,
    @SerializedName( "avatarUrl" ) val avatarUrl: String?
) {
    private val usernameByProfile: String
        get() = profileUrl?.split( "/" )?.last().toString()

    @Composable
    fun Draw() {
        val uriHandler = LocalUriHandler.current
        val avatarPainter = rememberAsyncImagePainter( this.avatarUrl )
        val backgroundColor = Color.Transparent


        Card(
            modifier = Modifier
                .padding(start = 12.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth(),
            colors = CardColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 15.dp)
                    .background(backgroundColor, RoundedCornerShape( 12.dp )),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = avatarPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip( RoundedCornerShape( 25.dp ) )
                        .border( 1.dp, Color.White, RoundedCornerShape( 25.dp ) ),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width( 16.dp ) )

                Column( Modifier.fillMaxWidth().padding(end = 10.dp) ) {
                    Text(
                        text = displayName ?: username,
                        style = TextStyle(
                            color = colorPalette().text,
                            fontSize = typography().xs.fontSize,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Start
                    )

                    Row( Modifier.fillMaxWidth() ) {
                        Text(
                            text = if (profileUrl != null) "@${usernameByProfile}" else "@${username}",
                            style = TextStyle(
                                color = colorPalette().textSecondary,
                                fontSize = typography().xs.fontSize,
                                fontStyle = FontStyle.Italic,
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable {
                                    if (profileUrl != null) {
                                        uriHandler.openUri(profileUrl)
                                    }
                                },
                        )

                        val color = colorPalette().favoritesIcon.copy( alpha = .8f )

                        Text(
                            text = languages,
                            style = TextStyle(
                                color = color,
                                fontSize = typography().xs.fontSize,
                            ),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight( 1f )
                        )

                        Spacer( Modifier.width(5.dp ) )

                        Icon(
                            painter = painterResource( R.drawable.translate ),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size( typography().xs.fontSize.value.dp )
                        )
                    }
                }
            }
        }
    }
}
