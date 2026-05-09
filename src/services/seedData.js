import { 
  studentService, 
  knowledgeService, 
  planService, 
  recitationService, 
  dictationService, 
  homeworkService 
} from './storage'

const SEED_KEY = 'study_room_seeded_v1'

export function isSeeded() {
  return localStorage.getItem(SEED_KEY) === 'true'
}

export function seedData() {
  if (isSeeded()) return
  
  const students = [
    { name: '张三', grade: '七年级', studyStage: '基础阶段', contact: '13800138001' },
    { name: '李四', grade: '七年级', studyStage: '强化阶段', contact: '13800138002' },
    { name: '王五', grade: '八年级', studyStage: '基础阶段', contact: '13800138003' },
    { name: '赵六', grade: '九年级', studyStage: '冲刺阶段', contact: '13800138004' }
  ]
  
  const createdStudents = students.map(s => studentService.add(s))
  
  const knowledges = [
    { title: '一元一次方程', grade: '七年级', subject: '数学', content: '只含有一个未知数，并且未知数的最高次数是1的整式方程', isImportant: true },
    { title: '整式的加减', grade: '七年级', subject: '数学', content: '同类项的合并，去括号法则', isImportant: false },
    { title: '古诗默写', grade: '七年级', subject: '语文', content: '《观沧海》曹操', isImportant: true },
    { title: '一般现在时', grade: '七年级', subject: '英语', content: '表示经常性、习惯性的动作或状态', isImportant: true },
    { title: '勾股定理', grade: '八年级', subject: '数学', content: '直角三角形两直角边的平方和等于斜边的平方', isImportant: true },
    { title: '一元二次方程', grade: '九年级', subject: '数学', content: '形如ax² + bx + c = 0 (a≠0) 的方程', isImportant: true }
  ]
  
  knowledges.forEach(k => knowledgeService.add(k))
  
  const today = new Date()
  const monday = new Date(today)
  monday.setDate(today.getDate() - today.getDay() + 1)
  const weekStart = monday.toISOString().split('T')[0]
  
  const plans = [
    {
      studentId: createdStudents[0].id,
      weekStart: weekStart,
      weekEnd: new Date(monday.setDate(monday.getDate() + 6)).toISOString().split('T')[0],
      tasks: [
        { day: '周一', content: '完成数学练习册P15-16', knowledge: '一元一次方程', completed: true },
        { day: '周二', content: '背诵语文古诗', knowledge: '古诗默写', completed: true },
        { day: '周三', content: '英语单词Unit 5', knowledge: '一般现在时', completed: false },
        { day: '周四', content: '数学错题整理', knowledge: '整式的加减', completed: false },
        { day: '周五', content: '复习本周知识点', knowledge: '', completed: false }
      ]
    },
    {
      studentId: createdStudents[1].id,
      weekStart: weekStart,
      weekEnd: new Date(monday.setDate(monday.getDate() + 6)).toISOString().split('T')[0],
      tasks: [
        { day: '周一', content: '英语语法练习', knowledge: '一般现在时', completed: true },
        { day: '周二', content: '数学课外题', knowledge: '一元一次方程', completed: true },
        { day: '周三', content: '语文阅读理解', knowledge: '', completed: true },
        { day: '周四', content: '复习英语单词', knowledge: '', completed: false },
        { day: '周五', content: '周测准备', knowledge: '', completed: false }
      ]
    }
  ]
  
  plans.forEach(p => planService.add(p))
  
  const todayStr = today.toISOString().split('T')[0]
  
  const recitations = [
    { studentId: createdStudents[0].id, subject: '语文', content: '《观沧海》曹操', status: 'skilled', checkDate: todayStr, remark: '背诵流畅，无错误' },
    { studentId: createdStudents[0].id, subject: '英语', content: 'Unit 4 单词', status: 'not_skilled', checkDate: todayStr, remark: '部分单词发音不准确' },
    { studentId: createdStudents[1].id, subject: '语文', content: '《闻王昌龄左迁龙标遥有此寄》', status: 'skilled', checkDate: todayStr, remark: '' },
    { studentId: createdStudents[2].id, subject: '语文', content: '《赤壁》杜牧', status: 'not_started', checkDate: todayStr, remark: '' }
  ]
  
  recitations.forEach(r => recitationService.add(r))
  
  const dictations = [
    { studentId: createdStudents[0].id, subject: '语文', content: '古诗词名句默写', correctCount: 18, totalCount: 20, accuracy: 90, errors: '第3、8题错误', checkDate: todayStr },
    { studentId: createdStudents[0].id, subject: '英语', content: 'Unit 5 单词听写', correctCount: 15, totalCount: 20, accuracy: 75, errors: '5个单词拼写错误', checkDate: todayStr },
    { studentId: createdStudents[1].id, subject: '英语', content: '短语听写', correctCount: 8, totalCount: 10, accuracy: 80, errors: '', checkDate: todayStr },
    { studentId: createdStudents[2].id, subject: '语文', content: '易错字听写', correctCount: 5, totalCount: 10, accuracy: 50, errors: '多个字形错误', checkDate: todayStr }
  ]
  
  dictations.forEach(d => dictationService.add(d))
  
  const homeworks = [
    { studentId: createdStudents[0].id, subject: '数学', content: '练习册P20-21', completed: true, date: todayStr },
    { studentId: createdStudents[0].id, subject: '英语', content: '抄写单词3遍', completed: true, date: todayStr },
    { studentId: createdStudents[1].id, subject: '语文', content: '背诵课文', completed: true, date: todayStr },
    { studentId: createdStudents[1].id, subject: '数学', content: '错题本整理', completed: false, date: todayStr },
    { studentId: createdStudents[2].id, subject: '英语', content: '语法练习', completed: true, date: todayStr },
    { studentId: createdStudents[3].id, subject: '数学', content: '模拟试卷', completed: false, date: todayStr }
  ]
  
  homeworks.forEach(h => homeworkService.add(h))
  
  localStorage.setItem(SEED_KEY, 'true')
}
