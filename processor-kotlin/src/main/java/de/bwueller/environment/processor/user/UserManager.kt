package de.bwueller.environment.processor.user

import de.bwueller.environment.processor.actorManager
import de.bwueller.environment.processor.soundManager
import de.bwueller.environment.protocol.*
import org.java_websocket.WebSocket
import java.util.*

class UserManager {

  private val users = mutableMapOf<String, User>()
  private val socketUsers = mutableMapOf<WebSocket, User>()

  /**
   * Unregisters the user from the actor he is currently connected to.
   *
   * @param user the user to unregister.
   */
  @Synchronized
  fun unregisterUser(user: User) {
    // Stop any currently playing sounds.
    soundManager.stopAllSounds(user)

    user.socket?.close()
    user.actor.users.remove(user)

    println("User ${user.name} has been unregistered from actor ${user.actor.name}.")

    // Try to remove the user if he is neither connected nor registered.
    tryRemoveUser(user)
  }

  /**
   * Checks whether the user is either registered or connected to the processor. If not he will get removed.
   *
   * @param user the user to try to remove.
   */
  @Synchronized
  fun tryRemoveUser(user: User) {
    // If the user is part of the last actor he was in, he is still registered.
    if (user.actor.users.contains(user)) return

    // If the user socket is still open, he is still connected.
    if (user.socket !== null) return

    // The user is neither connected nor registered.
    users.remove(user.name)

    println("User ${user.name} has been removed.")
  }

  /**
   * Disconnects a user with the given WebSocket connection.
   *
   * @param socket the socket to disconnect the user with.
   */
  @Synchronized
  fun disconnectUser(socket: WebSocket) {
    val user = socketUsers.remove(socket) ?: return
    user.socket = null

    // Send update to actor.
    val actorBuilder = UpdateUserStatus.UpdateUserStatusRequest.newBuilder()
    actorBuilder.user = user.name
    actorBuilder.status = UpdateUserStatus.UpdateUserStatusRequest.Status.DISCONNECTED

    if (user.actor.socket.isOpen) {
      user.actor.socket.send(serializePacket(actorBuilder.build()))
    }

    tryRemoveUser(user)

    println("User ${user.name} has disconnected.")
  }

  /**
   * Removes a user with the given WebSocket connection.
   *
   * @param socket the socket to remove the user with.
   */
  @Synchronized
  fun handleConnectUserRequest(packet: ConnectUser.ConnectUserRequest, socket: WebSocket) {
    val userName = packet.user
    val key = packet.key

    val builder = ConnectUser.ConnectUserResponse.newBuilder()
    builder.user = userName

    if (users.containsKey(userName)) {
      val user = users[userName]!!

      if (user.key == key) {
        if (user.socket === null) {
          // The user is allowed to connect.
          builder.status = ConnectUser.ConnectUserResponse.Status.SUC_CONNECTED

          // Set socket for user.
          socketUsers[socket] = user
          user.socket = socket

          // Send update to actor.
          val actorBuilder = UpdateUserStatus.UpdateUserStatusRequest.newBuilder()
          actorBuilder.user = user.name
          actorBuilder.status = UpdateUserStatus.UpdateUserStatusRequest.Status.CONNECTED
          user.actor.socket.send(serializePacket(actorBuilder.build()))
        } else {
          // The user is already connected with another WebSocket connection. Set error status.
          builder.status = ConnectUser.ConnectUserResponse.Status.ERR_ALREADY_CONNECTED
        }
      } else {
        println("${user.key}, $key")

        // The key is incorrect. Set error status.
        builder.status = ConnectUser.ConnectUserResponse.Status.ERR_NOT_FOUND
      }
    } else {
      println("User not found")

      // The user could not be found. Set error status.
      builder.status = ConnectUser.ConnectUserResponse.Status.ERR_NOT_FOUND
    }

    socket.send(serializePacket(builder.build()))

    if (builder.status === ConnectUser.ConnectUserResponse.Status.SUC_CONNECTED) {
      println("User $userName has been accepted.")
    } else {
      println("User $userName has been rejected.")
    }
  }

  fun handleRegisterUserRequest(packet: RegisterActorUser.RegisterActorUserRequest, socket: WebSocket) {
    val actor = actorManager.getActor(socket) ?: return
    val userName = packet.user

    // Find the existing user or create a new one.
    val user = users[userName] ?: User(packet.user, String(Base64.getEncoder().encode(UUID.randomUUID().toString().toByteArray())), actor, null)

    synchronized(user) {
      // Remove the user from the actor he is currently connected to.
      user.actor.users.remove(user)

      // Save the user to the new actor.
      users[userName] = user
      user.actor = actor
      user.actor.users.add(user)

      // Send response
      val builder = RegisterActorUser.RegisterActorUserResponse.newBuilder()
      builder.user = user.name
      builder.key = user.key

      socket.send(serializePacket(builder.build()))
    }

    println("User $userName has been registered for actor ${actor.name}.")
  }

  fun handleUnregisterUserRequest(packet: UnregisterActorUser.UnregisterActorUserRequest, socket: WebSocket) {
    val actor = actorManager.getActor(socket) ?: return
    val user = users[packet.user] ?: return

    // Allow this action if the user is connected to this actor only.
    synchronized(user) {
      if (user.actor === actor) {
        unregisterUser(user)
      }
    }
  }

  fun isUserSocket(socket: WebSocket) = socketUsers.containsKey(socket)

  fun getUser(user: String) = users[user]

  fun getUser(socket: WebSocket) = socketUsers[socket]
}
