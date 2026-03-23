import request from '@/utils/request'

// 获取员工总数
export const getEmpCountService = () => {
    return request.get('/dashboard/emp/count')
}

// 获取今天请假人数
export const getLeaveCountService = () => {
    return request.get('/dashboard/leave/count')
}

// 获取今天考勤异常人数
export const getAttendanceCountService = () => {
    return request.get('/dashboard/attendance/count')
}

// 获取部门人员分布
export const getDeptDistributionService = () => {
    return request({
        url: '/dashboard/dept/emp/count',
        method: 'get'
    })
}

// 获取月度考勤统计
export const getMonthlyAttendanceService = (year, month) => {
    return request({
        url: '/dashboard/attendance/monthly',
        method: 'get',
        params: { year, month }
    })
}

// 获取考勤统计数据
export const getAttendanceStatisticsService = () => {
    return request({
        url: '/dashboard/attendance/statistics',
        method: 'get'
    })
}

// 获取考勤趋势数据
export const getAttendanceTrendService = (days = 7) => {
    return request({
        url: '/dashboard/attendance/trend',
        method: 'get',
        params: { days }
    })
}

// 获取部门考勤统计
export const getDeptAttendanceService = () => {
    return request({
        url: '/dashboard/attendance/dept',
        method: 'get'
    })
}

// 获取考勤异常TOP数据
export const getAttendanceTopService = (limit = 10, type = 1) => {
    return request({
        url: '/dashboard/attendance/top',
        method: 'get',
        params: { limit, type }
    })
}