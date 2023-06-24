package httpk.http.io

import httpk.util.linesSequence
import httpk.util.readLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class InputStreamExtensionsTest {

    @Test
    fun testReadLine() {
        val message = buildString {
            append("POST /hoge HTTP/1.1").append("\r\n")
            append("Host: www.example.com").append("\r\n")
            append("Content-Type: application/json").append("\r\n")
            append("Content-Length: 16").append("\r\n")
            append("\r\n")
            append("{\"name\": \"test\"}").append("\r\n")
        }

        val inputStream = message.byteInputStream()

        // 実行
        assertEquals("POST /hoge HTTP/1.1", inputStream.readLine())
        assertEquals("Host: www.example.com", inputStream.readLine())
        assertEquals("Content-Type: application/json", inputStream.readLine())
        assertEquals("Content-Length: 16", inputStream.readLine())
        assertEquals("", inputStream.readLine())
        assertEquals("{\"name\": \"test\"}", inputStream.readLine())

        // 終端まで達していたら空文字が返ること
        assertEquals("", inputStream.readLine())
    }

    @Test
    fun linesSequence() {
        val message = buildString {
            append("POST /hoge HTTP/1.1").append("\r\n")
            append("Host: www.example.com").append("\r\n")
            append("Content-Type: application/json").append("\r\n")
            append("Content-Length: 16").append("\r\n")
            append("\r\n")
            append("{\"name\": \"test\"}").append("\r\n")
        }

        val inputStream = message.byteInputStream()

        // 検証
        val firstThree = inputStream.linesSequence().take(3).toList()
        assertContentEquals(listOf("POST /hoge HTTP/1.1","Host: www.example.com","Content-Type: application/json"), firstThree)
    }
}