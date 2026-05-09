import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/students',
    name: 'Students',
    component: () => import('../views/Students.vue'),
    meta: { title: '学员管理' }
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('../views/Knowledge.vue'),
    meta: { title: '知识点同步' }
  },
  {
    path: '/plans',
    name: 'Plans',
    component: () => import('../views/Plans.vue'),
    meta: { title: '周计划' }
  },
  {
    path: '/recitation',
    name: 'Recitation',
    component: () => import('../views/Recitation.vue'),
    meta: { title: '背诵检查' }
  },
  {
    path: '/dictation',
    name: 'Dictation',
    component: () => import('../views/Dictation.vue'),
    meta: { title: '默写检查' }
  },
  {
    path: '/homework',
    name: 'Homework',
    component: () => import('../views/Homework.vue'),
    meta: { title: '作业检查' }
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('../views/Analysis.vue'),
    meta: { title: '学情分析' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '个性化自习室'} - 个性化自习室管理系统`
  next()
})

export default router
