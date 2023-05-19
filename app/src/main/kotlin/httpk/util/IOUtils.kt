package httpk.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket


/**
 * 改行文字（\r\n）まで読み取る。
 *
 * @return \r\n の直前までのバイト列を文字列にしたもの。
 */
fun InputStream.readLine(): String {
    val out = ByteArrayOutputStream()

    var previous: Int = -1
    var current: Int = -1
    while (this.read().also { current = it } != -1) {
        if (previous == '\r'.code && current == '\n'.code) break
        out.write(current)
        previous = current
    }

    return String(out.toByteArray()).trimEnd('\r')
}

/**
 * [InputStream.readLine] を呼び出す無限シーケンス。
 *
 * @return 読み取った行のシーケンス
 */
fun InputStream.linesSequence(): Sequence<String> = sequence {
    while (true) {
        yield(this@linesSequence.readLine())
    }
}

suspend fun Socket.getOutputStreamSuspending(): OutputStream {
    val socket = this
    return withContext(Dispatchers.IO) { socket.getOutputStream() }
}

suspend fun ServerSocket.acceptSuspending(): Socket {
    val serverSocket = this
    return withContext(Dispatchers.IO) { serverSocket.accept() }
}