package httpk.handler

import httpk.core.message.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.relativeTo

class StaticResourceHandler(private val documentRoot: Path) : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        val relativePath = Path.of(request.path.removePrefix("/"))

        val body = try {
            Files.readString(documentRoot.resolve(relativePath))
        } catch (ex: IOException) {
            return HttpResponse.notFound(HttpHeaders(), "")
        }

        return HttpResponse.ok(HttpHeaders(), body)
    }
}