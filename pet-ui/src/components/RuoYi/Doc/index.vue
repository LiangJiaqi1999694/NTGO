<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: '宠物-Wiki',
  data() {
    return {
      url: 'https://gitee.com/JavaLionLi/宠物/wikis'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
