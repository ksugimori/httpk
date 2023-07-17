package httpk.handler

import httpk.exception.ResourceNotFoundException
import httpk.http.semantics.*

/**
 * HTTP ハンドラ。
 */
abstract class HttpHandler {

    /**
     * HTTP リクエストの処理を行う。
     *
     * 例外はすべてキャッチされ、必ずレスポンスが返ります。
     * @param request HTTP リクエスト
     * @return HTTP レスポンス
     */
    fun handle(request: HttpRequest): HttpResponse {
        return try {
            doHandle(request)
        } catch (ex: ResourceNotFoundException) {
            val body = "<!DOCTYPE html><html><body><h1>NOT FOUND</h1></body></html>".toByteArray()
            val headers = HttpHeaders {
                contentType = MimeType.HTML
                contentLength = body.size
            }
            HttpResponse(status = HttpStatus.NOT_FOUND, headers = headers, body = body)
        } catch (ex: Exception) {
            HttpResponse(status = HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * HTTP リクエストの処理の拡張ポイント。
     *
     * @param request HTTP リクエスト
     * @return HTTP レスポンス
     */
    protected abstract fun doHandle(request: HttpRequest): HttpResponse
}

