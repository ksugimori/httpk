package httpk.server

import httpk.exception.InvalidHttpMessageException
import httpk.exception.ResourceNotFoundException
import httpk.handler.DummyHttpHandler
import httpk.handler.HttpHandler
import httpk.handler.Router
import httpk.http.io.HttpReader
import httpk.http.io.HttpWriter
import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpResponse
import httpk.http.semantics.HttpStatus
import httpk.util.consoleLog
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.file.Path

private fun InputStream.httpReader() = HttpReader(this)
private fun OutputStream.httpWriter() = HttpWriter(this)

class Worker(private val router: Router) {

    fun execute(socket: Socket) {
        val httpReader = socket.getInputStream().httpReader()
        val httpWriter = socket.getOutputStream().httpWriter()

        val request = try {
            httpReader.readRequest()
        } catch (ex: InvalidHttpMessageException) {
            httpWriter.writeResponse(HttpResponse(status = HttpStatus.BAD_REQUEST))
            consoleLog("\"cannot parse request\" : 400 : ${ex.message}")
            return
        }

        val handler: HttpHandler = router.getHandler(request.target)

        val response = handler.handle(request)
        httpWriter.writeResponse(response)

        consoleLog("\"${request.method} ${request.target} ${request.version}\" : ${response.status.code}")
    }

}