package de.bwueller.environment.actor.api

import de.bwueller.environment.protocol.*

internal class SoundManager(var client: Client) {

  private val sounds = mutableMapOf<String, Sound>()

  /**
   * Map of user identifier to sound identifiers.
   */
  private val startingSounds = mutableMapOf<String, MutableList<String>>()

  /**
   * Map of sound identifier to user identifiers.
   */
  private val playingSounds = mutableMapOf<String, MutableList<String>>()

  /**
   * Map of user identifier to sound identifiers.
   */
  private val stoppingSounds = mutableMapOf<String, MutableList<String>>()

  fun playSound(sound: Sound, user: String) {
    if (!ActorApi.isUserRegistered(user)) return

    sounds.putIfAbsent(sound.identifier.toString(), sound)

    synchronized(startingSounds) {
      startingSounds.putIfAbsent(user, mutableListOf())
      startingSounds[user]?.add(sound.identifier.toString())
    }

    val builder = PlaySound.PlaySoundRequest.newBuilder()
    builder.user = user
    builder.identifier = sound.identifier.toString()
    if (sound.intro !== null) {
      builder.introSound = sound.intro
    }
    builder.mainSound = sound.main
    builder.volume = sound.volume
    builder.rate = sound.rate
    builder.loop = sound.loop

    client.send(serializePacket(builder.build()))
  }

  fun stopSound(sound: Sound, user: String, delay: Long, duration: Long) {
    if (!ActorApi.isUserRegistered(user)) return

    playingSounds[sound.identifier.toString()]?.remove(user)

    synchronized(stoppingSounds) {
      stoppingSounds.putIfAbsent(user, mutableListOf())
      stoppingSounds[user]?.add(sound.identifier.toString())
    }

    val builder = StopSound.StopSoundRequest.newBuilder()
    builder.user = user
    builder.sound = sound.identifier.toString()
    builder.delay = delay
    builder.duration = duration

    client.send(serializePacket(builder.build()))
  }

  fun changeVolumeOfSound(sound: Sound, volume: Double, duration: Long) {
    val builder = UpdateSoundVolume.UpdateSoundVolumeRequest.newBuilder()
    builder.sound = sound.identifier.toString()
    builder.duration = duration
    builder.volume = volume

    client.send(serializePacket(builder.build()))

    sound.triggerVolumeListeners(volume)
  }

  fun changeRateOfSound(sound: Sound, rate: Double) {
    val builder = UpdateSoundRate.UpdateSoundRateRequest.newBuilder()
    builder.sound = sound.identifier.toString()
    builder.rate = rate

    client.send(serializePacket(builder.build()))

    sound.triggerRateListeners(rate)
  }

  fun handlePlaySoundResponse(packet: PlaySound.PlaySoundResponse) {
    synchronized(playingSounds) {
      playingSounds.putIfAbsent(packet.identifier, mutableListOf())
      playingSounds[packet.identifier]?.add(packet.user)
    }

    synchronized(startingSounds) {
      startingSounds[packet.user]?.remove(packet.identifier)
      if (startingSounds[packet.user]?.size ?: 0 <= 0) {
        startingSounds.remove(packet.user)
      }
    }

    sounds[packet.identifier]?.triggerStartListeners(packet.user)
  }

  fun handleStopSoundResponse(packet: StopSound.StopSoundResponse) {
    playingSounds[packet.sound]?.remove(packet.user)

    synchronized(stoppingSounds) {
      stoppingSounds[packet.user]?.remove(packet.sound)
      if (stoppingSounds[packet.user]?.size ?: 0 <= 0) {
        stoppingSounds.remove(packet.user)
      }
    }

    sounds[packet.sound]?.triggerStopListeners(packet.user)

    synchronized(playingSounds) {
      if (playingSounds[packet.sound]?.size ?: 0 <= 0) {
        playingSounds.remove(packet.sound)
        sounds.remove(packet.sound)
      }
    }
  }

  fun isPlayingFor(sound: Sound, user: String): Boolean {
    return playingSounds[sound.identifier.toString()]?.contains(user) ?: false
  }

  fun isStartingFor(sound: Sound, user: String): Boolean {
    return startingSounds[sound.identifier.toString()]?.contains(user) ?: false
  }

  fun isStoppingFor(sound: Sound, user: String): Boolean {
    return stoppingSounds[sound.identifier.toString()]?.contains(user) ?: false
  }

  fun clear() {
    sounds.clear()
    playingSounds.clear()
    stoppingSounds.clear()
    startingSounds.clear()
  }
}
