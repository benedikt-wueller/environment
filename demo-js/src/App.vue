<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: "app"
  }
</script>

<style>
  html, body, #app {
    height: 100%;
    box-sizing: border-box;
    overflow: auto;
  }
</style>
