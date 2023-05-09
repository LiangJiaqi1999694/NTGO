<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: '烛龙-Wiki',
  data() {
    return {
      url: 'https://gitee.com/JavaLionLi/烛龙/wikis'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
