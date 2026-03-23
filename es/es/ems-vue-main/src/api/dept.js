import request from '@/utils/request'

// 获取部门列表（用于部门管理页面）
export const getDeptListService = () => {
    return request({
        url: '/dept/list',
        method: 'get'
    })
}

// 获取部门树列表
export const getDeptTreeService = () => {
    return request({
        url: '/dept/tree',
        method: 'get'
    })
}

// 新增部门
export const addDeptService = (data) => {
    return request({
        url: '/dept',
        method: 'post',
        data
    })
}

// 修改部门
export const updateDeptService = (data) => {
    return request({
        url: '/dept',
        method: 'put',
        data
    })
}

// 删除部门
export const deleteDeptService = (id) => {
    return request({
        url: `/dept/${id}`,
        method: 'delete'
    })
}

// 检查部门是否有员工或子部门
export const hasEmpService = (id) => {
    return request({
        url: `/dept/hasEmp/${id}`,
        method: 'get'
    })
} 