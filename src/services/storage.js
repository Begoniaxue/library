const API_BASE = '/api'
const STORAGE_PREFIX = 'study_room_'

async function request(url, options = {}) {
  const config = {
    ...options,
    headers: {
      ...options.headers
    }
  }
  
  if (config.method && config.method !== 'GET' && config.body) {
    config.headers['Content-Type'] = 'application/json'
  }
  
  try {
    const response = await fetch(API_BASE + url, config)
    
    if (!response.ok) {
      const errorText = await response.text().catch(() => '')
      throw new Error(`HTTP error! status: ${response.status}, ${errorText}`)
    }
    
    if (response.status === 204) {
      return null
    }
    
    return response.json()
  } catch (error) {
    console.error('Request failed:', url, error)
    throw error
  }
}

function getStorageKey(key) {
  return STORAGE_PREFIX + key
}

function getFromStorage(key, defaultValue = null) {
  try {
    const data = localStorage.getItem(getStorageKey(key))
    return data ? JSON.parse(data) : defaultValue
  } catch (error) {
    console.error('Failed to get from storage:', key, error)
    return defaultValue
  }
}

function saveToStorage(key, data) {
  try {
    localStorage.setItem(getStorageKey(key), JSON.stringify(data))
  } catch (error) {
    console.error('Failed to save to storage:', key, error)
  }
}

function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

const STUDENTS_KEY = 'students'
const KNOWLEDGE_KEY = 'knowledge'
const PLANS_KEY = 'plans'
const RECITATIONS_KEY = 'recitations'
const DICTATIONS_KEY = 'dictations'
const HOMEWORKS_KEY = 'homeworks'

async function getAllWithFallback(apiUrl, storageKey) {
  try {
    const result = await request(apiUrl)
    saveToStorage(storageKey, result)
    return result
  } catch (error) {
    console.warn(`Using fallback for ${apiUrl}:`, error.message)
    return getFromStorage(storageKey, [])
  }
}

async function getByIdWithFallback(apiUrl, storageKey, id) {
  try {
    const result = await request(`${apiUrl}/${id}`)
    const allItems = await getAllWithFallback(apiUrl, storageKey)
    const updated = allItems.map(item => item.id === id ? result : item)
    saveToStorage(storageKey, updated)
    return result
  } catch (error) {
    console.warn(`Using fallback for ${apiUrl}/${id}:`, error.message)
    const allItems = getFromStorage(storageKey, [])
    return allItems.find(item => item.id === id) || null
  }
}

async function createWithFallback(apiUrl, storageKey, data, transformData) {
  try {
    const result = await request(apiUrl, { method: 'POST', body: JSON.stringify(data) })
    const allItems = await getAllWithFallback(apiUrl, storageKey)
    allItems.push(result)
    saveToStorage(storageKey, allItems)
    return result
  } catch (error) {
    console.warn(`Using fallback for create ${apiUrl}:`, error.message)
    const allItems = getFromStorage(storageKey, [])
    const newItem = transformData ? transformData(data) : { ...data, id: generateId(), createdAt: new Date().toISOString() }
    allItems.push(newItem)
    saveToStorage(storageKey, allItems)
    return newItem
  }
}

async function updateWithFallback(apiUrl, storageKey, id, data) {
  try {
    const result = await request(`${apiUrl}/${id}`, { method: 'PUT', body: JSON.stringify(data) })
    const allItems = await getAllWithFallback(apiUrl, storageKey)
    const updated = allItems.map(item => item.id === id ? result : item)
    saveToStorage(storageKey, updated)
    return result
  } catch (error) {
    console.warn(`Using fallback for update ${apiUrl}/${id}:`, error.message)
    const allItems = getFromStorage(storageKey, [])
    const updated = allItems.map(item => item.id === id ? { ...item, ...data } : item)
    saveToStorage(storageKey, updated)
    return updated.find(item => item.id === id)
  }
}

async function deleteWithFallback(apiUrl, storageKey, id) {
  try {
    await request(`${apiUrl}/${id}`, { method: 'DELETE' })
    const allItems = await getAllWithFallback(apiUrl, storageKey)
    const updated = allItems.filter(item => item.id !== id)
    saveToStorage(storageKey, updated)
    return true
  } catch (error) {
    console.warn(`Using fallback for delete ${apiUrl}/${id}:`, error.message)
    const allItems = getFromStorage(storageKey, [])
    const updated = allItems.filter(item => item.id !== id)
    saveToStorage(storageKey, updated)
    return true
  }
}

export const api = {
  get: (url) => request(url),
  post: (url, data) => request(url, { method: 'POST', body: JSON.stringify(data) }),
  put: (url, data) => request(url, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (url) => request(url, { method: 'DELETE' })
}

export const studentService = {
  async getAll() {
    return getAllWithFallback('/students', STUDENTS_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/students', STUDENTS_KEY, id)
  },
  
  async search(name = '', grade = '') {
    try {
      const params = new URLSearchParams()
      if (name) params.append('name', name)
      if (grade) params.append('grade', grade)
      const query = params.toString()
      return await request(`/students${query ? '?' + query : ''}`)
    } catch (error) {
      console.warn('Using fallback for student search:', error.message)
      const allItems = getFromStorage(STUDENTS_KEY, [])
      return allItems.filter(item => {
        const nameMatch = !name || item.name.includes(name)
        const gradeMatch = !grade || item.grade === grade
        return nameMatch && gradeMatch
      })
    }
  },
  
  async add(student) {
    return createWithFallback('/students', STUDENTS_KEY, student)
  },
  
  async update(id, student) {
    return updateWithFallback('/students', STUDENTS_KEY, id, student)
  },
  
  async delete(id) {
    return deleteWithFallback('/students', STUDENTS_KEY, id)
  }
}

export const knowledgeService = {
  async getAll() {
    return getAllWithFallback('/knowledge', KNOWLEDGE_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/knowledge', KNOWLEDGE_KEY, id)
  },
  
  async add(knowledge) {
    return createWithFallback('/knowledge', KNOWLEDGE_KEY, knowledge)
  },
  
  async update(id, knowledge) {
    return updateWithFallback('/knowledge', KNOWLEDGE_KEY, id, knowledge)
  },
  
  async delete(id) {
    return deleteWithFallback('/knowledge', KNOWLEDGE_KEY, id)
  },
  
  async getByGradeAndSubject(grade, subject) {
    try {
      const params = new URLSearchParams()
      if (grade) params.append('grade', grade)
      if (subject) params.append('subject', subject)
      const query = params.toString()
      return await request(`/knowledge${query ? '?' + query : ''}`)
    } catch (error) {
      console.warn('Using fallback for knowledge filter:', error.message)
      const allItems = getFromStorage(KNOWLEDGE_KEY, [])
      return allItems.filter(item => {
        const gradeMatch = !grade || item.grade === grade
        const subjectMatch = !subject || item.subject === subject
        return gradeMatch && subjectMatch
      })
    }
  },
  
  async toggleImportant(id) {
    try {
      const result = await request(`/knowledge/${id}/toggle-important`, { method: 'POST', body: JSON.stringify({}) })
      const allItems = await getAllWithFallback('/knowledge', KNOWLEDGE_KEY)
      const updated = allItems.map(item => item.id === id ? result : item)
      saveToStorage(KNOWLEDGE_KEY, updated)
      return result
    } catch (error) {
      console.warn('Using fallback for toggle important:', error.message)
      const allItems = getFromStorage(KNOWLEDGE_KEY, [])
      const updated = allItems.map(item => {
        if (item.id === id) {
          return { ...item, isImportant: !item.isImportant }
        }
        return item
      })
      saveToStorage(KNOWLEDGE_KEY, updated)
      return updated.find(item => item.id === id)
    }
  }
}

export const planService = {
  async getAll() {
    return getAllWithFallback('/plans', PLANS_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/plans', PLANS_KEY, id)
  },
  
  async add(plan) {
    return createWithFallback('/plans', PLANS_KEY, plan)
  },
  
  async update(id, plan) {
    return updateWithFallback('/plans', PLANS_KEY, id, plan)
  },
  
  async delete(id) {
    return deleteWithFallback('/plans', PLANS_KEY, id)
  },
  
  async getByStudent(studentId) {
    try {
      return await request(`/plans/student/${studentId}`)
    } catch (error) {
      console.warn('Using fallback for plans by student:', error.message)
      const allItems = getFromStorage(PLANS_KEY, [])
      return allItems.filter(item => item.studentId === studentId)
    }
  },
  
  async getByWeek(studentId, weekStart) {
    try {
      const params = new URLSearchParams()
      if (weekStart) params.append('weekStart', weekStart)
      const query = params.toString()
      return await request(`/plans/student/${studentId}${query ? '?' + query : ''}`)
    } catch (error) {
      console.warn('Using fallback for plans by week:', error.message)
      const allItems = getFromStorage(PLANS_KEY, [])
      return allItems.filter(item => {
        const studentMatch = item.studentId === studentId
        const weekMatch = !weekStart || item.weekStart === weekStart
        return studentMatch && weekMatch
      })
    }
  }
}

export const recitationService = {
  async getAll() {
    return getAllWithFallback('/recitations', RECITATIONS_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/recitations', RECITATIONS_KEY, id)
  },
  
  async add(record) {
    return createWithFallback('/recitations', RECITATIONS_KEY, record)
  },
  
  async update(id, record) {
    return updateWithFallback('/recitations', RECITATIONS_KEY, id, record)
  },
  
  async delete(id) {
    return deleteWithFallback('/recitations', RECITATIONS_KEY, id)
  },
  
  async getByStudent(studentId) {
    try {
      return await request(`/recitations/student/${studentId}`)
    } catch (error) {
      console.warn('Using fallback for recitations by student:', error.message)
      const allItems = getFromStorage(RECITATIONS_KEY, [])
      return allItems.filter(item => item.studentId === studentId)
    }
  }
}

export const dictationService = {
  async getAll() {
    return getAllWithFallback('/dictations', DICTATIONS_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/dictations', DICTATIONS_KEY, id)
  },
  
  async add(record) {
    return createWithFallback('/dictations', DICTATIONS_KEY, record)
  },
  
  async update(id, record) {
    return updateWithFallback('/dictations', DICTATIONS_KEY, id, record)
  },
  
  async delete(id) {
    return deleteWithFallback('/dictations', DICTATIONS_KEY, id)
  },
  
  async getByStudent(studentId) {
    try {
      return await request(`/dictations/student/${studentId}`)
    } catch (error) {
      console.warn('Using fallback for dictations by student:', error.message)
      const allItems = getFromStorage(DICTATIONS_KEY, [])
      return allItems.filter(item => item.studentId === studentId)
    }
  }
}

export const homeworkService = {
  async getAll() {
    return getAllWithFallback('/homeworks', HOMEWORKS_KEY)
  },
  
  async getById(id) {
    return getByIdWithFallback('/homeworks', HOMEWORKS_KEY, id)
  },
  
  async add(record) {
    return createWithFallback('/homeworks', HOMEWORKS_KEY, record)
  },
  
  async update(id, record) {
    return updateWithFallback('/homeworks', HOMEWORKS_KEY, id, record)
  },
  
  async delete(id) {
    return deleteWithFallback('/homeworks', HOMEWORKS_KEY, id)
  },
  
  async getByStudent(studentId) {
    try {
      return await request(`/homeworks/student/${studentId}`)
    } catch (error) {
      console.warn('Using fallback for homeworks by student:', error.message)
      const allItems = getFromStorage(HOMEWORKS_KEY, [])
      return allItems.filter(item => item.studentId === studentId)
    }
  },
  
  async getByDate(date) {
    try {
      return await request(`/homeworks/date/${date}`)
    } catch (error) {
      console.warn('Using fallback for homeworks by date:', error.message)
      const allItems = getFromStorage(HOMEWORKS_KEY, [])
      return allItems.filter(item => item.date === date)
    }
  }
}

export const constantsService = {
  async getConstants() {
    try {
      return await request('/constants')
    } catch (error) {
      console.warn('Using fallback for constants:', error.message)
      return null
    }
  },
  
  async getRecitationOptions(subject, grade) {
    try {
      const params = new URLSearchParams()
      params.append('subject', subject)
      params.append('grade', grade)
      return await request(`/constants/recitation-options?${params.toString()}`)
    } catch (error) {
      console.warn('Using fallback for recitation options:', error.message)
      return []
    }
  },
  
  async getRecitationById(id) {
    try {
      return await request(`/constants/recitation-options/${id}`)
    } catch (error) {
      console.warn('Using fallback for recitation by id:', error.message)
      return null
    }
  }
}

export const constants = {
  grades: ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级', '高一', '高二', '高三'],
  subjects: ['语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治'],
  studyStages: ['基础阶段', '强化阶段', '冲刺阶段'],
  recitationStatuses: {
    SKILLED: { value: 'skilled', label: '熟练', color: 'green' },
    NOT_SKILLED: { value: 'not_skilled', label: '不熟', color: 'yellow' },
    NOT_STARTED: { value: 'not_started', label: '未完成', color: 'red' }
  }
}
