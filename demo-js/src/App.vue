<template>
  <div id="app" style="height: 100%; display: flex; flex-direction: column">
    <router-view style="flex: 1"></router-view>

    <footer class="footer">
      <div class="container">
        <div class="content has-text-centered">
          <p>
            <strong>environment</strong> by <a href="https://bwueller.de">Benedikt Wüller (Bw2801)</a>. The source code is licensed under
            <a href="https://opensource.org/licenses/Apache-2.0">Apache 2.0</a>. The website content
            is licensed <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY NC SA 4.0</a>.
          </p>
        </div>
      </div>
    </footer>
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
