//导入vue-router
import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { usePermissionStore } from '@/stores/permission'
import { useUserInfoStore } from '@/stores/userInfo'
import { getEmpInfoService } from '@/api/emp'
import { ElLoading, ElMessage } from 'element-plus'
import Layout from '@/views/Layout.vue'

// 静态路由
export const constantRoutes = [
    {
        path: '/login',
        component: () => import('@/views/login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/',
        name: 'Layout',
        component: () => import('@/views/Layout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'account/info',
                name: 'AccountInfo',
                component: () => import('@/views/account/info.vue'),
                meta: { title: '个人中心' }
            }
        ]
    }
]

// 动态路由
export const asyncRoutes = [
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据统计', icon: 'DataLine', perms: 'ems:dashboard' }
    },
    {
        path: '/employee',
        name: 'Employee',
        component: () => import('@/views/Layout.vue'),
        redirect: '/employee/list',
        meta: { title: '员工管理', icon: 'User', perms: 'ems:employee' },
        children: [
            {
                path: 'list',
                name: 'EmployeeList',
                component: () => import('@/views/employee/EmployeeManage.vue'),
                meta: { title: '员工列表', icon: 'UserFilled', perms: 'ems:employee:list' }
            },
            {
                path: 'dept',
                name: 'DeptManage',
                component: () => import('@/views/employee/DeptManage.vue'),
                meta: { title: '部门管理', icon: 'OfficeBuilding', perms: 'ems:dept' }
            }
        ]
    },
    {
        path: '/attendance',
        name: 'Attendance',
        component: () => import('@/views/Layout.vue'),
        redirect: '/attendance/record',
        meta: { title: '考勤管理', icon: 'Stopwatch', perms: 'ems:attendance' },
        children: [
            {
                path: 'record',
                name: 'AttendanceRecord',
                component: () => import('@/views/attendance/AttendanceRecord.vue'),
                meta: { title: '考勤记录', icon: 'Calendar', perms: 'ems:attendance:record' }
            },
            {
                path: 'statistics',
                name: 'AttendanceStatistics',
                component: () => import('@/views/attendance/AttendanceStatistics.vue'),
                meta: { title: '考勤统计', icon: 'DataAnalysis', perms: 'ems:attendance:statistics' }
            },
            {
                path: 'my',
                name: 'MyAttendance',
                component: () => import('@/views/attendance/MyAttendance.vue'),
                meta: { title: '我的考勤', icon: 'UserFilled', perms: 'ems:attendance:my' }
            }
        ]
    },
    {
        path: '/leave',
        name: 'Leave',
        component: Layout,
        redirect: '/leave/record',
        meta: { title: '请假管理', icon: 'Timer', perms: 'ems:leave' },
        children: [
            {
                path: 'record',
                name: 'LeaveRecord',
                component: () => import('@/views/leave/LeaveRecord.vue'),
                meta: { title: '请假记录', icon: 'Clock', perms: 'ems:leave:list' }
            },
            {
                path: 'approve',
                name: 'LeaveApprove',
                component: () => import('@/views/leave/LeaveApprove.vue'),
                meta: { title: '请假审批', icon: 'Checked', perms: 'ems:leave:approve' }
            }
        ]
    },
    {
        path: '/job',
        name: 'Job',
        component: () => import('@/views/job/JobManage.vue'),
        meta: { title: '岗位管理', icon: 'Briefcase', perms: 'ems:job' }
    },
    {
        path: '/jobTransfer',
        name: 'JobTransfer',
        component: () => import('@/views/job/JobTransfer.vue'),
        meta: { title: '调岗管理', icon: 'Briefcase', perms: 'ems:jobTransfer' }
    },
    {
        path: '/permission',
        name: 'Permission',
        component: () => import('@/views/Layout.vue'),
        redirect: '/permission/role',
        meta: { title: '权限管理', icon: 'Setting', perms: 'ems:permission' },
        children: [
            {
                path: 'role',
                name: 'RoleManage',
                component: () => import('@/views/permission/RoleManage.vue'),
                meta: { title: '角色管理', icon: 'UserFilled', perms: 'ems:role' }
            },
            {
                path: 'menu',
                name: 'MenuManage',
                component: () => import('@/views/permission/MenuManage.vue'),
                meta: { title: '菜单管理', icon: 'Menu', perms: 'ems:menu' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: constantRoutes
})

// 白名单
const whiteList = ['/login']

// 添加动态路由的方法
async function loadAsyncRoutes() {
    const { data } = await getEmpInfoService()
    const permissionStore = usePermissionStore()
    const userInfoStore = useUserInfoStore()

    // 设置用户信息
    userInfoStore.setInfo(data.empInfo)

    // 先清除所有已存在的动态路由，但保留个人中心路由
    let existingRoutes = router.getRoutes()
    existingRoutes.forEach(route => {
        if (route.name && route.name !== 'Layout' && route.name !== 'NotFound' && route.name !== 'AccountInfo') {
            router.removeRoute(route.name)
        }
    })

    // 生成动态路由
    const accessRoutes = await permissionStore.generateRoutes(data.menus)

    // 将动态路由添加到Layout路由下
    accessRoutes.forEach(route => {
        if (route.children) {
            // 如果有子路由，直接添加父路由
            router.addRoute(route)
        } else {
            // 如果没有子路由，添加到Layout下
            router.addRoute('Layout', route)
        }
    })

    // 最后添加通配符路由
    router.addRoute('Layout', {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/error/UnderDevelopment.vue'),
        meta: { title: '开发中' }
    })

    return accessRoutes
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
    const tokenStore = useTokenStore()
    const permissionStore = usePermissionStore()
    const userInfoStore = useUserInfoStore()

    if (tokenStore.token) {
        if (to.path === '/login') {
            next('/')
        } else {
            try {
                // 判断是否需要加载动态路由
                if (!userInfoStore.info.id || permissionStore.routes.length === 0) {
                    const loadingInstance = ElLoading.service({
                        lock: true,
                        text: '加载中...',
                        background: 'rgba(0, 0, 0, 0.7)'
                    })

                    try {
                        await loadAsyncRoutes()
                        loadingInstance.close()

                        // 重要：刷新页面时，需要重新导航到目标路由
                        if (to.path === '/') {
                            const firstRoute = permissionStore.menus[0]
                            if (firstRoute) {
                                next({ path: firstRoute.path, replace: true })
                            } else {
                                ElMessage.error('您没有任何菜单权限')
                                next('/login')
                            }
                        } else {
                            // 重要：使用 replace: true 来避免导航重复
                            next({ ...to, replace: true })
                        }
                    } catch (error) {
                        loadingInstance.close()
                        console.error('加载路由失败:', error)
                        ElMessage.error('加载用户信息失败，请重新登录')
                        tokenStore.clearToken()
                        userInfoStore.clearInfo()
                        permissionStore.resetRoutes()
                        next('/login')
                    }
                } else {
                    // 检查当前路由是否存在，排除个人中心路由
                    if (!router.hasRoute(to.name) && to.name !== 'NotFound' && to.name !== 'AccountInfo') {
                        // 如果路由不存在且不是 NotFound 路由，重新加载路由
                        await loadAsyncRoutes()
                        next({ ...to, replace: true })
                    } else {
                        next()
                    }
                }
            } catch (error) {
                console.error('路由守卫错误:', error)
                tokenStore.clearToken()
                userInfoStore.clearInfo()
                permissionStore.resetRoutes()
                next('/login')
            }
        }
    } else {
        if (whiteList.includes(to.path)) {
            next()
        } else {
            next('/login')
        }
    }
})

// 重置路由方法
export function resetRouter() {
    // 移除所有动态添加的路由
    router.getRoutes().forEach(route => {
        if (route.name && !constantRoutes.some(r => r.name === route.name)) {
            router.removeRoute(route.name)
        }
    })
}

export default router