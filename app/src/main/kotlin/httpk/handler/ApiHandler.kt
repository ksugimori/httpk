package httpk.handler

import httpk.http.semantics.*

/**
 * 動的な処理を行うハンドラ
 */
class ApiHandler : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val apiPath = request.target.path.replace("^/api".toRegex(), "")

        if (apiPath == "/sum" && request.method == HttpMethod.POST) {
            val form = FormBody(request.body)

            val a = form["a"]?.toInt() ?: 0
            val b = form["b"]?.toInt() ?: 0

            val sum = a + b

            val responseBody = """
                {
                    "sum": $sum
                }
            """.trimIndent().toByteArray()

            return HttpResponse(
                status = HttpStatus.OK,
                headers = HttpHeaders {
                    contentLength = responseBody.size
                    contentType = MimeType.JSON
                },
                body = responseBody
            )
        } else {
            return HttpResponse(status = HttpStatus.NOT_FOUND)
        }
    }
}