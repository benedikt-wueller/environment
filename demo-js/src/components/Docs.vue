<template>
  <div id="docs">
    <env-header part="DOCUMENTATION"></env-header>

    <section class="section" style="flex: 1">
      <div class="container is-fullhd">
        <div class="columns">
          <div class="column is-one-quarter-desktop is-half-tablet is-hidden-mobile">
            <env-menu :groups="menuGroups" :active-group="activeMenuGroup"
                      :active-item="activeMenuItem" :active-sub-item="activeMenuSubItem"
                      @select="selectItem"></env-menu>
          </div>

          <div class="column">
            <h1 class="title">{{ activeItem.name }}</h1>
            <h2 class="subtitle" v-if="activeItem.parent">{{ activeItem.parent.name }}</h2>

            <hr/>

            <component v-if="activeItem.component" :is="activeItem.component"></component>

            <b-notification v-if="!activeItem.component" type="is-danger" :closable="false">
              This page's content is missing.
            </b-notification>
          </div>
        </div>
      </div>
    </section>

    <env-footer></env-footer>
  </div>
</template>

<script>
  import Terms from './docs/Terms'

  import BNotification from "buefy/src/components/notification/Notification";

  export default {
    components: {BNotification},
    name: "docs",

    data() {
      return {
        menuGroups: {
          'introduction': {
            name: 'Introduction',
            items: {
              'intro': {
                name: 'Introduction'
              },
              'terms': {
                name: 'Definitions and Terms',
                component: Terms
              },
              'demo': {
                name: 'Live Demonstration'
              },
              'start': {
                name: 'Getting Started'
              }
            }
          },
          'protocol': {
            name: 'Protocol',
            items: {
              'info': {
                name: 'Information'
              },
              'actors': {
                name: 'Manage Actors',
                items: {
                  'register': {
                    name: 'Register Actor'
                  },
                  'unregister': {
                    name: 'Unregister Actor'
                  },
                }
              },
              'users': {
                name: 'Manage Users',
                items: {
                  'register': {
                    name: 'Register User'
                  },
                  'unregister': {
                    name: 'Unregister User'
                  },
                  'connect': {
                    name: 'Connect User'
                  },
                  'disconnect': {
                    name: 'Disconnect User'
                  },
                  'meta': {
                    name: 'Update Meta Data'
                  }
                }
              },
              'sounds': {
                name: 'Manage Sounds',
                items: {
                  'start': {
                    name: 'Play Sound'
                  },
                  'stop': {
                    name: 'Stop Sound'
                  },
                  'volume': {
                    name: 'Change Volume'
                  },
                  'rate': {
                    name: 'Change Playback Rate'
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
      }
    },

    methods: {
      selectItem(group, item, subItem) {
        if (subItem === null) {
          this.$router.push({name: 'docs.item', params: {group: group, item: item}})
        } else {
          this.$router.push({name: 'docs.sub_item', params: {group: group, item: item, subItem: subItem}})
        }
      }
    }
  }
</script>

<style scoped>

</style>
