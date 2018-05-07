<template>
  <div id="app">
    <router-view style="height: 100%; display: flex; flex-direction: column"></router-view>
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
