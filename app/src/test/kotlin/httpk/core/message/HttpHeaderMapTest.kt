package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpHeaderMapTest {
    @Test
    fun `setter, getter - OK - single value`() {
        val headers = HttpHeaderMap()

        headers["Content-Length"] = "99"

        assertEquals(listOf("99"), headers["Content-Length"])
    }

    @Test
    fun `setter, getter - OK - list values`() {
        val headers = HttpHeaderMap()
        headers["Accept-Encoding"] = "gzip, deflate, br"

        assertEquals(listOf("gzip", "deflate", "br"), headers["Accept-Encoding"])
    }

    @Test
    fun `add - OK`() {
        val headers = HttpHeaderMap()
        headers.add(HttpHeader.parse("Accept-Encoding: gzip, deflate, br"))

        assertEquals(listOf("gzip", "deflate", "br"), headers["Accept-Encoding"])
    }

    @Test
    fun `contentLength - OK`() {
        val headers = HttpHeaderMap()

        // この時点では 0
        assertEquals(0, headers.contentLength)

        headers["Content-Length"] = "123"
        assertEquals(123, headers.contentLength)
    }
}