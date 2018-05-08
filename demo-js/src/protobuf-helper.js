export default {

  initialized: false,

  packets: {},

  packetIds: {
    'RegisterActorRequest': 0,
    'RegisterActorResponse': 1,
    'RegisterActorUserRequest': 2,
    'RegisterActorUserResponse': 3,
    'UnregisterActorUserRequest': 4,
    'ConnectUserRequest': 5,
    'ConnectUserResponse': 6,
    'UpdateUserStatusRequest': 7,
    'UpdateActorUserMetaRequest': 8,
    'PlaySoundRequest': 9,
    'PlaySoundResponse': 10,
    'StopSoundRequest': 11,
    'StopSoundResponse': 12,
    'UpdateSoundVolumeRequest': 13,
    'UpdateSoundRateRequest': 14
  },

  initialize (callback) {
    const protobuf = require("protobufjs")

    const packetTypes = {
      'register_actor': [
        'RegisterActorRequest',
        'RegisterActorResponse'
      ],
      'register_actor_user': [
        'RegisterActorUserRequest',
        'RegisterActorUserResponse'
      ],
      'unregister_actor_user': [
        'UnregisterActorUserRequest'
      ],
      'connect_user': [
        'ConnectUserRequest',
        'ConnectUserResponse'
      ],
      'update_user_status': [
        'UpdateUserStatusRequest'
      ],
      'update_actor_user_meta': [
        'UpdateActorUserMetaRequest'
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
    let toBeLoaded = Object.keys(packetTypes).length

    for (let i = 0; i < Object.keys(packetTypes).length; i++) {
      protobuf.load('/protocol/' + Object.keys(packetTypes)[i] + '.proto', (err, root) => {
        const types = packetTypes[Object.keys(packetTypes)[i]]
        for (let j = 0; j < types.length; j++) {
          packets[types[j]] = root.lookupType(types[j])
        }

        toBeLoaded--;
        if (toBeLoaded <= 0) {
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
