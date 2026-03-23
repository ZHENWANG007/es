import { defineStore } from "pinia"
import { ref } from 'vue'

export const useUserInfoStore = defineStore('userInfo', () => {
    // 定义用户信息
    const info = ref({})
    const empInfo = ref(null)
    const empLogin = ref(null)

    // 设置用户信息
    const setInfo = (newInfo) => {
        info.value = newInfo
        empInfo.value = newInfo.empInfo
        empLogin.value = newInfo.empLogin
    }

    // 清空用户信息
    const clearInfo = () => {
        info.value = {}
        empInfo.value = null
        empLogin.value = null
    }

    return {
        info,
        empInfo,
        empLogin,
        setInfo,
        clearInfo
    }
}, {
    persist: true
})