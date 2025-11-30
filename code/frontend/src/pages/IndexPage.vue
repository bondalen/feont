<template>
  <q-page class="flex flex-center">
    <div class="q-pa-md" style="max-width: 800px; width: 100%">
      <q-card>
        <q-card-section>
          <div class="text-h6">FEONT - Knowledge Graph Application</div>
          <div class="text-subtitle2">Приложение для работы с графом знаний на основе RDF/SHACL</div>
        </q-card-section>

        <q-card-section>
          <q-list>
            <q-item>
              <q-item-section avatar>
                <q-icon name="storage" color="primary" />
              </q-item-section>
              <q-item-section>
                <q-item-label>Хранилище</q-item-label>
                <q-item-label caption>Apache Jena TDB2</q-item-label>
              </q-item-section>
            </q-item>

            <q-item>
              <q-item-section avatar>
                <q-icon name="code" color="primary" />
              </q-item-section>
              <q-item-section>
                <q-item-label>SPARQL</q-item-label>
                <q-item-label caption>SPARQL 1.1 Query и Update</q-item-label>
              </q-item-section>
            </q-item>

            <q-item>
              <q-item-section avatar>
                <q-icon name="schema" color="primary" />
              </q-item-section>
              <q-item-section>
                <q-item-label>Онтология</q-item-label>
                <q-item-label caption>RDFS, OWL 2, SHACL</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-section>
          <div class="text-subtitle2 q-mb-sm">Основные возможности:</div>
          <q-list dense>
            <q-item>
              <q-item-section avatar>
                <q-icon name="check_circle" color="positive" size="sm" />
              </q-item-section>
              <q-item-section>Выполнение SPARQL запросов</q-item-section>
            </q-item>
            <q-item>
              <q-item-section avatar>
                <q-icon name="check_circle" color="positive" size="sm" />
              </q-item-section>
              <q-item-section>Визуализация графа знаний</q-item-section>
            </q-item>
            <q-item>
              <q-item-section avatar>
                <q-icon name="check_circle" color="positive" size="sm" />
              </q-item-section>
              <q-item-section>Работа с онтологией</q-item-section>
            </q-item>
            <q-item>
              <q-item-section avatar>
                <q-icon name="check_circle" color="positive" size="sm" />
              </q-item-section>
              <q-item-section>Управление данными через SPARQL Update</q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="SPARQL" color="primary" to="/sparql" />
          <q-btn flat label="Граф" color="primary" to="/graph" />
          <q-btn flat label="Онтология" color="primary" to="/ontology" />
        </q-card-actions>
      </q-card>
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'IndexPage'
})
</script>

