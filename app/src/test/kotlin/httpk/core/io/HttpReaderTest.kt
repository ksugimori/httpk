package httpk.core.io

import httpk.core.message.HttpMethod
import httpk.core.message.HttpVersion
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val CRLF = "\r\n"

class HttpReaderTest {
    @Test
    fun `readRequest - OK - リクエストボディあり`() {
        val message = buildString {
            append("POST /hoge HTTP/1.1").append(CRLF)
            append("Host: www.example.com").append(CRLF)
            append("Content-Type: application/json").append(CRLF)
            append("Content-Length: 16").append(CRLF)
            append(CRLF)
            append("{\"name\": \"test\"}").append(CRLF)
        }

        val inputStream = ByteArrayInputStream(message.toByteArray())

        // 実行
        val httpReader = HttpReader(inputStream)
        val result = httpReader.readRequest()

        // 検証
        assertEquals(HttpMethod.POST, result.method)
        assertEquals("/hoge", result.path)
        assertEquals(HttpVersion.HTTP_1_1, result.version)
        // TODO HttpHeaders#equals メソッド作成したい
        assertEquals(listOf("www.example.com"), result.headers["Host"])
        assertEquals(listOf("application/json"), result.headers["Content-Type"])
        assertEquals(listOf("16"), result.headers["Content-Length"])
        assertEquals("{\"name\": \"test\"}", result.body)
    }

    @Test
    fun `readRequest - OK - リクエストボディなし`() {
        val message = buildString {
            append("GET /foo/bar HTTP/1.1").append(CRLF)
            append("Host: www.example.jp").append(CRLF)
            append(CRLF)
        }

        val inputStream = ByteArrayInputStream(message.toByteArray())

        // 実行
        val httpReader = HttpReader(inputStream)
        val result = httpReader.readRequest()

        // 検証
        assertEquals(HttpMethod.GET, result.method)
        assertEquals("/foo/bar", result.path)
        assertEquals(HttpVersion.HTTP_1_1, result.version)
        assertEquals(listOf("www.example.jp"), result.headers["Host"])
        assertNull(result.body)
    }
}