package de.bwueller.environment.processor.user

import de.bwueller.environment.processor.actor.Actor
import jdk.incubator.http.WebSocket

data class User(val name: String, val key: String, var actor: Actor, var socket: WebSocket?)
