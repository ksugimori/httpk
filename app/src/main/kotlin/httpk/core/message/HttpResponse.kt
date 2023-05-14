package httpk.core.message

data class HttpResponse(
    val version: HttpVersion,
    val status: HttpStatus,
    val headers: HttpHeaders,
    val body: String
)