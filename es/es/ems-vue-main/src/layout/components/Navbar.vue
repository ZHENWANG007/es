import { useRouter } from 'vue-router'
import { useUserInfoStore } from '@/stores/userInfo'
import { usePermissionStore } from '@/stores/permission'
import { useTokenStore } from '@/stores/token'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const userInfoStore = useUserInfoStore()
const permissionStore = usePermissionStore()
const tokenStore = useTokenStore()

// 退出登录
const handleLogout = () => {
    ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            // 清除token
            tokenStore.clearToken()
            // 清除用户信息
            userInfoStore.clearInfo()
            // 重置路由和权限
            permissionStore.resetRoutes()
            // 跳转到登录页
            await router.push('/login')
            ElMessage.success('退出成功')
        } catch (error) {
            console.error('退出失败:', error)
            ElMessage.error('退出失败')
        }
    }).catch(() => {})
} 