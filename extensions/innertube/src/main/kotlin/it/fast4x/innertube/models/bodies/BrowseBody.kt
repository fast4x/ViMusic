package it.fast4x.innertube.models.bodies

import it.fast4x.innertube.models.Context
import it.fast4x.innertube.models.WatchEndpoint
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context = Context.DefaultWeb,
    val browseId: String?,
    val params: String? = null,
)
