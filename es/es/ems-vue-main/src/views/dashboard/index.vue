<template>
  <div class="dashboard-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="24">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in statistics" :key="item.id">
        <div class="stat-card" :class="item.type">
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-label">{{ item.label }}</div>
              <div class="icon-box" :class="item.type">
                <component :is="item.icon" />
              </div>
            </div>
            <div class="stat-body">
              <div class="stat-value">{{ animatedValue[item.id] }}</div>
              <div class="stat-trend-box" :class="item.trend > 0 ? 'up' : 'down'" v-if="item.trend !== undefined">
                <div class="trend-icon">
                  <component :is="item.trend > 0 ? CaretTop : CaretBottom" />
                </div>
                <span class="trend-value">{{ Math.abs(item.trend) }}%</span>
                <span class="trend-label">较上月</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="chart-row">
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="title">
              <el-icon><Histogram /></el-icon>
              <span>部门人员分布</span>
              <el-tag size="small" effect="dark" type="primary" class="tag">实时</el-tag>
            </div>
            <div class="chart-controls">
              <el-radio-group v-model="deptChartType" size="small" @change="handleChartTypeChange">
                <el-radio-button value="pie">
                  <el-icon><PieChart /></el-icon>饼图
                </el-radio-button>
                <el-radio-button value="bar">
                  <el-icon><Histogram /></el-icon>柱状图
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-content">
            <div ref="deptChartRef" class="chart"></div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="title">
              <el-icon><DataLine /></el-icon>
              <span>考勤统计</span>
              <el-tag size="small" type="success" effect="dark" class="tag">月度</el-tag>
            </div>
            <div class="chart-controls">
              <el-select v-model="attendanceMonth" size="small" class="month-select" @change="handleMonthChange">
                <el-option v-for="m in months" :key="m" :label="`${m}月`" :value="m" />
              </el-select>
              <el-button-group class="view-toggle" size="small">
                <el-tooltip content="日视图" placement="top">
                  <el-button :type="viewType === 'day' ? 'primary' : ''" @click="handleViewTypeChange('day')">
                    <el-icon><Calendar /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="周视图" placement="top">
                  <el-button :type="viewType === 'week' ? 'primary' : ''" @click="handleViewTypeChange('week')">
                    <el-icon><Calendar /></el-icon>
                  </el-button>
                </el-tooltip>
              </el-button-group>
            </div>
          </div>
          <div class="chart-content">
            <div ref="attendanceChartRef" class="chart"></div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import * as echarts from 'echarts'
import {
  User,
  Briefcase,
  Calendar,
  Warning,
  CaretTop,
  CaretBottom,
  PieChart,
  Histogram,
  DataLine,
} from '@element-plus/icons-vue'
import { getDeptDistributionService, getMonthlyAttendanceService, getEmpCountService, getLeaveCountService, getAttendanceCountService } from '@/api/dashboard'

// 统计数据
const statistics = ref([
  {
    id: 1,
    label: '员工总数',
    value: 0,
    icon: markRaw(User),
    type: 'primary',
    trend: 5.2
  },
  {
    id: 2,
    label: '在岗人数',
    value: 0,
    icon: markRaw(Briefcase),
    type: 'success',
    trend: 3.1
  },
  {
    id: 3,
    label: '请假人数',
    value: 0,
    icon: markRaw(Calendar),
    type: 'warning',
    trend: -2.5
  },
  {
    id: 4,
    label: '考勤异常',
    value: 0,
    icon: markRaw(Warning),
    type: 'danger',
    trend: -1.8
  }
])

// 在script setup顶部定义响应式数据
const deptData = ref([])
const monthlyData = ref(null)
const attendanceYear = ref(new Date().getFullYear())
const attendanceMonth = ref(new Date().getMonth() + 1)
const months = Array.from({length: 12}, (_, i) => i + 1)
const viewType = ref('day')

// 图表相关
const deptChartRef = ref(null)
const deptChart = ref(null)
const deptChartType = ref('pie')
const attendanceChartRef = ref(null)
const attendanceChart = ref(null)

// 添加动画数值状态
const animatedValue = ref({})

// 数字动画函数
const animateValue = (id, start, end, duration) => {
  const startTime = Date.now()
  const updateValue = () => {
    const currentTime = Date.now()
    const elapsed = currentTime - startTime
    
    if (elapsed < duration) {
      const value = Math.round(easeOutQuad(elapsed, start, end - start, duration))
      animatedValue.value[id] = value
      requestAnimationFrame(updateValue)
    } else {
      animatedValue.value[id] = end
    }
  }
  updateValue()
}

// 缓动函数
const easeOutQuad = (t, b, c, d) => {
  t /= d
  return -c * t * (t - 2) + b
}

// 获取部门分布数据
const getDeptData = async () => {
    try {
        const res = await getDeptDistributionService()
        if (res.code === 1) {
            deptData.value = res.data
            initDeptChart()
        }
    } catch (error) {
        console.error('获取部门分布数据失败:', error)
    }
}

// 获取月度考勤数据
const getMonthlyData = async () => {
    try {
        const res = await getMonthlyAttendanceService(
            attendanceYear.value,
            attendanceMonth.value
        )
        if (res.code === 1) {
            monthlyData.value = res.data
            initAttendanceChart()
        }
    } catch (error) {
        console.error('获取月度考勤数据失败:', error)
    }
}

// 获取统计数据
const getStatisticsData = async () => {
  try {
    const [empRes, leaveRes, attendanceRes] = await Promise.all([
      getEmpCountService(),
      getLeaveCountService(),
      getAttendanceCountService()
    ])

    if (empRes.code === 1 && leaveRes.code === 1 && attendanceRes.code === 1) {
      const empCount = empRes.data
      const leaveCount = leaveRes.data
      const attendanceCount = attendanceRes.data

      // 更新统计数据
      statistics.value[0].value = empCount
      statistics.value[1].value = empCount - leaveCount // 在岗人数 = 员工总数 - 请假人数
      statistics.value[2].value = leaveCount
      statistics.value[3].value = attendanceCount

      // 重新触发数字动画
      statistics.value.forEach(item => {
        animateValue(item.id, 0, item.value, 2000)
      })
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化部门分布图表
const initDeptChart = () => {
    if (!deptChartRef.value) return
    
    if (!deptChart.value) {
        deptChart.value = echarts.init(deptChartRef.value)
    }

    const options = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c}人 ({d}%)',
            backgroundColor: 'rgba(0,0,0,0.7)',
            borderColor: '#333',
            textStyle: {
                color: '#fff'
            }
        },
        legend: {
            orient: 'horizontal',
            bottom: 0,
            left: 'center',
            itemWidth: 12,
            itemHeight: 12,
            textStyle: {
                color: '#666',
                fontSize: 12
            }
        },
        series: [
            {
                name: '部门分布',
                type: deptChartType.value,
                radius: deptChartType.value === 'pie' ? ['45%', '65%'] : undefined,
                center: ['50%', '45%'],
                data: deptData.value,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                itemStyle: {
                    borderRadius: 8,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: true,
                    formatter: '{c}人'
                }
            }
        ]
    }

    // 如果是柱状图，添加坐标轴配置
    if (deptChartType.value === 'bar') {
        options.xAxis = {
            type: 'category',
            data: deptData.value.map(item => item.name)
        }
        options.yAxis = {
            type: 'value',
            name: '人数'
        }
    }

    deptChart.value.setOption(options)
}

// 初始化考勤统计图表
const initAttendanceChart = () => {
    if (!attendanceChartRef.value || !monthlyData.value) return
    
    if (!attendanceChart.value) {
        attendanceChart.value = echarts.init(attendanceChartRef.value)
    }

    const { statistics, summary } = monthlyData.value

    const options = {
        tooltip: {
            trigger: 'axis',
            axisPointer: { 
                type: 'shadow',
                shadowStyle: {
                    color: 'rgba(0,0,0,0.05)'
                }
            }
        },
        legend: {
            data: ['正常', '迟到', '缺勤'],
            bottom: 0,
            icon: 'circle',
            itemWidth: 8,
            itemHeight: 8,
            textStyle: {
                color: '#666'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            top: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: statistics.dates,
            axisLine: {
                lineStyle: {
                    color: '#ddd'
                }
            },
            axisTick: {
                show: false
            }
        },
        yAxis: {
            type: 'value',
            name: '人数',
            splitLine: {
                lineStyle: {
                    color: '#eee'
                }
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            }
        },
        series: [
            {
                name: '正常',
                type: 'line',
                data: statistics.normal,
                smooth: true,
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: { 
                    color: '#67C23A',
                    borderWidth: 2,
                    borderColor: '#fff'
                },
                lineStyle: {
                    width: 3
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: 'rgba(103,194,58,0.2)'
                        }, {
                            offset: 1,
                            color: 'rgba(103,194,58,0)'
                        }]
                    }
                }
            },
            {
                name: '迟到',
                type: 'bar',
                data: statistics.late,
                barWidth: 10,
                itemStyle: { 
                    color: '#E6A23C',
                    borderRadius: [10, 10, 0, 0]
                }
            },
            {
                name: '缺勤',
                type: 'bar',
                data: statistics.absent,
                barWidth: 10,
                itemStyle: { 
                    color: '#F56C6C',
                    borderRadius: [10, 10, 0, 0]
                }
            }
        ]
    }
    attendanceChart.value.setOption(options)
}

// 处理图表类型变化
const handleChartTypeChange = () => {
  initDeptChart()
}

// 处理月份变化
const handleMonthChange = () => {
    getMonthlyData()
}

// 处理视图类型变化
const handleViewTypeChange = (type) => {
  viewType.value = type
  // 这里可以添加根据视图类型更新数据的逻辑
  initAttendanceChart()
}

// 监听窗口大小变化
const handleResize = () => {
  deptChart.value?.resize()
  attendanceChart.value?.resize()
}

onMounted(() => {
  // 获取统计数据
  getStatisticsData()

  // 初始化数字动画
  statistics.value.forEach(item => {
    animateValue(item.id, 0, item.value, 2000)
  })

  // 初始化图表
  getDeptData()
  getMonthlyData()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  if (deptChart.value) {
    deptChart.value.dispose()
  }
  if (attendanceChart.value) {
    attendanceChart.value.dispose()
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f6f8fb;
  min-height: calc(100vh - 84px);
  
  .stat-card {
    padding: 24px;
    margin-bottom: 24px;
    border-radius: 16px;
    background: #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
    }
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    }
    
    &.primary::before { background: linear-gradient(90deg, #409EFF, #85c5ff); }
    &.success::before { background: linear-gradient(90deg, #67C23A, #95d475); }
    &.warning::before { background: linear-gradient(90deg, #E6A23C, #f3d19e); }
    &.danger::before { background: linear-gradient(90deg, #F56C6C, #fab6b6); }
    
    .stat-content {
      .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .stat-label {
          font-size: 16px;
          color: #606266;
          font-weight: 500;
        }
        
        .icon-box {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          transition: all 0.3s ease;
          
          &.primary {
            background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.2));
            color: #409EFF;
          }
          
          &.success {
            background: linear-gradient(135deg, rgba(103, 194, 58, 0.1), rgba(103, 194, 58, 0.2));
            color: #67C23A;
          }
          
          &.warning {
            background: linear-gradient(135deg, rgba(230, 162, 60, 0.1), rgba(230, 162, 60, 0.2));
            color: #E6A23C;
          }
          
          &.danger {
            background: linear-gradient(135deg, rgba(245, 108, 108, 0.1), rgba(245, 108, 108, 0.2));
            color: #F56C6C;
          }

          &:hover {
            transform: scale(1.05) rotate(5deg);
          }
        }
      }
      
      .stat-body {
        .stat-value {
          font-size: 36px;
          font-weight: 700;
          background: linear-gradient(45deg, #1a1a1a, #4a4a4a);
          -webkit-background-clip: text;
          color: transparent;
          margin-bottom: 16px;
        }
        
        .stat-trend-box {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 6px 12px;
          border-radius: 20px;
          font-size: 13px;
          
          &.up {
            background: linear-gradient(to right, rgba(103, 194, 58, 0.1), rgba(103, 194, 58, 0.05));
            
            .trend-icon {
              background-color: #67C23A;
              color: white;
            }
            
            .trend-value {
              color: #67C23A;
            }
          }
          
          &.down {
            background: linear-gradient(to right, rgba(245, 108, 108, 0.1), rgba(245, 108, 108, 0.05));
            
            .trend-icon {
              background-color: #F56C6C;
              color: white;
            }
            
            .trend-value {
              color: #F56C6C;
            }
          }

          .trend-icon {
            width: 16px;
            height: 16px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
          }

          .trend-value {
            font-weight: 600;
          }

          .trend-label {
            color: #909399;
            font-size: 12px;
          }
        }
      }
    }
  }
  
  .chart-card {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 24px;
    
    .chart-header {
      padding: 20px 24px;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        font-size: 16px;
        
        .el-icon {
          font-size: 20px;
          color: #409EFF;
        }
        
        .tag {
          margin-left: 8px;
          text-transform: uppercase;
          font-size: 11px;
          padding: 0 8px;
          border-radius: 4px;
        }
      }
      
      .chart-controls {
        display: flex;
        gap: 12px;
        align-items: center;
        
        .month-select {
          width: 100px;
        }
        
        .el-radio-button__inner {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
    
    .chart-content {
      padding: 24px;
      
      .chart {
        height: 400px;
        transition: all 0.3s;
      }
    }
  }
}

.primary .icon-box { color: #409EFF; }
.success .icon-box { color: #67C23A; }
.warning .icon-box { color: #E6A23C; }
.danger .icon-box { color: #F56C6C; }
</style> 