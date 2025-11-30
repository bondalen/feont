/**
 * Сервис для работы с SPARQL endpoint
 */

const SPARQL_ENDPOINT = process.env.VUE_APP_SPARQL_ENDPOINT || 'http://localhost:8083/ds/sparql'
const UPDATE_ENDPOINT = process.env.VUE_APP_UPDATE_ENDPOINT || 'http://localhost:8083/ds/update'
const DATA_ENDPOINT = process.env.VUE_APP_DATA_ENDPOINT || 'http://localhost:8083/ds/data'

/**
 * Выполнение SPARQL SELECT запроса
 * @param {string} query - SPARQL запрос
 * @returns {Promise<Object>} Результат в формате JSON
 */
export async function executeSelect(query) {
  const response = await fetch(SPARQL_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `query=${encodeURIComponent(query)}&format=json`
  })

  if (!response.ok) {
    throw new Error(`SPARQL запрос не выполнен: ${response.statusText}`)
  }

  return await response.json()
}

/**
 * Выполнение SPARQL CONSTRUCT запроса
 * @param {string} query - SPARQL запрос
 * @returns {Promise<string>} Результат в формате Turtle
 */
export async function executeConstruct(query) {
  const response = await fetch(SPARQL_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `query=${encodeURIComponent(query)}&format=turtle`
  })

  if (!response.ok) {
    throw new Error(`SPARQL запрос не выполнен: ${response.statusText}`)
  }

  return await response.text()
}

/**
 * Выполнение SPARQL ASK запроса
 * @param {string} query - SPARQL запрос
 * @returns {Promise<boolean>} Результат запроса
 */
export async function executeAsk(query) {
  const response = await fetch(SPARQL_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `query=${encodeURIComponent(query)}&format=json`
  })

  if (!response.ok) {
    throw new Error(`SPARQL запрос не выполнен: ${response.statusText}`)
  }

  const result = await response.json()
  return result.boolean || false
}

/**
 * Выполнение SPARQL DESCRIBE запроса
 * @param {string} query - SPARQL запрос
 * @returns {Promise<string>} Результат в формате Turtle
 */
export async function executeDescribe(query) {
  const response = await fetch(SPARQL_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `query=${encodeURIComponent(query)}&format=turtle`
  })

  if (!response.ok) {
    throw new Error(`SPARQL запрос не выполнен: ${response.statusText}`)
  }

  return await response.text()
}

/**
 * Выполнение SPARQL Update запроса
 * @param {string} update - SPARQL Update запрос
 * @returns {Promise<void>}
 */
export async function executeUpdate(update) {
  const response = await fetch(UPDATE_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `update=${encodeURIComponent(update)}`
  })

  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(`SPARQL Update не выполнен: ${response.statusText} - ${errorText}`)
  }
}

/**
 * Получение данных из Named Graph
 * @param {string} graphUri - URI Named Graph
 * @param {string} format - Формат (turtle, rdf, json)
 * @returns {Promise<string>} Данные в указанном формате
 */
export async function getGraphData(graphUri, format = 'turtle') {
  const response = await fetch(`${DATA_ENDPOINT}?graph=${encodeURIComponent(graphUri)}&format=${format}`)

  if (!response.ok) {
    throw new Error(`Не удалось получить данные: ${response.statusText}`)
  }

  return await response.text()
}

/**
 * Получение информации о доступных Named Graphs
 * @returns {Promise<Array<string>>} Массив URI Named Graphs
 */
export async function getNamedGraphs() {
  const query = `
    SELECT DISTINCT ?g
    WHERE {
      GRAPH ?g { ?s ?p ?o }
    }
  `
  
  try {
    const result = await executeSelect(query)
    return result.results.bindings.map(binding => binding.g.value)
  } catch (error) {
    console.error('Ошибка получения Named Graphs:', error)
    return []
  }
}

