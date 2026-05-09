const STORAGE_KEYS = {
  STUDENTS: 'study_room_students',
  KNOWLEDGE: 'study_room_knowledge',
  PLANS: 'study_room_plans',
  RECITATION: 'study_room_recitation',
  DICTATION: 'study_room_dictation',
  HOMEWORK: 'study_room_homework'
}

function getData(key, defaultValue = []) {
  try {
    const data = localStorage.getItem(key)
    return data ? JSON.parse(data) : defaultValue
  } catch {
    return defaultValue
  }
}

function setData(key, data) {
  try {
    localStorage.setItem(key, JSON.stringify(data))
    return true
  } catch {
    return false
  }
}

function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

export const studentService = {
  getAll() {
    return getData(STORAGE_KEYS.STUDENTS, [])
  },
  
  getById(id) {
    return this.getAll().find(s => s.id === id)
  },
  
  add(student) {
    const students = this.getAll()
    const newStudent = {
      id: generateId(),
      ...student,
      createdAt: new Date().toISOString()
    }
    students.push(newStudent)
    setData(STORAGE_KEYS.STUDENTS, students)
    return newStudent
  },
  
  update(id, student) {
    const students = this.getAll()
    const index = students.findIndex(s => s.id === id)
    if (index !== -1) {
      students[index] = { ...students[index], ...student, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.STUDENTS, students)
      return students[index]
    }
    return null
  },
  
  delete(id) {
    const students = this.getAll().filter(s => s.id !== id)
    setData(STORAGE_KEYS.STUDENTS, students)
    return true
  },
  
  search(name = '', grade = '') {
    let students = this.getAll()
    if (name) {
      students = students.filter(s => s.name.includes(name))
    }
    if (grade) {
      students = students.filter(s => s.grade === grade)
    }
    return students
  }
}

export const knowledgeService = {
  getAll() {
    return getData(STORAGE_KEYS.KNOWLEDGE, [])
  },
  
  getById(id) {
    return this.getAll().find(k => k.id === id)
  },
  
  add(knowledge) {
    const items = this.getAll()
    const newItem = {
      id: generateId(),
      ...knowledge,
      isImportant: false,
      createdAt: new Date().toISOString()
    }
    items.push(newItem)
    setData(STORAGE_KEYS.KNOWLEDGE, items)
    return newItem
  },
  
  update(id, knowledge) {
    const items = this.getAll()
    const index = items.findIndex(k => k.id === id)
    if (index !== -1) {
      items[index] = { ...items[index], ...knowledge, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.KNOWLEDGE, items)
      return items[index]
    }
    return null
  },
  
  delete(id) {
    const items = this.getAll().filter(k => k.id !== id)
    setData(STORAGE_KEYS.KNOWLEDGE, items)
    return true
  },
  
  getByGradeAndSubject(grade, subject) {
    let items = this.getAll()
    if (grade) items = items.filter(k => k.grade === grade)
    if (subject) items = items.filter(k => k.subject === subject)
    return items
  },
  
  toggleImportant(id) {
    const items = this.getAll()
    const item = items.find(k => k.id === id)
    if (item) {
      item.isImportant = !item.isImportant
      setData(STORAGE_KEYS.KNOWLEDGE, items)
      return item
    }
    return null
  }
}

export const planService = {
  getAll() {
    return getData(STORAGE_KEYS.PLANS, [])
  },
  
  getById(id) {
    return this.getAll().find(p => p.id === id)
  },
  
  add(plan) {
    const items = this.getAll()
    const newItem = {
      id: generateId(),
      ...plan,
      tasks: plan.tasks || [],
      completed: false,
      createdAt: new Date().toISOString()
    }
    items.push(newItem)
    setData(STORAGE_KEYS.PLANS, items)
    return newItem
  },
  
  update(id, plan) {
    const items = this.getAll()
    const index = items.findIndex(p => p.id === id)
    if (index !== -1) {
      items[index] = { ...items[index], ...plan, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.PLANS, items)
      return items[index]
    }
    return null
  },
  
  delete(id) {
    const items = this.getAll().filter(p => p.id !== id)
    setData(STORAGE_KEYS.PLANS, items)
    return true
  },
  
  getByStudent(studentId) {
    return this.getAll().filter(p => p.studentId === studentId)
  },
  
  getByWeek(studentId, weekStart) {
    const items = this.getByStudent(studentId)
    if (weekStart) {
      return items.filter(p => p.weekStart === weekStart)
    }
    return items
  }
}

export const recitationService = {
  getAll() {
    return getData(STORAGE_KEYS.RECITATION, [])
  },
  
  getById(id) {
    return this.getAll().find(r => r.id === id)
  },
  
  add(record) {
    const items = this.getAll()
    const newItem = {
      id: generateId(),
      ...record,
      createdAt: new Date().toISOString()
    }
    items.push(newItem)
    setData(STORAGE_KEYS.RECITATION, items)
    return newItem
  },
  
  update(id, record) {
    const items = this.getAll()
    const index = items.findIndex(r => r.id === id)
    if (index !== -1) {
      items[index] = { ...items[index], ...record, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.RECITATION, items)
      return items[index]
    }
    return null
  },
  
  delete(id) {
    const items = this.getAll().filter(r => r.id !== id)
    setData(STORAGE_KEYS.RECITATION, items)
    return true
  },
  
  getByStudent(studentId) {
    return this.getAll().filter(r => r.studentId === studentId)
  }
}

export const dictationService = {
  getAll() {
    return getData(STORAGE_KEYS.DICTATION, [])
  },
  
  getById(id) {
    return this.getAll().find(d => d.id === id)
  },
  
  add(record) {
    const items = this.getAll()
    const newItem = {
      id: generateId(),
      ...record,
      createdAt: new Date().toISOString()
    }
    items.push(newItem)
    setData(STORAGE_KEYS.DICTATION, items)
    return newItem
  },
  
  update(id, record) {
    const items = this.getAll()
    const index = items.findIndex(d => d.id === id)
    if (index !== -1) {
      items[index] = { ...items[index], ...record, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.DICTATION, items)
      return items[index]
    }
    return null
  },
  
  delete(id) {
    const items = this.getAll().filter(d => d.id !== id)
    setData(STORAGE_KEYS.DICTATION, items)
    return true
  },
  
  getByStudent(studentId) {
    return this.getAll().filter(d => d.studentId === studentId)
  }
}

export const homeworkService = {
  getAll() {
    return getData(STORAGE_KEYS.HOMEWORK, [])
  },
  
  getById(id) {
    return this.getAll().find(h => h.id === id)
  },
  
  add(record) {
    const items = this.getAll()
    const newItem = {
      id: generateId(),
      ...record,
      createdAt: new Date().toISOString()
    }
    items.push(newItem)
    setData(STORAGE_KEYS.HOMEWORK, items)
    return newItem
  },
  
  update(id, record) {
    const items = this.getAll()
    const index = items.findIndex(h => h.id === id)
    if (index !== -1) {
      items[index] = { ...items[index], ...record, updatedAt: new Date().toISOString() }
      setData(STORAGE_KEYS.HOMEWORK, items)
      return items[index]
    }
    return null
  },
  
  delete(id) {
    const items = this.getAll().filter(h => h.id !== id)
    setData(STORAGE_KEYS.HOMEWORK, items)
    return true
  },
  
  getByStudent(studentId) {
    return this.getAll().filter(h => h.studentId === studentId)
  },
  
  getByDate(date) {
    return this.getAll().filter(h => h.date === date)
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
