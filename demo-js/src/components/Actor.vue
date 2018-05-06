<template>
  <div id="actor">
    <section class="hero is-dark">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">
            <small style="font-weight: 200">demo.</small>ENVIRONMENT
            <div class="is-pulled-right">
              <span style="font-weight: 200">ACTOR</span>
            </div>
          </h1>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="columns is-multiline">
          <div class="column is-full">
            <h3 class="title is-3">First step</h3>
            <h4 class="subtitle is-5">Connect the actor</h4>

            <b-notification v-if="registered" type="is-success" :closable="false">
              The actor has successfully been connected with the id <b>{{ id }}</b>.
            </b-notification>

            <div class="content" v-if="!registered">
              <p class="has-text-justified">
                To test any of the features you have to connect the actor to the processor. Normally the
                internal processor port <code>24499</code> would be blocked for any non-owned actors
                (i.e. using firewalls). In this case, the processor is open to use. However, it is not
                recommended for use in a production environment as it is subject to constant change
                and unexpected downtime. Feel free to create custom actors and clients and test them
                against the demo processor <code>ws://root.bwueller.de:24499</code>. <b>Select a unique name for this
                demo actor and connect it to the processor.</b>
              </p>
            </div>

            <b-notification v-if="initialized && !connected" type="is-danger" :closable="false">
              The actor could not be connected to the processor. It is offline or unreachable. Please try again later by refreshing this page.
            </b-notification>

            <b-field :grouped="true" v-if="initialized && connected">
              <p class="control is-expanded">
                <input class="input" :disabled="registered" type="text" v-model="name" placeholder="Name your actor">
              </p>
              <p class="control">
                <button class="button is-info" :disabled="name.length <= 0 || !connected || registered" @click="connect()">Connect</button>
              </p>
            </b-field>
          </div>

          <div class="column is-full" v-if="registered">
            <h3 class="title is-3">Second step</h3>
            <h4 class="subtitle is-5">Register a user</h4>

            <b-notification v-if="userRegistered" type="is-success" :closable="false">
              The user <b>{{ username }}</b> has been registered with the key <b>{{ secret }}</b>.
            </b-notification>

            <div class="content" v-if="!userRegistered">
              <p class="has-text-justified">
                An actor is responsible for registering users to play sounds and send sound updates. The
                processor will generate a unique key and send it back to the actor. The actor will
                forward the key to the user (and only this user) for him to use it to connect to the
                processor (using the client). This ensures that only the privileged user is able to
                identify as himself. <b>Select a username and register him to the processor.</b> Choose
                a custom name as the same username can only be registered by one actor at a time.
              </p>
            </div>

            <b-field :grouped="true">
              <p class="control is-expanded">
                <input class="input" :disabled="userRegistered" type="text" v-model="username" placeholder="Name your actor">
              </p>
              <p class="control">
                <button class="button is-info" :disabled="username.length === 0 || userRegistered" @click="registerUser()">Register</button>
              </p>
              <p class="control">
                <button class="button is-info" :disabled="!userRegistered" @click="unregisterUser()">Unregister</button>
              </p>
            </b-field>
          </div>

          <div class="column is-full" v-if="userRegistered">
            <h3 class="title is-3">Third step</h3>
            <h4 class="subtitle is-5">Connect user to processor</h4>

            <b-notification v-if="userConnected" type="is-success" :closable="false">
              The client is connected.
            </b-notification>

            <b-notification v-if="!userConnected" type="is-danger" :closable="false">
              The client is not connected.
            </b-notification>

            <div class="content" v-if="!userConnected">
              <p class="has-text-justified">
                Normally the actor would forward the username and key to the user. The user would then
                connect to the processor using a client (e.g. via a website) which identifies as the
                user using the given credentials. The actor will be notified when the client connects to
                or disconnects from the processor. <b>Open the <a @click="openClient()">Demo Client</a>
                to simulate this behaviour.</b> It should open in a new window. If it just opens in a
                new tab, the functionality could be delayed but should work just fine.
              </p>
            </div>
          </div>

          <div class="column is-full" v-if="userConnected">
            <h3 class="title is-3">Fourth step</h3>
            <h4 class="subtitle is-5">Execute sound actions</h4>

            <div class="content">
              <p class="has-text-justified" v-if="!hasRequested">
                With the registered user connected you can now execute sound actions such as playing
                and stopping sounds and changing their volume and playback rate. After requesting a
                sound it will be highlighted in red until it has been confirmed by the client. After
                stopping a sound it will no longer be shown. <b>Request a sound and see the action
                happen!</b>
              </p>
            </div>

            <div class="content">
              <button class="button is-info" @click="requestPlaySoundModal()">Play Sound</button>
            </div>

            <div class="content">
              <div class="columns is-multiline">
                <div v-for="sound in sounds" v-bind:key="sound.identifier" class="column is-half-tablet">
                  <div :class="{message: true, 'is-success': sound.confirmed, 'is-danger': !sound.confirmed}">
                    <div class="message-body">
                      <div class="is-clearfix">
                        <b>{{ sound.name }}</b>
                        <div class="is-pulled-right">
                          <span class="tag"><b>Rate:</b>&nbsp;{{ Math.ceil(sound.rate * 100) }}%</span>&nbsp;<span class="tag"><b>Volume:</b>&nbsp;{{ Math.ceil(sound.volume * 100) }}%</span>
                        </div>
                      </div>

                      <div class="content">
                        <small><small>{{ sound.identifier }}</small></small>
                      </div>

                      <p>
                        <button v-if="sound.confirmed && !sound.stopping" class="button is-info is-small is-danger" @click="() => requestStopSoundModal(sound.identifier)">Stop</button>
                        &nbsp;
                        <button v-if="sound.confirmed && !sound.stopping" class="button is-info is-small is-info" @click="() => requestUpdateVolumeModal(sound.identifier)">Update Volume</button>
                        &nbsp;
                        <button v-if="sound.confirmed && !sound.stopping" class="button is-info is-small is-info" @click="() => requestUpdateRateModal(sound.identifier)">Update Rate</button>
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <b-modal :active="showPlaySoundModal" :has-modal-card="false" @close="() => {this.showPlaySoundModal = false}">
      <div class="modal-card">
        <div class="modal-card-head">
          <span class="modal-card-title">Play a new sound</span>
        </div>
        <div class="modal-card-body">
          <div class="columns">
            <div class="column is-half">
              <b-field label="Intro Sound">
                <b-select v-model="introSound">
                  <option :value="null">No Intro</option>
                  <option v-for="sound in Object.keys(availableSounds)" v-bind:key="'intro_' + sound" :value="sound">{{ availableSounds[sound] }}</option>
                </b-select>
              </b-field>
            </div>

            <div class="column is-half">
              <b-field label="Main Sound">
                <b-select v-model="mainSound">
                  <option v-for="sound in Object.keys(availableSounds)" v-bind:key="'main_' + sound" :value="sound">{{ availableSounds[sound] }}</option>
                </b-select>
              </b-field>
            </div>
          </div>

          <div class="columns">
            <div class="column is-one-third">
              <b-field label="Initial Volume (%)" :message="'Volume Factor: ' + volume">
                <input class="input" type="number" min="0" max="100" step="1" v-model="volumePercentage" />
              </b-field>
            </div>

            <div class="column is-one-third">
              <b-field label="Initial Rate (%)" :message="'Rate Factor: ' + rate">
                <input class="input" type="number" min="50" max="400" step="1" v-model="ratePercentage" />
              </b-field>
            </div>

            <div class="column is-one-third">
              <b-field label="Repeating">
                <b-checkbox v-model="looping">Loop sound</b-checkbox>
              </b-field>
            </div>
          </div>
        </div>
        <div class="modal-card-foot">
          <button class="button is-info" @click="playSound()">Play Sound</button>
        </div>
      </div>
    </b-modal>

    <b-modal :active="showStopSoundModal" :has-modal-card="false" @close="() => {this.showStopSoundModal = false}">
      <div v-if="stoppingSound" class="modal-card">
        <div class="modal-card-head">
          <span class="modal-card-title">Stop sound: <b>{{ stoppingSound.name }}</b></span>
        </div>
        <div class="modal-card-body">
          <div class="columns">
            <div class="column is-half">
              <b-field label="Delay" message="Stop after the given time in milliseconds.">
                <input class="input" type="number" min="0" step="1" v-model="stopDelay">
              </b-field>
            </div>

            <div class="column is-half">
              <b-field label="Fade Duration" message="Fade out the sound after the given delay.">
                <input class="input" type="number" min="0" step="1" v-model="stopDuration">
              </b-field>
            </div>
          </div>
        </div>
        <div class="modal-card-foot">
          <button class="button is-danger" @click="stopSound()">Stop Sound</button>
        </div>
      </div>
    </b-modal>

    <b-modal :active="showUpdateVolumeModal" :has-modal-card="false" @close="() => {this.showUpdateVolumeModal = false}">
      <div v-if="updateSound !== null" class="modal-card">
        <div class="modal-card-head">
          <span class="modal-card-title">Update volume for <b>{{ updateSound.name }}</b></span>
        </div>
        <div class="modal-card-body">
          <div class="columns">
            <div class="column is-half">
              <b-field label="Volume (%)" :message="'Volume Factor: ' + updatedVolume">
                <input class="input" type="number" min="0" max="100" step="1" v-model="updateVolumePercentage" />
              </b-field>
            </div>

            <div class="column is-half">
              <b-field label="Fade Duration"  message="Fade the sound in the given amount of time in milliseconds.">
                <input class="input" type="number" min="0" step="1" v-model="updateVolumeDuration" />
              </b-field>
            </div>
          </div>
        </div>
        <div class="modal-card-foot">
          <button class="button is-info" @click="updateVolume()">Update Volume</button>
        </div>
      </div>
    </b-modal>

    <b-modal :active="showUpdateRateModal" :has-modal-card="false" @close="() => {this.showUpdateRateModal = false}">
      <div v-if="updateSound !== null" class="modal-card">
        <div class="modal-card-head">
          <span class="modal-card-title">Update rate for <b>{{ updateSound.name }}</b></span>
        </div>
        <div class="modal-card-body">
          <div class="columns">
            <div class="column is-full">
              <b-field label="Playback Rate (%)" :message="'Rate Factor: ' + updatedRate">
                <input class="input" type="number" min="50" max="400" step="1" v-model="updateRatePercentage" />
              </b-field>
            </div>
          </div>
        </div>
        <div class="modal-card-foot">
          <button class="button is-info" @click="updateRate()">Update Rate</button>
        </div>
      </div>
    </b-modal>
  </div>
</template>

<script>
  import BNotification from "buefy/src/components/notification/Notification";
  import Actor from "../actor";
  import BModal from "buefy/src/components/modal/Modal";
  import BSelect from "buefy/src/components/select/Select";
  import BField from "buefy/src/components/field/Field";
  import BInput from "buefy/src/components/input/Input";
  import BCheckbox from "buefy/src/components/checkbox/Checkbox";

  export default {
    components: {
      BCheckbox,
      BInput,
      BField,
      BSelect,
      BModal,
      BNotification},
    name: "actor",

    data() {
      return {
        availableSounds: {
          'mac_leod.frozen_star': 'Frozen Star (Kevin MacLeod)',
          'soundbible.sawing': 'Sawing (Soundbible)',
          'soundbible.waterfall': 'Waterfall (Soundbible)'
        },

        hasRequested: false,

        initialized: false,
        registered: false,
        connected: false,

        errorCode: 0,

        name: '',
        id: '',

        username: '',
        userRegistered: false,
        userConnected: false,
        secret: null,
        sounds: [],

        clientWindow: null,

        showPlaySoundModal: false,
        introSound: null,
        mainSound: 'mac_leod.frozen_star',
        volumePercentage: 100,
        ratePercentage: 100,
        looping: false,

        showStopSoundModal: false,
        stoppingSound: null,
        stopDelay: 0,
        stopDuration: 0,

        updateSound: null,

        showUpdateVolumeModal: false,
        updateVolumePercentage: 100,
        updateVolumeDuration: 0,

        showUpdateRateModal: false,
        updateRatePercentage: 100,
      }
    },

    computed: {
      volume() {
        return this.volumePercentage / 100.0
      },
      rate() {
        return this.ratePercentage / 100.0
      },
      updatedVolume() {
        return this.updateVolumePercentage / 100.0
      },
      updatedRate() {
        return this.updateRatePercentage / 100.0
      }
    },

    mounted() {
      Actor.initialize((connected) => {
        this.connected = connected
        this.registered = false
        this.initialized = true
      })
    },

    methods: {
      connect() {
        Actor.connect(this.name, (registered, id, code) => {
          this.registered = registered
          this.id = id
          this.errorCode = code
        })
      },

      registerUser() {
        Actor.setClientCallback((user, connected) => {
          this.userConnected = connected

          if (!connected) {
            this.sounds = []
            this.showPlaySoundModal = false;
          }
        })

        Actor.setPlaySoundCallback((user, identifier) => {
          const sound = this.sounds.filter((item) => {
            return item.identifier === identifier
          })[0]

          sound.confirmed = true
        })

        Actor.setStopSoundCallback((user, identifier) => {
          const sound = this.sounds.filter((item) => {
            return item.identifier === identifier
          })[0]

          this.sounds.splice(this.sounds.indexOf(sound), 1)
        })

        Actor.registerUser(this.username, (user, key) => {
          this.username = user
          this.secret = key
          this.userRegistered = true
          this.userConnected = false
        })
      },

      unregisterUser() {
        this.clientWindow.close()
        this.userRegistered = false
        this.userConnected = false
        Actor.unregisterUser(this.username)
      },

      openClient() {
        this.clientWindow = window.open('/client/' + this.username + '/' + this.secret, '_blank', 'toolbar=0,location=0,menubar=0')
      },

      requestPlaySoundModal() {
        this.showPlaySoundModal = true
      },

      playSound() {
        this.showPlaySoundModal = false
        this.hasRequested = true

        const sound = {
          identifier: this.guid(),
          name: (this.introSound === null ? '' : (this.introSound + ' / ')) + this.mainSound,
          volume: this.volume,
          rate: this.rate,
          confirmed: false,
          stopping: false,
        }

        this.sounds.push(sound);
        Actor.playSound(this.username, sound.identifier, this.introSound, this.mainSound, sound.volume, sound.rate, this.looping)
        return sound.identifier
      },

      requestStopSoundModal(identifier) {
        const sound = this.sounds.filter((item) => {
          return item.identifier === identifier
        })[0]

        this.stoppingSound = sound
        this.stopDelay = 0
        this.stopDuration = 0
        this.showStopSoundModal = true
      },

      stopSound() {
        this.showStopSoundModal = false
        this.stoppingSound.stopping = true

        Actor.stopSound(this.username, this.stoppingSound.identifier, this.stopDelay, this.stopDuration)
      },

      requestUpdateVolumeModal(identifier) {
        const sound = this.sounds.filter((item) => {
          return item.identifier === identifier
        })[0]

        this.updateSound = sound
        this.updateVolumePercentage = 100
        this.updateVolumeDuration = 0
        this.showUpdateVolumeModal = true
      },

      updateVolume() {
        this.updateSound.volume = this.updatedVolume
        this.showUpdateVolumeModal = false

        Actor.updateVolume(this.updateSound.identifier, this.updateSound.volume, this.updateVolumeDuration)
      },

      requestUpdateRateModal(identifier) {
        const sound = this.sounds.filter((item) => {
          return item.identifier === identifier
        })[0]

        this.updateSound = sound
        this.updateRatePercentage = 100
        this.showUpdateRateModal = true
      },

      updateRate() {
        this.updateSound.rate = this.updatedRate
        this.showUpdateRateModal = false

        Actor.updateRate(this.updateSound.identifier, this.updateSound.rate)
      },


      guid() {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
          .toString(16)
          .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
      }
    }
  }
</script>

<style scoped>

</style>