import './assets/main.scss'

import { createApp } from 'vue' // 导入vue
import ElementPlus from 'element-plus' // 导入element-plus
import 'element-plus/dist/index.css' // 导入element-plus样式
import router from '@/router'
import { createPinia } from 'pinia'
//导入持久化插件
import { createPersistedState } from 'pinia-persistedstate-plugin'
import locale from 'element-plus/dist/locale/zh-cn.js'

import App from './App.vue' // 导入App.vue

const app = createApp(App) // 创建应用实例

const pinia = createPinia()
const persist = createPersistedState()
//pinia使用持久化插件
pinia.use(persist)
app.use(pinia)

app.use(ElementPlus, { locale }) // 使用element-plus
app.use(router)

app.mount('#app') // 挂载到id为app的div上
