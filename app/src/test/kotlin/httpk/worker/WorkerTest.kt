package httpk.worker

import httpk.core.message.*
import httpk.handler.HttpHandler
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals

private class MockSocket(
    var inputStreamMock: InputStream? = null,
    var outputStreamMock: OutputStream? = null,
) : Socket() {
    override fun getInputStream(): InputStream? = this.inputStreamMock
    override fun getOutputStream(): OutputStream? = this.outputStreamMock
}

private class MockHttpHandler : HttpHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        return HttpResponse(
            status = HttpStatus.CREATED,
            version = HttpVersion.HTTP_1_1,
            headers = HttpHeaders(
                mutableMapOf(
                    "Content-Type" to listOf("application/json"),
                    "Content-Length" to listOf("26")
                )
            ),
            body = "{\"id\": 99, \"name\": \"test\"}",
        )
    }
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

        val worker = Worker(MockHttpHandler())

        runBlocking {
            worker.execute(mockSocket)
        }

        assertEquals(
            expected = listOf(
                "HTTP/1.1 201 Created",
                "Content-Type: application/json",
                "Content-Length: 26",
                "",
                "{\"id\": 99, \"name\": \"test\"}"
            ),
            actual = output.toString().split("\r\n")
        )
    }
}