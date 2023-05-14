package httpk.core.message

import kotlin.test.Test
import kotlin.test.assertEquals

class RequestLineTest {
    @Test
    fun `parse - GET HTTP1_1`() {
        val actual = RequestLine.parse("GET /users/100 HTTP/1.1")

        assertEquals(HttpMethod.GET, actual.method)
        assertEquals("/users/100", actual.path)
        assertEquals(HttpVersion.HTTP_1_1, actual.version)
    }

    @Test
    fun `parse - POST HTTP2`() {
        val actual = RequestLine.parse("POST /books HTTP/2")

        assertEquals(HttpMethod.POST, actual.method)
        assertEquals("/books", actual.path)
        assertEquals(HttpVersion.HTTP_2, actual.version)
    }
}