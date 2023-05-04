package httpk.handler

import java.net.Socket

interface Handler {
    suspend fun handle(socket: Socket)
}