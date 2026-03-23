import request from '@/utils/request'

// 获取岗位列表（分页）
export const getJobListService = (params) => {
    return request({
        url: '/job/page',
        method: 'get',
        params
    })
}

// 新增岗位
export const addJobService = (data) => {
    return request({
        url: '/job',
        method: 'post',
        data
    })
}

// 修改岗位
export const updateJobService = (data) => {
    return request({
        url: '/job',
        method: 'put',
        data
    })
}

// 批量删除岗位
export const deleteJobService = (ids) => {
    return request({
        url: '/job',
        method: 'delete',
        data: ids
    })
}

// 获取岗位名称列表
export const getJobNameListService = () => {
    return request({
        url: '/job/nameList',
        method: 'get'
    })
}

// 检查岗位是否有员工
export const hasEmpService = (ids) => {
    return request({
        url: '/job/hasEmp',
        method: 'post',
        data: ids
    })
} 