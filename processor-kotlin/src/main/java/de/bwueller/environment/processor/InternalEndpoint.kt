package de.bwueller.environment.processor

import javax.websocket.OnMessage
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/private")
class InternalEndpoint {

    @OnMessage
    fun onMessage(message: Array<Byte>, session: Session) {

    }
}
