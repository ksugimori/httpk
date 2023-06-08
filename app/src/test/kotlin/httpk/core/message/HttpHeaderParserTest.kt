package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpHeaderParserTest {
    @Test
    fun `parse - OK - single value`() {
        val (key, values) = HttpHeaderParser.parse("Content-Length: 99")

        assertEquals("Content-Length", key)
        assertEquals(listOf("99"), values)
    }

    @Test
    fun `parse - OK - list values`() {
        val (key, values) = HttpHeaderParser.parse("Accept-Encoding:  gzip, deflate, br")

        assertEquals("Accept-Encoding", key)
        assertEquals(listOf("gzip", "deflate", "br"), values)
    }
}