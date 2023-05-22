package httpk.worker

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

        runBlocking {
            Worker(mockSocket).execute()
        }

        assertEquals("hoge", output.toString(Charsets.UTF_8))
    }
}