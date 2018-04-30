package de.bwueller.environment.processor

import de.bwueller.environment.protocol.deserializePacket
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class InternalWebsocketServer(private val serverPort: Int) : WebSocketServer(InetSocketAddress(serverPort)) {

    init {
        start()
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        if (message === null) return

        val data = ByteArray(message.remaining())
        message.get(data)

        val packet = deserializePacket(data)
        println(packet)
    }

    override fun onStart() {
        println("Internal server started on port $serverPort.")
    }

    override fun onOpen(socket: WebSocket?, handshake: ClientHandshake?) = Unit
    override fun onMessage(socket: WebSocket?, message: String?) = Unit
    override fun onClose(socket: WebSocket?, code: Int, reason: String, remote: Boolean) = Unit

    override fun onError(socket: WebSocket?, exception: Exception?) {
        exception?.printStackTrace()
    }
}

