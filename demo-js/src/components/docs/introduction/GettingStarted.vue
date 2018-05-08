<template>
  <div class="has-text-justified">
    <p class="content">
      This guide will lead to running the processor written in Java/Kotlin and the live demo (client
      and actor) written in JavaScript. If you want to run software written in another language, see
      the <router-link :to="{name: 'docs.item', params: {group: 'customize', item: 'info'}}">Custom Software</router-link> section.
    </p>

    <p class="content">
      The easiest way to start using Environment is to checkout the
      <a href="https://github.com/Bw2801/environment/" target="_blank">GitHub repository</a>.
    </p>

    <pre class="content">git clone https://github.com/Bw2801/environment.git</pre>

    <p class="content">
      First you will need to build the protocol. The repository contains pre-generated java files
      representing the Protobuf3 definitions. You will have to build those using Maven to add them
      to your local repository.
    </p>

    <pre class="content">
cd protocol-kotlin
mvn clean install</pre>

    <p class="content">
      Next the Environment processor has to be built. To do so run the following commands.
    </p>

    <pre class="content">
cd processor-kotlin
mvn clean install</pre>

    <p class="content">
      The processor is good to go. You can run it like any other command-line Java application. To
      test everything you have to run an actor and a client. In this case we will use the components
      used in the live demo. First you will have to adjust the URIs used to connect to the
      processor. Edit the files <code>client.js:23</code> and <code>actor.js:28</code> in
      <code>demo-js/src/</code>. You can then run the JavaScript application using the following
      commands.
    </p>

    <pre class="content">
cd demo-js
npm install
npm run serve</pre>

    <p class="content">
      If you want to build the live demo to be uploaded to a web server, run the following command
      instead. To test the components please read the section
      <router-link :to="{name: 'docs.item', params: {group: 'introduction', item: 'demo'}}">Live Demonstration</router-link>.
    </p>

    <pre class="content">npm run build</pre>

    <p class="content">
      If you are running Environment in a production environment, make sure only your servers are
      allowed to communicate with the processor on port <code>24499</code>. Port <code>24500</code>
      however should be open to the public.
    </p>

    <p class="content">
      You can now start to modify or build your custom Environment components. See the section
      <router-link :to="{name: 'docs.item', params: {group: 'customize', item: 'info'}}">Custom Software</router-link>.
    </p>
  </div>
</template>

<script>
  export default {
    name: "getting-started"
  }
</script>

<style scoped>

</style>
