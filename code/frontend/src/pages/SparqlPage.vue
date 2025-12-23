<template>
  <q-page class="q-pa-md">
    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-card>
          <q-card-section>
            <div class="text-h6">SPARQL Query / Update</div>
            <div class="text-caption text-grey-6 q-mt-xs">
              Выберите тип запроса: SELECT, CONSTRUCT, ASK, DESCRIBE для чтения данных или UPDATE для изменения данных
            </div>
          </q-card-section>

          <q-card-section>
            <q-input
              v-model="query"
              type="textarea"
              label="SPARQL запрос"
              rows="10"
              outlined
            />
          </q-card-section>

          <q-card-section>
            <q-select
              v-model="queryType"
              :options="queryTypes"
              label="Тип запроса"
              outlined
            />
          </q-card-section>

          <q-card-actions align="right">
            <q-btn
              label="Выполнить"
              color="primary"
              @click="executeQuery"
              :loading="loading"
            />
            <q-btn
              label="Очистить"
              flat
              @click="clearQuery"
            />
          </q-card-actions>
        </q-card>
      </div>

      <div class="col-12 col-md-6">
        <q-card>
          <q-card-section>
            <div class="text-h6">Результат</div>
          </q-card-section>

          <q-card-section>
            <div v-if="loading" class="text-center">
              <q-spinner color="primary" size="3em" />
            </div>
            <div v-else-if="error" class="text-negative">
              {{ error }}
            </div>
            <div v-else-if="result" class="result-container">
              <pre v-if="queryType === 'UPDATE'" class="q-pa-sm bg-positive text-white rounded-borders">{{ result }}</pre>
              <pre v-else class="q-pa-sm bg-grey-2 rounded-borders">{{ result }}</pre>
            </div>
            <div v-else class="text-grey-6 text-center">
              Результат появится здесь
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { executeSelect, executeConstruct, executeAsk, executeDescribe, executeUpdate } from 'src/services/sparql.service'
import { useQuasar } from 'quasar'

export default defineComponent({
  name: 'SparqlPage',

  setup() {
    const $q = useQuasar()
    const query = ref(`PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?s ?p ?o
WHERE {
  ?s ?p ?o
}
LIMIT 10`)
    const queryType = ref('SELECT')
    const queryTypes = ['SELECT', 'CONSTRUCT', 'ASK', 'DESCRIBE', 'UPDATE']
    const result = ref('')
    const loading = ref(false)
    const error = ref('')

    const executeQuery = async () => {
      if (!query.value.trim()) {
        $q.notify({
          type: 'negative',
          message: 'Введите SPARQL запрос'
        })
        return
      }

      loading.value = true
      error.value = ''
      result.value = ''

      try {
        let response
        switch (queryType.value) {
          case 'SELECT':
            response = await executeSelect(query.value)
            result.value = JSON.stringify(response, null, 2)
            break
          case 'CONSTRUCT':
            response = await executeConstruct(query.value)
            result.value = response
            break
          case 'ASK':
            response = await executeAsk(query.value)
            result.value = response.toString()
            break
          case 'DESCRIBE':
            response = await executeDescribe(query.value)
            result.value = response
            break
          case 'UPDATE':
            await executeUpdate(query.value)
            result.value = 'UPDATE запрос выполнен успешно. Данные обновлены.'
            break
        }
        $q.notify({
          type: 'positive',
          message: 'Запрос выполнен успешно'
        })
      } catch (err) {
        error.value = err.message
        $q.notify({
          type: 'negative',
          message: `Ошибка: ${err.message}`
        })
      } finally {
        loading.value = false
      }
    }

    const clearQuery = () => {
      query.value = ''
      result.value = ''
      error.value = ''
    }

    return {
      query,
      queryType,
      queryTypes,
      result,
      loading,
      error,
      executeQuery,
      clearQuery
    }
  }
})
</script>

<style scoped>
.result-container {
  max-height: 500px;
  overflow: auto;
}

.result-container pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 12px;
}
</style>

