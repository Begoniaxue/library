<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">周计划</h2>
      <el-button type="primary" @click="openModal()" :icon="Plus">
        创建周计划
      </el-button>
    </div>
    
    <el-card>
      <div class="mb-4">
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
      
      <el-empty v-if="!filteredPlans.length" description="暂无周计划，请创建新计划" />
      
      <el-collapse v-else accordion>
        <el-collapse-item
          v-for="plan in filteredPlans"
          :key="plan.id"
          :name="plan.id"
        >
          <template #title>
            <div class="flex items-center justify-between w-full pr-4">
              <div class="flex items-center space-x-3">
                <el-avatar :size="28" class="bg-blue-100">
                  <span class="text-blue-600 text-xs font-medium">{{ getStudentName(plan.studentId).charAt(0) }}</span>
                </el-avatar>
                <span class="font-medium">{{ getStudentName(plan.studentId) }}</span>
                <span class="text-sm text-gray-500">{{ plan.weekStart }} 至 {{ plan.weekEnd }}</span>
              </div>
              <div class="flex items-center space-x-2">
                <el-tag :type="getPlanCompleteStatus(plan).type" size="small" effect="light">
                  {{ getPlanCompleteStatus(plan).text }}
                </el-tag>
              </div>
            </div>
          </template>
          
          <div class="space-y-2">
            <div 
              v-for="(task, index) in plan.tasks" 
              :key="index"
              class="flex items-center justify-between p-3 border border-gray-100 rounded-lg hover:bg-gray-50"
            >
              <div class="flex items-center space-x-3">
                <el-checkbox
                  :model-value="task.completed"
                  @change="toggleTask(plan, index)"
                />
                <div>
                  <p :class="task.completed ? 'line-through text-gray-400' : 'text-gray-700'">{{ task.content }}</p>
                  <div class="flex items-center space-x-2 text-xs text-gray-400 mt-1">
                    <el-tag type="info" size="small">{{ task.day }}</el-tag>
                    <span v-if="task.knowledge">· {{ task.knowledge }}</span>
                    <span v-if="task.deadline">· 截止: {{ task.deadline }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="!plan.tasks || plan.tasks.length === 0" class="text-center py-4 text-gray-400">
              暂无学习任务
            </div>
          </div>
          
          <div class="flex justify-end space-x-2 mt-4 pt-4 border-t border-gray-100">
            <el-button size="small" @click="openModal(plan)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePlan(plan)">删除</el-button>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑周计划' : '创建周计划'"
      width="600px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="学员" required>
          <el-select v-model="form.studentId" placeholder="请选择学员" style="width: 100%">
            <el-option
              v-for="s in students"
              :key="s.id"
              :label="s.name"
              :value="s.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="周起始日" required>
          <el-date-picker
            v-model="form.weekStart"
            type="date"
            placeholder="选择周起始日"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      
      <div class="mb-4">
        <div class="flex items-center justify-between mb-3">
          <span class="font-medium">学习任务</span>
          <el-button type="primary" link @click="addTask" :icon="Plus">添加任务</el-button>
        </div>
        
        <div class="space-y-3 max-h-80 overflow-y-auto">
          <div 
            v-for="(task, index) in form.tasks" 
            :key="index"
            class="p-3 border border-gray-200 rounded-lg bg-gray-50"
          >
            <div class="flex items-start justify-between mb-2">
              <span class="text-sm font-medium text-gray-600">任务 {{ index + 1 }}</span>
              <el-button type="danger" link size="small" @click="removeTask(index)">删除</el-button>
            </div>
            <el-row :gutter="12" class="mb-2">
              <el-col :span="12">
                <el-select v-model="task.day" placeholder="选择日期" style="width: 100%" size="small">
                  <el-option label="周一" value="周一" />
                  <el-option label="周二" value="周二" />
                  <el-option label="周三" value="周三" />
                  <el-option label="周四" value="周四" />
                  <el-option label="周五" value="周五" />
                  <el-option label="周六" value="周六" />
                  <el-option label="周日" value="周日" />
                </el-select>
              </el-col>
              <el-col :span="12">
                <el-date-picker
                  v-model="task.deadline"
                  type="date"
                  placeholder="截止日期"
                  style="width: 100%"
                  size="small"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-col>
            </el-row>
            <el-input v-model="task.content" placeholder="任务内容" size="small" class="mb-2" />
            <el-input v-model="task.knowledge" placeholder="关联知识点（选填）" size="small" />
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { planService, studentService } from '../services/storage'

const selectedStudentId = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const refreshKey = ref(0)

const form = reactive({
  studentId: '',
  weekStart: '',
  weekEnd: '',
  tasks: []
})

const students = computed(() => studentService.getAll())

const allPlans = computed(() => {
  refreshKey.value
  return planService.getAll()
})

const filteredPlans = computed(() => {
  let plans = allPlans.value
  if (selectedStudentId.value) {
    plans = plans.filter(p => p.studentId === selectedStudentId.value)
  }
  return plans.sort((a, b) => new Date(b.weekStart) - new Date(a.weekStart))
})

const getStudentName = (id) => {
  const s = students.value.find(x => x.id === id)
  return s ? s.name : '未知学员'
}

const getPlanCompleteStatus = (plan) => {
  if (!plan.tasks || plan.tasks.length === 0) {
    return { text: '无任务', type: 'info' }
  }
  const completed = plan.tasks.filter(t => t.completed).length
  const total = plan.tasks.length
  if (completed === total) {
    return { text: '已完成', type: 'success' }
  } else if (completed === 0) {
    return { text: '未开始', type: 'info' }
  } else {
    return { text: `进行中 ${completed}/${total}`, type: 'warning' }
  }
}

const openModal = (plan = null) => {
  if (plan) {
    isEdit.value = true
    editingId.value = plan.id
    Object.assign(form, {
      studentId: plan.studentId,
      weekStart: plan.weekStart,
      weekEnd: plan.weekEnd || '',
      tasks: plan.tasks ? JSON.parse(JSON.stringify(plan.tasks)) : []
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      studentId: '',
      weekStart: getMonday(),
      weekEnd: '',
      tasks: []
    })
  }
  dialogVisible.value = true
}

const getMonday = () => {
  const now = new Date()
  const day = now.getDay()
  const diff = now.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(now.setDate(diff)).toISOString().split('T')[0]
}

const addTask = () => {
  form.tasks.push({
    day: '',
    content: '',
    knowledge: '',
    deadline: '',
    completed: false
  })
}

const removeTask = (index) => {
  form.tasks.splice(index, 1)
}

const toggleTask = (plan, taskIndex) => {
  const newTasks = [...plan.tasks]
  newTasks[taskIndex].completed = !newTasks[taskIndex].completed
  planService.update(plan.id, { tasks: newTasks })
  refreshKey.value++
}

const submitForm = () => {
  if (!form.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!form.weekStart) {
    ElMessage.warning('请选择周起始日')
    return
  }
  
  const weekEndDate = new Date(form.weekStart)
  weekEndDate.setDate(weekEndDate.getDate() + 6)
  const weekEnd = weekEndDate.toISOString().split('T')[0]
  
  const planData = {
    studentId: form.studentId,
    weekStart: form.weekStart,
    weekEnd: weekEnd,
    tasks: form.tasks
  }
  
  if (isEdit.value && editingId.value) {
    planService.update(editingId.value, planData)
    ElMessage.success('周计划已更新')
  } else {
    planService.add(planData)
    ElMessage.success('周计划创建成功')
  }
  refreshKey.value++
  dialogVisible.value = false
}

const deletePlan = (plan) => {
  ElMessageBox.confirm(`确定要删除"${getStudentName(plan.studentId)}"的周计划吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    planService.delete(plan.id)
    refreshKey.value++
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
