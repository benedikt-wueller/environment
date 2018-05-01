package de.bwueller.environment.processor

import com.google.protobuf.GeneratedMessageV3
import de.bwueller.environment.processor.actor.Actor
import de.bwueller.environment.processor.user.User
import de.bwueller.environment.protocol.*

class SoundManager {

  private val soundUsers = mutableMapOf<String, User>()

  fun stopAllSounds(user: User) {
    val builder = StopSound.StopSoundRequest.newBuilder()
    builder.delay = 0
    builder.duration = 1000

    // Remove all sounds for this user.
    soundUsers
        .filter { it -> it.value === user }
        .forEach { entry ->
          soundUsers.remove(entry.key)

          // Send stop sound request.
          if (user.socket !== null) {
            builder.sound = entry.key
            user.socket?.send(serializePacket(builder.build()))
          }
        }
  }

  fun handlePlaySoundRequest(packet: PlaySound.PlaySoundRequest) {
    val user = userManager.getUser(packet.user) ?: return
    soundUsers[packet.identifier] = user
    forwardPacketToUser(packet, user)
  }

  fun handleUpdateVolumeRequest(packet: UpdateSoundVolume.UpdateSoundVolumeRequest) {
    val user = soundUsers[packet.sound] ?: return
    forwardPacketToUser(packet, user)
  }

  fun handleUpdateRateRequest(packet: UpdateSoundRate.UpdateSoundRateRequest) {
    val user = soundUsers[packet.sound] ?: return
    forwardPacketToUser(packet, user)
  }

  fun handleStopSoundRequest(packet: StopSound.StopSoundRequest) {
    val user = soundUsers.remove(packet.sound) ?: return
    forwardPacketToUser(packet, user)
  }

  fun handlePlaySoundResponse(packet: PlaySound.PlaySoundResponse) {
    val user = userManager.getUser(packet.identifier) ?: return
    soundUsers[packet.identifier] = user
    forwardPacketToActor(packet, user.actor)
  }

  fun handleUpdateVolumeResponse(packet: UpdateSoundVolume.UpdateSoundVolumeResponse) {
    val user = soundUsers[packet.sound] ?: return
    forwardPacketToActor(packet, user.actor)
  }

  fun handleUpdateRateResponse(packet: UpdateSoundRate.UpdateSoundRateResponse) {
    val user = soundUsers[packet.sound] ?: return
    forwardPacketToActor(packet, user.actor)
  }

  fun handleStopSoundResponse(packet: StopSound.StopSoundResponse) {
    val user = soundUsers.remove(packet.sound) ?: return
    forwardPacketToActor(packet, user.actor)
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
