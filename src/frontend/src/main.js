import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { mask } from 'vue-the-mask'

const app = createApp(App)
// Formatting phone numbers
app.directive('mask', mask)

app.use(router).mount('#app')