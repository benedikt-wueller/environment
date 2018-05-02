# environment

Modular sound system based on WebSockets using the user's browser as audio player.


## Terms and Definitions

This section explains terms and definitions used withing this project.

### User / Client

Defines the software or person playing or receiving the sounds to be played and sound updates to be executed. Typically the audio player website or an browser addon.

### Actor

Defines the software sending sounds to be played and updates to be executed to the processor to be delivered to the user. The actor can be a external dashboard or a software communicating with another system (e.g. a Spigot server plugin). 

### Processor / Server

Defines the software processing sound and update requests sent by the actor and forwarding them to the user. It provides two WebSocket servers. One for internal communication and the other for external communication.


## Build

### Update protocol for Kotlin

1. `protoc -I=protocol --java_out=protocol-kotlin:/src/main/java protocol/*.proto`
2. `cd protocol-kotlin`
3. `mvn clean install`

### Update protocol for JavaScript

1. `protoc -I=protocol --js_out=library=environment_protocol,binary:. protocol/*proto`

### Build actor api for Kotlin

1. `cd actor-api-kotlin`
2. `mvn clean install`

### Build processor for Kotlin

1. `cd processor-kotlin`
2. `mvn clean install`


## License

This code is licensed under the Apache 2.0 License. You can grab a copy of it [here](https://www.apache.org/licenses/LICENSE-2.0). The license is also provided in the project.
