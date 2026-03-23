import request from '@/utils/request'

// 获取考勤列表
export function getAttendanceListService(params) {
    return request({
        url: 'attendance/page',
        method: 'get',
        params
    })
}

// 获取考勤统计数据
export function getAttendanceStatisticsService(params) {
    return request({
        url: 'attendance/statistics',
        method: 'get',
        params
    })
}

// 新增考勤记录
export function addAttendanceService(data) {
    return request({
        url: 'attendance',
        method: 'post',
        data
    })
}

// 修改考勤记录
export function updateAttendanceService(data) {
    return request({
        url: 'attendance',
        method: 'put',
        data
    })
}

// 删除考勤记录
export function deleteAttendanceService(id) {
    return request({
        url: `attendance/${id}`,
        method: 'delete'
    })
}

// 批量删除考勤记录
export function batchDeleteAttendanceService(ids) {
    return request({
        url: 'attendance/batch',
        method: 'delete',
        data: { ids }
    })
}

// 员工上班打卡
export function checkInService() {
    return request({
        url: 'attendance/check-in',
        method: 'post'
    })
}

// 员工下班打卡
export function checkOutService() {
    return request({
        url: 'attendance/check-out',
        method: 'post'
    })
}

// 获取员工当天打卡状态
export function getTodayAttendanceService() {
    return request({
        url: 'attendance/myAttendanceToday',
        method: 'get'
    })
}

// 处理异常考勤
export function handleAbnormalAttendanceService(data) {
    return request({
        url: 'attendance/abnormal',
        method: 'post',
        data
    })
}

// 获取个人考勤记录
export function getPersonalAttendanceService(params) {
    return request({
        url: 'attendance/personal',
        method: 'get',
        params
    })
}

// 获取个人月度考勤记录
export function getPersonalMonthAttendanceService(params) {
    return request({
        url: 'attendance/personal/month',
        method: 'get',
        params
    })
}

// 员工打卡
export function clockService(data) {
    return request({
        url: '/attendance/clock',
        method: 'post',
        data
    })
}

// 获取月度考勤记录
export function getMonthHistoryService(yearMonth) {
    return request({
        url: '/attendance/month',
        method: 'get',
        params: { yearMonth }
    })
}

// 获取个人考勤统计
export const getMyAttendanceCountService = () => {
    return request.get('/attendance/myAttendanceCount')
} 