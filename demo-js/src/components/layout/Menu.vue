<template>
  <aside class="menu" style="margin-right: 3em">
    <div v-for="(group, groupIndex) in groups" v-bind:key="'group_menu_' + groupIndex">
      <p class="menu-label">{{ group.name }}</p>

      <ul class="menu-list">
        <li v-for="(item, itemIndex) in group.items" v-bind:key="'group_menu_' + groupIndex + '_item_' + itemIndex">
          <a :class="{'is-active': activeGroup === groupIndex && activeItem === itemIndex && activeSubItem === null}"
             @click="selectItem(groupIndex, itemIndex)">
            <span v-if="item.name">{{ item.name }}</span>
            <span v-else>{{ item }}</span>
          </a>

          <ul v-if="item.items !== undefined && activeGroup === groupIndex && activeItem === itemIndex">
            <li v-for="(subItem, subItemIndex) in item.items">
              <a :class="{'is-active': activeSubItem === subItemIndex}"
                 @click="selectItem(groupIndex, itemIndex, subItemIndex)"
                 v-bind:key="'group_menu_' + groupIndex + '_item_' + itemIndex + '_sub_' + subItemIndex">
                {{ subItem }}
              </a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script>
  export default {
    name: "env-menu",

    props: {
      'groups': Object,
      'active-group': String,
      'active-item': String,
      'active-sub-item': [String, null],
    },

    methods: {
      selectItem(group, item, subItem = null) {
        this.$emit('select', group, item, subItem)
      }
    }
  }
</script>

<style scoped>

</style>
