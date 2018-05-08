<template>
  <div class="has-text-justified">
    <div class="content">
      <h3 class="title is-5">General</h3>

      Developing custom software using Java or Kotlin is one of the fastest ways to do so. Just grab
      yourself the pre-generated Java protocol classes and the included helper class to serialize
      and deserialize packets. To do so, clone the
      <a href="https://github.com/Bw2801/environment/" target="_blank">GitHub repository</a> and
      run the following commands.
    </div>

    <pre class="content">
cd protocol-kotlin
mvn clean install</pre>

    <div class="content">
      You can now include the protocol as dependency in your maven project.
    </div>

    <pre class="content">
&lt;dependency&gt;
  &lt;groupId&gt;de.bwueller.environment&lt;/groupId&gt;
  &lt;artifactId&gt;protocol&lt;/artifactId&gt;
  &lt;version&gt;{VERSION}&lt;/version&gt;
&lt;/dependency&gt;</pre>

    <div class="content">
      You can now use the following methods to serialize and deserialize packets.
    </div>

    <table class="table">
      <thead>
      <tr>
        <th>Method</th>
        <th>Description</th>
      </tr>
      </thead>

      <tbody>
      <tr>
        <td><code>HelperKt.<b>serializePacket(message: GeneratedMessageV3)</b>: byte[]</code></td>
        <td>Serialize a packet to receive a byte array.</td>
      </tr>
      <tr>
        <td><code>HelperKt.<b>deserializePacket(bytes: byte[])</b>: GeneratedMessageV3</code></td>
        <td>Deserialize a byte array to receive a packet.</td>
      </tr>
      </tbody>
    </table>

    <div class="content">
      <h3 class="title is-5">Actor</h3>

      If you are building a custom actor in Java or Kotlin, you can use the provided actor api. To
      do so run the following commands to add it to your local maven repository.
    </div>

    <pre class="content">
cd actor-api-kotlin
mvn clean install</pre>

    <div class="content">
      You can now import it as a dependency using maven.
    </div>

    <pre class="content">
&lt;dependency&gt;
  &lt;groupId&gt;de.bwueller.environment&lt;/groupId&gt;
  &lt;artifactId&gt;actor-api&lt;/artifactId&gt;
  &lt;version&gt;{VERSION}&lt;/version&gt;
&lt;/dependency&gt;</pre>

    <div class="content">
      The actor api provides a lot of functionality. See the tables below for more information. It
      will connect to the processor automatically using the config provided. By default, the api
      will look for a <code>config.json</code> in it's running directory. To change this, you have
      to edit the resource <code>config.properties</code>.
    </div>

    <table class="table">
      <thead>
      <tr>
        <th>Action</th>
        <th>Method(s)</th>
        <th>Comment</th>
      </tr>
      </thead>

      <tbody>
      <tr>
        <td>Check the current connection status</td>
        <td><pre>ActorApi.<b>isConnected()</b></pre></td>
        <td>
          Returns whether the actor is currently connected.
        </td>
      </tr>

      <tr>
        <td>Receiving the actor identifier</td>
        <td><pre>ActorApi.<b>getIdentifier()</b></pre></td>
        <td>Returns the actor's identifier. Can be <code>null</code> if it is not connected.</td>
      </tr>

      <tr>
        <td>Listen for a connection to the processor</td>
        <td>
          <pre>ActorApi.<b>addListener(listener)</b>
ActorApi.<b>removeListener(listener)</b></pre>
        </td>
        <td>
          The listener will be called whenever the connection is established or lost. When lost, the
          actor will try to reconnect to the processor if possible.
        </td>
      </tr>

      <tr>
        <td>Register a user</td>
        <td><pre>ActorApi.<b>registerUser(user, registerListener, connectListener)</b></pre></td>
        <td>
          The <code>registerCallback</code> will be invoked when the user has successfully been
          registered to the processor. It returns the key to be used by the user to connect to the
          processor using the client. The <code>connectListener</code> will be called when the user
          connects to or disconnects from the processor.
        </td>
      </tr>

      <tr>
        <td>Unregister a user</td>
        <td><pre>ActorApi.<b>unregisterUser(user)</b></pre></td>
        <td>
          Removes the user from the processor. The actor can no longer send sound requests for the
          given user until re-registering.
        </td>
      </tr>

      <tr>
        <td>Check user status</td>
        <td><pre>ActorApi.<b>isUserRegistered(user)</b>
ActorApi.<b>isUserConnected(user)</b></pre></td>
        <td></td>
      </tr>
      </tbody>
    </table>

    <table class="table">
      <thead>
      <tr>
        <th>Action</th>
        <th>Method(s)</th>
        <th>Comment</th>
      </tr>
      </thead>

      <tbody>
      <tr>
        <td>Create a sound</td>
        <td><pre>Sound sound = <b>new Sound(intro, main, volume, rate, looping)</b>;</pre></td>
        <td>
          <code>intro</code> and <code>main</code> are strings which identify the sound to be
          played. The <code>intro</code> sound is optional. <code>volume</code> and <code>rate</code>
          are factors (multipliers).
        </td>
      </tr>

      <tr>
        <td>Play a sound to a user</td>
        <td><pre>sound.<b>play(user)</b>;</pre></td>
        <td>
          The sound will be played with the current volume and rate. If you want to play the same
          sound for multiple users with different volumes or rates each, you have to create multiple
          sound instances.
        </td>
      </tr>

      <tr>
        <td>Stop a sound for a user</td>
        <td><pre>sound.<b>stop(user, delay, fadeDuration)</b>;</pre></td>
        <td>
          The sound will be stopped for the given user. The sound will fade out for
          <code>fadeDuration</code> milliseconds after the given <code>delay</code> in milliseconds.
        </td>
      </tr>

      <tr>
        <td>Update Volume</td>
        <td><pre>sound.<b>setVolume(volume)</b>;
sound.<b>fadeToVolume(volume, duration)</b>;</pre></td>
        <td></td>
      </tr>

      <tr>
        <td>Update Rate</td>
        <td><pre>sound.<b>setRate(rate)</b>;</pre></td>
        <td></td>
      </tr>

      <tr>
        <td>Check sound status</td>
        <td><pre>
sound.<b>getVolume()</b>;
sound.<b>getRate()</b>;

sound.<b>isPlayingFor(user)</b>;
sound.<b>isStartingFor(user)</b>;
sound.<b>isStoppingFor(user)</b>;</pre></td>
        <td></td>
      </tr>

      <tr>
        <td>Listen for changes</td>
        <td><pre>sound.<b>addListeners(start, stop, volume, rate)</b>;
sound.<b>removeListener(listener)</b>;</pre></td>
        <td>
          <code>start</code>, <code>stop</code>, <code>volume</code> and <code>rate</code>
          represent listeners which are called if the corresponding event occurs.
        </td>
      </tr>
      </tbody>
    </table>

  </div>
</template>

<script>
  export default {
    name: "custom-java"
  }
</script>

<style scoped>

</style>