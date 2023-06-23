package httpk.core.io

import httpk.core.message.*
import httpk.exception.InvalidHttpMessageException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

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
        val expected = HttpRequest(
            method = HttpMethod.POST,
            target = "/hoge",
            version = HttpVersion.HTTP_1_1,
            headers = HttpHeaders {
                add("Host", "www.example.com")
                add("Content-Type", "application/json")
                add("Content-Length", "16")
            },
            body = "{\"name\": \"test\"}".toByteArray()
        )

        assertEquals(expected, result)
    }

    @Test
    fun `readRequest - OK - リクエストボディなし`() {
        val message = buildString {
            append("GET /foo/bar HTTP/1.1").append(CRLF)
            append("Host: www.example.jp").append(CRLF)
            append("Accept-Encoding: gzip, deflate, br").append(CRLF)
            append(CRLF)
        }

        val inputStream = ByteArrayInputStream(message.toByteArray())

        // 実行
        val httpReader = HttpReader(inputStream)
        val result = httpReader.readRequest()

        // 検証
        val expected = HttpRequest(
            method = HttpMethod.GET,
            target = "/foo/bar",
            version = HttpVersion.HTTP_1_1,
            headers = HttpHeaders {
                add("Host", "www.example.jp")
                addAll("Accept-Encoding", listOf("gzip", "deflate", "br"))
            },
            body = ByteArray(0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun `readRequest - NG - request line のフォーマット不正の場合 InvalidHttpMessageException が投げられること`() {
        val message = buildString {
            append("GET HTTP/1.1").append(CRLF) // target が無い
            append("Host: www.example.jp").append(CRLF)
            append(CRLF)
        }

        val inputStream = ByteArrayInputStream(message.toByteArray())

        // 実行
        assertThrows<InvalidHttpMessageException> {
            val httpReader = HttpReader(inputStream)
            httpReader.readRequest()
        }.also {
            assertEquals("invalid HTTP message: GET HTTP/1.1", it.message)
        }
    }
}