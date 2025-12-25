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
        <div class="row q-col-gutter-md">
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="maxElements"
              type="number"
              label="Максимум элементов"
              hint="Ограничение для больших графов"
              min="10"
              max="1000"
              outlined
            />
          </div>
          <div class="col-12 col-md-6">
            <q-btn
              label="Загрузить данные"
              color="primary"
              @click="loadGraphData"
              :loading="loading"
              class="full-width"
            />
          </div>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import cytoscape from 'cytoscape'
import dagre from 'cytoscape-dagre'
import { Parser } from 'n3'
import { executeConstruct } from 'src/services/sparql.service'
import { useQuasar } from 'quasar'

// Регистрация расширения layout
cytoscape.use(dagre)

export default defineComponent({
  name: 'GraphPage',

  setup() {
    const $q = useQuasar()
    const loading = ref(false)
    const maxElements = ref(200) // Ограничение на количество элементов для больших графов
    let cy = null

    const initCytoscape = () => {
      cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [],
        style: [
          // Базовые стили для узлов
          {
            selector: 'node',
            style: {
              'background-color': '#666',
              'label': 'data(label)',
              'width': 30,
              'height': 30,
              'font-size': '12px',
              'text-valign': 'center',
              'text-halign': 'center',
              'color': '#fff',
              'text-wrap': 'wrap',
              'text-max-width': '80px'
            }
          },
          // Стили для узлов типа Blank Node
          {
            selector: 'node[type = "BlankNode"]',
            style: {
              'background-color': '#999',
              'shape': 'diamond',
              'border-width': 2,
              'border-color': '#555'
            }
          },
          // Стили для узлов типа NamedNode
          {
            selector: 'node[type = "NamedNode"]',
            style: {
              'background-color': '#4A90E2',
              'shape': 'ellipse'
            }
          },
          // Стили для узлов с типом rdf:type
          {
            selector: 'node[hasType = "true"]',
            style: {
              'background-color': '#50C878',
              'border-width': 3,
              'border-color': '#2E8B57'
            }
          },
          // Стили для рёбер
          {
            selector: 'edge',
            style: {
              'width': 2,
              'line-color': '#999',
              'target-arrow-color': '#999',
              'target-arrow-shape': 'triangle',
              'label': 'data(label)',
              'curve-style': 'bezier',
              'font-size': '10px',
              'text-rotation': 'autorotate',
              'text-margin-y': -10
            }
          },
          // Стили для рёбер типа rdf:type
          {
            selector: 'edge[predicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"]',
            style: {
              'width': 3,
              'line-color': '#50C878',
              'target-arrow-color': '#50C878',
              'line-style': 'dashed'
            }
          }
        ],
        layout: {
          name: 'dagre',
          rankDir: 'TB'
        }
      })
    }

    /**
     * Преобразование полного URI в короткую форму
     * @param {string} uri - Полный URI
     * @returns {string} Короткая форма URI
     */
    const shortenUri = (uri) => {
      if (!uri || typeof uri !== 'string') return uri
      
      // Обработка Blank Nodes - используем короткую форму
      if (uri.startsWith('_:')) {
        return `bnode:${uri.substring(2).substring(0, 8)}`
      }
      
      // Стандартные префиксы
      const prefixes = {
        'http://www.w3.org/1999/02/22-rdf-syntax-ns#': 'rdf:',
        'http://www.w3.org/2000/01/rdf-schema#': 'rdfs:',
        'http://www.w3.org/2001/XMLSchema#': 'xsd:',
        'http://www.w3.org/2002/07/owl#': 'owl:',
        'https://feont.ontoline.ru/ontology/': 'feont:',
        'http://feont.ontoline.ru/ontology/': 'feont:', // Для локальной разработки без SSL
        'http://example.org/': 'ex:'
      }

      // Проверяем стандартные префиксы
      for (const [base, prefix] of Object.entries(prefixes)) {
        if (uri.startsWith(base)) {
          return prefix + uri.substring(base.length)
        }
      }

      // Если не найден стандартный префикс, извлекаем последний сегмент после / или #
      const match = uri.match(/([^/#]+)([#/]?)$/)
      if (match) {
        return match[1]
      }

      return uri
    }

    /**
     * Парсинг Turtle данных в массив триплетов
     * @param {string} turtleData - Строка с данными в формате Turtle
     * @returns {Promise<Array>} Массив триплетов в формате [subject, predicate, object]
     */
    const parseTurtleData = (turtleData) => {
      return new Promise((resolve, reject) => {
        if (!turtleData || turtleData.trim() === '') {
          resolve([])
          return
        }

        const parser = new Parser()
        const triples = []

        parser.parse(turtleData, (error, triple, prefixes) => {
          if (error) {
            reject(new Error(`Ошибка парсинга Turtle: ${error.message}`))
            return
          }

          if (triple) {
            // Добавляем триплет в массив
            triples.push({
              subject: triple.subject.value,
              predicate: triple.predicate.value,
              object: triple.object.value,
              objectType: triple.object.termType, // 'NamedNode', 'Literal', 'BlankNode'
              subjectType: triple.subject.termType
            })
          } else {
            // Парсинг завершён (triple === null)
            resolve(triples)
          }
        })
      })
    }

    /**
     * Преобразование RDF триплетов в формат Cytoscape.js
     * @param {Array} triples - Массив триплетов
     * @param {number} maxElementsLimit - Максимальное количество элементов для ограничения больших графов
     * @returns {Object} Объект с узлами и рёбрами для Cytoscape
     */
    const triplesToCytoscape = (triples, maxElementsLimit = 200) => {
      if (!triples || triples.length === 0) {
        return { nodes: [], edges: [] }
      }

      // Множества для уникализации узлов и рёбер
      const nodeSet = new Map() // Map<nodeId, {id, label, uri, type, hasType}>
      const edgeSet = new Set() // Set для уникализации рёбер
      const rdfType = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'

      // Первый проход: сбор информации о типах узлов (rdf:type)
      const nodeTypes = new Map() // Map<nodeId, Set<types>>
      triples.forEach((triple) => {
        if (triple.predicate === rdfType && 
            (triple.subjectType === 'NamedNode' || triple.subjectType === 'BlankNode') &&
            (triple.objectType === 'NamedNode' || triple.objectType === 'BlankNode')) {
          if (!nodeTypes.has(triple.subject)) {
            nodeTypes.set(triple.subject, new Set())
          }
          nodeTypes.get(triple.subject).add(triple.object)
        }
      })

      // Второй проход: извлечение узлов и рёбер из триплетов
      // Литералы исключаются из визуализации (не создаются как узлы)
      triples.forEach((triple, index) => {
        const subject = triple.subject
        const predicate = triple.predicate
        const object = triple.object

        // Добавляем субъект как узел (если это NamedNode или BlankNode, не Literal)
        if (triple.subjectType === 'NamedNode' || triple.subjectType === 'BlankNode') {
          if (!nodeSet.has(subject)) {
            nodeSet.set(subject, {
              id: subject,
              label: shortenUri(subject),
              uri: subject,
              type: triple.subjectType,
              hasType: nodeTypes.has(subject)
            })
          }
        }

        // Добавляем объект как узел (только если это NamedNode или BlankNode, не Literal)
        // Литералы исключаются из визуализации
        if (triple.objectType === 'NamedNode' || triple.objectType === 'BlankNode') {
          if (!nodeSet.has(object)) {
            nodeSet.set(object, {
              id: object,
              label: shortenUri(object),
              uri: object,
              type: triple.objectType,
              hasType: nodeTypes.has(object)
            })
          }

          // Создаём ребро только если объект - это узел (не литерал)
          const edgeId = `edge_${subject}_${predicate}_${object}_${index}`
          if (!edgeSet.has(edgeId)) {
            edgeSet.add(edgeId)
          }
        }
      })

      // Преобразование узлов в формат Cytoscape
      let nodes = Array.from(nodeSet.values()).map(node => ({
        data: {
          id: node.id,
          label: node.label,
          uri: node.uri,
          type: node.type,
          hasType: node.hasType ? 'true' : 'false'
        }
      }))

      // Преобразование рёбер в формат Cytoscape
      let edges = []
      triples.forEach((triple, index) => {
        // Создаём ребро только если объект - это узел (не литерал)
        // Литералы исключаются из визуализации
        if (triple.objectType === 'NamedNode' || triple.objectType === 'BlankNode') {
          const edgeId = `edge_${triple.subject}_${triple.predicate}_${triple.object}_${index}`
          
          // Проверяем, что оба узла существуют (могли быть отфильтрованы)
          if (nodeSet.has(triple.subject) && nodeSet.has(triple.object)) {
            edges.push({
              data: {
                id: edgeId,
                source: triple.subject,
                target: triple.object,
                label: shortenUri(triple.predicate),
                predicate: triple.predicate
              }
            })
          }
        }
      })

      // Обработка больших графов: ограничение количества элементов
      const totalElements = nodes.length + edges.length
      if (totalElements > maxElementsLimit) {
        // Приоритизируем узлы с типами (rdf:type)
        const typedNodes = nodes.filter(n => n.data.hasType === 'true')
        const untypedNodes = nodes.filter(n => n.data.hasType === 'false')
        
        // Оставляем все типизированные узлы и часть нетипизированных
        const nodesToKeep = maxElementsLimit - edges.length
        const keepTyped = Math.min(typedNodes.length, Math.floor(nodesToKeep * 0.7))
        const keepUntyped = Math.max(0, nodesToKeep - keepTyped)
        
        const selectedNodes = [
          ...typedNodes.slice(0, keepTyped),
          ...untypedNodes.slice(0, keepUntyped)
        ]
        
        const selectedNodeIds = new Set(selectedNodes.map(n => n.data.id))
        
        // Фильтруем рёбра, оставляя только те, которые связывают выбранные узлы
        edges = edges.filter(edge => 
          selectedNodeIds.has(edge.data.source) && selectedNodeIds.has(edge.data.target)
        )
        
        nodes = selectedNodes
        
        console.warn(`Граф ограничен до ${nodes.length} узлов и ${edges.length} рёбер (было ${totalElements} элементов)`)
      }

      return { nodes, edges, truncated: totalElements > maxElementsLimit }
    }

    const loadGraphData = async () => {
      loading.value = true

      try {
        // Запрос для получения данных из Named Graphs (онтология и данные)
        const query = `
          PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
          
          CONSTRUCT {
            ?s ?p ?o
          }
          WHERE {
            {
              GRAPH <urn:ontology> {
                ?s ?p ?o
              }
            }
            UNION
            {
              GRAPH <urn:data> {
                ?s ?p ?o
              }
            }
          }
          LIMIT 200
        `

        const turtleData = await executeConstruct(query)
        
        // Парсинг Turtle данных
        const triples = await parseTurtleData(turtleData)
        
        // Преобразование триплетов в формат Cytoscape с ограничением для больших графов
        const { nodes, edges, truncated } = triplesToCytoscape(triples, maxElements.value)
        
        console.log('Распарсенные триплеты:', triples)
        console.log('Преобразованные узлы:', nodes)
        console.log('Преобразованные рёбра:', edges)

        // Интеграция данных в Cytoscape
        if (!cy) {
          throw new Error('Cytoscape не инициализирован')
        }

        if (nodes.length === 0 && edges.length === 0) {
          $q.notify({
            type: 'warning',
            message: 'Данные для визуализации не найдены'
          })
          return
        }

        // Очистка существующих элементов графа перед загрузкой новых данных
        cy.elements().remove()

        // Добавление узлов и рёбер в Cytoscape
        const elements = [...nodes, ...edges]
        cy.add(elements)

        // Применение layout после добавления элементов
        cy.layout({
          name: 'dagre',
          rankDir: 'TB',
          spacingFactor: 1.5
        }).run()

        // Уведомление об успешной загрузке
        let message = `Визуализация загружена: ${nodes.length} узлов, ${edges.length} рёбер`
        if (truncated) {
          message += ` (граф был ограничен для производительности)`
        }
        $q.notify({
          type: truncated ? 'warning' : 'positive',
          message: message,
          timeout: truncated ? 4000 : 2000
        })

      } catch (err) {
        console.error('Ошибка загрузки данных:', err)
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
      maxElements,
      loadGraphData
    }
  }
})
</script>

