package me.knighthat.common

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.compression.brotli
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import it.fast4x.innertube.utils.ProxyPreferences
import it.fast4x.innertube.utils.getProxy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

internal object HttpFetcher {

    /**
     * Latest user agent string from [UserAgents.Me](https://useragents.me)
     *
     * TODO: Add a Github Action that can update this automatically
     */
    private const val LATEST_CHROME_ANDROID_UA =
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Mobile Safari/537.3"

    /**
     * **Connect timeout** is the maximum time (in milliseconds)
     * to wait for the connection to be established between client
     * and server before giving up
     */
    internal const val CONNECT_TIMEOUT = 2000L

    /**
     * **Socket timeout** is the maximum time (in milliseconds)
     * the client will wait for the server to response
     * before giving up and throwing [java.net.SocketTimeoutException]
     */
    internal const val SOCKET_TIMEOUT = 2000L

    /**
     * **Request timeout** is the maximum time (in milliseconds)
     * the client is allowed to wait. Meaning, at any stage of
     * the connection, if the wait time exceeds this number,
     * the client will give up.
     *
     * This number should be larger than [CONNECT_TIMEOUT] or [SOCKET_TIMEOUT]
     * to prevent the client from closing the connection.
     * 2/3 of total time is sufficient for current use.
     */
    internal const val REQUEST_TIMEOUT = 2 * (CONNECT_TIMEOUT + SOCKET_TIMEOUT) / 3

    // START - Regex's
    private val CAPTURE_DOMAIN_REGEX = Regex( "^(?:https?://)?([^/]+)(?:/.*)?\$" )
    private val TLD_REGEX = Regex("([a-zA-Z0-9-]+\\.[a-zA-Z]{2,})$")
    // END - Regex's

    val CLIENT = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT
            connectTimeoutMillis = CONNECT_TIMEOUT
            socketTimeoutMillis = SOCKET_TIMEOUT
        }
        install(ContentNegotiation) {
            @OptIn(ExperimentalSerializationApi::class)
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
            })
        }
        install(ContentEncoding) {
            brotli(1.0F)
            gzip(0.9F)
            deflate(0.8F)
        }
        ProxyPreferences.preference?.let {
            engine {
                proxy = getProxy(it)
            }
        }
        defaultRequest {
            headers.append( HttpHeaders.Accept, "*/*" )
            headers.append( HttpHeaders.UserAgent, LATEST_CHROME_ANDROID_UA )
        }
    }

    /**
     * Extract domain name from a full length URL.
     *
     * For example, url **https://sub1.example.org/path/to/file** will
     * return **sub1.example.org**
     *
     * If the provided is not a url, it will return the url
     *
     * @param url to extract domain name from
     */
    internal fun getDomainName( url: String ) =
        CAPTURE_DOMAIN_REGEX.find(url)?.groups?.get(1)?.value ?: url

    /**
     * Extract top-level domain name from a URL.
     *
     * For example, **sub1.example.org** will become **example.org**
     *
     * If the provided is not a url, it will return the url
     *
     * @param url to extract top-level domain name
     */
    internal fun getTld( url: String ): String {
        val domain = getDomainName( url )
        return TLD_REGEX.find( domain )?.value ?: domain
    }

    /**
     * This pattern is design to match all sub domains (whether it's there or not) of a domain.
     *
     * I.E. **_(?:[a-zA-Z0-9-]+\.)*example.org_** matches:
     * - example.org
     * - sub1.example.org
     * - extra.sub1.example.org
     */
    internal fun genMatchAllTld( url: String ) =
        Regex( "(?:[a-zA-Z0-9-]+\\.)*${getTld(url)}" )

    /**
     * Attempt to send a request to provided server
     *
     * @param method purpose of the request. For example, GET/POST/UPDATE. Visit [HttpMethod] for details
     * @param host domain name or IP address of the server
     * @param endpoint path to send request to on the server
     * @param protocol or scheme such as HTTP(S). To learn more, visit [URLProtocol]
     * @param port to connect. Defaults to port of [protocol]
     * @param body additional information to be sent with the request
     *
     * @return [HttpResponse] represents the data retrieved from the server
     *
     * @throws IOException mainly because of server takes too long to response
     */
    suspend inline fun singleRequest(
        method: HttpMethod,
        host: String,
        endpoint: String,
        protocol: URLProtocol = URLProtocol.HTTPS,
        port: Int = protocol.defaultPort,
        crossinline body: (HttpRequestBuilder) -> Unit = {}
    ): HttpResponse =
        CLIENT.request {

            this.method = method
            this.url {
                this.protocol = protocol
                this.host = "$host$endpoint"
                this.port = port
            }
            body( this )
        }

    /**
     * Simultaneously send out requests to multiple servers provided in [hosts].
     * Each response from server will be evaluate and convert into [T].
     * First response to parse successfully will be return, while others are getting
     * stopped all together.
     *
     * @param method purpose of the request. For example, GET/POST/UPDATE. Visit [HttpMethod] for details
     * @param hosts a list of domain names or IP addresses
     * @param endpoint path to send request to on the server
     * @param protocol or scheme such as HTTP(S). To learn more, visit [URLProtocol]
     * @param port to connect. Defaults to port of [protocol]
     * @param body additional information to be sent with the request
     * @param onServerFailure what to do when user takes too long to response or when [T] does
     * not represent the response
     */
    suspend inline fun <reified T> asyncMultiRequestGetFirstValid(
        method: HttpMethod,
        hosts: Collection<String>,
        endpoint: String,
        protocol: URLProtocol = URLProtocol.HTTPS,
        port: Int = protocol.defaultPort,
        crossinline body: (HttpRequestBuilder) -> Unit = {},
        crossinline onServerFailure: (Exception, String) -> T?
    ): T? =
        coroutineScope {

            val deferredResponses = hosts.map { hostUrl ->
                async {
                    try {
                        val response = singleRequest( method, hostUrl, endpoint, protocol, port, body )

                        // Only accept successful responses
                        if ( response.status == HttpStatusCode.OK ) {
                            println("Fetch $hostUrl$endpoint returned code: ${response.status}:")
                            println(response.bodyAsText().replace("\n", ""))

                            response.body<T>()
                        } else
                            null

                    } catch ( e: Exception ) {
                        when( e ) {
                            is IOException,
                            is NoTransformationFoundException -> onServerFailure(e, hostUrl)
                            else -> throw e
                        }
                    }
                }
            }

            // Wait for the first non-null response
            deferredResponses.firstNotNullOfOrNull { it.await() }
        }
}