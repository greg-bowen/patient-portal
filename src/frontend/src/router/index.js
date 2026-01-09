import { createRouter, createWebHistory } from 'vue-router'
import LoginForm from '../components/LoginForm.vue'
import ForgotPassword from '../components/ForgotPassword.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginForm
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: ForgotPassword
    }
  ]
})

export default router