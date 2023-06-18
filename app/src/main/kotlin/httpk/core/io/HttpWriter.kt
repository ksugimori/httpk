package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.OutputStream
import java.io.PrintWriter

private const val CRLF = "\r\n"

class HttpWriter(outputStream: OutputStream) {
    private val writer: PrintWriter

    init {
        writer = PrintWriter(outputStream.bufferedWriter())
    }

    fun writeResponse(response: HttpResponse) {
        writer.print(response.statusLine)
        writer.print(CRLF)
        response.headers.forEach { (key, values) ->
            writer.print("$key: ${values.joinToString(", ")}")
            writer.print(CRLF)
        }
        writer.print(CRLF)
        writer.print(response.body)

        writer.flush()
    }
}