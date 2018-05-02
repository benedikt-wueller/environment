package de.bwueller.environment.actor.api

import de.bwueller.environment.protocol.RegisterActorUser
import de.bwueller.environment.protocol.UnregisterActorUser
import de.bwueller.environment.protocol.UpdateUserStatus
import de.bwueller.environment.protocol.serializePacket

internal class UserManager(var client: Client) {

  private val registerCallbacks = mutableMapOf<String, (String, String) -> Unit>()
  private val connectCallbacks = mutableMapOf<String, (String, Boolean) -> Unit>()

  private val connectedUsers = mutableSetOf<String>()
  private val registeredUsers = mutableSetOf<String>()

  fun registerUser(user: String, registerCallback: (user: String, key: String) -> Unit, connectCallback: ((user: String, connected: Boolean) -> Unit)? = null) {
    registerCallbacks[user] = registerCallback
    if (connectCallback !== null) connectCallbacks[user] = connectCallback

    val builder = RegisterActorUser.RegisterActorUserRequest.newBuilder()
    builder.user = user

    client.send(serializePacket(builder.build()))
  }

  fun unregisterUser(user: String) {
    registerCallbacks.remove(user)
    registeredUsers.remove(user)
    connectedUsers.remove(user)

    val builder = UnregisterActorUser.UnregisterActorUserRequest.newBuilder()
    builder.user = user

    client.send(serializePacket(builder.build()))
  }

  fun handleRegisterActorUserResponse(packet: RegisterActorUser.RegisterActorUserResponse) {
    val callback = registerCallbacks[packet.user] ?: return
    registeredUsers.add(packet.user)
    callback.invoke(packet.user, packet.key)
  }

  fun handleUpdateUserStatusRequest(packet: UpdateUserStatus.UpdateUserStatusRequest) {
    if (!registeredUsers.contains(packet.user)) return

    when (packet.status) {
      UpdateUserStatus.UpdateUserStatusRequest.Status.CONNECTED -> connectedUsers.add(packet.user)
      UpdateUserStatus.UpdateUserStatusRequest.Status.DISCONNECTED -> connectedUsers.remove(packet.user)
      else -> Unit
    }

    connectCallbacks[packet.user]?.invoke(packet.user, packet.status == UpdateUserStatus.UpdateUserStatusRequest.Status.CONNECTED)
  }

  fun isUserRegistered(user: String) = registeredUsers.contains(user)

  fun isUserConnected(user: String) = connectedUsers.contains(user)

  fun clear() {
    registerCallbacks.clear()
    connectCallbacks.clear()
    connectedUsers.clear()
  }
}