package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpHeadersTest {
    @Test
    fun `setter, getter - OK - single value`() {
        val headers = HttpHeaders()

        headers["Content-Length"] = "99"

        assertEquals(listOf("99"), headers["Content-Length"])
    }

    @Test
    fun `setter, getter - OK - list values`() {
        val headers = HttpHeaders()
        headers["Accept-Encoding"] = "gzip, deflate, br"

        assertEquals(listOf("gzip", "deflate", "br"), headers["Accept-Encoding"])
    }

    @Test
    fun `add - OK`() {
        val headers = HttpHeaders()
        headers.add(HttpHeaderItem.parse("Accept-Encoding: gzip, deflate, br"))

        assertEquals(listOf("gzip", "deflate", "br"), headers["Accept-Encoding"])
    }

    @Test
    fun `contentLength - OK`() {
        val headers = HttpHeaders()

        // この時点では 0
        assertEquals(0, headers.contentLength)

        headers["Content-Length"] = "123"
        assertEquals(123, headers.contentLength)
    }
}