<template>
  <PatientLayout>
    <h1>Welcome to your Patient Portal</h1>
    
    <div class="dashboard-grid">
      <div class="card">
        <h3>Profile</h3>
        <p>View and manage your basic information</p>
        <button @click="goToProfile" class="card-btn">View Information</button>
      </div>

      <div class="card">
        <h3>Appointments</h3>
        <p>View and manage your upcoming appointments</p>
        <button class="card-btn">View Appointments</button>
      </div>
      
      <div class="card">
        <h3>Messages</h3>
        <p>Communicate with your care team</p>
        <button class="card-btn">View Messages</button>
      </div>
      
      <div class="card">
        <h3>Medical Records</h3>
        <p>Access your health information</p>
        <button class="card-btn">View Records</button>
      </div>

      <div class="card">
        <h3>Billing</h3>
        <p>View statements and payment history</p>
        <button class="card-btn">View Billing</button>
      </div>
    </div>
  </PatientLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import PatientLayout from './PatientLayout.vue'

const router = useRouter()

const goToProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
h1 {
  font-size: 2.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 2rem;
  text-align: center;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.card h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1rem;
}

.card p {
  color: #6b7280;
  margin-bottom: 1.5rem;
}

.card-btn {
  padding: 0.75rem 1.5rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.card-btn:hover {
  background: #2563eb;
}
</style>