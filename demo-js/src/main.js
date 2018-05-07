import Vue from '../node_modules/vue/dist/vue.common'

import App from './App.vue'
import Client from './components/Client.vue'
import Actor from './components/Actor.vue'
import Docs from './components/Docs.vue'

import Header from './components/layout/Header.vue'
import Footer from './components/layout/Footer.vue'
import Menu from './components/layout/Menu.vue'

import Buefy from 'buefy'
import 'buefy/lib/buefy.css'
import 'bulma/css/bulma.min.css'

import VueResource from 'vue-resource'
import VueRouter from 'vue-router'

Vue.config.productionTip = false

Vue.use(Buefy)
Vue.use(VueResource)
Vue.use(VueRouter)

Vue.component('env-header', Header)
Vue.component('env-footer', Footer)
Vue.component('env-menu', Menu)

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/docs/en'
    },
    {
      path: '/docs',
      redirect: '/docs/en'
    },
    {
      path: '/docs/:lang',
      component: Docs
    },
    {
      name: 'docs.item',
      path: '/docs/:lang/:group/:item',
      component: Docs
    },
    {
      name: 'docs.sub_item',
      path: '/docs/:lang/:group/:item/:subItem',
      component: Docs
    },
    {
      path: '/client',
      component: Client
    },
    {
      path: '/client/:user',
      component: Client
    },
    {
      path: '/client/:user/:key',
      component: Client
    },
    {
      path: '/actor',
      component: Actor
    }
  ]
})

new Vue({
  render: h => h(App),
  router
}).$mount('#app')
