package httpk.core.message

data class HttpResponse(
    val version: HttpVersion,
    val status: HttpStatus,
    val headers: HttpHeaders,
    val body: ByteArray
) {

    companion object {
        fun ok(headers: HttpHeaders, body: ByteArray): HttpResponse {
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.OK,
                headers = headers,
                body = body
            )
        }

        fun notFound(headers: HttpHeaders, body: ByteArray): HttpResponse {
            return HttpResponse(
                version = HttpVersion.HTTP_1_1,
                status = HttpStatus.NOT_FOUND,
                headers = headers,
                body = body
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpResponse

        if (version != other.version) return false
        if (status != other.status) return false
        if (headers != other.headers) return false
        return body.contentEquals(other.body)
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}