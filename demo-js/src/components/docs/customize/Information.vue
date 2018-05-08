<template>
  <div class="has-text-justified">
    <div class="content">
      <h3 class="title is-5">Protocol</h3>

      Environment is designed to be as adjustable as possible. To achieve this, the fundamental
      protocol has been written in <a href="https://developers.google.com/protocol-buffers/docs/proto3" target="_blank">proto3 by Google</a>.
      It is used to define the structure of the packets to be sent and can be used to generate
      classes representing those for multiple programming languages. Officially supported are the
      following:

      <ul class="content">
        <li>C++</li>
        <li>Objective-C</li>
        <li>C#</li>
        <li>Go</li>
        <li>Java</li>
        <li>Ruby</li>
        <li>Python</li>
      </ul>
    </div>

    <div class="content">
      However, there are multiple unofficial libraries and compilers which can be used to support
      additional programming languages, such as JavaScript.
    </div>

    <div class="content">
      Before building custom software, please make sure that you understand the protocol and which
      actions have to be performed in order to see the results you want.
    </div>

    <div class="content">
      <h3 class="title is-5">Communication</h3>

      Environment uses TCP based WebSockets to communicate between it's three main components in
      real time. WebSockets are available pretty much everywhere (especially in web browsers) which
      allows the use on many platforms. They are fast and reliable. In most cases WebSockets would
      send JSON strings between server and client. In this case it uses byte arrays using the
      lightweight protocol to reduce the size of each request and to allow a lot more and faster
      sound requests and updates in the same amount of time.
    </div>

    <div class="content">
      There are a total of two WebSocket Servers running in the Environment processor. One for
      internal communication (Actor ⇆ Processor) running on port <code>24499</code> and the second
      one for external communication (Processor ⇆ Client) running on port <code>24500</code>.
    </div>
  </div>
</template>

<script>
  export default {
    name: "custom-information"
  }
</script>

<style scoped>

</style>