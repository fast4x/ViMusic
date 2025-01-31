package it.fast4x.invidious

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import it.fast4x.invidious.models.Instances
import it.fast4x.invidious.models.InvidiousResponse
import it.fast4x.invidious.utils.ProxyPreferences
import it.fast4x.invidious.utils.getProxy
import it.fast4x.invidious.utils.runCatchingCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.logging.HttpLoggingInterceptor


operator fun Url.div(path: String) = URLBuilder(this).apply { path(path) }.build()
operator fun JsonElement.div(key: String) = jsonObject[key]!!

object Invidious {
    @OptIn(ExperimentalSerializationApi::class)
    private val client by lazy {
        HttpClient(OkHttp) {
            //BrowserUserAgent()

            //expectSuccess = true

            install(ContentNegotiation) {
                val feature = Json {
                    ignoreUnknownKeys = true
                    //explicitNulls = false
                    //encodeDefaults = true
                    isLenient = true
                }

                json(feature)
                //json(feature, ContentType.Text.Html)
                //json(feature, ContentType.Text.Plain)
            }

            install(HttpRequestRetry) {
                exponentialDelay()
                maxRetries = 2
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 1000L
                requestTimeoutMillis = 5000L
            }

            /*
            install(ContentEncoding) {
                gzip()
                deflate()
            }
             */

            ProxyPreferences.preference?.let {
                engine {
                    proxy = getProxy(it)
                }
            }

            expectSuccess = true

            engine {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }

            defaultRequest {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }



        }
    }


    private val mutex = Mutex()

    private suspend fun request(
        instance: Instances = Instances.NADEKO,
        endpoint: String,
        block: HttpRequestBuilder.() -> Unit = { }
    ) = mutex.withLock {
        client.get(
            Url("${instance.apiUrl} / $endpoint"),
        ) {
            block()
        }
    }

    private suspend fun HttpResponse.isOk() =
        (body<JsonElement>() / "message").jsonPrimitive.content == "ok"

    private suspend fun HttpResponse.bodyAsText() = body<String>()

    val api = Api()

    @Serializable
    data class Message(
        val error: String? = null,
        val message: String? = null
    )

    class Api internal constructor() {

        suspend fun videos(videoId: String) = runCatchingCancellable {
            println("Invidious.api.videos request started")
            val url = "${Instances.YEWTU.apiUrl}videos/${videoId}"
            println("Invidious.api.videos url: $url")
            val response = client.get(url) {
                contentType(ContentType.Application.Json)
            }.body<InvidiousResponse>()
            println("Invidious.api.videos request finished $response")
            return@runCatchingCancellable response
        }?.onFailure {
            println("Invidious.api.videos request failed: $it")
        }

    }


}
