<template>
  <aside class="menu" style="margin-right: 2em">
    <div style="margin-bottom: 2.5rem">
      <p class="menu-label">Switch Language</p>

      <div class="field is-fullwidth">
        <b-select :value="lang" @input="(value) => this.$emit('selectLanguage', value)">
          <option value="en">English</option>
          <!--option value="de">Deutsch</option-->
        </b-select>
      </div>
    </div>

    <div v-for="(group, groupIndex) in groups" v-bind:key="'group_menu_' + groupIndex">
      <p class="menu-label">{{ group.name[lang] }}</p>

      <ul class="menu-list" style="margin-bottom: 2.5rem">
        <li v-for="(item, itemIndex) in group.items" v-bind:key="'group_menu_' + groupIndex + '_item_' + itemIndex">
          <a :class="{'is-active': activeGroup === groupIndex && activeItem === itemIndex && activeSubItem === null}"
             @click="selectItem(groupIndex, itemIndex)">
            {{ item.name[lang] }}
          </a>

          <ul v-if="item.items !== undefined && activeGroup === groupIndex && activeItem === itemIndex">
            <li v-for="(subItem, subItemIndex) in item.items">
              <a :class="{'is-active': activeSubItem === subItemIndex}"
                 @click="selectItem(groupIndex, itemIndex, subItemIndex)"
                 v-bind:key="'group_menu_' + groupIndex + '_item_' + itemIndex + '_sub_' + subItemIndex">
                {{ subItem.name[lang] }}
              </a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script>
  import BSelect from "buefy/src/components/select/Select";

  export default {
    components: {BSelect},
    name: "env-menu",

    props: {
      'lang': String,
      'groups': Object,
      'active-group': String,
      'active-item': String,
      'active-sub-item': [String, null],
    },

    methods: {
      selectItem(group, item, subItem = null) {
        this.$emit('select', group, item, subItem)
      }
    },

    mounted() {
      console.log(this.lang)
    }
  }
</script>

<style scoped>

</style>
