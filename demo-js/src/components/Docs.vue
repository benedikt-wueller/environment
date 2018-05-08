<template>
  <div id="docs">
    <env-header part="DOCUMENTATION"></env-header>

    <section class="section" style="flex: 1">
      <div class="container is-fullhd">
        <div class="columns">
          <div class="column is-one-quarter-desktop is-half-tablet is-hidden-mobile">
            <env-menu :lang="language" :groups="menuGroups" :active-group="activeMenuGroup"
                      :active-item="activeMenuItem" :active-sub-item="activeMenuSubItem"
                      @select="selectItem"></env-menu>
          </div>

          <div class="column">
            <h1 class="title">{{ activeItem.name[language] }}</h1>
            <h2 class="subtitle" v-if="activeItem.parent">{{ activeItem.parent.name[language] }}</h2>

            <hr/>

            <component v-if="activeItem.component" :is="activeItem.component" :lang="language"></component>

            <b-notification v-if="!activeItem.component" type="is-danger" :closable="false">
              <span v-if="language === 'en'">
                This page's content is missing.
              </span>
              <span v-else>
                Der Inhalt dieser Seite fehlt.
              </span>
            </b-notification>
          </div>
        </div>
      </div>
    </section>

    <env-footer></env-footer>
  </div>
</template>

<script>
  import IntroTerms from './docs/introduction/Terms'
  import IntroLicense from './docs/introduction/License'
  import IntroDemo from './docs/introduction/Demo'
  import IntroGettingStarted from './docs/introduction/GettingStarted'

  import CustomInfo from './docs/customize/Information'

  import BNotification from "buefy/src/components/notification/Notification";

  export default {
    components: {BNotification},
    name: "docs",

    data() {
      return {
        menuGroups: {
          'introduction': {
            name: {en: 'Introduction', de: 'Einleitung'},
            items: {
              'intro': {
                name: {en: 'Introduction (TODO)', de: 'Einleitung (TODO)'}
              },
              'terms': {
                name: {en: 'Definitions and Terms', de: 'Begrifflichkeiten (TODO)'},
                component: IntroTerms
              },
              'how-it-works': {
                name: {en: 'How it works (TODO)', de: 'Funktionsweise (TODO)'}
              },
              'license': {
                name: {en: 'License', de: 'Lizenz (TODO)'},
                component: IntroLicense
              },
              'start': {
                name: {en: 'Getting Started', de: 'Erste Schritte (TODO)'},
                component: IntroGettingStarted
              },
              'demo': {
                name: {en: 'Live Demonstration', de: 'Live Demo (TODO)'},
                component: IntroDemo
              },
            }
          },
          'customize': {
            name: {en: 'Custom Software', de: 'Software Modifizieren'},
            items: {
              'info': {
                name: {en: 'Information', de: 'Informationen (TODO)'},
                component: CustomInfo
              },
              'java': {
                name: {en: 'Java / Kotlin (TODO)', de: 'Java / Kotlin (TODO)'}
              },
              'js': {
                name: {en: 'JavaScript (TODO)', de: 'JavaScript (TODO)'}
              },
              'other': {
                name: {en: 'Other Languages (TODO)', de: 'Weitere Sprachen (TODO)'}
              }
            }
          },
          'protocol': {
            name: {en: 'Protocol', de: 'Protokoll'},
            items: {
              'info': {
                name: {en: 'Information (TODO)', de: 'Informationen (TODO)'}
              },
              'actors': {
                name: {en: 'Manage Actors (TODO)', de: 'Aktoren verwalten (TODO)'},
                items: {
                  'register': {
                    name: {en: 'Register Actor', de: 'Aktor registrieren'}
                  },
                  'unregister': {
                    name: {en: 'Unregister Actor', de: 'Aktor entfernen'}
                  },
                }
              },
              'users': {
                name: {en: 'Manage Users (TODO)', de: 'Benutzer verwalten (TODO)'},
                items: {
                  'register': {
                    name: {en: 'Register User', de: 'Benutzer registrieren'}
                  },
                  'unregister': {
                    name: {en: 'Unregister User', de: 'Benutzer entfernen'}
                  },
                  'connect': {
                    name: {en: 'Connect User', de: 'Benutzer verbinden'}
                  },
                  'disconnect': {
                    name: {en: 'Disconnect User', de: 'Benutzer trennen'}
                  },
                  'meta': {
                    name: {en: 'Update Meta Data', de: 'Meta-Daten aktualisieren'}
                  }
                }
              },
              'sounds': {
                name: {en: 'Manage Sounds (TODO)', de: 'Sounds verwalten (TODO)'},
                items: {
                  'start': {
                    name: {en: 'Play Sound', de: 'Sound abspielen'}
                  },
                  'stop': {
                    name: {en: 'Stop Sound', de: 'Sound stoppen'}
                  },
                  'volume': {
                    name: {en: 'Change Volume', de: 'Lautstärke anpassen'}
                  },
                  'rate': {
                    name: {en: 'Change Playback Rate', de: 'Geschwindigkeit anpassen'}
                  }
                }
              },
            }
          }
        },
      }
    },

    computed: {
      activeMenuGroup() {
        const group = this.$route.params['group']
        if (group) return group
        return Object.keys(this.menuGroups)[0]
      },

      activeMenuItem() {
        const item = this.$route.params['item']
        if (item) return item
        return Object.keys(this.menuGroups[this.activeMenuGroup].items)[0]
      },

      activeMenuSubItem() {
        const item = this.$route.params['subItem']
        if (item) return item
        return null
      },

      activeItem() {
        const item = this.menuGroups[this.activeMenuGroup].items[this.activeMenuItem]
        const subItem = this.activeMenuSubItem

        if (subItem) {
          const result = item.items[subItem]
          result.parent = item
          return result
        } else {
          return item
        }
      },

      language() {
        return this.$route.params['lang']
      }
    },

    methods: {
      selectItem(group, item, subItem) {
        if (subItem === null) {
          this.$router.push({name: 'docs.item', params: {lang: this.language, group: group, item: item}})
        } else {
          this.$router.push({name: 'docs.sub_item', params: {lang: this.language, group: group, item: item, subItem: subItem}})
        }
      }
    }
  }
</script>

<style scoped>

</style>
