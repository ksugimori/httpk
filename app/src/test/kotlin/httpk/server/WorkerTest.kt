package httpk.server

import httpk.handler.HttpHandler
import httpk.handler.Router
import httpk.http.semantics.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

private class MockSocket(
    var inputStreamMock: InputStream? = null,
    var outputStreamMock: OutputStream? = null,
) : Socket() {
    override fun getInputStream(): InputStream? = this.inputStreamMock
    override fun getOutputStream(): OutputStream? = this.outputStreamMock
}


class WorkerTest {
    @Test
    fun `execute - OK`() {
        val message = buildString {
            append("POST /hoge HTTP/1.1").append("\r\n")
            append("Host: www.example.com").append("\r\n")
            append("Content-Type: application/json").append("\r\n")
            append("Content-Length: 16").append("\r\n")
            append("\r\n")
            append("{\"name\": \"test\"}").append("\r\n")
        }

        val output = ByteArrayOutputStream()
        val mockSocket = MockSocket(
            inputStreamMock = ByteArrayInputStream(message.toByteArray()),
            outputStreamMock = output
        )

        val mockHttpHandler = object : HttpHandler {
            override fun handle(request: HttpRequest): HttpResponse {
                return HttpResponse(
                    status = HttpStatus.CREATED,
                    version = HttpVersion.HTTP_1_1,
                    headers = HttpHeaders {
                        add("Content-Type", "application/json")
                        add("Content-Length", "26")
                    },
                    body = "{\"id\": 99, \"name\": \"test\"}".toByteArray(),
                )
            }
        }

        val mockRouter: Router = object : Router {
            override fun getHandler(uri: URI): HttpHandler = mockHttpHandler
        }

        Worker(mockRouter).execute(mockSocket)

        assertEquals(
            expected = buildString {
                append("HTTP/1.1 201 Created").append("\r\n")
                append("Content-Type: application/json").append("\r\n")
                append("Content-Length: 26").append("\r\n")
                append("\r\n")
                append("{\"id\": 99, \"name\": \"test\"}")
            },
            actual = output.toString()
        )
    }
}