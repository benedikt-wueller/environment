package de.bwueller.environment.processor

import org.java_websocket.WebSocket
import java.util.*

data class Actor(val uuid: UUID, val name: String, val meta: Map<Int, String>, val socket: WebSocket, val users: MutableList<User> = mutableListOf())
