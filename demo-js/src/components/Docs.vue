<template>
  <div id="docs">
    <env-header part="DOCUMENTATION"></env-header>

    <section class="section" style="flex: 1">
      <div class="container is-fullhd">
        <div class="columns">
          <div class="column is-one-quarter-desktop is-half-tablet is-hidden-mobile">
            <env-menu :groups="menuGroups" :active-group="activeMenuGroup" :active-item="activeMenuItem" :active-sub-item="activeMenuSubItem"
                      @select="(group, item, subItem) => {
                        if (subItem === null) {
                          this.$router.push({name: 'docs.item', params: {group: group, item: item}})
                        } else {
                          this.$router.push({name: 'docs.sub_item', params: {group: group, item: item, subItem: subItem}})
                        }
                      }"></env-menu>
          </div>

          <div>
            testy
          </div>
        </div>
      </div>
    </section>

    <env-footer></env-footer>
  </div>
</template>

<script>
  export default {
    name: "docs",

    data() {
      return {
        menuGroups: {
          'introduction': {
            name: 'Hello World',
            items: {
              'testy': {
                name: 'Hello World Item',
                items: {
                  'test123': 'Hello World Sub Item 1',
                  'test234':'Hello World Sub Item 2'
                }
              },
              'testy2': 'Hello There'
            }
          },
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
        return Object.keys(this.activeMenuGroup().items)[0]
      },

      activeMenuSubItem() {
        const item = this.$route.params['subItem']
        if (item) return item
        return null
      }
    },

    mounted() {
      console.log(this.activeMenuGroup, this.activeMenuItem, this.activeMenuSubItem)
    }
  }
</script>

<style scoped>

</style>
