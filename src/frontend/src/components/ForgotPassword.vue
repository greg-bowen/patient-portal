<template>
  <div class="page-container">
    <div class="left-panel">
      <div class="reset-container">
        <img src="https://cdn.prod.website-files.com/64d91a88e5135a6a82e114d0/65a6933ecfb3491a563e5405_43Mindful_logo_01%201.svg" alt="Logo" class="logo" />
        <h1>Reset your password</h1>
        <p>Enter your email address and we'll send you a link to reset your password.</p>
        
        <form @submit.prevent="resetPassword" class="reset-form">
          <div class="form-group">
            <label>Email</label>
            <input
              v-model="email"
              type="email"
              required
            />
          </div>
          
          <button type="submit" :disabled="loading" class="reset-btn">
            {{ loading ? 'Sending...' : 'Send reset link' }}
          </button>
          
          <div v-if="message" :class="messageClass">
            {{ message }}
          </div>
        </form>
        
        <div class="back-section">
          <a href="/login" class="back-link">← Back to sign in</a>
        </div>
      </div>
    </div>
    <div class="right-panel">
      <img src="https://account.mindful.care/download_image/%2Fimages%2Fgroup_hangout_late.jpg" alt="Background" class="background-image" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const email = ref('')
const loading = ref(false)
const message = ref('')
const messageClass = ref('')

const resetPassword = async () => {
  loading.value = true
  message.value = ''
  
  try {
    const response = await fetch('/api/auth/reset-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
      }),
    })
    
    const data = await response.json()
    
    if (response.ok) {
      message.value = data.message || 'Password reset link sent to your email!'
      messageClass.value = 'success'
    } else {
      message.value = data.message || 'Error sending reset link'
      messageClass.value = 'error'
    }
  } catch (error) {
    message.value = 'Connection error'
    messageClass.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  display: flex;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.left-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 2rem;
}

.right-panel {
  flex: 1;
}

@media (max-width: 768px) {
  .right-panel {
    display: none;
  }
  
  .left-panel {
    flex: none;
    width: 100%;
  }
}

.background-image {
  width: 100%;
  height: 100vh;
  object-fit: cover;
}

.reset-container {
  width: 100%;
  max-width: 400px;
}

.logo {
  width: 180px;
  height: auto;
  margin-bottom: 2rem;
}

h1 {
  font-size: 2rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1rem;
  text-align: left;
}

p {
  color: #6b7280;
  margin-bottom: 2rem;
  text-align: left;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
  text-align: left;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.reset-btn {
  width: 100%;
  padding: 0.75rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 1rem;
}

.reset-btn:hover:not(:disabled) {
  background: #2563eb;
}

.reset-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.back-section {
  text-align: center;
  margin-top: 2rem;
}

.back-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.back-link:hover {
  text-decoration: underline;
}

.success {
  color: #059669;
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #ecfdf5;
  border-radius: 4px;
}

.error {
  color: #dc2626;
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #fef2f2;
  border-radius: 4px;
}
</style>