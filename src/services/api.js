const API_BASE = '/api'

async function request(url, options = {}) {
  const response = await fetch(API_BASE + url, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  })
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }
  
  if (response.status === 204) {
    return null
  }
  
  return response.json()
}

export const api = {
  get: (url) => request(url),
  post: (url, data) => request(url, { method: 'POST', body: JSON.stringify(data) }),
  put: (url, data) => request(url, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (url) => request(url, { method: 'DELETE' })
}

export const studentService = {
  async getAll() {
    return api.get('/students')
  },
  
  async getById(id) {
    return api.get(`/students/${id}`)
  },
  
  async search(name = '', grade = '') {
    const params = new URLSearchParams()
    if (name) params.append('name', name)
    if (grade) params.append('grade', grade)
    const query = params.toString()
    return api.get(`/students${query ? '?' + query : ''}`)
  },
  
  async add(student) {
    return api.post('/students', student)
  },
  
  async update(id, student) {
    return api.put(`/students/${id}`, student)
  },
  
  async delete(id) {
    await api.delete(`/students/${id}`)
    return true
  }
}

export const knowledgeService = {
  async getAll() {
    return api.get('/knowledge')
  },
  
  async getById(id) {
    return api.get(`/knowledge/${id}`)
  },
  
  async add(knowledge) {
    return api.post('/knowledge', knowledge)
  },
  
  async update(id, knowledge) {
    return api.put(`/knowledge/${id}`, knowledge)
  },
  
  async delete(id) {
    await api.delete(`/knowledge/${id}`)
    return true
  },
  
  async getByGradeAndSubject(grade, subject) {
    const params = new URLSearchParams()
    if (grade) params.append('grade', grade)
    if (subject) params.append('subject', subject)
    const query = params.toString()
    return api.get(`/knowledge${query ? '?' + query : ''}`)
  },
  
  async toggleImportant(id) {
    return api.post(`/knowledge/${id}/toggle-important`, {})
  }
}

export const planService = {
  async getAll() {
    return api.get('/plans')
  },
  
  async getById(id) {
    return api.get(`/plans/${id}`)
  },
  
  async add(plan) {
    return api.post('/plans', plan)
  },
  
  async update(id, plan) {
    return api.put(`/plans/${id}`, plan)
  },
  
  async delete(id) {
    await api.delete(`/plans/${id}`)
    return true
  },
  
  async getByStudent(studentId) {
    return api.get(`/plans/student/${studentId}`)
  },
  
  async getByWeek(studentId, weekStart) {
    const params = new URLSearchParams()
    if (weekStart) params.append('weekStart', weekStart)
    const query = params.toString()
    return api.get(`/plans/student/${studentId}${query ? '?' + query : ''}`)
  }
}

export const recitationService = {
  async getAll() {
    return api.get('/recitations')
  },
  
  async getById(id) {
    return api.get(`/recitations/${id}`)
  },
  
  async add(record) {
    return api.post('/recitations', record)
  },
  
  async update(id, record) {
    return api.put(`/recitations/${id}`, record)
  },
  
  async delete(id) {
    await api.delete(`/recitations/${id}`)
    return true
  },
  
  async getByStudent(studentId) {
    return api.get(`/recitations/student/${studentId}`)
  }
}

export const dictationService = {
  async getAll() {
    return api.get('/dictations')
  },
  
  async getById(id) {
    return api.get(`/dictations/${id}`)
  },
  
  async add(record) {
    return api.post('/dictations', record)
  },
  
  async update(id, record) {
    return api.put(`/dictations/${id}`, record)
  },
  
  async delete(id) {
    await api.delete(`/dictations/${id}`)
    return true
  },
  
  async getByStudent(studentId) {
    return api.get(`/dictations/student/${studentId}`)
  }
}

export const homeworkService = {
  async getAll() {
    return api.get('/homeworks')
  },
  
  async getById(id) {
    return api.get(`/homeworks/${id}`)
  },
  
  async add(record) {
    return api.post('/homeworks', record)
  },
  
  async update(id, record) {
    return api.put(`/homeworks/${id}`, record)
  },
  
  async delete(id) {
    await api.delete(`/homeworks/${id}`)
    return true
  },
  
  async getByStudent(studentId) {
    return api.get(`/homeworks/student/${studentId}`)
  },
  
  async getByDate(date) {
    return api.get(`/homeworks/date/${date}`)
  }
}

export const constantsService = {
  async getConstants() {
    return api.get('/constants')
  },
  
  async getRecitationOptions(subject, grade) {
    const params = new URLSearchParams()
    params.append('subject', subject)
    params.append('grade', grade)
    return api.get(`/constants/recitation-options?${params.toString()}`)
  },
  
  async getRecitationById(id) {
    return api.get(`/constants/recitation-options/${id}`)
  }
}

export const stickyNoteService = {
  async getAll() {
    return api.get('/sticky-notes')
  },
  
  async create(x, y, content = '') {
    return api.post('/sticky-notes', { x, y, content })
  },
  
  async updatePosition(id, x, y) {
    return api.put(`/sticky-notes/${id}/position`, { x, y })
  },
  
  async updateContent(id, content) {
    return api.put(`/sticky-notes/${id}/content`, { content })
  },
  
  async delete(id) {
    await api.delete(`/sticky-notes/${id}`)
    return true
  }
}

export const blogService = {
  async getAll() {
    return api.get('/blogs')
  },
  
  async getById(id) {
    return api.get(`/blogs/${id}`)
  },
  
  async add(blog) {
    return api.post('/blogs', blog)
  },
  
  async delete(id) {
    await api.delete(`/blogs/${id}`)
    return true
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
