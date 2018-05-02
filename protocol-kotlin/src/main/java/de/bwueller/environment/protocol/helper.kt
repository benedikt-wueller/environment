package de.bwueller.environment.protocol

import com.google.protobuf.GeneratedMessageV3

/* NOPE. */

fun deserializePacket(bytes: ByteArray): GeneratedMessageV3? {
  val type = if (bytes.isNotEmpty()) bytes[0] else return null
  val data = if (bytes.size >= 2) bytes.copyOfRange(1, bytes.size) else ByteArray(0)

  try {
    when (type.toInt()) {
      0 -> return RegisterActor.RegisterActorRequest.parseFrom(data)
      1 -> return RegisterActor.RegisterActorResponse.parseFrom(data)
      2 -> return RegisterActorUser.RegisterActorUserRequest.parseFrom(data)
      3 -> return RegisterActorUser.RegisterActorUserResponse.parseFrom(data)
      4 -> return UnregisterActorUser.UnregisterActorUserRequest.parseFrom(data)
      5 -> return ConnectUser.ConnectUserRequest.parseFrom(data)
      6 -> return ConnectUser.ConnectUserResponse.parseFrom(data)
      7 -> return UpdateUserStatus.UpdateUserStatusRequest.parseFrom(data)
      8 -> return UpdateActorUserMeta.UpdateActorUserMetaRequest.parseFrom(data)
      9 -> return PlaySound.PlaySoundRequest.parseFrom(data)
      10 -> return PlaySound.PlaySoundResponse.parseFrom(data)
      11 -> return StopSound.StopSoundRequest.parseFrom(data)
      12 -> return StopSound.StopSoundResponse.parseFrom(data)
      13 -> return UpdateSoundVolume.UpdateSoundVolumeRequest.parseFrom(data)
      14 -> return UpdateSoundRate.UpdateSoundRateRequest.parseFrom(data)
    }
  } catch (ex: Exception) {
  }

  return null
}

fun serializePacket(message: GeneratedMessageV3): ByteArray? {
  val type: Byte = when (message) {
    is RegisterActor.RegisterActorRequest -> 0
    is RegisterActor.RegisterActorResponse -> 1
    is RegisterActorUser.RegisterActorUserRequest -> 2
    is RegisterActorUser.RegisterActorUserResponse -> 3
    is UnregisterActorUser.UnregisterActorUserRequest -> 4
    is ConnectUser.ConnectUserRequest -> 5
    is ConnectUser.ConnectUserResponse -> 6
    is UpdateUserStatus.UpdateUserStatusRequest -> 7
    is UpdateActorUserMeta.UpdateActorUserMetaRequest -> 8
    is PlaySound.PlaySoundRequest -> 9
    is PlaySound.PlaySoundResponse -> 10
    is StopSound.StopSoundRequest -> 11
    is StopSound.StopSoundResponse -> 12
    is UpdateSoundVolume.UpdateSoundVolumeRequest -> 13
    is UpdateSoundRate.UpdateSoundRateRequest -> 14
    else -> return null
  }

  val data = message.toByteArray()
  return ByteArray(1, { type }).plus(data)
}
