<template>
  <div class="page-container">
    <div class="left-panel">
      <div class="login-container">
        <img src="https://cdn.prod.website-files.com/64d91a88e5135a6a82e114d0/65a6933ecfb3491a563e5405_43Mindful_logo_01%201.svg" alt="Logo" class="logo" />
        <h1>Sign in to your account</h1>
        <form @submit.prevent="validateLogin" class="login-form">
          <div class="form-group">
            <label>Email</label>
            <input
              v-model="email"
              type="email"
              required
            />
          </div>
          
          <div class="form-group">
            <label>Password</label>
            <input
              v-model="password"
              type="password"
              required
            />
          </div>
          
          <button type="submit" :disabled="loading" class="login-btn">
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>
          
          <div class="reset-password">
            <a href="/reset-password" class="reset-link">Forgot your password?</a>
          </div>
          
          <div v-if="message" :class="messageClass">
            {{ message }}
          </div>
        </form>
        
        <div class="signup-section">
          <span>New to Mindful Care? </span>
          <a href="https://mindful.care/pre-registration" class="signup-link" target="_blank">
            Create an account
          </a>
        </div>
      </div>
    </div>
    <div class="right-panel">
      <img src="https://account.mindful.care/download_image/%2Fimages%2Fgroup_hangout_late.jpg" alt="Background" class="background-image" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserIP } from '../../utils/ip.js'

const router = useRouter()
const email = ref('gbowen@mindful.care')
const password = ref('')
const loading = ref(false)
const message = ref('')
const messageClass = ref('')
const userIP = ref(null)

onMounted(async () => {
  userIP.value = await getUserIP()
})

const validateLogin = async () => {
  loading.value = true
  message.value = ''
  
  try {
    const response = await fetch('/api/auth/validate-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
        password: password.value,
        ipAddress: userIP.value,
      }),
    })
    
    const data = await response.json()
    
    if (response.ok) {
      if (data.valid) {
        router.push('/home')
        // await router.push({
        //   name: 'home',
        //   query: {
        //     email: email.value // or whatever variable holds the email
        //   }
        // })
      } else {
        message.value = data.message || 'Login failed'
        messageClass.value = 'error'
      }
    } else {
      message.value = data.message || 'Login failed'
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

.login-container {
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

.login-btn {
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

.login-btn:hover:not(:disabled) {
  background: #2563eb;
}

.login-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.signup-section {
  text-align: center;
  margin-top: 2rem;
  color: #6b7280;
}

.signup-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.signup-link:hover {
  text-decoration: underline;
}

.reset-password {
  text-align: center;
  margin-bottom: 1rem;
}

.reset-link {
  color: #3b82f6;
  text-decoration: none;
  font-size: 0.9rem;
}

.reset-link:hover {
  text-decoration: underline;
}
</style>