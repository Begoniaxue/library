<template>
  <el-aside class="sidebar-container flex-shrink-0">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      router
    >
      <el-menu-item 
        v-for="item in menuItems" 
        :key="item.path" 
        :index="item.path"
        class="menu-item"
      >
        <el-icon class="w-5 h-5"><component :is="item.icon" /></el-icon>
        <span class="ml-2">{{ item.label }}</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeFilled,
  User,
  Document,
  Calendar,
  ChatDotRound,
  EditPen,
  List,
  DataAnalysis
} from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => route.path)

const menuItems = [
  { path: '/', label: '首页概览', icon: HomeFilled },
  { path: '/students', label: '学员管理', icon: User },
  { path: '/knowledge', label: '知识点同步', icon: Document },
  { path: '/plans', label: '周计划', icon: Calendar },
  { path: '/recitation', label: '背诵检查', icon: ChatDotRound },
  { path: '/dictation', label: '默写检查', icon: EditPen },
  { path: '/homework', label: '作业检查', icon: List },
  { path: '/analysis', label: '学情分析', icon: DataAnalysis }
]
</script>

<style scoped>
.sidebar-container {
  width: 220px;
  min-width: 220px;
  max-width: 220px;
  background-color: #ffffff;
  border-right: 1px solid #e5e7eb;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  display: flex;
  align-items: center;
}
</style>
