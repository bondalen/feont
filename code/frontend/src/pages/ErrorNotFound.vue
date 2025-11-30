<template>
  <q-page class="flex flex-center">
    <div class="text-center">
      <div style="font-size: 30vh">
        404
      </div>

      <div class="text-h2" style="opacity:.4">
        Упс. Ничего здесь нет...
      </div>

      <q-btn
        color="primary"
        style="width:200px;"
        to="/"
        label="На главную"
        no-caps
      />
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'ErrorNotFound'
})
</script>

