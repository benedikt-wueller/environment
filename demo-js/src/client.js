import Protocol from './protobuf'

let socket = null

let connectCallback = null
let playSoundCallback = null
let stopSoundCallback = null
let updateVolumeCallback = null
let updateRateCallback = null

function doubleValue(value) {
  return value === undefined ? 0.0 : value
}

function intValue(value) {
  return value === undefined ? 0 : value
}

const Client = {
  connected: false,

  init(callback) {
    socket = new WebSocket('ws://localhost:24500')
    socket.binaryType = 'arraybuffer'

    socket.onmessage = (message) => {
      const packet = Protocol.deserialize(new Uint8Array(message.data))

      if (packet.type === 'ConnectUserResponse' && connectCallback !== null) {
        if (packet.payload.status === undefined) {
          connectCallback(true, 0)
        } else {
          connectCallback(false, packet.payload.status)
        }
      }

      if (packet.type === 'PlaySoundRequest' && playSoundCallback !== null) {
        playSoundCallback(packet.payload.user, packet.payload.identifier,
            packet.payload.introSound, packet.payload.mainSound,
            doubleValue(packet.payload.volume), doubleValue(packet.payload.rate),
            packet.payload.loop)
      }

      if (packet.type === 'StopSoundRequest' && stopSoundCallback !== null) {
        stopSoundCallback(packet.payload.user, packet.payload.sound,
            intValue(packet.payload.delay), intValue(packet.payload.duration))
      }

      if (packet.type === 'UpdateSoundVolumeRequest' && updateVolumeCallback !== null) {
        updateVolumeCallback(packet.payload.sound,
            doubleValue(packet.payload.volume), intValue(packet.payload.duration))
      }

      if (packet.type === 'UpdateSoundrateRequest' && updateRateCallback !== null) {
        updateRateCallback(packet.payload.sound, doubleValue(packet.payload.rate))
      }
    }

    socket.onclose = () => {
      Client.connected = false
      if (connectCallback !== null) connectCallback(false, 3)
    }

    Protocol.init(callback)
  },

  connect(user, key, callback) {
    if (this.connected || !socket.isOpen) callback(false, 3)
    connectCallback = callback

    const data = Protocol.serialize('ConnectUserRequest', {user: user, key: key})
    socket.send(data)
  },

  setPlaySoundCallback(callback) {
    playSoundCallback = callback
  },

  setUpdateVolumeCallback(callback) {
    updateVolumeCallback = callback
  },

  setUpdateRateCallback(callback) {
    updateRateCallback = callback
  },

  setStopSoundCallback(callback) {
    stopSoundCallback = callback
  },

  handleSoundStarted(user, sound) {
    const data = Protocol.serialize('PlaySoundResponse', {user: user, sound: sound})
    socket.send(data)
  },

  handleSoundStopped(user, sound) {
    const data = Protocol.serialize('StopSoundResponse', {user: user, sound: sound})
    socket.send(data)
  }
}

export default Client
