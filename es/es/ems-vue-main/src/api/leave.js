import request from '@/utils/request'

/**
 * 获取待审批的请假申请列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getPendingApprovals(params) {
    return request({
        url: '/leave/pending',
        method: 'get',
        params
    })
}

// 获取请假列表
export const getLeaveListService = (params) => {
    return request({
        url: '/leave/page',
        method: 'get',
        params
    })
}

// 批量删除请假记录
export const batchDeleteLeaveService = (ids) => {
    return request({
        url: '/leave/batch',
        method: 'delete',
        data: ids
    })
}

// 审批请假申请（支持单个和批量）
export const approveLeaveService = (data) => {
    return request({
        url: '/leave/approve',
        method: 'post',
        data
    })
}

// 请假申请
export const applyLeaveService = (data) => {
    return request({
        url: '/leave',
        method: 'post',
        data
    })
}

// 批量审批请假申请
export const approveBatchService = (data) => {
    return request({
        url: '/leave/approve',
        method: 'post',
        data
    })
} 