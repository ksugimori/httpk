package httpk.handler

import httpk.core.HttpRequest
import httpk.core.HttpRequestReader
import httpk.log
import httpk.util.getBufferedReaderSuspending
import httpk.util.getBufferedWriterSuspending
import java.io.PrintWriter
import java.net.Socket


class HttpHandler() : Handler {

    override suspend fun handle(socket: Socket) {
        socket.use {
            val reader = HttpRequestReader(it.getBufferedReaderSuspending())
            val writer = PrintWriter(it.getBufferedWriterSuspending()) // TODO HttpResponseWriter 作ろう

            val requestLine = reader.readRequestLine()
            val headers = reader.readHeaders()
            val body = reader.readBody()

            val request = HttpRequest(
                method = requestLine.method,
                path = requestLine.path,
                version = requestLine.version,
                headers = headers,
                body = body
            )
            log("Request: $request")

            // TODO ドキュメント取得
            // TODO Response クラス作成
            writer.println("HTTP/1.1 200 OK")
            writer.flush()
        }

        log("close connection: ${socket.inetAddress}")
    }

}