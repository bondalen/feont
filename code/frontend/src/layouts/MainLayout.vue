<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          FEONT - Knowledge Graph Application
        </q-toolbar-title>

        <q-space />

        <q-btn flat dense round icon="menu" aria-label="Menu" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header>
          Навигация
        </q-item-label>

        <EssentialLink
          v-for="link in essentialLinks"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from 'vue'
import EssentialLink from 'components/EssentialLink.vue'

const linksList = [
  {
    title: 'Главная',
    caption: 'Главная страница',
    icon: 'home',
    link: '/'
  },
  {
    title: 'SPARQL',
    caption: 'SPARQL запросы',
    icon: 'code',
    link: '/sparql'
  },
  {
    title: 'Граф знаний',
    caption: 'Визуализация графа',
    icon: 'account_tree',
    link: '/graph'
  },
  {
    title: 'Онтология',
    caption: 'Структура онтологии',
    icon: 'schema',
    link: '/ontology'
  }
]

export default defineComponent({
  name: 'MainLayout',

  components: {
    EssentialLink
  },

  setup () {
    const leftDrawerOpen = ref(false)

    return {
      essentialLinks: linksList,
      leftDrawerOpen
    }
  }
})
</script>

