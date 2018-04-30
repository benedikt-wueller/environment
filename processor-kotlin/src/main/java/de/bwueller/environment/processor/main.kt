package de.bwueller.environment.processor

import de.bwueller.environment.processor.internal.InternalWebsocketServer

fun main(args: Array<String>) {
    InternalWebsocketServer(1234)

    while (true) {}
}
