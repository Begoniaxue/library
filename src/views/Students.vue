<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">学员管理</h2>
      <el-button type="primary" @click="openModal()" :icon="Plus">
        新增学员
      </el-button>
    </div>
    
    <el-card>
      <div class="mb-4">
        <el-input
          v-model="searchName"
          placeholder="按姓名搜索"
          style="width: 200px"
          :prefix-icon="Search"
          clearable
          class="mr-3"
        />
        <el-select
          v-model="searchGrade"
          placeholder="选择年级"
          style="width: 150px"
          clearable
        >
          <el-option
            v-for="grade in grades"
            :key="grade"
            :label="grade"
            :value="grade"
          />
        </el-select>
      </div>
      
      <el-table :data="filteredStudents" stripe style="width: 100%">
        <el-table-column prop="name" label="姓名" min-width="120">
          <template #default="{ row }">
            <div class="flex items-center space-x-2">
              <el-avatar :size="32" class="bg-blue-100">
                <span class="text-blue-600 text-sm font-medium">{{ row.name.charAt(0) }}</span>
              </el-avatar>
              <span class="font-medium">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="年级" width="100">
          <template #default="{ row }">
            <el-tag type="primary" effect="light">{{ row.grade }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studyStage" label="学习阶段" width="120" />
        <el-table-column prop="contact" label="联系方式" width="150">
          <template #default="{ row }">
            {{ row.contact || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="录入时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openModal(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteStudent(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!filteredStudents.length" description="暂无学员数据，请添加新学员" />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学员' : '新增学员'"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入学员姓名" />
        </el-form-item>
        <el-form-item label="年级" required>
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
            <el-option
              v-for="grade in grades"
              :key="grade"
              :label="grade"
              :value="grade"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学习阶段" required>
          <el-select v-model="form.studyStage" placeholder="请选择学习阶段" style="width: 100%">
            <el-option
              v-for="stage in studyStages"
              :key="stage"
              :label="stage"
              :value="stage"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.contact" placeholder="请输入联系方式（选填）" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { studentService, constants } from '../services/storage'

const { grades, studyStages } = constants

const searchName = ref('')
const searchGrade = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const filteredStudents = ref([])
const loading = ref(false)

const form = reactive({
  name: '',
  grade: '',
  studyStage: '',
  contact: ''
})

const loadStudents = async () => {
  loading.value = true
  try {
    filteredStudents.value = await studentService.search(searchName.value, searchGrade.value)
  } catch (error) {
    console.error('Failed to load students:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStudents()
})

watch([searchName, searchGrade], () => {
  loadStudents()
})

const openModal = (student = null) => {
  if (student) {
    isEdit.value = true
    editingId.value = student.id
    Object.assign(form, {
      name: student.name,
      grade: student.grade,
      studyStage: student.studyStage,
      contact: student.contact || ''
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      name: '',
      grade: '',
      studyStage: '',
      contact: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.name) {
    ElMessage.warning('请输入学员姓名')
    return
  }
  if (!form.grade) {
    ElMessage.warning('请选择年级')
    return
  }
  if (!form.studyStage) {
    ElMessage.warning('请选择学习阶段')
    return
  }
  
  try {
    if (isEdit.value && editingId.value) {
      await studentService.update(editingId.value, { ...form })
      ElMessage.success('学员信息已更新')
    } else {
      await studentService.add({ ...form })
      ElMessage.success('学员添加成功')
    }
    await loadStudents()
    dialogVisible.value = false
  } catch (error) {
    console.error('Failed to save student:', error)
    ElMessage.error('保存失败')
  }
}

const deleteStudent = (student) => {
  ElMessageBox.confirm(`确定要删除学员"${student.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await studentService.delete(student.id)
      await loadStudents()
      ElMessage.success('删除成功')
    } catch (error) {
      console.error('Failed to delete student:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}
</script>
