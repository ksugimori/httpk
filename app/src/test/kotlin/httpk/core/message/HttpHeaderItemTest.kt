package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpHeaderItemTest {
    @Test
    fun `parse - OK - single value`() {
        val actual = HttpHeaderItem.parse("Content-Length: 99")

        assertEquals("Content-Length", actual.key)
        assertEquals(listOf("99"), actual.values)
    }

    @Test
    fun `parse - OK - list values`() {
        val actual = HttpHeaderItem.parse("Accept-Encoding:  gzip, deflate, br")

        assertEquals("Accept-Encoding", actual.key)
        assertEquals(listOf("gzip", "deflate", "br"), actual.values)
    }
}