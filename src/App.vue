<template>
  <el-container class="h-screen flex flex-col">
    <AppHeader />
    <el-container class="flex-1 flex overflow-hidden">
      <AppSidebar />
      <el-main class="bg-gray-50 overflow-auto flex-1 p-6">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import AppHeader from './components/AppHeader.vue'
import AppSidebar from './components/AppSidebar.vue'
</script>
