package httpk.handler

import httpk.core.message.HttpHeaders
import httpk.core.message.HttpRequest
import httpk.core.message.HttpResponse
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class StaticResourceHandler(private val documentRoot: Path) : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val relativePath = Path.of(request.path.removePrefix("/"))

        val body = try {
            Files.readAllBytes(documentRoot.resolve(relativePath))
        } catch (ex: IOException) {
            return HttpResponse.notFound(HttpHeaders(), "")
        }

        // TODO MIME types
        val headers = HttpHeaders()
        headers.contentType = "text/html"
        headers.contentLength = body.size

        return HttpResponse.ok(headers, body.decodeToString())
    }
}