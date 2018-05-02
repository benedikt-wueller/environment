package de.bwueller.environment.actor.api

import java.util.*

data class Sound(val intro: String?, val main: String, private val initialVolume: Double = 1.0, private val initialRate: Double = 1.0, val loop: Boolean = false) {

  private val startListeners = mutableListOf<(String) -> Unit>()
  private val stopListeners = mutableListOf<(String) -> Unit>()

  private val volumeListeners = mutableListOf<(Double) -> Unit>()
  private val rateListeners = mutableListOf<(Double) -> Unit>()

  val identifier: UUID = UUID.randomUUID()

  var volume: Double = initialVolume
    set(value) = updateVolume(value)

  var rate: Double = initialRate
    set(value) = updateRate(value)

  fun play(user: String) {
    ActorApi.soundManager.playSound(this, user)
  }

  fun stop(user: String, delay: Long = 0L, duration: Long = 0L) {
    ActorApi.soundManager.stopSound(this, user, delay, duration)
  }

  fun isPlayingFor(user: String) = ActorApi.soundManager.isPlayingFor(this, user)

  fun isStartingFor(user: String) = ActorApi.soundManager.isStartingFor(this, user)

  fun isStoppingFor(user: String) = ActorApi.soundManager.isStoppingFor(this, user)

  fun fadeToVolume(volume: Double, duration: Long = 0L) {
    updateVolume(volume, duration)
  }

  fun addListeners(startListener: ((String) -> Unit)? = null,
                   stopListener: ((String) -> Unit)? = null,
                   volumeListener: ((Double) -> Unit)? = null,
                   rateListener: ((Double) -> Unit)? = null) {

    if (startListener !== null) startListeners.add(startListener)
    if (stopListener !== null) stopListeners.add(stopListener)
    if (volumeListener !== null) volumeListeners.add(volumeListener)
    if (rateListener !== null) rateListeners.add(rateListener)
  }

  fun removeListener(listener: (Any) -> Unit) {
    startListeners.remove(listener)
    stopListeners.remove(listener)
    volumeListeners.remove(listener)
    rateListeners.remove(listener)
  }

  internal fun triggerStartListeners(user: String) {
    startListeners.forEach { it.invoke(user) }
  }

  internal fun triggerStopListeners(user: String) {
    stopListeners.forEach { it.invoke(user) }
  }

  internal fun triggerVolumeListeners(volume: Double) {
    volumeListeners.forEach { it.invoke(volume) }
  }

  internal fun triggerRateListeners(rate: Double) {
    rateListeners.forEach { it.invoke(rate) }
  }

  private fun updateVolume(value: Double, duration: Long = 0L) {
    if (value < 0 || value > 1) throw IllegalArgumentException("The volume must be a factor between 0.0 and 1.0.")
    ActorApi.soundManager.changeVolumeOfSound(this, value, duration)
  }

  private fun updateRate(value: Double) {
    if (value < 0.5 || value > 4.0) throw IllegalArgumentException("The rate must be a factor between 0.5 and 4.0.")
    ActorApi.soundManager.changeRateOfSound(this, value)
  }
}
