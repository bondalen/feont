const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'sparql', component: () => import('pages/SparqlPage.vue') },
      { path: 'graph', component: () => import('pages/GraphPage.vue') },
      { path: 'ontology', component: () => import('pages/OntologyPage.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes

