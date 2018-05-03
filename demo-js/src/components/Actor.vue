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
                (i.e. using firewalls). In this case, the processor is open to use. However, it has been
                modified to only allow specific actions performed by the registered actors. Feel free to
                create custom actors and test them against the demo processor <code>ws://localhost:24499</code>.
                <b>Select a name for this demo actor and connect it to the processor.</b>
              </p>
            </div>

            <b-notification v-if="initialized && !connected" type="is-danger" :closable="false">
              The actor could not be connected to the processor. It is offline or unreachable. Please try again later by refreshing this page.
            </b-notification>

            <div class="field is-grouped" v-if="initialized && connected">
              <p class="control is-expanded">
                <input class="input" :disabled="registered" type="text" v-model="name" placeholder="Name your actor">
              </p>
              <p class="control">
                <button class="button is-info" :disabled="name.length <= 0 || !connected || registered" @click="connect()">Connect</button>
              </p>
            </div>
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
                identify as himself. <b>Select a username and register him to the processor.</b>
              </p>
            </div>

            <div class="field is-grouped">
              <p class="control is-expanded">
                <input class="input" :disabled="userRegistered" type="text" v-model="username" placeholder="Name your actor">
              </p>
              <p class="control">
                <button class="button is-info" :disabled="userRegistered" @click="registerUser()">Register</button>
              </p>
              <p class="control">
                <button class="button is-info" :disabled="!userRegistered" @click="unregisterUser()">Unregister</button>
              </p>
            </div>
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
                to simulate this behaviour.</b>
              </p>
            </div>
          </div>

          <div class="column is-full" v-if="userConnected">
            <h3 class="title is-3">Fourth step</h3>
            <h4 class="subtitle is-5">Execute sound actions</h4>

            <div class="content">
              <div class="columns is-multiline">
                <div v-for="sound in sounds" v-bind:key="sound.identifier" class="column is-half-tablet">
                  <div :class="{message: true, 'is-success': sound.confirmed, 'is-danger': !sound.confirmed}">
                    <div class="message-body">
                      <div class="is-clearfix">
                        <b>{{ sound.mainSound }}</b>
                        <div class="is-pulled-right">
                          <span class="tag"><b>Rate:</b>&nbsp;{{ Math.ceil(sound.rate * 100) }}%</span>&nbsp;<span class="tag"><b>Volume:</b>&nbsp;{{ Math.ceil(sound.volume * 100) }}%</span>
                        </div>
                      </div>
                      <small><small>{{ sound.identifier }}</small></small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
  import BNotification from "buefy/src/components/notification/Notification";
  import Actor from "../actor";

  export default {
    components: {BNotification},
    name: "actor",

    data() {
      return {
        initialized: false,
        registered: false,
        connected: false,

        errorCode: 0,

        name: '',
        id: '',

        username: 'Username',
        userRegistered: false,
        userConnected: false,
        secret: null,
        sounds: [],

        clientWindow: null,
      }
    },

    mounted() {
      Actor.initialize((connected) => {
        this.name = btoa(this.guid())
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
          } else {
            this.playSound(user, this.guid(), null, 'soundbible.sawing', 0.0, 1.0, false)
          }
        })

        Actor.setPlaySoundCallback((user, identifier) => {
          const sound = this.sounds.filter((item) => {
            return item.identifier === identifier
          })[0]

          sound.confirmed = true
          Actor.updateVolume(sound.identifier, 1.0, 2500)
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

      playSound(user, identifier, introSound, mainSound, volume, rate, loop) {
        const sound = {
          identifier: identifier,
          name: (introSound === undefined ? '' : (introSound + ' / ')) + mainSound,
          volume: volume,
          rate: rate,
          confirmed: false,
        }

        this.sounds.push(sound);
        Actor.playSound(user, identifier, introSound, mainSound, volume, rate, loop)
        return sound.identifier
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