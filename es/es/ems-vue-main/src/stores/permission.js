import { defineStore } from 'pinia'
import { ref } from 'vue'
import { asyncRoutes, constantRoutes } from '@/router'

export const usePermissionStore = defineStore('permission', () => {
    // 状态
    const routes = ref([])
    const menus = ref([])
    const buttons = ref([])

    // 生成动态路由
    const generateRoutes = async (userMenus) => {
        try {
            menus.value = userMenus
            buttons.value = userMenus.buttons || []

            // 将后端返回的菜单格式化为路由格式
            const formatRoutes = (menus, parentPath = '') => {
                return menus.map(menu => {
                    // 对于权限管理的子菜单，直接从预设路由中查找
                    if (parentPath === '/permission') {
                        const parentRoute = asyncRoutes.find(route => route.path === parentPath)
                        if (parentRoute && parentRoute.children) {
                            const matchedChild = parentRoute.children.find(child =>
                                child.meta?.perms === menu.perms ||
                                child.path === menu.path.replace(/^\//, '')
                            )
                            return matchedChild
                        }
                        return null
                    }

                    // 查找匹配的预设路由
                    const matchedRoute = asyncRoutes.find(route =>
                        route.meta?.perms === menu.perms ||
                        route.path === menu.path
                    )

                    if (!matchedRoute) return null

                    // 创建新的路由对象
                    const route = { ...matchedRoute }

                    // 处理子菜单
                    if (menu.children && menu.children.length > 0) {
                        const children = formatRoutes(menu.children, menu.path)
                        if (children.length) {
                            route.children = children
                        }
                    }

                    return route
                }).filter(Boolean)
            }

            const accessedRoutes = formatRoutes(userMenus)
            routes.value = constantRoutes.concat(accessedRoutes)
            return accessedRoutes
        } catch (error) {
            console.error('生成动态路由失败:', error)
            return []
        }
    }

    // 重置路由
    const resetRoutes = () => {
        routes.value = []
        menus.value = []
        buttons.value = []
    }

    return {
        routes,
        menus,
        buttons,
        generateRoutes,
        resetRoutes
    }
}, {
    persist: {
        paths: ['menus', 'buttons']
    }
})

// 过滤路由
function filterAsyncRoutes(routes, menus) {
    return routes.filter(route => {
        const tmp = { ...route }
        const hasPermission = checkPermission(menus, tmp)
        if (hasPermission) {
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, menus)
            }
            return true
        }
        return false
    })
}

// 检查权限
function checkPermission(menus, route) {
    if (!route.meta?.perms) return true
    return menus.some(menu => menu.perms === route.meta.perms)
}
