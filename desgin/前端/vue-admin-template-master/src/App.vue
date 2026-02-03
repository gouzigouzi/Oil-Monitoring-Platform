<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  provide (){
    return {
      reload: this.reload
    }
  }
}
</script>
