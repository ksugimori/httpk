package httpk.core

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val version: HttpVersion,
    val headers: List<String> = emptyList(),
    val body: String? = null
)