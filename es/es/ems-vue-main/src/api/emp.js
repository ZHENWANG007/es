import request from '@/utils/request'

// 登录
export const loginService = (loginData) => {
    return request.post('/emp/login', loginData)
}

// 登出
export const logoutService = () => {
    return request.post('/emp/logout')
}

// 获取员工信息
export const getEmpInfoService = () => {
    return request.get('/emp/info')
}

// 获取员工头像
export const getAvatarService = () => {
    return request({
        url: '/emp/avatar',
        method: 'get'
    })
}

// 上传头像
export const uploadAvatarService = (file) => {
    const formData = new FormData()
    formData.append('imgfile', file)
    return request.post('/emp/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 获取员工列表（分页）
export const getEmpListService = (params) => {
    return request({
        url: '/emp/page',
        method: 'get',
        params
    })
}

// 新增员工
export const addEmpService = (data) => {
    return request({
        url: '/emp',
        method: 'post',
        data
    })
}

// 修改员工
export const updateEmpService = (data) => {
    return request({
        url: '/emp',
        method: 'put',
        data
    })
}

// 删除员工
export const deleteEmpService = (id) => {
    return request({
        url: `/emp/${id}`,
        method: 'delete'
    })
}

// 批量删除员工
export const batchDeleteEmpService = (ids) => {
    return request({
        url: '/emp',
        method: 'delete',
        data: ids
    })
}

// 获取员工详情
export const getEmpDetailService = (id) => {
    return request.get(`/emp/${id}`)
}

// 导入员工
export const importEmpService = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/emp/import',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 导出员工
export const exportEmpService = (params) => {
    return request({
        url: '/emp/export',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

// 更新员工状态
export const updateEmpStatusService = (id, status) => {
    return request({
        url: `/emp/${id}/status/${status}`,
        method: 'put'
    })
}

// 批量重置密码
export const resetPasswordService = (ids) => {
    return request({
        url: '/emp/reset',
        method: 'put',
        data: ids
    })
}

// 获取用户权限标识
export const getRoleKeyService = () => {
    return request({
        url: '/emp/roleKey',
        method: 'get'
    })
}

// 更新用户信息
export const updateUserInfoService = (data) => {
    return request({
        url: '/emp/detail',
        method: 'put',
        data
    })
}

// 获取员工详细信息
export const getEmpDetailInfoService = () => {
    return request({
        url: '/emp/empDetail',
        method: 'get'
    })
}

// 修改密码
export const updatePasswordService = (data) => {
    return request({
        url: '/emp/password',
        method: 'put',
        data
    })
}

// 文件上传
export const uploadFileService = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/common/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 更新用户头像
export const updateAvatarService = (url) => {
    return request({
        url: '/emp/avatar',
        method: 'put',
        params: { url }
    })
} 