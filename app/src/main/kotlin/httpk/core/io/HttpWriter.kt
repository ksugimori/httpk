package httpk.core.io

import httpk.core.message.HttpResponse
import java.io.OutputStream
import java.io.PrintWriter

private const val SP = " "
private const val CRLF = "\r\n"

class HttpWriter(private val outputStream: OutputStream) {

    fun writeResponse(response: HttpResponse) {
        val writer = PrintWriter(outputStream)

        // status line
        writer.print(response.version)
        writer.print(SP)
        writer.print(response.status.code)
        writer.print(SP)
        writer.print(response.status.message)
        writer.print(CRLF)

        // headers
        for ((fieldName, fieldValues) in response.headers) {
            writer.print(fieldName)
            writer.print(":")
            writer.print(SP)
            writer.print(fieldValues.joinToString(", "))
            writer.print(CRLF)
        }

        // ヘッダーとボディの区切りの空行
        writer.print(CRLF)

        writer.flush()

        // body
        outputStream.write(response.body)
    }
}