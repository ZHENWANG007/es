//包装全局的请求在统一配置下
import axios from "axios"
const request = axios.create({
    timeout: 10000,
    baseURL: import.meta.env.VITE_BASE_URL
});
request.interceptors.request.use((config) => {
    //加token
    return config
})
request.interceptors.response.use((res) => {
    if (res.status !== 200) {
        // 如果 HTTP 状态码不是 200，提示错误
        alert("HTTP 请求错误");
    }
    //  else if (res.data && res.data.code !== 200) {
    //     // 如果存在 res.data.code，且它不是 200，提示错误
    //     alert("API 响应错误");
    // }
    return res;
}, (res) => {
    alert("请求失败，请检查网络或服务器")
    throw new Error(res);
})
export default request
