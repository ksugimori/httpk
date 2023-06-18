package httpk.core.message

data class HttpResponse(
    val version: HttpVersion,
    val status: HttpStatus,
    val headers: HttpHeaders,
    val body: String
) {
    val statusLine: String
        get() = "${this.version} ${this.status.code} ${this.status.message}"
    
    companion object {
        fun ok(headers: HttpHeaders, body: String): HttpResponse {
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.OK,
                headers = headers,
                body = body
            )
        }

        fun notFound(headers: HttpHeaders, body: String): HttpResponse { 
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.NOT_FOUND,
                headers = headers,
                body = body
            )
        }
    }
}