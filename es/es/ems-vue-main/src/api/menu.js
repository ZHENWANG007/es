import request from '@/utils/request'

// 获取菜单树列表
export const getMenuListService = () => {
    return request({
        url: '/menu/allMenus',
        method: 'get'
    })
}

// 获取菜单树形选择数据（用于角色分配权限）
export const getMenuTreeSelectService = () => {
    return request({
        url: '/menu/allMenus',
        method: 'get'
    })
}

// 新增菜单
export const addMenuService = (data) => {
    return request({
        url: '/menu',
        method: 'post',
        data
    })
}

// 修改菜单
export const updateMenuService = (data) => {
    return request({
        url: '/menu',
        method: 'put',
        data
    })
}

// 删除菜单
export const deleteMenuService = (id) => {
    return request({
        url: `/menu/${id}`,
        method: 'delete'
    })
} 