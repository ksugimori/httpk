package httpk.handler

import java.net.Socket

interface Handler {
    fun handle(socket: Socket)
}