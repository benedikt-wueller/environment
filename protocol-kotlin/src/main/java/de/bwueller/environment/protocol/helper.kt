package de.bwueller.environment.protocol

import com.google.protobuf.GeneratedMessageV3
import de.bwueller.environment.protocol.*

/* NOPE. */

fun deserializePacket(bytes: ByteArray): GeneratedMessageV3? {
    val type = if (bytes.isNotEmpty()) bytes[0] else return null
    val data = bytes.copyOfRange(1, bytes.size - 1)

    when (type) {
        0.toByte() -> return RegisterActor.RegisterActorRequest.parseFrom(data)
        1.toByte() -> return RegisterActor.RegisterActorResponse.parseFrom(data)
        2.toByte() -> return RegisterActorUser.RegisterActorUserRequest.parseFrom(data)
        3.toByte() -> return RegisterActorUser.RegisterActorUserResponse.parseFrom(data)
        4.toByte() -> return UnregisterActorUser.UnregisterActorUserRequest.parseFrom(data)
        5.toByte() -> return ConnectUser.ConnectUserRequest.parseFrom(data)
        6.toByte() -> return ConnectUser.ConnectUserResponse.parseFrom(data)
        7.toByte() -> return UpdateUserStatus.UpdateUserStatusRequest.parseFrom(data)
        8.toByte() -> return UpdateActorUserMeta.UpdateActorUserMetaRequest.parseFrom(data)
        9.toByte() -> return PlaySound.PlaySoundRequest.parseFrom(data)
        10.toByte() -> return PlaySound.PlaySoundResponse.parseFrom(data)
        11.toByte() -> return StopSound.StopSoundRequest.parseFrom(data)
        12.toByte() -> return StopSound.StopSoundResponse.parseFrom(data)
        13.toByte() -> return UpdateSoundVolume.UpdateSoundVolumeRequest.parseFrom(data)
        14.toByte() -> return UpdateSoundVolume.UpdateSoundVolumeResponse.parseFrom(data)
        15.toByte() -> return UpdateSoundRate.UpdateSoundRateRequest.parseFrom(data)
        16.toByte() -> return UpdateSoundRate.UpdateSoundRateResponse.parseFrom(data)
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
        is UpdateSoundVolume.UpdateSoundVolumeResponse -> 14
        is UpdateSoundRate.UpdateSoundRateRequest -> 15
        is UpdateSoundRate.UpdateSoundRateResponse -> 16
        else -> return null
    }

    val data = message.toByteArray()
    return ByteArray(1, { type }).plus(data)
}
