<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">博客文章列表</h2>
      <el-button type="primary" @click="goToEditor" :icon="Plus">
        新增博客
      </el-button>
    </div>
    
    <el-card>
      <el-table :data="blogs" stripe style="width: 100%" :loading="loading">
        <el-table-column prop="title" label="标题" min-width="250">
          <template #default="{ row }">
            <div class="font-medium text-gray-800">{{ row.title }}</div>
            <div class="text-xs text-gray-500 mt-1 line-clamp-1">
              {{ row.summary || getSummary(row.content) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="deleteBlog(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && !blogs.length" description="暂无博客文章，请点击右上角新增按钮创建" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { blogService } from '../services/api'

const router = useRouter()
const blogs = ref([])
const loading = ref(false)

const loadBlogs = async () => {
  loading.value = true
  try {
    blogs.value = await blogService.getAll()
  } catch (error) {
    console.error('Failed to load blogs:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBlogs()
})

const goToEditor = () => {
  router.push('/blog-editor')
}

const deleteBlog = (blog) => {
  ElMessageBox.confirm(`确定要删除博客"${blog.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await blogService.delete(blog.id)
      await loadBlogs()
      ElMessage.success('删除成功')
    } catch (error) {
      console.error('Failed to delete blog:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const getSummary = (content) => {
  if (!content) return ''
  return content.replace(/[#*`\[\]\(\)!]/g, '').substring(0, 80) + (content.length > 80 ? '...' : '')
}
</script>
