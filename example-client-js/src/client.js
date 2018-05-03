import Protocol from './protobuf'

const socket = new WebSocket('ws://localhost:24500')
socket.binaryType = 'arraybuffer'

let connectCallback = null
let playSoundCallback = null
let updateVolumeCallback = null
let updateRateCallback = null

const Client = {
  connected: false,

  init(callback) {
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

  handleSoundStarted(user, sound) {
    const data = Protocol.serialize('PlaySoundResponse', {user: user, sound: sound})
    socket.send(data)
  },

  handleSoundStopped(user, sound) {
    const data = Protocol.serialize('StopSoundResponse', {user: user, sound: sound})
    socket.send(data)
  }
}

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
        packet.payload.volume === undefined ? 0.0 : packet.payload.volume,
        packet.payload.rate === undefined ? 0.0 : packet.payload.rate,
        packet.payload.loop)
  }

  if (packet.type === 'UpdateSoundVolumeRequest' && updateVolumeCallback !== null) {
    updateVolumeCallback(packet.payload.sound,
        packet.payload.volume === undefined ? 0.0 : packet.payload.volume,
        packet.payload.duration === undefined ? 0 : packet.payload.duration)
  }

  if (packet.type === 'UpdateSoundrateRequest' && updateRateCallback !== null) {
    updateRateCallback(packet.payload.sound,
        packet.payload.rate === undefined ? 0.0 : packet.payload.rate)
  }
}

socket.onclose = () => {
  Client.connected = false
  if (connectCallback !== null) connectCallback(false, 3)
}

export default Client
