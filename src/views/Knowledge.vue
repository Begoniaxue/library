<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">知识点同步</h2>
      <el-button type="primary" @click="openModal()" :icon="Plus">
        新增知识点
      </el-button>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span class="font-semibold">目录导航</span>
          </template>
          <el-empty v-if="!groupedGrades.length" description="暂无知识点" />
          <el-collapse v-else accordion>
            <el-collapse-item
              v-for="grade in groupedGrades"
              :key="grade"
              :title="grade"
              :name="grade"
            >
              <div 
                v-for="subject in getSubjectsByGrade(grade)" 
                :key="subject"
                class="flex items-center justify-between px-2 py-2 cursor-pointer rounded hover:bg-gray-50 transition-colors"
                :class="isSelected(grade, subject) ? 'bg-blue-50' : ''"
                @click="selectFilter(grade, subject)"
              >
                <span :class="isSelected(grade, subject) ? 'text-blue-600 font-medium' : 'text-gray-600'">
                  {{ subject }}
                </span>
                <el-tag type="info" size="small">{{ getCount(grade, subject) }}</el-tag>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-semibold">
                {{ filterGrade || filterSubject ? `${filterGrade || '全部'} · ${filterSubject || '全部学科'}` : '全部知识点' }}
              </span>
              <el-button v-if="filterGrade || filterSubject" type="text" @click="clearFilter">
                清除筛选
              </el-button>
            </div>
          </template>
          
          <el-empty v-if="!filteredKnowledge.length" description="暂无知识点，请添加新知识点" />
          
          <div v-else class="space-y-3">
            <div 
              v-for="item in filteredKnowledge" 
              :key="item.id"
              class="p-4 border border-gray-100 rounded-lg hover:border-blue-200 transition-colors"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-2 mb-1">
                    <h4 class="font-medium text-gray-800">{{ item.title }}</h4>
                    <el-tag v-if="item.isImportant" type="danger" effect="light">★ 重点</el-tag>
                  </div>
                  <p class="text-sm text-gray-500 mb-2">{{ item.content }}</p>
                  <div class="flex items-center space-x-2 text-xs text-gray-400">
                    <el-tag type="info" size="small">{{ item.grade }}</el-tag>
                    <el-tag type="info" size="small">{{ item.subject }}</el-tag>
                  </div>
                </div>
                <div class="flex items-center space-x-1 ml-4">
                  <el-tooltip content="标记重点">
                    <el-button
                      :type="item.isImportant ? 'warning' : 'default'"
                      :icon="Star"
                      circle
                      size="small"
                      @click="toggleImportant(item)"
                    />
                  </el-tooltip>
                  <el-tooltip content="编辑">
                    <el-button
                      type="primary"
                      :icon="Edit"
                      circle
                      size="small"
                      @click="openModal(item)"
                    />
                  </el-tooltip>
                  <el-tooltip content="删除">
                    <el-button
                      type="danger"
                      :icon="Delete"
                      circle
                      size="small"
                      @click="deleteItem(item)"
                    />
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑知识点' : '新增知识点'"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入知识点标题" />
        </el-form-item>
        <el-form-item label="年级" required>
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
            <el-option
              v-for="g in grades"
              :key="g"
              :label="g"
              :value="g"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学科" required>
          <el-select v-model="form.subject" placeholder="请选择学科" style="width: 100%">
            <el-option
              v-for="s in subjects"
              :key="s"
              :label="s"
              :value="s"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入知识点内容"
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
import { Plus, Star, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { knowledgeService, constants } from '../services/storage'

const { grades, subjects } = constants

const filterGrade = ref('')
const filterSubject = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const refreshKey = ref(0)

const form = reactive({
  title: '',
  grade: '',
  subject: '',
  content: ''
})

const allKnowledge = computed(() => {
  refreshKey.value
  return knowledgeService.getAll()
})

const groupedGrades = computed(() => {
  const set = new Set(allKnowledge.value.map(k => k.grade))
  return Array.from(set)
})

const filteredKnowledge = computed(() => {
  let items = allKnowledge.value
  if (filterGrade.value) items = items.filter(k => k.grade === filterGrade.value)
  if (filterSubject.value) items = items.filter(k => k.subject === filterSubject.value)
  return items.sort((a, b) => b.isImportant - a.isImportant)
})

const getSubjectsByGrade = (grade) => {
  const set = new Set(allKnowledge.value.filter(k => k.grade === grade).map(k => k.subject))
  return Array.from(set)
}

const getCount = (grade, subject) => {
  return allKnowledge.value.filter(k => k.grade === grade && k.subject === subject).length
}

const selectFilter = (grade, subject) => {
  filterGrade.value = grade
  filterSubject.value = subject
}

const isSelected = (grade, subject) => {
  return filterGrade.value === grade && filterSubject.value === subject
}

const clearFilter = () => {
  filterGrade.value = ''
  filterSubject.value = ''
}

const openModal = (item = null) => {
  if (item) {
    isEdit.value = true
    editingId.value = item.id
    Object.assign(form, {
      title: item.title,
      grade: item.grade,
      subject: item.subject,
      content: item.content
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      title: '',
      grade: '',
      subject: '',
      content: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = () => {
  if (!form.title) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.grade || !form.subject) {
    ElMessage.warning('请选择年级和学科')
    return
  }
  if (!form.content) {
    ElMessage.warning('请输入内容')
    return
  }
  
  if (isEdit.value && editingId.value) {
    knowledgeService.update(editingId.value, { ...form })
    ElMessage.success('更新成功')
  } else {
    knowledgeService.add({ ...form })
    ElMessage.success('添加成功')
  }
  refreshKey.value++
  dialogVisible.value = false
}

const toggleImportant = (item) => {
  knowledgeService.toggleImportant(item.id)
  refreshKey.value++
  ElMessage.success(item.isImportant ? '已取消重点标记' : '已标记为重点')
}

const deleteItem = (item) => {
  ElMessageBox.confirm(`确定要删除知识点"${item.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    knowledgeService.delete(item.id)
    refreshKey.value++
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
