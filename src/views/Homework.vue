<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">作业检查</h2>
      <el-button type="primary" @click="openBatchModal()" :icon="List">
        批量检查
      </el-button>
    </div>
    
    <el-card>
      <div class="mb-4">
        <el-date-picker
          v-model="checkDate"
          type="date"
          placeholder="选择检查日期"
          style="width: 200px"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          class="mr-3"
        />
        <el-select
          v-model="selectedStudentId"
          placeholder="筛选学员"
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
        <el-table-column prop="subject" label="科目" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.subject }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="作业内容" min-width="200" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.completed ? 'success' : 'danger'" effect="light">
              {{ row.completed ? '已完成' : '未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="检查日期" width="130" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              :type="row.completed ? 'warning' : 'success'" 
              link
              @click="toggleStatus(row)"
            >
              {{ row.completed ? '标记未完成' : '标记完成' }}
            </el-button>
            <el-button type="danger" link @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!filteredRecords.length" description="暂无作业检查记录" />
    </el-card>
    
    <el-dialog
      v-model="batchDialogVisible"
      :title="'批量作业检查 - ' + checkDate"
      width="600px"
    >
      <el-form :model="batchForm" label-width="80px">
        <el-form-item label="检查日期">
          <el-date-picker
            v-model="checkDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="batchForm.subject" placeholder="请选择科目" style="width: 100%">
            <el-option
              v-for="s in subjects"
              :key="s"
              :label="s"
              :value="s"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <div class="mb-4">
        <div class="text-sm font-medium mb-3">学员作业状态</div>
        <div class="space-y-2 max-h-80 overflow-y-auto">
          <div 
            v-for="student in students" 
            :key="student.id"
            class="flex items-center justify-between p-3 border border-gray-200 rounded-lg"
          >
            <div class="flex items-center space-x-3">
              <el-checkbox
                :model-value="isStudentSelected(student.id)"
                @change="toggleStudentSelection(student.id)"
              />
              <el-avatar :size="28" class="bg-blue-100">
                <span class="text-blue-600 text-xs font-medium">{{ student.name.charAt(0) }}</span>
              </el-avatar>
              <span class="font-medium">{{ student.name }}</span>
              <el-tag type="info" size="small">{{ student.grade }}</el-tag>
            </div>
            <div class="flex items-center space-x-2">
              <el-button
                size="small"
                :type="getStatusType(student.id, true)"
                @click="setStudentStatus(student.id, true)"
              >
                完成
              </el-button>
              <el-button
                size="small"
                :type="getStatusType(student.id, false)"
                @click="setStudentStatus(student.id, false)"
              >
                未完成
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <el-form :model="batchForm" label-width="80px">
        <el-form-item label="作业内容">
          <el-input v-model="batchForm.content" placeholder="请输入作业内容（如：课本第25页练习）" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatch">保存检查记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { homeworkService, studentService, constants } from '../services/storage'

const { subjects } = constants

const checkDate = ref(new Date().toISOString().split('T')[0])
const selectedStudentId = ref('')
const batchDialogVisible = ref(false)
const students = ref([])
const allRecords = ref([])

const batchForm = reactive({
  subject: '语文',
  content: '',
  studentStatuses: new Map()
})

const filteredRecords = ref([])

const loadData = async () => {
  try {
    students.value = await studentService.getAll()
    allRecords.value = await homeworkService.getAll()
    updateFilteredRecords()
  } catch (error) {
    console.error('Failed to load homework data:', error)
  }
}

const updateFilteredRecords = () => {
  let records = allRecords.value.filter(r => r.date === checkDate.value)
  if (selectedStudentId.value) {
    records = records.filter(r => r.studentId === selectedStudentId.value)
  }
  filteredRecords.value = records.sort((a, b) => {
    const dateA = a.createdAt || a.date
    const dateB = b.createdAt || b.date
    return new Date(dateB) - new Date(dateA)
  })
}

watch([checkDate, selectedStudentId], () => {
  updateFilteredRecords()
})

const getStudentName = (id) => {
  const s = students.value.find(x => x.id === id)
  return s ? s.name : '未知'
}

const isStudentSelected = (id) => {
  return batchForm.studentStatuses.has(id)
}

const toggleStudentSelection = (id) => {
  if (batchForm.studentStatuses.has(id)) {
    batchForm.studentStatuses.delete(id)
  } else {
    batchForm.studentStatuses.set(id, true)
  }
}

const setStudentStatus = (id, completed) => {
  batchForm.studentStatuses.set(id, completed)
}

const getStatusType = (id, completed) => {
  const status = batchForm.studentStatuses.get(id)
  if (status === undefined) {
    return ''
  }
  if (status === completed) {
    return completed ? 'success' : 'danger'
  }
  return ''
}

const openBatchModal = () => {
  batchForm.subject = '语文'
  batchForm.content = ''
  batchForm.studentStatuses = new Map()
  batchDialogVisible.value = true
}

const toggleStatus = async (record) => {
  try {
    await homeworkService.update(record.id, { completed: !record.completed })
    await loadData()
    ElMessage.success(record.completed ? '已标记为未完成' : '已标记为完成')
  } catch (error) {
    console.error('Failed to update status:', error)
    ElMessage.error('更新失败')
  }
}

const submitBatch = async () => {
  if (batchForm.studentStatuses.size === 0) {
    ElMessage.warning('请至少选择一个学员')
    return
  }
  
  try {
    const entries = Array.from(batchForm.studentStatuses.entries())
    for (const [studentId, completed] of entries) {
      await homeworkService.add({
        studentId: studentId,
        subject: batchForm.subject,
        content: batchForm.content || '日常作业',
        completed: completed,
        date: checkDate.value
      })
    }
    
    await loadData()
    batchDialogVisible.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('Failed to submit batch:', error)
    ElMessage.error('保存失败')
  }
}

const deleteRecord = (record) => {
  ElMessageBox.confirm('确定要删除这条作业检查记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await homeworkService.delete(record.id)
      await loadData()
      ElMessage.success('删除成功')
    } catch (error) {
      console.error('Failed to delete:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
