<template>
  <div id="client">
    <div style="flex: 1">
      <env-header part="CLIENT"></env-header>

      <section class="section">
        <div class="container">
          <b-notification v-if="connectErrorCode === -1" type="is-info" :closable="false">
            Go to the <router-link to="/actor">Demo Actor</router-link> and create a new temporary demo user to test
            all the features of environment.
          </b-notification>

          <b-notification v-if="connectErrorCode === 0" type="is-success" :closable="false">
            You are connected as <b>{{ user }}</b>.
          </b-notification>

          <b-notification v-if="connectErrorCode === 1" type="is-danger" :closable="false">
            The given user is already connected to the processor.
          </b-notification>

          <b-notification v-if="connectErrorCode === 2" type="is-danger" :closable="false">
            The user <b>{{ user }}</b> could not be found or the key is incorrect.
          </b-notification>

          <b-notification v-if="connectErrorCode === 3" type="is-danger" :closable="false">
            The processor could not be reached or the connection has been cancelled. This page will close within 10 seconds.
          </b-notification>

          <div class="columns is-multiline">
            <div v-for="sound in sounds" v-bind:key="sound.id" class="column is-half-tablet">
              <div :class="{message: true, 'is-success': sound.isPlaying(), 'is-warning': sound.stopping, 'is-danger': !sound.isPlaying()}">
                <div class="message-body">
                  <div class="is-clearfix">
                    <b>{{ sound.name }}</b>
                    <div class="is-pulled-right">
                      <span class="tag"><b>Rate:</b>&nbsp;{{ Math.ceil(sound.getRate() * 100) }}%</span>&nbsp;<span class="tag"><b>Volume:</b>&nbsp;{{ Math.ceil(sound.getVolume() * 100) }}%</span>
                    </div>
                  </div>
                  <small><small>{{ sound.id }}</small></small><br/>
                  <small><small>{{ sound.licenses.join(' &mdash; ') }}</small></small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <env-footer></env-footer>
  </div>
</template>

<script>
  import Client from '../client'
  import BNotification from "buefy/src/components/notification/Notification";

  import {Howl} from 'howler'

  export default {
    components: {BNotification},
    name: 'client',

    props: {
      connected: false,
      connectErrorCode: -1,

      user: null,
      secret: null,

      sounds: [],

      timeout: null,
    },

    mounted() {
      this.user = this.$route.params['user']
      this.secret = this.$route.params['key']

      if (this.user === undefined || this.secret === undefined) {
        this.connectErrorCode = -1
        return
      }

      Client.initialize((connected) => {
        if (!connected) return;

        Client.setUpdateVolumeCallback((identifier, volume, duration) => {
          const obj = this.sounds.filter((item) => {
            return item.id === identifier
          })[0]

          if (obj.getVolume() === volume) return

          if (duration <= 0) {
            if (obj.introHowl !== undefined) obj.introHowl.volume(volume)
            obj.mainHowl.volume(volume)
          } else {
            if (obj.introHowl !== undefined) obj.introHowl.fade(obj.introHowl.volume(), volume, duration)
            obj.mainHowl.fade(obj.mainHowl.volume(), volume, duration)
          }
        })

        Client.setUpdateRateCallback((identifier, rate) => {
          const obj = this.sounds.filter((item) => {
            return item.id === identifier
          })[0]

          if (obj.introHowl !== undefined) obj.introHowl.rate(rate)
          obj.mainHowl.rate(rate)
        })

        Client.setPlaySoundCallback((user, identifier, introSound, mainSound, volume, rate, loop) => {
          if (introSound !== undefined) {
            this.loadSound(introSound, volume, rate, false, (introConfig, introHowl) => {
              this.loadSound(mainSound, volume, rate, loop, (mainConfig, mainHowl) => {
                const obj = {
                  id: identifier,
                  name: (introSound === undefined ? '' : (introSound + ' / ')) + mainSound,
                  licenses: [introConfig.license, mainConfig.license],
                  introHowl: introHowl,
                  mainHowl: mainHowl,
                  stopping: false,
                  isPlaying() {
                    return introHowl.playing() || mainHowl.playing()
                  },
                  getRate() {
                    return introHowl.playing() ? introHowl.rate() : mainHowl.rate()
                  },
                  getVolume() {
                    return introHowl.playing() ? introHowl.volume() : mainHowl.volume()
                  }
                }

                introHowl.on('end', () => {
                  mainHowl.play()
                })

                mainHowl.on('end', () => {
                  if (!mainHowl.loop()) {
                    Client.handleSoundStopped(user, identifier)
                  }
                })

                mainHowl.on('stop', () => {
                  Client.handleSoundStopped(user, identifier)
                })

                introHowl.play()
                Client.handleSoundStarted(user, identifier)
                this.sounds.push(obj)
              })
            })
          } else {
            this.loadSound(mainSound, volume, rate, loop, (mainConfig, mainHowl) => {
              const obj = {
                id: identifier,
                name: mainSound,
                licenses: [mainConfig.license],
                mainHowl: mainHowl,
                stopping: false,
                isPlaying() {
                  return mainHowl.playing()
                },
                getRate() {
                  return mainHowl.rate()
                },
                getVolume() {
                  return mainHowl.volume()
                }
              }

              mainHowl.on('end', () => {
                if (!mainHowl.loop()) {
                  Client.handleSoundStopped(user, identifier)
                }
              })

              mainHowl.on('stop', () => {
                Client.handleSoundStopped(user, identifier)
              })

              mainHowl.play()
              Client.handleSoundStarted(user, identifier)
              this.sounds.push(obj)
            })
          }
        })

        Client.setStopSoundCallback((user, identifier, delay, duration) => {
          const obj = this.sounds.filter((item) => {
            return item.id === identifier
          })[0]

          obj.stopping = true

          obj.mainHowl.loop(false)
          setTimeout(() => {
            if (obj.introHowl !== undefined) obj.introHowl.fade(obj.introHowl.volume(), 0, duration)
            obj.mainHowl.fade(obj.mainHowl.volume(), 0, duration)

            setTimeout(() => {
              if (obj.introHowl !== undefined) obj.introHowl.stop()
              obj.mainHowl.stop()
            }, duration + 100)
          }, delay)
        })

        Client.connect(this.user, this.secret, (connected, code) => {
          if (!this.connected && connected) {
            this.sounds = []
          }

          if (this.timeout !== null) {
            clearTimeout(this.timeout)
            this.timeout = null
          }

          this.connectErrorCode = code
          this.connected = connected

          if (code === 3) {
            this.timeout = setTimeout(() => window.close(), 10000)
          }
        })
      })
    },

    methods: {
      loadSound(name, volume, rate, loop, callback) {
        this.$http.get('/sounds/' + name.split('.').join('/') + '.json').then((data) => {
          const sound = new Howl({
            src: data.body.sources,
            loop: loop,
            volume: volume,
            rate: rate,
            html5: true
          });

          callback(data.body, sound)
        })
      }
    }
  }
</script>

<style>
</style>
