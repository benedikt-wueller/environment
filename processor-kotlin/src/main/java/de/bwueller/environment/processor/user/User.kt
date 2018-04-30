package de.bwueller.environment.processor.user

import de.bwueller.environment.processor.actor.Actor
import org.java_websocket.WebSocket

data class User(val name: String, val key: String, var actor: Actor, var socket: WebSocket?)
