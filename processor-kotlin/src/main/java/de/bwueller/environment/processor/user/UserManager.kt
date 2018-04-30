package de.bwueller.environment.processor.user

import de.bwueller.environment.processor.actorManager
import de.bwueller.environment.protocol.RegisterActorUser
import de.bwueller.environment.protocol.UnregisterActorUser
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
        user.actor.users.remove(user)

        println("User ${user.name} has been unregistered from actor ${user.actor.name}.")

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

    fun handleRegisterUserRequest(packet: RegisterActorUser.RegisterActorUserRequest, socket: WebSocket) {
        val actor = actorManager.getActor(socket) ?: return
        val userName = packet.user

        // Find the existing user or create a new one.
        val user = users[userName] ?: User(packet.user, UUID.randomUUID().toString(), actor, null)

        synchronized(user) {
            // Remove the user from the actor he is currently connected to.
            user.actor.users.remove(user)

            // Save the user to the new actor.
            users[userName] = user
            user.actor = actor

            // Send response
            val builder = RegisterActorUser.RegisterActorUserResponse.newBuilder()
            builder.user = user.name
            builder.key = user.key
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
}