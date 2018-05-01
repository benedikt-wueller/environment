package de.bwueller.environment.processor.actor

import com.google.protobuf.GeneratedMessageV3
import de.bwueller.environment.processor.userManager
import de.bwueller.environment.protocol.PlaySound
import de.bwueller.environment.protocol.RegisterActor
import de.bwueller.environment.protocol.serializePacket
import org.java_websocket.WebSocket
import java.util.*

class ActorManager {

  private val actors = mutableMapOf<String, Actor>()
  private val socketActors = mutableMapOf<WebSocket, Actor>()

  /**
   * Handles a request to register a new actor.
   *
   * @param packet the packet to parse.
   * @param socket the WebSocket connection to reply to.
   */
  fun handleRegisterActorRequest(packet: RegisterActor.RegisterActorRequest, socket: WebSocket) {
    val name = packet.name
    val packetMeta = packet.metaList

    val meta = mutableMapOf<Int, String>()
    packetMeta.forEach { entry -> meta[entry.identifier] = entry.value }

    val responseBuilder = RegisterActor.RegisterActorResponse.newBuilder()

    val actor: Actor
    if (actors.containsKey(name)) {
      // An actor with the same name is already connected.
      actor = actors[name]!!
      responseBuilder.status = RegisterActor.RegisterActorResponse.Status.ERR_ALREADY_CONNECTED
    } else {
      actor = Actor(UUID.randomUUID(), name, meta, socket)
      responseBuilder.status = RegisterActor.RegisterActorResponse.Status.SUC_CONNECTED

      actors[actor.name] = actor
      socketActors[socket] = actor
    }

    // Send response.
    responseBuilder.identifier = actor.uuid.toString()
    socket.send(serializePacket(responseBuilder.build()))
    println("Actor ${actor.name}(${actor.uuid}) has been registered.")
  }

  /**
   * Handles the request to unregister an actor. Typically called when a WebSocket connection to an actor is closed.
   *
   * @param socket the WebSocket to close the actor for.
   */
  fun unregisterActor(socket: WebSocket) {
    val actor = socketActors.remove(socket) ?: return
    actors.remove(actor.name)

    if (actor.users.isNotEmpty()) {
      synchronized(actor) {
        for (i in actor.users.size - 1 downTo 0) {
          userManager.unregisterUser(actor.users[i])
        }
      }
    }

    println("Actor ${actor.name}(${actor.uuid}) has been unregistered.")
  }

  fun isActorSocket(socket: WebSocket) = socketActors.containsKey(socket)

  fun getActor(socket: WebSocket) = socketActors[socket]
}
