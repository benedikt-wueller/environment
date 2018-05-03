import Protocol from './protobuf'

let socket = null

let connectCallback = null
let userCallback = null
let clientCallback = null
let playSoundCallback = null
let stopSoundCallback = null

function doubleValue(value) {
  return value === undefined ? 0.0 : value
}

function intValue(value) {
  return value === undefined ? 0 : value
}

const Actor = {
  connected: false,
  registered: false,

  initialize(callback) {
    socket = new WebSocket('ws://localhost:24499')
    socket.binaryType = 'arraybuffer'

    socket.onmessage = (message) => {
      const packet = Protocol.deserialize(new Uint8Array(message.data))

      if (packet.type === 'RegisterActorResponse') {
        const status = intValue(packet.payload.status)
        this.registered = true
        connectCallback(status === 0, packet.payload.identifier, status)
      }

      if (packet.type === 'RegisterActorUserResponse' && userCallback !== null) {
        userCallback(packet.payload.user, packet.payload.key)
      }

      if (packet.type === 'UpdateUserStatusRequest' && clientCallback !== null) {
        clientCallback(packet.payload.user, intValue(packet.payload.status) === 0)
      }

      if (packet.type === 'PlaySoundResponse' && playSoundCallback !== null) {
        playSoundCallback(packet.payload.user, packet.payload.identifier)
      }

      if (packet.type === 'StopSoundResponse' && stopSoundCallback !== null) {
        stopSoundCallback(packet.payload.user, packet.payload.sound)
      }
    }

    socket.onopen = () => {
      Actor.connected = true

      Protocol.init(() => {
        callback(true)
      })
    }

    socket.onclose = () => {
      Actor.connected = false
      Actor.registered = false

      callback(false)
    }
  },

  connect(name, callback) {
    if (!this.connected) {
      callback(false, null, 2)
      return
    }

    connectCallback = callback

    const data = Protocol.serialize('RegisterActorRequest', {name: name})
    socket.send(data)
  },

  registerUser(username, callback) {
    userCallback = callback

    const data = Protocol.serialize('RegisterActorUserRequest', {user: username})
    socket.send(data)
  },

  unregisterUser(username) {
    const data = Protocol.serialize('UnregisterActorUserRequest', {user: username})
    socket.send(data)
  },

  setClientCallback(callback) {
    clientCallback = callback
  },

  setPlaySoundCallback(callback) {
    playSoundCallback = callback
  },

  setStopSoundCallback(callback) {
    stopSoundCallback = callback
  },

  playSound(user, identifier, introSound, mainSound, volume, rate, loop) {
    const data = Protocol.serialize('PlaySoundRequest', {
      user: user,
      identifier: identifier,
      introSound: introSound,
      mainSound: mainSound,
      volume: volume,
      rate: rate,
      loop: loop
    })

    socket.send(data)
  },

  stopSound(user, identifier, delay, duration) {
    const data = Protocol.serialize('StopSoundRequest', {
      user: user,
      sound: identifier,
      delay: delay,
      duration: duration
    })

    socket.send(data)
  },

  updateVolume(identifier, volume, duration) {
    const data = Protocol.serialize('UpdateSoundVolumeRequest', {
      sound: identifier,
      volume: volume,
      duration: duration
    })

    socket.send(data)
  },

  updateRate(identifier, rate) {
    const data = Protocol.serialize('UpdateSoundRateRequest', {
      sound: identifier,
      rate: rate
    })

    socket.send(data)
  }
}

export default Actor
