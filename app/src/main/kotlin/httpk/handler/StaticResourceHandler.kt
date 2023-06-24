package httpk.handler

import httpk.exception.ResourceNotFoundException
import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpRequest
import httpk.http.semantics.HttpResponse
import httpk.http.semantics.HttpStatus
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * 静的コンテンツにアクセスするための HTTP ハンドラ。
 *
 * @param documentRoot ドキュメントルート
 */
class StaticResourceHandler(private val documentRoot: Path) : HttpHandler() {
    override fun doHandle(request: HttpRequest): HttpResponse {
        var path = Path.of(documentRoot.pathString, request.target.path)
        if (path.isDirectory()) {
            path = path.resolve("index.html")
        }

        val body = try {
            Files.readAllBytes(path)
        } catch (ex: IOException) {
            throw ResourceNotFoundException("${request.target} not found.", ex)
        }

        // TODO MIME types
        val headers = HttpHeaders()
        headers.contentType = "text/html"
        headers.contentLength = body.size

        return HttpResponse(status = HttpStatus.OK, headers = headers, body = body)
    }
}