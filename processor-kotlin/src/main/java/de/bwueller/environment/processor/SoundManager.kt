package de.bwueller.environment.processor

import com.google.protobuf.GeneratedMessageV3
import de.bwueller.environment.processor.actor.Actor
import de.bwueller.environment.processor.user.User
import de.bwueller.environment.protocol.*
import org.java_websocket.WebSocket

class SoundManager {

  private val soundUsers = mutableMapOf<String, MutableList<User>>()

  fun stopAllSounds(user: User) {
    val requestBuilder = StopSound.StopSoundRequest.newBuilder()
    requestBuilder.user = user.name
    requestBuilder.delay = 0
    requestBuilder.duration = 1000

    val responseBuilder = StopSound.StopSoundResponse.newBuilder()
    responseBuilder.user = user.name

    // Remove all soundUsers for this user.
    soundUsers.entries
        .filter { it.value.contains(user) }
        .forEach { entry ->
          entry.value.remove(user)

          if (entry.value.isEmpty()) {
            soundUsers.remove(entry.key)
          }

          // Send stop sound request.
          if (user.socket !== null) {
            requestBuilder.sound = entry.key
            user.socket?.send(serializePacket(requestBuilder.build()))

            if (user.actor.socket.isOpen) {
              responseBuilder.sound = entry.key
              user.actor.socket.send(serializePacket(responseBuilder.build()))
            }
          }
        }
  }

  fun handlePlaySoundRequest(packet: PlaySound.PlaySoundRequest, socket: WebSocket) {
    val user = userManager.getUser(packet.user) ?: return
    if (socket !== user.actor.socket) return

    soundUsers.putIfAbsent(packet.identifier, mutableListOf())
    soundUsers[packet.identifier]!!.add(user)

    forwardPacketToUser(packet, user)
  }

  fun handleUpdateVolumeRequest(packet: UpdateSoundVolume.UpdateSoundVolumeRequest, socket: WebSocket) {
    val users = soundUsers[packet.sound] ?: return
    users.forEach {
      if (socket !== it.actor.socket) return@forEach
      forwardPacketToUser(packet, it)
    }
  }

  fun handleUpdateRateRequest(packet: UpdateSoundRate.UpdateSoundRateRequest, socket: WebSocket) {
    val users = soundUsers[packet.sound] ?: return
    users.forEach {
      if (socket !== it.actor.socket) return@forEach
      forwardPacketToUser(packet, it)
    }
  }

  fun handleStopSoundRequest(packet: StopSound.StopSoundRequest, socket: WebSocket) {
    val user = userManager.getUser(packet.user) ?: return
    if (user.actor.socket !== socket) return

    if (soundUsers.containsKey(packet.sound)) {
      soundUsers[packet.sound]!!.remove(user)
      if (soundUsers[packet.sound]!!.isEmpty()) {
        soundUsers.remove(packet.sound)
      }
    }

    forwardPacketToUser(packet, user)
  }

  fun handlePlaySoundResponse(packet: PlaySound.PlaySoundResponse, socket: WebSocket) {
    val user = userManager.getUser(socket) ?: return

    val builder = PlaySound.PlaySoundResponse.newBuilder()
    builder.user = user.name
    builder.identifier = packet.identifier

    forwardPacketToActor(builder.build(), user.actor)
  }

  fun handleStopSoundResponse(packet: StopSound.StopSoundResponse, socket: WebSocket) {
    val user = userManager.getUser(socket) ?: return

    val builder = StopSound.StopSoundResponse.newBuilder()
    builder.user = user.name
    builder.sound = packet.sound

    forwardPacketToActor(builder.build(), user.actor)
  }

  private fun forwardPacketToActor(packet: GeneratedMessageV3, actor: Actor) {
    synchronized(actor) {
      actor.socket.send(serializePacket(packet))
    }
  }

  private fun forwardPacketToUser(packet: GeneratedMessageV3, user: User) {
    synchronized(user) {
      if (user.socket === null) return
      user.socket!!.send(serializePacket(packet))
    }
  }
}
