<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">背诵检查</h2>
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
        <el-table-column label="背诵内容" min-width="250">
          <template #default="{ row }">
            <div class="space-y-1">
              <div class="flex items-center space-x-2">
                <el-tag type="info" size="small">{{ row.subject }}</el-tag>
                <span class="font-medium">{{ row.contentTitle || row.content }}</span>
              </div>
              <div v-if="row.contentCategory" class="text-xs text-gray-400">
                分类：{{ row.contentCategory }} | 年级：{{ row.contentGrade }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkDate" label="检查日期" width="130" />
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openModal(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!filteredRecords.length" description="暂无背诵检查记录" />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑检查记录' : '新增检查记录'"
      width="600px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="学员" required>
          <el-select 
            v-model="form.studentId" 
            placeholder="请选择学员" 
            style="width: 100%"
            @change="onStudentChange"
          >
            <el-option
              v-for="s in students"
              :key="s.id"
              :label="s.name"
              :value="s.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" required>
          <el-select 
            v-model="form.subject" 
            placeholder="请选择科目" 
            style="width: 100%"
            @change="onSubjectChange"
          >
            <el-option
              v-for="s in availableSubjects"
              :key="s"
              :label="s"
              :value="s"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级" required>
          <el-select 
            v-model="form.contentGrade" 
            placeholder="请选择年级" 
            style="width: 100%"
            @change="onGradeChange"
            :disabled="!form.subject"
          >
            <el-option
              v-for="g in availableGrades"
              :key="g"
              :label="g"
              :value="g"
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
        <el-form-item label="背诵内容" required>
          <el-select 
            v-model="selectedRecitationId" 
            placeholder="请选择背诵内容" 
            style="width: 100%"
            :disabled="!form.subject || !form.contentGrade"
            filterable
            @change="onRecitationChange"
          >
            <el-option
              v-for="item in availableRecitations"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            >
              <div class="flex items-center justify-between">
                <span>{{ item.title }}</span>
                <el-tag size="small" type="info">{{ item.category }}</el-tag>
              </div>
            </el-option>
          </el-select>
          <div v-if="selectedRecitationDetail" class="mt-2 p-3 bg-gray-50 rounded-lg text-sm text-gray-600 whitespace-pre-wrap">
            <p class="font-medium mb-1">内容预览：</p>
            {{ selectedRecitationDetail.content }}
          </div>
        </el-form-item>
        <el-form-item label="检查状态" required>
          <el-radio-group v-model="form.status">
            <el-radio-button value="skilled" class="w-28">
              <span class="text-green-600">熟练</span>
            </el-radio-button>
            <el-radio-button value="not_skilled" class="w-28">
              <span class="text-yellow-600">不熟</span>
            </el-radio-button>
            <el-radio-button value="not_started" class="w-28">
              <span class="text-red-600">未完成</span>
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="选填，如：部分段落不熟练"
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
import { ref, computed, reactive, watch, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { recitationService, studentService, constants } from '../services/storage'
import { textbookRecitations, getRecitationById } from '../services/textbookRecitations'

const { subjects } = constants

const selectedStudentId = ref('')
const selectedDate = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const students = ref([])
const allRecords = ref([])

const selectedRecitationId = ref('')

const today = new Date().toISOString().split('T')[0]

const form = reactive({
  studentId: '',
  subject: '',
  contentGrade: '',
  contentId: '',
  contentTitle: '',
  content: '',
  contentCategory: '',
  status: 'skilled',
  checkDate: today,
  remark: ''
})

const filteredRecords = ref([])

const loadData = async () => {
  try {
    students.value = await studentService.getAll()
    allRecords.value = await recitationService.getAll()
    updateFilteredRecords()
  } catch (error) {
    console.error('Failed to load recitation data:', error)
  }
}

const updateFilteredRecords = () => {
  let records = allRecords.value
  if (selectedStudentId.value) {
    records = records.filter(r => r.studentId === selectedStudentId.value)
  }
  if (selectedDate.value) {
    records = records.filter(r => r.checkDate === selectedDate.value)
  }
  filteredRecords.value = records.sort((a, b) => new Date(b.checkDate) - new Date(a.checkDate))
}

watch([selectedStudentId, selectedDate], () => {
  updateFilteredRecords()
})

const availableSubjects = computed(() => {
  return Object.keys(textbookRecitations)
})

const availableGrades = computed(() => {
  if (!form.subject) return []
  return Object.keys(textbookRecitations[form.subject] || {})
})

const availableRecitations = computed(() => {
  if (!form.subject || !form.contentGrade) return []
  const data = textbookRecitations[form.subject] || {}
  return data[form.contentGrade] || []
})

const selectedRecitationDetail = computed(() => {
  if (!selectedRecitationId.value) return null
  return getRecitationById(selectedRecitationId.value)
})

const getStudentName = (id) => {
  const s = students.value.find(x => x.id === id)
  return s ? s.name : '未知'
}

const getStatusType = (status) => {
  switch (status) {
    case 'skilled': return 'success'
    case 'not_skilled': return 'warning'
    case 'not_started': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'skilled': return '熟练'
    case 'not_skilled': return '不熟'
    case 'not_started': return '未完成'
    default: return '未知'
  }
}

const onStudentChange = () => {
  if (!isEdit.value && form.studentId) {
    const student = students.value.find(s => s.id === form.studentId)
    if (student && student.grade) {
      if (form.subject && textbookRecitations[form.subject] && textbookRecitations[form.subject][student.grade]) {
        form.contentGrade = student.grade
      }
    }
  }
}

const onSubjectChange = () => {
  form.contentGrade = ''
  selectedRecitationId.value = ''
  form.contentId = ''
  form.contentTitle = ''
  form.content = ''
  form.contentCategory = ''
}

const onGradeChange = () => {
  selectedRecitationId.value = ''
  form.contentId = ''
  form.contentTitle = ''
  form.content = ''
  form.contentCategory = ''
}

const onRecitationChange = () => {
  const detail = selectedRecitationDetail.value
  if (detail) {
    form.contentId = detail.id
    form.contentTitle = detail.title
    form.content = detail.content
    form.contentCategory = detail.category
    form.contentGrade = detail.grade || form.contentGrade
  }
}

const openModal = (record = null) => {
  if (record) {
    isEdit.value = true
    editingId.value = record.id
    Object.assign(form, {
      studentId: record.studentId,
      subject: record.subject,
      contentGrade: record.contentGrade || '',
      contentId: record.contentId || '',
      contentTitle: record.contentTitle || record.content,
      content: record.content,
      contentCategory: record.contentCategory || '',
      status: record.status,
      checkDate: record.checkDate,
      remark: record.remark || ''
    })
    selectedRecitationId.value = record.contentId || ''
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      studentId: '',
      subject: '',
      contentGrade: '',
      contentId: '',
      contentTitle: '',
      content: '',
      contentCategory: '',
      status: 'skilled',
      checkDate: today,
      remark: ''
    })
    selectedRecitationId.value = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!form.subject) {
    ElMessage.warning('请选择科目')
    return
  }
  if (!form.contentGrade) {
    ElMessage.warning('请选择年级')
    return
  }
  if (!form.contentTitle || !form.content) {
    ElMessage.warning('请选择背诵内容')
    return
  }
  
  try {
    if (isEdit.value && editingId.value) {
      await recitationService.update(editingId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await recitationService.add({ ...form })
      ElMessage.success('添加成功')
    }
    await loadData()
    dialogVisible.value = false
  } catch (error) {
    console.error('Failed to save recitation:', error)
    ElMessage.error('保存失败')
  }
}

const deleteRecord = (record) => {
  ElMessageBox.confirm('确定要删除这条检查记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await recitationService.delete(record.id)
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
