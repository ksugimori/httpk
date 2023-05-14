package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpHeaderTest {
    @Test
    fun `parse - OK - single value`() {
        val actual = HttpHeader.parse("Content-Length: 99")

        assertEquals("Content-Length", actual.key)
        assertEquals("99", actual.value)
        assertEquals(listOf("99"), actual.valueAsList)
    }

    @Test
    fun `parse - OK - list values`() {
        val actual = HttpHeader.parse("Accept-Encoding:  gzip, deflate, br")

        assertEquals("Accept-Encoding", actual.key)
        assertEquals("gzip, deflate, br", actual.value)
        assertEquals(listOf("gzip", "deflate", "br"), actual.valueAsList)
    }
}