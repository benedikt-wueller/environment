package de.bwueller.environment.processor.internal

import de.bwueller.environment.protocol.RegisterActor
import de.bwueller.environment.protocol.deserializePacket
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer

// TODO: implement logger

class InternalWebsocketServer(private val serverPort: Int) : WebSocketServer(InetSocketAddress(serverPort)) {

    private val actorManager = ActorManager()

    init {
        start()
    }

    override fun onStart() {
        println("Internal server started on port $serverPort.")
    }

    override fun onMessage(socket: WebSocket?, message: ByteBuffer?) {
        if (message === null) return
        if (socket === null) return

        val data = ByteArray(message.remaining())
        message.get(data)

        val packet = deserializePacket(data) ?: return

        // Pass packet to the corresponding handlers.
        when (packet) {
            is RegisterActor.RegisterActorRequest -> actorManager.handleRegisterActorRequest(packet, socket)
        }
    }

    override fun onClose(socket: WebSocket?, code: Int, reason: String, remote: Boolean) {
        if (socket === null) return

        // Unregister possible actor.
        if (actorManager.isActorSocket(socket)) {
            actorManager.unregisterActor(socket)
        }
    }

    override fun onOpen(socket: WebSocket?, handshake: ClientHandshake?) = Unit
    override fun onMessage(socket: WebSocket?, message: String?) = Unit

    override fun onError(socket: WebSocket?, exception: Exception?) {
        exception?.printStackTrace()
    }
}
