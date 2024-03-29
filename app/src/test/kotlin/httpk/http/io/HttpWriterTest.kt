package httpk.http.io

import httpk.http.semantics.HttpHeaders
import httpk.http.semantics.HttpResponse
import httpk.http.semantics.HttpStatus
import httpk.http.semantics.HttpVersion
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpWriterTest {
    @Test
    fun `writeResponse - OK`() {
        val outputStream = ByteArrayOutputStream()
        val response = HttpResponse(
            version = HttpVersion.HTTP_1_1,
            status = HttpStatus.OK,
            headers = HttpHeaders {
                add("Content-Type", "text/plain")
                add("Content-Length", "5")
            },
            body = "Hello".toByteArray()
        )

        // 実行
        val httpWriter = HttpWriter(outputStream)
        httpWriter.writeResponse(response)

        // 検証
        assertEquals(
            expected = buildString {
                append("HTTP/1.1 200 OK").append("\r\n")
                append("Content-Type: text/plain").append("\r\n")
                append("Content-Length: 5").append("\r\n")
                append("\r\n")
                append("Hello")
            },
            actual = outputStream.toString()
        )
    }
}