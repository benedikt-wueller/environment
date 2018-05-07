<template>
  <section class="hero is-dark">
    <div class="hero-body">
      <div class="container">
        <h1 class="title">
          <small style="font-weight: 200">demo.</small>ENVIRONMENT
          <div class="is-pulled-right">
            <span style="font-weight: 200">{{ part }}</span>
          </div>
        </h1>
      </div>
    </div>
  </section>
</template>

<script>
  export default {
    name: "env-header",

    props: ['part']
  }
</script>

<style scoped>

</style>
