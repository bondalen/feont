<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Визуализация графа знаний</div>
        <div class="text-subtitle2">Компонент визуализации будет реализован с использованием Cytoscape.js</div>
      </q-card-section>

      <q-card-section>
        <div id="cy" style="height: 600px; width: 100%; border: 1px solid #ccc;"></div>
      </q-card-section>

      <q-card-section>
        <q-btn
          label="Загрузить данные"
          color="primary"
          @click="loadGraphData"
          :loading="loading"
        />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import cytoscape from 'cytoscape'
import dagre from 'cytoscape-dagre'
import { executeConstruct } from 'src/services/sparql.service'
import { useQuasar } from 'quasar'

// Регистрация расширения layout
cytoscape.use(dagre)

export default defineComponent({
  name: 'GraphPage',

  setup() {
    const $q = useQuasar()
    const loading = ref(false)
    let cy = null

    const initCytoscape = () => {
      cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [],
        style: [
          {
            selector: 'node',
            style: {
              'background-color': '#666',
              'label': 'data(label)',
              'width': 30,
              'height': 30
            }
          },
          {
            selector: 'edge',
            style: {
              'width': 3,
              'line-color': '#ccc',
              'target-arrow-color': '#ccc',
              'target-arrow-shape': 'triangle',
              'label': 'data(label)',
              'curve-style': 'bezier'
            }
          }
        ],
        layout: {
          name: 'dagre',
          rankDir: 'TB'
        }
      })
    }

    const loadGraphData = async () => {
      loading.value = true

      try {
        // Пример запроса для получения данных графа
        const query = `
          PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
          
          CONSTRUCT {
            ?s ?p ?o
          }
          WHERE {
            ?s ?p ?o
          }
          LIMIT 50
        `

        const turtleData = await executeConstruct(query)
        
        // TODO: Парсинг Turtle и преобразование в формат Cytoscape
        // Здесь нужно будет использовать rdflib.js или n3 для парсинга
        
        $q.notify({
          type: 'info',
          message: 'Визуализация будет реализована после парсинга RDF данных'
        })

        // Временная демо-структура
        if (cy) {
          cy.add([
            { data: { id: 'node1', label: 'Node 1' } },
            { data: { id: 'node2', label: 'Node 2' } },
            { data: { id: 'edge1', source: 'node1', target: 'node2', label: 'relates' } }
          ])
          cy.layout({ name: 'dagre' }).run()
        }

      } catch (err) {
        $q.notify({
          type: 'negative',
          message: `Ошибка загрузки данных: ${err.message}`
        })
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      initCytoscape()
    })

    onUnmounted(() => {
      if (cy) {
        cy.destroy()
      }
    })

    return {
      loading,
      loadGraphData
    }
  }
})
</script>

