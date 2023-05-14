package httpk.core.message

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val version: HttpVersion,
    val headers: HttpHeaders,
    val body: String? = null
) {
    val requestLine: String
        get() = "$method $path $version"
}