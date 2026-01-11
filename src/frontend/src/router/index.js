import { createRouter, createWebHistory } from 'vue-router'
import LoginForm from '../components/auth/LoginForm.vue'
import ForgotPassword from '../components/auth/ForgotPassword.vue'
import PatientHome from '../components/PatientHome.vue'
import PatientProfile from '../components/PatientProfile.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/auth/login'
    },
    {
      path: '/auth/login',
      name: 'login',
      component: LoginForm
    },
    {
      path: '/auth/reset-password',
      name: '/auth/reset-password',
      component: ForgotPassword
    },
    {
      path: '/home',
      name: 'home',
      component: PatientHome
    },
    {
      path: '/profile',
      name: 'profile',
      component: PatientProfile
    }
  ]
})

export default router