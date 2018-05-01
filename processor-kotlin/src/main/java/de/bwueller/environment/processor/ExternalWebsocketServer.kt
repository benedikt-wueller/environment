package de.bwueller.environment.processor

import de.bwueller.environment.protocol.*
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class ExternalWebsocketServer(private val serverPort: Int) : WebSocketServer(InetSocketAddress(serverPort)) {

  init {
    start()
  }

  override fun onStart() {
    println("External server started on port $serverPort.")
  }

  override fun onMessage(socket: WebSocket?, message: ByteBuffer?) {
    if (message === null) return
    if (socket === null) return

    val data = ByteArray(message.remaining())
    message.get(data)

    val packet = deserializePacket(data) ?: return

    // Pass packet to the corresponding handlers.
    when (packet) {
      is ConnectUser.ConnectUserRequest -> userManager.handleConnectUserRequest(packet, socket)
      is PlaySound.PlaySoundResponse -> soundManager.handlePlaySoundResponse(packet)
      is StopSound.StopSoundResponse -> soundManager.handleStopSoundResponse(packet)
      is UpdateSoundVolume.UpdateSoundVolumeResponse -> soundManager.handleUpdateVolumeResponse(packet)
      is UpdateSoundRate.UpdateSoundRateResponse -> soundManager.handleUpdateRateResponse(packet)
    }
  }

  override fun onClose(socket: WebSocket?, code: Int, reason: String, remote: Boolean) {
    if (socket === null) return

    // Unregister possible user.
    if (userManager.isUserSocket(socket)) {
      userManager.disconnectUser(socket)
    }
  }

  override fun onOpen(socket: WebSocket?, handshake: ClientHandshake?) = Unit
  override fun onMessage(socket: WebSocket?, message: String?) = Unit

  override fun onError(socket: WebSocket?, exception: Exception?) {
    exception?.printStackTrace()
  }
}
