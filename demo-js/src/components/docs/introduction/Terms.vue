<template>
  <div class="has-text-justified">
    <p class="content">
      This documentation uses specific wording to describe the functionality of Environment. These
      are described on this section.
    </p>

    <div class="content">
      Environment is split into the following pieces:

      <ul>
        <li>
          <b>Protocol</b><br/>
          This is the most basic part of Environment. It defines what functionality is available and
          which information are needed to fulfill it. Every component has to implement at least parts
          of this protocol to listen for events or perform certain actions. You can find more
          information on this topic in the section <router-link :to="{name: 'docs.item', params: {group: 'protocol', item: 'info'}}">Protocol</router-link>.
        </li>

        <li>
          <b>User</b><br/>
          The user is the person who should be able to listen to the requested sounds in the end.
        </li>

        <li>
          <b>Client</b><br/>
          This is the component executing the sound requests such as playing and stopping sounds,
          changing the volume and showing meta data to the user. In most cases this will be a
          web application or browser extension but there is no limitation as to what the client
          is as long as it is able to play the requested sounds to the user.
        </li>

        <li>
          <b>Actor</b><br/>
          This component is responsible for performing the sound requests. An actor could be a
          plugin or mod running on a game server, a sound board which allows for manual control of
          the sounds to be played (like the demo actor) or any kind of software which is able to
          perform sound requests. The user may trigger the actor to send sound requests to the
          client such as performing a specific action in-game.
        </li>

        <li>
          <b>Processor</b><br/>
          The processor component is the core of Environment. It handles all incoming sound requests
          from the actors, verifies which actor is allowed to perform which action for which user
          and forwards those to the corresponding clients. On the other hand it listens for
          confirmation requests sent by the client and forwards those to the actors. This component
          can also be referred to as server.
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
  export default {
    name: "terms"
  }
</script>

<style scoped>

</style>