import request from '@/utils/request'

// 获取角色列表（分页）
export const getRoleListService = (params) => {
    return request({
        url: '/role/page',
        method: 'get',
        params
    })
}

// 更新员工角色
export const updateEmpRoleService = (data) => {
    return request({
        url: '/emp/role',
        method: 'put',
        data
    })
}

// 获取角色详情
export const getRoleDetailService = (id) => {
    return request({
        url: `/role/${id}`,
        method: 'get'
    })
}

// 新增角色
export const addRoleService = (data) => {
    return request({
        url: '/role',
        method: 'post',
        data
    })
}

// 修改角色
export const updateRoleService = (data) => {
    return request({
        url: '/role',
        method: 'put',
        data
    })
}

// 删除角色
export const deleteRoleService = (id) => {
    return request({
        url: `/role/${id}`,
        method: 'delete'
    })
}

// 更新角色状态
export const updateRoleStatusService = (id, status) => {
    return request({
        url: `/role/${id}/status/${status}`,
        method: 'put'
    })
}

// 获取角色菜单权限
export const getRoleMenusService = (roleId) => {
    return request({
        url: `/role/menus/${roleId}`,
        method: 'get'
    })
}

// 分配角色权限
export const assignRoleMenusService = (data) => {
    return request({
        url: '/role/menus',
        method: 'post',
        data
    })
}

// 获取所有角色列表
export const getAllRoleListService = () => {
    return request({
        url: '/role/list',
        method: 'get'
    })
} 