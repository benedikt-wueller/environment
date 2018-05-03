import Vue from 'vue'
import App from './App.vue'

import Buefy from 'buefy'
import 'buefy/lib/buefy.css'
import 'bulma/css/bulma.min.css'

import VueResource from 'vue-resource'

Vue.config.productionTip = false

Vue.use(Buefy)
Vue.use(VueResource)

new Vue({
  render: h => h(App)
}).$mount('#app')
