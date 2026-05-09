<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">首页概览</h2>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">{{ item.title }}</p>
              <p class="text-3xl font-bold mt-2" :class="item.valueColor">{{ item.value }}</p>
            </div>
            <div class="w-14 h-14 rounded-xl flex items-center justify-center" :class="item.bgColor">
              <el-icon :size="28" :class="item.iconColor">
                <component :is="item.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-semibold">最近学员</span>
          </template>
          <el-empty v-if="!recentStudents.length" description="暂无学员数据" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="student in recentStudents"
              :key="student.id"
              :timestamp="formatDate(student.createdAt)"
              placement="top"
            >
              <el-card shadow="never" class="mb-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-3">
                    <el-avatar :size="36" class="bg-blue-100">
                      <span class="text-blue-600 font-medium">{{ student.name.charAt(0) }}</span>
                    </el-avatar>
                    <div>
                      <p class="font-medium text-gray-800">{{ student.name }}</p>
                      <p class="text-sm text-gray-500">{{ student.grade }} · {{ student.studyStage }}</p>
                    </div>
                  </div>
                  <el-tag type="info" size="small">{{ student.contact || '无联系方式' }}</el-tag>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="font-semibold">快速入口</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="12" v-for="item in quickLinks" :key="item.path" class="mb-4">
              <router-link :to="item.path">
                <div 
                  class="p-4 rounded-lg flex items-center space-x-3 transition-all hover:shadow-md cursor-pointer"
                  :class="item.bgColor"
                >
                  <el-icon :size="24" :class="item.iconColor">
                    <component :is="item.icon" />
                  </el-icon>
                  <span class="font-medium text-gray-700">{{ item.label }}</span>
                </div>
              </router-link>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Document, 
  Calendar, 
  ChatDotRound,
  EditPen,
  List,
  DataAnalysis
} from '@element-plus/icons-vue'
import { studentService, planService, homeworkService, knowledgeService } from '../services/storage'

const router = useRouter()

const recentStudents = computed(() => {
  return studentService.getAll().slice(-5).reverse()
})

const today = new Date().toISOString().split('T')[0]

const statCards = computed(() => [
  {
    title: '学员总数',
    value: studentService.getAll().length,
    icon: User,
    bgColor: 'bg-blue-50',
    iconColor: 'text-blue-500',
    valueColor: 'text-blue-600'
  },
  {
    title: '本周计划数',
    value: planService.getAll().length,
    icon: Calendar,
    bgColor: 'bg-green-50',
    iconColor: 'text-green-500',
    valueColor: 'text-green-600'
  },
  {
    title: '今日作业检查',
    value: homeworkService.getByDate(today).length,
    icon: List,
    bgColor: 'bg-purple-50',
    iconColor: 'text-purple-500',
    valueColor: 'text-purple-600'
  },
  {
    title: '知识点总数',
    value: knowledgeService.getAll().length,
    icon: Document,
    bgColor: 'bg-orange-50',
    iconColor: 'text-orange-500',
    valueColor: 'text-orange-600'
  }
])

const quickLinks = [
  { path: '/students', label: '新增学员', icon: User, bgColor: 'bg-blue-50', iconColor: 'text-blue-500' },
  { path: '/plans', label: '创建计划', icon: Calendar, bgColor: 'bg-green-50', iconColor: 'text-green-500' },
  { path: '/recitation', label: '背诵检查', icon: ChatDotRound, bgColor: 'bg-purple-50', iconColor: 'text-purple-500' },
  { path: '/homework', label: '作业检查', icon: List, bgColor: 'bg-orange-50', iconColor: 'text-orange-500' }
]

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}
</script>
