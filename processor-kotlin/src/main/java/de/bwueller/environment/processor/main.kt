package de.bwueller.environment.processor

import de.bwueller.environment.processor.actor.ActorManager
import de.bwueller.environment.processor.user.UserManager

// TODO: implement logger

val actorManager = ActorManager()
val userManager = UserManager()

fun main(args: Array<String>) {
    InternalWebsocketServer(1234)

    while (true);
}
