# environment
Modular sound system based on WebSockets using the user's browser as audio player.

## Build

### Update protocol for java

1. `protoc -I=protocol --java_out=protocol-java/src/main/java protocol/*.proto`
2. `cd protocol-java`
3. `mvn clean install`
