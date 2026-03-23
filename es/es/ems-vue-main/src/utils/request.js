//定制请求的实例

//导入axios  npm install axios
import axios from 'axios';
import { ElMessage } from 'element-plus'
//导入token状态
import { useTokenStore } from '@/stores/token.js';
//定义一个变量,记录公共的前缀  ,  baseURL
// const baseURL = 'http://localhost:8080';
const baseURL = '/api';
const instance = axios.create({ baseURL })

// 防抖处理错误提示
const errorMessageDebounce = {
    timer: null,
    messages: new Set()
}

const showErrorMessage = (message) => {
    // 如果是相同的错误信息，且在防抖时间内，则不重复显示
    if (errorMessageDebounce.messages.has(message)) {
        return
    }

    // 添加错误信息到Set中
    errorMessageDebounce.messages.add(message)

    // 清除之前的定时器
    if (errorMessageDebounce.timer) {
        clearTimeout(errorMessageDebounce.timer)
    }

    // 显示错误提示
    ElMessage.error(message)

    // 设置新的定时器，3秒后清除该错误信息
    errorMessageDebounce.timer = setTimeout(() => {
        errorMessageDebounce.messages.clear()
    }, 3000)
}

//添加请求拦截器
instance.interceptors.request.use(
    (config) => {
        //在发送请求之前做什么
        let tokenStore = useTokenStore()
        //如果token中有值，在携带
        if (tokenStore.token) {
            config.headers.Authorization = tokenStore.token
        }
        return config
    },
    (err) => {
        //如果请求错误做什么
        showErrorMessage('请求发送失败')
        return Promise.reject(err)
    }
)
//导入vue-router
import router from '@/router'

//添加响应拦截器
instance.interceptors.response.use(
    result => {
        //如果业务状态码为1，代表本次操作成功
        if (result.data.code === 1) {
            return result.data;
        }
        //代码走到这里，代表业务状态码不是1，本次操作失败
        // 如果是token过期，清除token并跳转到登录页
        if (result.data.message === 'token已过期') {
            const tokenStore = useTokenStore();
            tokenStore.removeToken();
            router.push('/login');
            ElMessage.error('登录已过期，请重新登录');
        } else {
            ElMessage.error(result.data.message || '服务异常');
        }
        return Promise.reject(result.data);//异步的状态转化成失败的状态
    },
    err => {
        //如果响应状态码是401，代表未登录或token过期，给出对应的提示，并跳转到登录页
        if (err.response?.status === 401) {
            const tokenStore = useTokenStore();
            tokenStore.removeToken();
            ElMessage.error('登录已过期，请重新登录')
            router.push('/login')
        } else {
            ElMessage.error(err.response?.data?.message || '服务异常');
        }
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

export default instance;