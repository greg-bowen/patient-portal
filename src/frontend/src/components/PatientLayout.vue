<template>
  <div class="layout-container">
    <header class="header">
      <img src="https://cdn.prod.website-files.com/64d91a88e5135a6a82e114d0/65a6933ecfb3491a563e5405_43Mindful_logo_01%201.svg" alt="Logo" class="logo" />
      <button @click="logout" class="logout-btn">Sign out</button>
    </header>
    
    <div class="content-wrapper">
      <aside class="sidebar">
        <nav class="nav-menu">
          <button @click="goToHome" class="nav-item">Home</button>
          <button @click="goToProfile" class="nav-item">Profile</button>
          <button class="nav-item">Appointments</button>
          <button class="nav-item">Messages</button>
          <button class="nav-item">Medical Records</button>
          <button class="nav-item">Billing</button>
        </nav>
      </aside>
      
      <main class="main-content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  router.push('/login')
}

const goToHome = () => {
  router.push('/home')
}

const goToProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: #f9fafb;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 40px;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.logout-btn:hover {
  background: #dc2626;
}

.content-wrapper {
  display: flex;
  min-height: calc(100vh - 80px);
}

.sidebar {
  width: 250px;
  background: white;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
  padding: 2rem 0;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.nav-item {
  padding: 1rem;
  background: none;
  border: none;
  text-align: left;
  font-size: 1rem;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.nav-item:hover {
  background: #f3f4f6;
  color: #3b82f6;
}

.main-content {
  flex: 1;
  padding: 2rem;
}
</style>