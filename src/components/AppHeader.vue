<template>
  <el-header class="header-container">
    <div class="flex items-center space-x-3">
      <div class="logo-icon">
        <el-icon class="text-white" :size="20">
          <Reading />
        </el-icon>
      </div>
      <h1 class="text-xl font-bold text-gray-800">个性化自习室管理系统</h1>
    </div>
    <div class="flex items-center space-x-4">
      <span class="text-sm text-gray-500">{{ currentDate }}</span>
      <el-avatar :size="36" class="bg-gray-200">
        <el-icon class="text-gray-600"><User /></el-icon>
      </el-avatar>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { Reading, User } from '@element-plus/icons-vue'

const currentDate = computed(() => {
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return now.toLocaleDateString('zh-CN', options)
})
</script>

<style scoped>
.header-container {
  height: 60px;
  min-height: 60px;
  background-color: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background-color: #409eff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
