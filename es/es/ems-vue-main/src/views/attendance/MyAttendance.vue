<template>
    <div class="my-attendance">
        <!-- 打卡区域 -->
        <div class="check-area">
            <div class="main-section">
                <div class="time-block">
                    <div class="time-display">
                        <div class="current-time">{{ currentTime }}</div>
                        <div class="current-date">{{ currentDate }}</div>
                    </div>
                    <div class="location-info" :class="{ 'warning': !isInRange && currentLocation, 'success': isInRange }">
                        <el-icon><Location /></el-icon>
                        <span>{{ locationInfo }}</span>
                    </div>
                    <div class="location-error" v-if="locationError">{{ locationError }}</div>
                </div>
                <div class="check-group">
                    <div class="check-item">
                        <el-button 
                            class="check-button"
                            type="primary" 
                            :disabled="hasCheckedIn"
                            @click="handleCheckInWithLocation"
                            :loading="checkingIn || checkingLocation"
                            size="large"
                        >
                            <el-icon><AlarmClock /></el-icon>上班打卡
                        </el-button>
                        <div class="status-card">
                            <div class="label">上班打卡</div>
                            <div class="value" :class="{ checked: hasCheckedIn }">
                                {{ hasCheckedIn ? '已打卡' : '未打卡' }}
                            </div>
                        </div>
                    </div>
                    <div class="check-item">
                        <el-button 
                            class="check-button"
                            type="success" 
                            :disabled="!hasCheckedIn || hasCheckedOut"
                            @click="handleCheckOutWithLocation"
                            :loading="checkingOut || checkingLocation"
                            size="large"
                        >
                            <el-icon><Switch /></el-icon>下班打卡
                        </el-button>
                        <div class="status-card">
                            <div class="label">下班打卡</div>
                            <div class="value" :class="{ checked: hasCheckedOut }">
                                {{ hasCheckedOut ? '已打卡' : '未打卡' }}
                            </div>
                        </div>
                    </div>
                    <div class="work-duration">
                        <div class="label">工作时长</div>
                        <div class="progress-container">
                            <div 
                                class="progress-bar" 
                                :style="{ width: workProgress + '%' }"
                            ></div>
                            <div class="progress-text">{{ workDuration }}</div>
                        </div>
                        <div class="time-details">
                            {{ workStartTime }} - {{ workEndTime }}
                        </div>
                    </div>
                </div>
            </div>

            <div class="stats-section">
                <div class="stat-item" v-loading="loading">
                    <div class="label">正常出勤</div>
                    <div class="value">{{ monthlyStats.normal || 0 }}天</div>
                </div>
                <div class="stat-item" v-loading="loading">
                    <div class="label">迟到</div>
                    <div class="value">{{ monthlyStats.late || 0 }}次</div>
                </div>
                <div class="stat-item" v-loading="loading">
                    <div class="label">早退</div>
                    <div class="value">{{ monthlyStats.early || 0 }}次</div>
                </div>
                <div class="stat-item" v-loading="loading">
                    <div class="label">旷工</div>
                    <div class="value">{{ monthlyStats.absent || 0 }}天</div>
                </div>
                <div class="stat-item" v-loading="loading">
                    <div class="label">请假</div>
                    <div class="value">{{ monthlyStats.leave || 0 }}天</div>
                </div>
            </div>
        </div>

        <!-- 考勤日历 -->
        <div class="attendance-calendar">
            <div class="calendar-header">
                <div class="month-selector">
                    <el-button :icon="ArrowLeft" @click="changeMonth(-1)" />
                    <div class="current-month">{{ formatYearMonth }}</div>
                    <el-button :icon="ArrowRight" @click="changeMonth(1)" />
                </div>
            </div>
            
            <el-calendar v-model="calendarDate">
                <template #header>
                    <!-- 覆盖默认的日历头部，使用空模板 -->
                </template>
                <template #dateCell="{ data }">
                    <div class="calendar-cell">
                        <div class="date-number">{{ data.day.split('-')[2] }}</div>
                        <template v-if="data.type === 'current'">
                            <div 
                                class="attendance-info"
                                :class="getAttendanceClass(data)"
                            >
                                <template v-if="getAttendanceInfo(data)">
                                    <div class="status-tag">
                                        {{ getStatusText(getAttendanceInfo(data).status) }}
                                    </div>
                                    <div class="check-time">
                                        上班：{{ getAttendanceInfo(data).checkInTime }}
                                    </div>
                                    <div class="check-time">
                                        下班：{{ getAttendanceInfo(data).checkOutTime }}
                                    </div>
                                </template>
                            </div>
                        </template>
                    </div>
                </template>
            </el-calendar>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { AlarmClock, Switch, Location, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import {
    clockService,
    getMonthHistoryService,
    getMyAttendanceCountService,
    getTodayAttendanceService
} from '@/api/attendance'

// 当前时间显示
const currentTime = ref('')
const currentDate = ref(new Date())
let timer = null
let locationTimer = null

// 更新当前时间
const updateTime = () => {
    const now = new Date()
    currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
    currentDate.value = now.toLocaleDateString('zh-CN')
    // 只在已打卡且未下班时更新工作时长
    if (hasCheckedIn.value && !hasCheckedOut.value) {
        calculateWorkDuration()
    }
}

// 打卡状态
const hasCheckedIn = ref(false)
const hasCheckedOut = ref(false)
const checkingIn = ref(false)
const checkingOut = ref(false)

// 定位相关
const companyLocation = {
    latitude: 29.421122,  // 公司纬度
    longitude: 106.593879 // 公司经度
}
const currentLocation = ref(null)
const locationError = ref('')
const isInRange = ref(false)
const checkingLocation = ref(false)
const locationDetail = ref('')

// 高德地图配置
const AMAP_KEY = 'b430f758ec7d1928b4a3015767119ad2'
const AMAP_API = 'https://restapi.amap.com/v3/geocode/regeo'

// 月度考勤数据
const monthlyAttendance = ref([])
const monthlyStats = ref({
    normal: 0,  // 正常
    late: 0,    // 迟到
    early: 0,   // 早退
    absent: 0,  // 旷工
    leave: 0    // 请假
})

// 添加加载状态
const loading = ref(false)

// 工作时长相关
const workDuration = ref('0小时0分')
const workProgress = ref(0)
const workStartTime = ref('--:--')
const workEndTime = ref('--:--')

// 计算工作时长和进度
const calculateWorkDuration = () => {
    if (!hasCheckedIn.value) {
        workDuration.value = '0小时0分'
        workProgress.value = 0
        workStartTime.value = '--:--'
        workEndTime.value = '--:--'
        return
    }

    const now = new Date()
    const checkInTime = new Date() // 这里应该使用实际的打卡时间
    checkInTime.setHours(9, 0, 0) // 假设上班时间为9点
    let endTime = hasCheckedOut.value ? new Date() : now // 如果未下班，使用当前时间
    
    // 设置工作时间显示
    workStartTime.value = checkInTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: false 
    })
    workEndTime.value = hasCheckedOut.value ? endTime.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    }) : '--:--'

    // 计算工作时长
    const duration = endTime - checkInTime
    const hours = Math.floor(duration / (1000 * 60 * 60))
    const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
    
    workDuration.value = `${hours}小时${minutes}分`

    // 计算工作进度（假设正常工作时间为8小时）
    const progress = Math.min((duration / (8 * 60 * 60 * 1000)) * 100, 100)
    workProgress.value = Math.max(0, progress)
}

// 日历相关
const calendarDate = ref(new Date())

// 格式化年月显示
const formatYearMonth = computed(() => {
    const date = calendarDate.value
    return `${date.getFullYear()}年${date.getMonth() + 1}月`
})

// 获取日期对应的考勤信息
const getAttendanceInfo = (data) => {
    if (!monthlyAttendance.value?.length || !data?.day) return null
    return monthlyAttendance.value.find(item => item.date === data.day)
}

// 获取考勤状态文本
const getStatusText = (status) => {
    if (!status) return '正常'
    const statuses = status.split(',')
    const texts = statuses.map(s => {
        switch (s) {
            case '0': return '正常'
            case '1': return '迟到'
            case '2': return '早退'
            case '3': return '旷工'
            case '4': return '请假'
            default: return '正常'
        }
    })
    return texts.join('/')
}

// 获取考勤状态样式类
const getAttendanceClass = (data) => {
    const attendance = getAttendanceInfo(data)
    if (!attendance) return ''
    
    const status = attendance.status ? attendance.status.split(',')[0] : '0'
    switch (status) {
        case '0': return 'normal'
        case '1': return 'late'
        case '2': return 'early'
        case '3': return 'absent'
        case '4': return 'leave'
        default: return ''
    }
}

// 切换月份
const changeMonth = async (offset) => {
    const date = new Date(calendarDate.value)
    date.setMonth(date.getMonth() + offset)
    calendarDate.value = date
    updateAllData() // 使用统一的更新方法
}

// 获取月度考勤记录
const getMonthlyAttendance = async () => {
    try {
        const date = calendarDate.value
        const yearMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
        console.log('请求月份:', yearMonth)
        
        const res = await getMonthHistoryService(yearMonth)
        if (res.code === 1) {
            // 直接使用后端返回的数据
            monthlyAttendance.value = res.data
        }
    } catch (error) {
        console.error('获取月度考勤记录失败:', error)
        ElMessage.error('获取月度考勤记录失败')
    }
}

// 获取位置信息并解析地址
const initLocationInfo = async () => {
    checkingLocation.value = true
    let locationData = null

    try {
        // 尝试获取浏览器位置
        if (navigator.geolocation) {
            locationData = await new Promise((resolve) => {
                const timeoutId = setTimeout(() => {
                    resolve(null)
                }, 3000)

                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        clearTimeout(timeoutId)
                        resolve({
                            latitude: position.coords.latitude,
                            longitude: position.coords.longitude
                        })
                    },
                    () => {
                        clearTimeout(timeoutId)
                        resolve(null)
                    },
                    { enableHighAccuracy: true, timeout: 3000 }
                )
            })
        }

        // 使用获取到的位置或默认位置
        const { latitude, longitude } = locationData || companyLocation
        currentLocation.value = { latitude, longitude }

        // 计算与公司的距离
        const distance = calculateDistance(
            latitude,
            longitude,
            companyLocation.latitude,
            companyLocation.longitude
        )
        isInRange.value = distance <= 0.1

        // 调用高德地图API解析地址
        const location = `${longitude},${latitude}`
        const url = `${AMAP_API}?key=${AMAP_KEY}&location=${location}&extensions=base`
        const response = await fetch(url)
        const data = await response.json()

        if (data.status === '1') {
            locationDetail.value = data.regeocode.formatted_address
            console.log({
                coords: { latitude, longitude },
                distance: (distance * 1000).toFixed(0) + '米',
                address: locationDetail.value
            })
        }
    } catch (error) {
        console.error('位置服务异常', error)
    } finally {
        checkingLocation.value = false
    }
}

// 计算两点之间的距离（km）
const calculateDistance = (lat1, lon1, lat2, lon2) => {
    const R = 6371 // 地球半径（km）
    const dLat = (lat2 - lat1) * Math.PI / 180
    const dLon = (lon2 - lon1) * Math.PI / 180
    const a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
        Math.sin(dLon/2) * Math.sin(dLon/2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    return R * c
}

// 处理打卡前的位置检查
const handleCheckInWithLocation = async () => {
    try {
        await initLocationInfo()
        if (!isInRange.value) {
            ElMessage.warning(`距离过远，请在公司附近打卡`)
            return
        }
        handleCheckIn()
    } catch (error) {
        ElMessage.error('位置服务异常')
    }
}

const handleCheckOutWithLocation = async () => {
    try {
        await initLocationInfo()
        if (!isInRange.value) {
            ElMessage.warning(`距离过远，请在公司附近打卡`)
            return
        }
        handleCheckOut()
    } catch (error) {
        ElMessage.error('位置服务异常')
    }
}

// 上班打卡
const handleCheckIn = async () => {
    try {
        checkingIn.value = true
        const res = await clockService({ type: 'IN' })
        if (res.code === 1) {
            ElMessage.success('上班打卡成功')
            console.log('上班打卡返回:', res.data)
            // 打卡成功后立即更新状态和统计
            await Promise.all([
                getTodayAttendance(),
                getMonthlyStats()
            ])
        } else {
            ElMessage.error(res.message || '上班打卡失败')
        }
    } catch (error) {
        console.error('上班打卡失败:', error)
        ElMessage.error('上班打卡失败')
    } finally {
        checkingIn.value = false
    }
}

// 下班打卡
const handleCheckOut = async () => {
    try {
        checkingOut.value = true
        const res = await clockService({ type: 'OUT' })
        if (res.code === 1) {
            ElMessage.success('下班打卡成功')
            console.log('下班打卡返回:', res.data)
            // 打卡成功后立即更新状态和统计
            await Promise.all([
                getTodayAttendance(),
                getMonthlyStats()
            ])
        } else {
            ElMessage.error(res.message || '下班打卡失败')
        }
    } catch (error) {
        console.error('下班打卡失败:', error)
        ElMessage.error('下班打卡失败')
    } finally {
        checkingOut.value = false
    }
}

// 获取当天打卡状态
const getTodayAttendance = async () => {
    try {
        const res = await getTodayAttendanceService()
        if (res.code === 1) {
            console.log('今日打卡状态:', res.data)
            // 0表示未打卡，1表示已打卡
            hasCheckedIn.value = res.data.IN === 1
            hasCheckedOut.value = res.data.OUT === 1
            
            // 如果已打卡，更新对应的时间显示
            if (hasCheckedIn.value) {
                const now = new Date()
                now.setHours(9, 0, 0) // 设置为上班时间9点
                workStartTime.value = now.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                })
            } else {
                workStartTime.value = '--:--'
            }
            
            if (hasCheckedOut.value) {
                const now = new Date()
                now.setHours(18, 0, 0) // 设置为下班时间18点
                workEndTime.value = now.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                })
            } else {
                workEndTime.value = '--:--'
            }
            
            calculateWorkDuration()
        }
    } catch (error) {
        console.error('获取打卡状态失败:', error)
    }
}

// 更新位置显示模板
const locationInfo = computed(() => {
    if (checkingLocation.value) return '定位中...'
    if (!currentLocation.value || !locationDetail.value) return '定位服务异常'
    
    const distance = calculateDistance(
        currentLocation.value.latitude,
        currentLocation.value.longitude,
        companyLocation.latitude,
        companyLocation.longitude
    )
    
    const distanceText = (distance * 1000).toFixed(0)
    return isInRange.value 
        ? `${locationDetail.value}（${distanceText}米）`
        : `${locationDetail.value}（${distanceText}米）`
})

// 更新时间和位置信息
const updateTimeAndLocation = () => {
    updateTime()
    initLocationInfo()
}

// 获取月度统计数据
const getMonthlyStats = async () => {
    try {
        loading.value = true  // 只对统计区域添加loading
        const res = await getMyAttendanceCountService()
        if (res.code === 1) {
            const data = res.data
            // 直接更新数据，不使用中间变量
            monthlyStats.value.normal = data[0] || 0  // 正常
            monthlyStats.value.late = data[1] || 0    // 迟到
            monthlyStats.value.early = data[2] || 0   // 早退
            monthlyStats.value.absent = data[3] || 0  // 旷工
            monthlyStats.value.leave = data[4] || 0   // 请假
        }
    } catch (error) {
        console.error('获取月度统计数据失败:', error)
        ElMessage.error('获取月度统计数据失败')
    } finally {
        loading.value = false
    }
}

// 更新所有数据的方法
const updateAllData = async () => {
    try {
        // 先获取统计数据，因为这个比较快
        await getMonthlyStats()
        // 然后再获取月度考勤记录
        await getMonthlyAttendance()
    } catch (error) {
        console.error('更新数据失败:', error)
    }
}

// 初始化
onMounted(async () => {
    console.log('组件挂载')
    // 并行获取打卡状态和统计数据
    await Promise.all([
        getTodayAttendance(),
        getMonthlyStats()
    ])
    // 然后再获取月度考勤记录
    getMonthlyAttendance()
    updateTimeAndLocation()
    
    // 启动定时器更新时间
    timer = setInterval(() => {
        updateTime()
    }, 1000)

    // 添加页面可见性监听
    document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 组件卸载时清除定时器和事件监听
onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
        timer = null
    }
    document.removeEventListener('visibilitychange', handleVisibilityChange)
})

// 修改页面可见性变化处理
const handleVisibilityChange = () => {
    if (document.visibilityState === 'visible') {
        updateTimeAndLocation()
        // 只更新必要的数据
        getTodayAttendance()
        getMonthlyStats()
    }
}

// 监听日历日期变化
watch(calendarDate, () => {
    getMonthlyAttendance() // 只更新日历数据
})
</script>

<style lang="scss" scoped>
.my-attendance {
    padding: 16px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 84px);

    .check-area {
        background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
        padding: 20px;
        border-radius: 12px;
        margin-bottom: 16px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

        .main-section {
            display: flex;
            align-items: stretch;
            gap: 16px;
            margin-bottom: 20px;

            .time-block {
                display: flex;
                flex-direction: column;
                background-color: rgba(255, 255, 255, 0.8);
                padding: 12px;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                width: 360px;

                .time-display {
                    flex-grow: 1;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    margin-bottom: 8px;

                    .current-time {
                        font-size: 56px;
                        font-weight: 700;
                        line-height: 1.1;
                        font-family: 'Arial', sans-serif;
                        letter-spacing: 2px;
                        background: linear-gradient(135deg, #1a365d 0%, #2563eb 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.15);
                        white-space: nowrap;
                        text-align: center;
                    }

                    .current-date {
                        font-size: 16px;
                        color: #64748b;
                        font-weight: 500;
                        text-align: center;
                        margin-top: 8px;
                    }
                }

                .location-info {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    color: #64748b;
                    font-size: 13px;
                    padding: 8px;
                    border-radius: 6px;
                    background-color: #f8f9fa;

                    .el-icon {
                        font-size: 15px;
                        color: #3b82f6;
                        flex-shrink: 0;
                    }

                    span {
                        flex-grow: 1;
                        line-height: 1.4;
                    }

                    &.warning {
                        color: #ef4444;
                    }

                    &.success {
                        color: #10b981;
                    }
                }
            }

            .check-group {
                display: flex;
                align-items: stretch;
                gap: 16px;
                flex: 1;

                .check-item, .work-duration {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    background-color: rgba(255, 255, 255, 0.8);
                    padding: 12px;
                    border-radius: 8px;
                    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                    min-width: 120px;

                    .check-button {
                        flex-grow: 1;
                        height: 42px;
                        font-size: 14px;
                        border-radius: 6px;
                        transition: all 0.3s ease;

                        &.el-button--primary {
                            background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
                            border: none;
                        }

                        &.el-button--success {
                            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                            border: none;
                        }
                    }

                    .status-card {
                        text-align: center;
                        padding: 8px 0 0;

                        .label {
                            font-size: 13px;
                            color: #64748b;
                            margin-bottom: 4px;
                        }

                        .value {
                            font-size: 14px;
                            font-weight: 600;
                            color: #ef4444;

                            &.checked {
                                color: #10b981;
                            }
                        }
                    }
                }

                .work-duration {
                    .label {
                        font-size: 13px;
                        color: #64748b;
                        text-align: center;
                        margin-bottom: 8px;
                    }

                    .progress-container {
                        flex-grow: 1;
                        position: relative;
                        background-color: #f1f5f9;
                        border-radius: 6px;
                        overflow: hidden;
                        min-height: 42px;

                        .progress-bar {
                            position: absolute;
                            left: 0;
                            top: 0;
                            height: 100%;
                            background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
                            transition: width 0.3s ease;
                        }

                        .progress-text {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            color: #1a365d;
                            font-size: 14px;
                            font-weight: 600;
                            z-index: 1;
                        }
                    }

                    .time-details {
                        text-align: center;
                        font-size: 12px;
                        color: #64748b;
                        padding-top: 8px;
                    }
                }
            }
        }

        .stats-section {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            padding-top: 16px;
            border-top: 1px solid rgba(0, 0, 0, 0.05);

            .stat-item {
                flex: 1;
                background: rgba(255, 255, 255, 0.8);
                padding: 10px;
                border-radius: 6px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                text-align: center;

                .label {
                    font-size: 12px;
                    color: #64748b;
                    margin-bottom: 4px;
                }

                .value {
                    font-size: 20px;
                    font-weight: 600;
                    background: linear-gradient(135deg, #1a365d 0%, #2563eb 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                }
            }
        }
    }

    .attendance-calendar {
        background: #ffffff;
        padding: 20px;
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

        .calendar-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 1px solid #ebeef5;

            .month-selector {
                display: flex;
                align-items: center;
                gap: 8px;

                .el-button {
                    padding: 8px;
                    border-radius: 8px;
                    color: #1a365d;
                    
                    &:hover {
                        background-color: #f0f7ff;
                    }
                }

                .current-month {
                    font-size: 20px;
                    font-weight: 600;
                    color: #1a365d;
                    min-width: 120px;
                    text-align: center;
                }
            }
        }

        :deep(.el-calendar) {
            background-color: transparent;
            
            .el-calendar__header {
                padding: 0;
                border-bottom: none;
            }
            
            .el-calendar__body {
                padding: 12px 20px;
            }

            .calendar-cell {
                height: 100%;
                padding: 8px;
                border-radius: 4px;
                min-height: 80px;

                .date-number {
                    font-size: 14px;
                    color: #333;
                    margin-bottom: 4px;
                }

                .attendance-info {
                    font-size: 12px;
                    display: flex;
                    flex-direction: column;
                    gap: 4px;

                    .status-tag {
                        display: inline-block;
                        padding: 2px 6px;
                        border-radius: 4px;
                        margin-bottom: 4px;
                        font-weight: 500;
                    }

                    .check-time {
                        color: #666;
                        line-height: 1.5;
                        white-space: nowrap;
                    }

                    &.normal {
                        .status-tag {
                            background-color: rgba(82, 196, 26, 0.1);
                            color: #52c41a;
                        }
                    }

                    &.late {
                        .status-tag {
                            background-color: rgba(250, 140, 22, 0.1);
                            color: #fa8c16;
                        }
                    }

                    &.early {
                        .status-tag {
                            background-color: rgba(250, 140, 22, 0.1);
                            color: #fa8c16;
                        }
                    }

                    &.absent {
                        .status-tag {
                            background-color: rgba(245, 34, 45, 0.1);
                            color: #f5222d;
                        }
                    }

                    &.leave {
                        .status-tag {
                            background-color: rgba(114, 46, 209, 0.1);
                            color: #722ed1;
                        }
                    }
                }
            }
        }

        .el-calendar-day {
            height: 100px;
            padding: 4px;
            vertical-align: top;
        }
    }
}

:deep(.el-calendar-table) {
    td {
        border: 1px solid #ebeef5;
        height: auto;
        padding: 0;
    }
    
    .el-calendar-day {
        height: auto;
        padding: 0;
        min-height: 80px;
    }
}
</style> 