<template>
  <div id="app">
    <section class="hero is-dark">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">
            <small style="font-weight: 200">demo.</small>ENVIRONMENT
            <span class="is-pulled-right" style="font-weight: 200;">CLIENT</span>
          </h1>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <b-notification v-if="connectErrorCode === 0" type="is-success" :closable="false">
          You are connected as <b>{{ user }}</b>.
        </b-notification>

        <b-notification v-if="connectErrorCode === 1" type="is-danger" :closable="false">
          The given user is already connected to the processor.
        </b-notification>

        <b-notification v-if="connectErrorCode === 2" type="is-danger" :closable="false">
          The user <b>{{ user }}</b> could not be found or the key is incorrect.
        </b-notification>

        <b-notification v-if="connectErrorCode === 3" type="is-success" :closable="false">
          You are connected as {{ user }}
        </b-notification>

        <div class="columns is-multiline">
          <div v-for="sound in sounds" v-bind:key="sound.id" class="column is-half-tablet">
            <div :class="{message: true, 'is-success': sound.howl.playing(), 'is-warning': !sound.howl.playing()}">
              <div class="message-body">
                <div class="is-clearfix">
                  <b>{{ sound.name }}</b>
                  <div class="is-pulled-right">
                    <span class="tag"><b>Rate:</b>&nbsp;{{ Math.ceil(sound.howl.rate() * 100) }}%</span>&nbsp;<span class="tag"><b>Volume:</b>&nbsp;{{ Math.ceil(sound.howl.volume() * 100) }}%</span>
                  </div>
                </div>
                <small><small>{{ sound.id }}</small></small><br/>
                <small><small>{{ sound.license }}</small></small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
  import Client from './client'
  import BNotification from "buefy/src/components/notification/Notification";

  import {Howl, Howler} from 'howler'

  export default {
    components: {BNotification},
    name: 'app',

    props: {
      connected: false,
      connectErrorCode: -1,

      user: null,
      secret: null,

      sounds: [],
    },

    mounted() {
      this.user = this.findGetParameter('user')
      this.secret = this.findGetParameter('key')

      if (this.user === null || this.secret === null) {

        return
      }

      Client.init(() => {
        Client.setUpdateVolumeCallback((identifier, volume, duration) => {
          const obj = this.sounds.filter((item) => {
            return item.id === identifier
          })[0]

          obj.howl.fade(obj.howl.volume(), volume, duration)
        })

        Client.setPlaySoundCallback((user, identifier, introSound, mainSound, volume, rate, loop) => {
          let sound = null

          this.$http.get('/sounds/' + mainSound.split('.').join('/') + '.json').then((data) => {
            sound = new Howl({
              src: data.body.sources,
              loop: loop,
              volume: volume,
              rate: rate
            });

            sound.once('load', () => {
              sound.play()
              Client.handleSoundStarted(user, identifier)

              const obj = {
                id: identifier,
                name: (introSound === undefined ? '' : (introSound + '/')) + mainSound,
                license: data.body.license,
                howl: sound,
                status: 'playing'
              }

              this.sounds.push(obj)
            })

            sound.once('end', () => {
              Client.handleSoundStopped(user, identifier)
            })
          })
        })

        Client.connect(this.user, this.secret, (connected, code) => {
          if (!this.connected && connected) {
            this.sounds = []
          }

          this.connectErrorCode = code
          this.connected = connected
        })
      })
    },

    methods: {
      findGetParameter(parameterName) {
        let result = null, tmp = []
        const items = location.search.substr(1).split("&");
        for (let index = 0; index < items.length; index++) {
          tmp = items[index].split("=");
          if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        }
        return result;
      }
    }
  }
</script>

<style>
  html, body, #app {
    height: 100%;
    box-sizing: border-box;
    overflow: auto;
  }
</style>
