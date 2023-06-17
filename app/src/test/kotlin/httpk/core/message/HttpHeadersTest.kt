package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class HttpHeadersTest {
    @Test
    fun `constructor - OK - `() {
        val result = HttpHeaders {
            add("Hoge", 100)
            addAll("Fuga", listOf("200", "300"))
        }

        assertEquals(listOf("100"), result["Hoge"])
        assertEquals(listOf("200", "300"), result["Fuga"])
    }

    @Test
    fun `add - OK - single value`() {
        val headers = HttpHeaders()

        headers.add("Content-Length", "99")

        assertEquals(listOf("99"), headers["Content-Length"])
    }

    @Test
    fun `addAll - OK - list values`() {
        val headers = HttpHeaders()
        headers.addAll("Accept-Encoding", listOf("gzip", "deflate", "br"))

        assertContentEquals(listOf("gzip", "deflate", "br"), headers["Accept-Encoding"])
    }

    @Test
    fun `contentLength - OK`() {
        val headers = HttpHeaders()

        // この時点では 0
        assertEquals(0, headers.contentLength)

        headers.add("Content-Length", "123")
        assertEquals(123, headers.contentLength)
    }
}