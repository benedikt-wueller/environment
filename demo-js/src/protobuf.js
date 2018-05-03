export default {

  initialized: false,

  packets: {},

  packetIds: {
    'ConnectUserRequest': 5,
    'ConnectUserResponse': 6,
    'PlaySoundRequest': 9,
    'PlaySoundResponse': 10,
    'StopSoundRequest': 11,
    'StopSoundResponse': 12,
    'UpdateSoundVolumeRequest': 13,
    'UpdateSoundRateRequest': 14
  },

  init (callback) {
    const protobuf = require("protobufjs")

    const packetTypes = {
      'connect_user': [
        'ConnectUserRequest',
        'ConnectUserResponse'
      ],
      'play_sound': [
        'PlaySoundRequest',
        'PlaySoundResponse'
      ],
      'stop_sound': [
        'StopSoundRequest',
        'StopSoundResponse'
      ],
      'update_sound_volume': [
        'UpdateSoundVolumeRequest'
      ],
      'update_sound_rate': [
        'UpdateSoundRateRequest'
      ]
    }

    const packets = {}

    for (let i = 0; i < Object.keys(packetTypes).length; i++) {
      let done = false

      protobuf.load('protocol/' + Object.keys(packetTypes)[i] + '.proto', (err, root) => {
        done = true

        const types = packetTypes[Object.keys(packetTypes)[i]]
        for (let j = 0; j < types.length; j++) {
          packets[types[j]] = root.lookupType(types[j])
        }

        if (i === Object.keys(packetTypes).length - 1) {
          this.packets = packets
          this.initialized = true
          callback()
        }
      })
    }
  },

  serialize (type, payload) {
    const messageType = this.packets[type]
    const message = messageType.create(payload)
    const messageBytes = messageType.encode(message).finish()

    const result = new Uint8Array(messageBytes.length + 1)
    result.set([this.packetIds[type]], 0)
    result.set(messageBytes, 1)

    return result
  },

  deserialize (data) {
    const typeId = data[0]
    let type = null

    for (let i = 0; i < Object.keys(this.packetIds).length; i++) {
      if (typeId === this.packetIds[Object.keys(this.packetIds)[i]]) {
        type = Object.keys(this.packetIds)[i]
      }
    }

    if (type === null) return null

    const message = data.slice(1, data.length)
    const messageType = this.packets[type]

    return {
      type: type,
      payload: messageType.toObject(messageType.decode(message))
    }
  }
}
