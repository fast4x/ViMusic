package it.fast4x.rimusic.utils

import io.ktor.http.Url
import it.fast4x.rimusic.models.PipedSession
import it.fast4x.rimusic.appContext
import timber.log.Timber

/*
@Composable
fun getPipedSession(): PipedSession {
    val pipedUsername by rememberEncryptedPreference(pipedUsernameKey, "")
    val pipedInstanceName by rememberEncryptedPreference(pipedInstanceNameKey, "")
    val pipedApiBaseUrl by rememberEncryptedPreference(pipedApiBaseUrlKey, "")
    val pipedApiToken by rememberEncryptedPreference(pipedApiTokenKey, "")
    return PipedSession(
        instanceName = pipedInstanceName,
        apiBaseUrl = Url(pipedApiBaseUrl),
        token = pipedApiToken,
        username = pipedUsername
    )
}
*/


fun getPipedSession(): PipedSession {

    val context = appContext()
    val pipedSession = PipedSession(
        instanceName = "",
        apiBaseUrl = Url(""),
        token = "",
        username = ""
    )
    if (context.preferences.getBoolean(isPipedEnabledKey, false) && isAtLeastAndroid7) {
        runCatching {
            with(context.encryptedPreferences) {
                pipedSession.username = getString(pipedUsernameKey, "").toString()
                pipedSession.instanceName = getString(pipedInstanceNameKey, "").toString()
                pipedSession.apiBaseUrl = Url(getString(pipedApiBaseUrlKey, "").toString())
                pipedSession.token = getString(pipedApiTokenKey, "").toString()
            }
        }.onFailure {
            Timber.e("GetPipedSession get encryptedPreferences error ${it.stackTraceToString()}")
        }
    }

    return pipedSession

}