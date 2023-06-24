package httpk.http.semantics

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HttpHeadersTest {
    @Test
    fun `constructor - OK - `() {
        val result = HttpHeaders {
            add("Hoge", "100")
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

        headers.contentLength = 123
        assertEquals(123, headers.contentLength)
    }

    @Test
    fun `contentType - OK`() {
        val headers = HttpHeaders()

        // この時点では未設定
        assertEquals("", headers.contentType)

        headers.contentType = "application/json"
        assertEquals("application/json", headers.contentType)
    }

    @Test
    fun `equals - OK`() {

        assertEquals(
            HttpHeaders {
                addAll("Foo-Bar", listOf("A", "B", "C"))
                add("Host", "test.example.com")
            }, HttpHeaders {
                addAll("Foo-Bar", listOf("A", "B", "C"))
                add("Host", "test.example.com")
            }
        )

        assertNotEquals(
            HttpHeaders {
                addAll("Foo-Bar", listOf("A", "B", "C"))
                add("Host", "test.example.com")
            }, HttpHeaders {
                addAll("Foo-Bar", listOf("A", "B", "C"))
            }
        )
    }

    @Test
    fun `equals - 同じ名前のヘッダーが存在するとき全てが保持されること`() {

        assertEquals(
            HttpHeaders {
                addAll("Foo-Bar", listOf("A", "B", "C"))
            }, HttpHeaders {
                add("Foo-Bar", "A")
                add("Foo-Bar", "B")
                add("Foo-Bar", "C")
            }
        )

    }
}