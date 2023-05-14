package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.Closeable
import java.io.OutputStream
import java.io.PrintWriter

class HttpResponseWriter(private val outputStream: OutputStream) : Closeable by outputStream {
    private val writer: PrintWriter = PrintWriter(outputStream)

    fun writeResponse(response: HttpResponse) {
        writer.print("${response.statusLine}$CRLF")
        response.headers.forEach { item ->
            writer.print("$item$CRLF")
        }
        writer.print(CRLF)
        writer.print(response.body)

        writer.flush()
    }

    companion object {
        private const val CRLF = "\r\n"
    }
}