<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">学情分析</h2>
      <el-select
        v-model="selectedStudentId"
        placeholder="选择学员"
        style="width: 200px"
        clearable
      >
        <el-option
          v-for="s in students"
          :key="s.id"
          :label="s.name"
          :value="s.id"
        />
      </el-select>
    </div>
    
    <el-row :gutter="20" v-if="selectedStudentId">
      <el-col :span="6" v-for="item in studentStatCards" :key="item.title">
        <el-card shadow="hover">
          <div class="text-center">
            <p class="text-sm text-gray-500">{{ item.title }}</p>
            <el-progress
              :percentage="item.value"
              :stroke-width="16"
              :color="item.color"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-semibold">学员学情概览</span>
          </template>
          
          <el-table :data="students" stripe style="width: 100%">
            <el-table-column prop="name" label="学员" min-width="120">
              <template #default="{ row }">
                <div class="flex items-center space-x-2">
                  <el-avatar :size="28" class="bg-blue-100">
                    <span class="text-blue-600 text-xs font-medium">{{ row.name.charAt(0) }}</span>
                  </el-avatar>
                  <span class="font-medium">{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="周计划" width="130">
              <template #default="{ row }">
                <el-progress 
                  :percentage="getStudentStats(row.id).planCompletionRate" 
                  :stroke-width="8"
                  :show-text="false"
                />
                <div class="text-xs text-gray-500 text-center">
                  {{ getStudentStats(row.id).planCompletionRate }}%
                </div>
              </template>
            </el-table-column>
            <el-table-column label="背诵" width="130">
              <template #default="{ row }">
                <el-progress 
                  :percentage="getStudentStats(row.id).recitationRate" 
                  :stroke-width="8"
                  :show-text="false"
                  color="#67c23a"
                />
                <div class="text-xs text-gray-500 text-center">
                  {{ getStudentStats(row.id).recitationRate }}%
                </div>
              </template>
            </el-table-column>
            <el-table-column label="默写" width="130">
              <template #default="{ row }">
                <el-progress 
                  :percentage="getStudentStats(row.id).avgDictationAccuracy" 
                  :stroke-width="8"
                  :show-text="false"
                  color="#9b59b6"
                />
                <div class="text-xs text-gray-500 text-center">
                  {{ getStudentStats(row.id).avgDictationAccuracy }}%
                </div>
              </template>
            </el-table-column>
            <el-table-column label="作业" width="130">
              <template #default="{ row }">
                <el-progress 
                  :percentage="getStudentStats(row.id).homeworkCompletionRate" 
                  :stroke-width="8"
                  :show-text="false"
                  color="#e67e22"
                />
                <div class="text-xs text-gray-500 text-center">
                  {{ getStudentStats(row.id).homeworkCompletionRate }}%
                </div>
              </template>
            </el-table-column>
            <el-table-column label="综合评级" width="100" fixed="right">
              <template #default="{ row }">
                <el-tag :type="getOverallGrade(getStudentStats(row.id)).type" effect="light">
                  {{ getOverallGrade(getStudentStats(row.id)).text }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!students.length" description="暂无学员数据" />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-semibold">薄弱知识点分析</span>
          </template>
          
          <el-empty v-if="!weakPoints.length" description="暂无薄弱知识点数据" />
          
          <el-timeline v-else>
            <el-timeline-item
              v-for="(point, index) in weakPoints"
              :key="index"
              :type="point.level === 'high' ? 'danger' : point.level === 'medium' ? 'warning' : 'info'"
            >
              <el-card shadow="never" class="mb-2">
                <div class="flex items-center justify-between">
                  <div>
                    <div class="flex items-center space-x-2 mb-1">
                      <el-tag type="info" size="small">{{ point.type }}</el-tag>
                      <span class="text-sm text-gray-500">{{ point.student }}</span>
                    </div>
                    <p class="font-medium text-gray-700">{{ point.content }}</p>
                  </div>
                  <el-tag 
                    :type="point.level === 'high' ? 'danger' : point.level === 'medium' ? 'warning' : 'info'" 
                    effect="light"
                    size="small"
                  >
                    {{ point.level === 'high' ? '重点关注' : point.level === 'medium' ? '需要加强' : '一般' }}
                  </el-tag>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card v-if="selectedStudentId">
      <template #header>
        <span class="font-semibold">学习记录详情</span>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="bg-blue-50">
            <div class="mb-3">
              <el-tag type="primary" effect="light">周计划 ({{ studentPlans.length }})</el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="plan in studentPlans.slice(0, 5)"
                :key="plan.id"
                :timestamp="plan.weekStart"
                placement="top"
                type="primary"
              >
                <div class="text-sm">
                  <p class="text-gray-700">{{ getPlanProgress(plan) }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-if="!studentPlans.length" description="暂无记录" :image-size="60" />
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="bg-green-50">
            <div class="mb-3">
              <el-tag type="success" effect="light">背诵检查 ({{ studentRecitations.length }})</el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="r in studentRecitations.slice(0, 5)"
                :key="r.id"
                :timestamp="r.checkDate"
                placement="top"
                :type="r.status === 'skilled' ? 'success' : r.status === 'not_skilled' ? 'warning' : 'danger'"
              >
                <div class="text-sm">
                  <p class="text-gray-700 truncate">{{ r.content }}</p>
                  <p class="text-xs text-gray-500">{{ r.status === 'skilled' ? '熟练' : r.status === 'not_skilled' ? '不熟' : '未完成' }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-if="!studentRecitations.length" description="暂无记录" :image-size="60" />
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="bg-purple-50">
            <div class="mb-3">
              <el-tag type="warning" effect="light">默写检查 ({{ studentDictations.length }})</el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="d in studentDictations.slice(0, 5)"
                :key="d.id"
                :timestamp="d.checkDate"
                placement="top"
                :type="d.accuracy >= 80 ? 'success' : d.accuracy >= 60 ? 'warning' : 'danger'"
              >
                <div class="text-sm">
                  <p class="text-gray-700 truncate">{{ d.content }}</p>
                  <p class="text-xs text-purple-600">正确率: {{ d.accuracy }}%</p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-if="!studentDictations.length" description="暂无记录" :image-size="60" />
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="bg-orange-50">
            <div class="mb-3">
              <el-tag type="danger" effect="light">作业检查 ({{ studentHomeworks.length }})</el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="h in studentHomeworks.slice(0, 5)"
                :key="h.id"
                :timestamp="h.date"
                placement="top"
                :type="h.completed ? 'success' : 'danger'"
              >
                <div class="text-sm">
                  <p class="text-gray-700 truncate">{{ h.subject }}: {{ h.content }}</p>
                  <p class="text-xs" :class="h.completed ? 'text-green-600' : 'text-red-600'">
                    {{ h.completed ? '已完成' : '未完成' }}
                  </p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-if="!studentHomeworks.length" description="暂无记录" :image-size="60" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { 
  studentService, 
  planService, 
  recitationService, 
  dictationService, 
  homeworkService 
} from '../services/storage'

const selectedStudentId = ref('')

const students = ref([])
const allPlans = ref([])
const allRecitations = ref([])
const allDictations = ref([])
const allHomeworks = ref([])

const loadAllData = async () => {
  try {
    const [studentsData, plansData, recitationsData, dictationsData, homeworksData] = await Promise.all([
      studentService.getAll(),
      planService.getAll(),
      recitationService.getAll(),
      dictationService.getAll(),
      homeworkService.getAll()
    ])
    students.value = studentsData
    allPlans.value = plansData
    allRecitations.value = recitationsData
    allDictations.value = dictationsData
    allHomeworks.value = homeworksData
  } catch (error) {
    console.error('Failed to load analysis data:', error)
  }
}

const getStudentStats = (studentId) => {
  const plans = allPlans.value.filter(p => p.studentId === studentId)
  const recitations = allRecitations.value.filter(r => r.studentId === studentId)
  const dictations = allDictations.value.filter(d => d.studentId === studentId)
  const homeworks = allHomeworks.value.filter(h => h.studentId === studentId)
  
  let planCompletionRate = 0
  if (plans.length > 0) {
    const totalTasks = plans.reduce((sum, p) => sum + (p.tasks?.length || 0), 0)
    const completedTasks = plans.reduce((sum, p) => sum + (p.tasks?.filter(t => t.completed).length || 0), 0)
    planCompletionRate = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0
  }
  
  let recitationRate = 0
  if (recitations.length > 0) {
    const skilled = recitations.filter(r => r.status === 'skilled').length
    recitationRate = Math.round((skilled / recitations.length) * 100)
  }
  
  let avgDictationAccuracy = 0
  if (dictations.length > 0) {
    const sum = dictations.reduce((acc, d) => acc + d.accuracy, 0)
    avgDictationAccuracy = Math.round(sum / dictations.length)
  }
  
  let homeworkCompletionRate = 0
  if (homeworks.length > 0) {
    const completed = homeworks.filter(h => h.completed).length
    homeworkCompletionRate = Math.round((completed / homeworks.length) * 100)
  }
  
  return {
    planCompletionRate,
    recitationRate,
    avgDictationAccuracy,
    homeworkCompletionRate
  }
}

const studentStatCards = computed(() => {
  if (!selectedStudentId.value) return []
  const stats = getStudentStats(selectedStudentId.value)
  return [
    { title: '周计划完成率', value: stats.planCompletionRate, color: '#409eff' },
    { title: '背诵熟练率', value: stats.recitationRate, color: '#67c23a' },
    { title: '默写平均正确率', value: stats.avgDictationAccuracy, color: '#9b59b6' },
    { title: '作业完成率', value: stats.homeworkCompletionRate, color: '#e67e22' }
  ]
})

const getOverallGrade = (stats) => {
  const avg = (stats.planCompletionRate + stats.recitationRate + stats.avgDictationAccuracy + stats.homeworkCompletionRate) / 4
  if (avg >= 90) return { text: '优秀', type: 'success' }
  if (avg >= 75) return { text: '良好', type: 'primary' }
  if (avg >= 60) return { text: '及格', type: 'warning' }
  return { text: '需努力', type: 'danger' }
}

const weakPoints = computed(() => {
  const points = []
  
  students.value.forEach(student => {
    const recitations = allRecitations.value.filter(r => r.studentId === student.id)
    const dictations = allDictations.value.filter(d => d.studentId === student.id)
    
    recitations.filter(r => r.status !== 'skilled').forEach(r => {
      points.push({
        student: student.name,
        content: r.content,
        type: '背诵',
        level: r.status === 'not_started' ? 'high' : 'medium'
      })
    })
    
    dictations.filter(d => d.accuracy < 70).forEach(d => {
      points.push({
        student: student.name,
        content: d.content,
        type: '默写',
        level: d.accuracy < 50 ? 'high' : 'medium'
      })
    })
  })
  
  return points.slice(0, 8)
})

const studentPlans = computed(() => {
  return selectedStudentId.value ? allPlans.value.filter(p => p.studentId === selectedStudentId.value) : []
})

const studentRecitations = computed(() => {
  return selectedStudentId.value ? allRecitations.value.filter(r => r.studentId === selectedStudentId.value) : []
})

const studentDictations = computed(() => {
  return selectedStudentId.value ? allDictations.value.filter(d => d.studentId === selectedStudentId.value) : []
})

const studentHomeworks = computed(() => {
  return selectedStudentId.value ? allHomeworks.value.filter(h => h.studentId === selectedStudentId.value) : []
})

const getPlanProgress = (plan) => {
  if (!plan.tasks || plan.tasks.length === 0) return '无任务'
  const completed = plan.tasks.filter(t => t.completed).length
  return `${completed}/${plan.tasks.length} 任务完成`
}

onMounted(() => {
  loadAllData()
})
</script>
