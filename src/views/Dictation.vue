<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">默写检查</h2>
      <el-button type="primary" @click="openModal()" :icon="Plus">
        新增检查
      </el-button>
    </div>
    
    <el-card>
      <div class="mb-4">
        <el-select
          v-model="selectedStudentId"
          placeholder="筛选学员"
          style="width: 200px"
          clearable
          class="mr-3"
        >
          <el-option
            v-for="s in students"
            :key="s.id"
            :label="s.name"
            :value="s.id"
          />
        </el-select>
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择检查日期"
          style="width: 200px"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          clearable
        />
      </div>
      
      <el-table :data="filteredRecords" stripe style="width: 100%">
        <el-table-column label="学员" min-width="150">
          <template #default="{ row }">
            <div class="flex items-center space-x-2">
              <el-avatar :size="32" class="bg-blue-100">
                <span class="text-blue-600 text-sm font-medium">{{ getStudentName(row.studentId).charAt(0) }}</span>
              </el-avatar>
              <span class="font-medium">{{ getStudentName(row.studentId) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="默写内容" min-width="200" />
        <el-table-column prop="subject" label="科目" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.subject }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="正确率" width="180">
          <template #default="{ row }">
            <div class="flex items-center space-x-2">
              <el-progress 
                :percentage="row.accuracy" 
                :stroke-width="12"
                :color="getProgressColor(row.accuracy)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="errors" label="错误内容" min-width="180">
          <template #default="{ row }">
            <el-tooltip v-if="row.errors" :content="row.errors" placement="top">
              <span class="cursor-pointer text-red-500">{{ truncate(row.errors, 20) }}</span>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="checkDate" label="检查日期" width="130" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openModal(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!filteredRecords.length" description="暂无默写检查记录" />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑检查记录' : '新增检查记录'"
      width="550px"
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
        <el-form-item label="科目" required>
          <el-select v-model="form.subject" placeholder="请选择科目" style="width: 100%">
            <el-option
              v-for="s in subjects"
              :key="s"
              :label="s"
              :value="s"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="检查日期" required>
          <el-date-picker
            v-model="form.checkDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="默写内容" required>
          <el-input v-model="form.content" placeholder="请输入默写内容（如：英语单词 Unit 5）" />
        </el-form-item>
        <el-form-item label="正确题数" required>
          <el-input-number 
            v-model="form.correctCount" 
            :min="0" 
            style="width: 100%"
            @change="calculateAccuracy"
          />
        </el-form-item>
        <el-form-item label="总题数" required>
          <el-input-number 
            v-model="form.totalCount" 
            :min="1" 
            style="width: 100%"
            @change="calculateAccuracy"
          />
        </el-form-item>
        <el-form-item label="正确率">
          <el-progress 
            :percentage="form.accuracy" 
            :stroke-width="16"
            :color="getProgressColor(form.accuracy)"
          />
        </el-form-item>
        <el-form-item label="错误内容">
          <el-input
            v-model="form.errors"
            type="textarea"
            :rows="2"
            placeholder="记录错误内容，如：第2、5、8题错误"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '添加' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dictationService, studentService, constants } from '../services/storage'

const { subjects } = constants

const selectedStudentId = ref('')
const selectedDate = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const refreshKey = ref(0)

const today = new Date().toISOString().split('T')[0]

const form = reactive({
  studentId: '',
  subject: '语文',
  content: '',
  correctCount: 0,
  totalCount: 10,
  accuracy: 0,
  errors: '',
  checkDate: today
})

const students = computed(() => studentService.getAll())

const allRecords = computed(() => {
  refreshKey.value
  return dictationService.getAll()
})

const filteredRecords = computed(() => {
  let records = allRecords.value
  if (selectedStudentId.value) {
    records = records.filter(r => r.studentId === selectedStudentId.value)
  }
  if (selectedDate.value) {
    records = records.filter(r => r.checkDate === selectedDate.value)
  }
  return records.sort((a, b) => new Date(b.checkDate) - new Date(a.checkDate))
})

const getStudentName = (id) => {
  const s = students.value.find(x => x.id === id)
  return s ? s.name : '未知'
}

const getProgressColor = (accuracy) => {
  if (accuracy >= 80) return '#67c23a'
  if (accuracy >= 60) return '#e6a23c'
  return '#f56c6c'
}

const truncate = (str, len) => {
  if (str.length <= len) return str
  return str.slice(0, len) + '...'
}

const calculateAccuracy = () => {
  if (form.totalCount > 0) {
    form.accuracy = Math.round((form.correctCount / form.totalCount) * 100)
  } else {
    form.accuracy = 0
  }
}

const openModal = (record = null) => {
  if (record) {
    isEdit.value = true
    editingId.value = record.id
    Object.assign(form, {
      studentId: record.studentId,
      subject: record.subject,
      content: record.content,
      correctCount: record.correctCount,
      totalCount: record.totalCount,
      accuracy: record.accuracy,
      errors: record.errors || '',
      checkDate: record.checkDate
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      studentId: '',
      subject: '语文',
      content: '',
      correctCount: 0,
      totalCount: 10,
      accuracy: 0,
      errors: '',
      checkDate: today
    })
  }
  dialogVisible.value = true
}

const submitForm = () => {
  if (!form.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!form.content) {
    ElMessage.warning('请输入默写内容')
    return
  }
  
  const data = { ...form }
  
  if (isEdit.value && editingId.value) {
    dictationService.update(editingId.value, data)
    ElMessage.success('更新成功')
  } else {
    dictationService.add(data)
    ElMessage.success('添加成功')
  }
  refreshKey.value++
  dialogVisible.value = false
}

const deleteRecord = (record) => {
  ElMessageBox.confirm('确定要删除这条检查记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    dictationService.delete(record.id)
    refreshKey.value++
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
