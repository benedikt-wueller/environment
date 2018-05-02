package de.bwueller.environment.actor.api

import de.bwueller.environment.protocol.RegisterActorUser
import de.bwueller.environment.protocol.UnregisterActorUser
import de.bwueller.environment.protocol.serializePacket

internal class UserManager(var client: Client) {

  private val callbacks = mutableMapOf<String, (String, String) -> Unit>()

  private val connectedUsers = mutableSetOf<String>()

  fun registerUser(user: String, callback: (user: String, key: String) -> Unit) {
    callbacks[user] = callback

    val builder = RegisterActorUser.RegisterActorUserRequest.newBuilder()
    builder.user = user

    client.send(serializePacket(builder.build()))
  }

  fun unregisterUser(user: String) {
    callbacks.remove(user)
    connectedUsers.remove(user)

    val builder = UnregisterActorUser.UnregisterActorUserRequest.newBuilder()
    builder.user = user

    client.send(serializePacket(builder.build()))
  }

  fun handleRegisterActorUserResponse(packet: RegisterActorUser.RegisterActorUserResponse) {
    val callback = callbacks[packet.user] ?: return
    connectedUsers.add(packet.user)
    callback.invoke(packet.user, packet.key)
  }

  fun isUserConnected(user: String) = connectedUsers.contains(user)

  fun clear() {
    callbacks.clear()
    connectedUsers.clear()
  }
}