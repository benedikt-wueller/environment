package de.bwueller.environment.actor.api

import de.bwueller.environment.protocol.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

internal class Client(private val name: String, uri: URI, private val meta: Map<Int, String> = mapOf()) : WebSocketClient(uri) {

  init {
    connect()
  }

  override fun onOpen(handshake: ServerHandshake?) {
    registerActor()
  }

  override fun onMessage(message: ByteBuffer?) {
    if (message === null) return
    if (socket === null) return

    val data = ByteArray(message.remaining())
    message.get(data)

    val packet = deserializePacket(data) ?: return

    when (packet) {
      is RegisterActor.RegisterActorResponse -> handleRegisterActorResponse(packet)
      is PlaySound.PlaySoundResponse -> ActorApi.soundManager.handlePlaySoundResponse(packet)
      is StopSound.StopSoundResponse -> ActorApi.soundManager.handleStopSoundResponse(packet)
      is RegisterActorUser.RegisterActorUserResponse -> ActorApi.userManager.handleRegisterActorUserResponse(packet)
    }
  }

  override fun onClose(code: Int, reason: String, remote: Boolean) {
    ActorApi.userManager.clear()
    ActorApi.soundManager.clear()

    ActorApi.setConnected(false, null)
  }

  private fun registerActor() {
    println(System.currentTimeMillis())

    val builder = RegisterActor.RegisterActorRequest.newBuilder()
    builder.name = name

    for (entry in meta.entries) {
      val metaBuilder = General.Meta.newBuilder()
      metaBuilder.identifier = entry.key
      metaBuilder.value = entry.value
      builder.addMeta(metaBuilder.build())
    }

    send(serializePacket(builder.build()))
  }

  private fun handleRegisterActorResponse(packet: RegisterActor.RegisterActorResponse) {
    if (ActorApi.isConnected) return

    if (packet.status == RegisterActor.RegisterActorResponse.Status.SUC_CONNECTED) {
      ActorApi.setConnected(true, UUID.fromString(packet.identifier))
    }

    println(System.currentTimeMillis())
  }

  override fun onMessage(message: String?) = Unit

  override fun onError(exception: Exception?) {
    exception?.printStackTrace()
  }
}
