<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Онтология</div>
        <div class="text-subtitle2">Структура онтологии будет отображаться здесь</div>
      </q-card-section>

      <q-card-section>
        <q-list>
          <q-item>
            <q-item-section>
              <q-item-label>Named Graphs</q-item-label>
              <q-item-label caption>Структурированное хранение данных</q-item-label>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section>
              <q-item-label>urn:ontology</q-item-label>
              <q-item-label caption>Схема/онтология: классы, свойства, отношения</q-item-label>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section>
              <q-item-label>urn:data</q-item-label>
              <q-item-label caption>Экземпляры данных: фактические данные приложения</q-item-label>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section>
              <q-item-label>urn:shacl:shapes</q-item-label>
              <q-item-label caption>SHACL shapes для валидации данных</q-item-label>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section>
              <q-item-label>urn:ontology:history</q-item-label>
              <q-item-label caption>История изменений схемы/онтологии</q-item-label>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section>
              <q-item-label>urn:validation:results</q-item-label>
              <q-item-label caption>Результаты валидации данных</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'OntologyPage'
})
</script>

