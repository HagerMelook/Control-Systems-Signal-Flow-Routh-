<template>
  <!-- <img alt="Vue logo" src="./assets/logo.png"> -->
  <Interface/>
</template>

<script>
import Interface from './components/Interface.vue'

export default {
  name: 'App',
  components: {
    Interface
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background-color: #edf0f3;
  /* background-color: rgb(171, 185, 201); */
}
</style>