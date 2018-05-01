package de.bwueller.environment.processor

import de.bwueller.environment.processor.actor.ActorManager
import de.bwueller.environment.processor.user.UserManager

// TODO: implement logger

val actorManager = ActorManager()
val userManager = UserManager()
val soundManager = SoundManager()

fun main(args: Array<String>) {
  InternalWebsocketServer(24499)
  ExternalWebsocketServer(24500)

  while (true);
}
