package httpk.core.message

import httpk.core.io.HttpResponseWriter

data class HttpResponse(
    val version: HttpVersion,
    val status: HttpStatus,
    val headers: HttpHeaders,
    val body: String
) {
    val statusLine: String
        get() = "${this.version} ${this.status.code} ${this.status.message}"
}