<template>
  <div class="attendance-statistics">
    <!-- 第一行：统计卡片和异常TOP榜 -->
    <el-row :gutter="15">
      <!-- 左侧统计卡片 -->
      <el-col :span="7">
        <el-row :gutter="15">
          <el-col :span="12">
            <div class="stat-card primary">
              <div class="stat-content">
                <div class="stat-header">
                  <div class="stat-label">总员工数</div>
                  <div class="icon-box">
                    <el-icon><User /></el-icon>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-value">{{ statistics.total }}</div>
                  <div class="stat-desc">出勤率 {{ statistics.attendanceRate }}</div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-card success">
              <div class="stat-content">
                <div class="stat-header">
                  <div class="stat-label">今日出勤</div>
                  <div class="icon-box">
                    <el-icon><Calendar /></el-icon>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-value">{{ statistics.present }}</div>
                  <div class="stat-desc">实到人数</div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-card warning">
              <div class="stat-content">
                <div class="stat-header">
                  <div class="stat-label">考勤异常</div>
                  <div class="icon-box">
                    <el-icon><Warning /></el-icon>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-value">{{ statistics.summary?.late + statistics.summary?.early }}</div>
                  <div class="stat-desc">迟到{{ statistics.summary?.late }}人 早退{{ statistics.summary?.early }}人</div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-card info">
              <div class="stat-content">
                <div class="stat-header">
                  <div class="stat-label">加班情况</div>
                  <div class="icon-box">
                    <el-icon><Timer /></el-icon>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-value">{{ statistics.summary?.overtime }}</div>
                  <div class="stat-desc">{{ formatMinutes(statistics.timeStats?.totalOvertimeMinutes) }}</div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-col>
      
      <!-- 右侧异常TOP榜 -->
      <el-col :span="17">
        <div class="chart-card">
          <div class="chart-header">
            <div class="title">
              <el-icon><List /></el-icon>
              <span>异常TOP榜</span>
            </div>
            <div class="chart-controls">
              <el-radio-group v-model="topType" size="small" @change="getTopData">
                <el-radio-button value="1">迟到</el-radio-button>
                <el-radio-button value="2">早退</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-content">
            <el-table :data="topList" style="width: 100%" size="small" border>
              <el-table-column prop="empName" label="姓名" width="80" align="center" />
              <el-table-column prop="deptName" label="部门" show-overflow-tooltip />
              <el-table-column prop="count" label="次数" width="60" align="center" />
              <el-table-column label="累计时长" width="100" align="center">
                <template #default="{ row }">
                  {{ formatMinutes(row.totalMinutes) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行：考勤趋势和部门统计 -->
    <el-row :gutter="15">
      <!-- 左侧考勤趋势 -->
      <el-col :xs="24" :md="17">
        <div class="chart-card">
          <div class="chart-header">
            <div class="title">
              <el-icon><DataLine /></el-icon>
              <span>考勤趋势</span>
            </div>
            <div class="chart-controls">
              <el-select v-model="trendDays" size="small" @change="getTrendData">
                <el-option label="最近7天" :value="7" />
                <el-option label="最近14天" :value="14" />
                <el-option label="最近30天" :value="30" />
              </el-select>
            </div>
          </div>
          <div class="chart-content">
            <div ref="trendChartRef" class="chart"></div>
          </div>
        </div>
      </el-col>

      <!-- 右侧部门统计 -->
      <el-col :xs="24" :md="7">
        <div class="chart-card">
          <div class="chart-header">
            <div class="title">
              <el-icon><OfficeBuilding /></el-icon>
              <span>部门考勤统计</span>
            </div>
          </div>
          <div class="chart-content">
            <el-table :data="deptStats" border style="width: 100%" size="small">
              <el-table-column prop="deptName" label="部门名称" min-width="120" />
              <el-table-column prop="total" label="总人数" width="70" align="center" />
              <el-table-column label="出勤率" width="100" align="center">
                <template #default="{ row }">
                  <el-progress 
                    :percentage="parseFloat(row.attendanceRate)" 
                    :color="getAttendanceRateColor(parseFloat(row.attendanceRate))"
                    :stroke-width="12"
                    :text-inside="true"
                  />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { 
  User, Calendar, Warning, Timer, DataLine, 
  List, OfficeBuilding 
} from '@element-plus/icons-vue'
import { 
  getAttendanceStatisticsService,
  getAttendanceTrendService,
  getDeptAttendanceService,
  getAttendanceTopService
} from '@/api/dashboard'

// 统计数据
const statistics = ref({
  total: 0,
  present: 0,
  attendanceRate: '0%',
  summary: {
    late: 0,
    early: 0,
    absent: 0,
    leave: 0,
    overtime: 0
  },
  timeStats: {
    totalLateMinutes: 0,
    totalEarlyMinutes: 0,
    totalOvertimeMinutes: 0
  }
})

// 趋势图相关
const trendChartRef = ref(null)
let trendChart = null
const trendDays = ref(7)
const trendData = ref([])

// TOP榜相关
const topType = ref('1')
const topList = ref([])

// 部门统计
const deptStats = ref([])

// 获取统计数据
const getStatisticsData = async () => {
  try {
    const res = await getAttendanceStatisticsService()
    if (res.code === 1) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('获取考勤统计数据失败:', error)
  }
}

// 获取趋势数据
const getTrendData = async () => {
  try {
    const res = await getAttendanceTrendService(trendDays.value)
    if (res.code === 1) {
      trendData.value = res.data
      initTrendChart()
    }
  } catch (error) {
    console.error('获取考勤趋势数据失败:', error)
  }
}

// 获取部门统计
const getDeptData = async () => {
  try {
    const res = await getDeptAttendanceService()
    if (res.code === 1) {
      deptStats.value = res.data
    }
  } catch (error) {
    console.error('获取部门考勤统计失败:', error)
  }
}

// 获取TOP榜数据
const getTopData = async () => {
  try {
    const res = await getAttendanceTopService(10, topType.value)
    if (res.code === 1) {
      topList.value = res.data
    }
  } catch (error) {
    console.error('获取考勤TOP数据失败:', error)
  }
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  // 确保容器有高度后再初始化
  nextTick(() => {
    if (!trendChart) {
      trendChart = echarts.init(trendChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: ['出勤率', '迟到', '早退', '加班'],
        bottom: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        top: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: trendData.value.map(item => item.date.substring(5)),
        axisLabel: {
          rotate: 30
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '人数',
          position: 'left',
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed'
            }
          }
        },
        {
          type: 'value',
          name: '出勤率',
          position: 'right',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value}%'
          },
          splitLine: {
            show: false
          }
        }
      ],
      series: [
        {
          name: '出勤率',
          type: 'line',
          yAxisIndex: 1,
          data: trendData.value.map(item => parseFloat(item.attendanceRate)),
          smooth: true,
          lineStyle: {
            width: 3
          },
          itemStyle: {
            color: '#409EFF'
          }
        },
        {
          name: '迟到',
          type: 'bar',
          stack: 'attendance',
          data: trendData.value.map(item => item.late),
          itemStyle: {
            color: '#E6A23C'
          }
        },
        {
          name: '早退',
          type: 'bar',
          stack: 'attendance',
          data: trendData.value.map(item => item.early),
          itemStyle: {
            color: '#F56C6C'
          }
        },
        {
          name: '加班',
          type: 'bar',
          data: trendData.value.map(item => item.overtime),
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    }

    trendChart.setOption(option)
    // 立即触发一次 resize
    trendChart.resize()
  })
}

// 格式化分钟数为小时分钟
const formatMinutes = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

// 获取出勤率颜色
const getAttendanceRateColor = (rate) => {
  if (rate >= 95) return '#67C23A'
  if (rate >= 85) return '#E6A23C'
  return '#F56C6C'
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
}

onMounted(() => {
  // 获取数据
  getStatisticsData()
  getTrendData()
  getDeptData()
  getTopData()

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
})
</script>

<style lang="scss" scoped>
.attendance-statistics {
  padding: 15px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
  
  .stat-card {
    height: 120px;
    padding: 16px;
    margin-bottom: 15px;
    border-radius: 12px;
    background: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
    }
    
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }
    
    &.primary {
      background: linear-gradient(135deg, rgba(24, 144, 255, 0.05) 0%, rgba(255, 255, 255, 1) 100%);
      &::before { background: linear-gradient(90deg, #1890ff, #69c0ff); }
      .icon-box { background: rgba(24, 144, 255, 0.1); color: #1890ff; }
    }
    &.success {
      background: linear-gradient(135deg, rgba(82, 196, 26, 0.05) 0%, rgba(255, 255, 255, 1) 100%);
      &::before { background: linear-gradient(90deg, #52c41a, #95de64); }
      .icon-box { background: rgba(82, 196, 26, 0.1); color: #52c41a; }
    }
    &.warning {
      background: linear-gradient(135deg, rgba(250, 173, 20, 0.05) 0%, rgba(255, 255, 255, 1) 100%);
      &::before { background: linear-gradient(90deg, #faad14, #ffd666); }
      .icon-box { background: rgba(250, 173, 20, 0.1); color: #faad14; }
    }
    &.info {
      background: linear-gradient(135deg, rgba(140, 140, 140, 0.05) 0%, rgba(255, 255, 255, 1) 100%);
      &::before { background: linear-gradient(90deg, #8c8c8c, #bfbfbf); }
      .icon-box { background: rgba(140, 140, 140, 0.1); color: #8c8c8c; }
    }
    
    .stat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      position: relative;
      z-index: 1;
      
      .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .stat-label {
          font-size: 14px;
          color: #595959;
          font-weight: 500;
          letter-spacing: 0.5px;
        }
        
        .icon-box {
          width: 36px;
          height: 36px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          transition: all 0.3s ease;
          
          &:hover {
            transform: scale(1.1) rotate(5deg);
          }
        }
      }
      
      .stat-body {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          background: linear-gradient(45deg, #262626, #595959);
          -webkit-background-clip: text;
          color: transparent;
          margin-bottom: 6px;
          line-height: 1.2;
        }
        
        .stat-desc {
          color: #8c8c8c;
          font-size: 13px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          line-height: 1.4;
        }
      }
    }
  }

  .chart-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    margin-bottom: 15px;
    
    .chart-header {
      padding: 16px 20px;
      border-bottom: 1px solid #f0f0f0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        font-size: 15px;
        color: #262626;
        
        .el-icon {
          font-size: 20px;
          color: #1890ff;
          background: rgba(24, 144, 255, 0.1);
          padding: 6px;
          border-radius: 8px;
        }
      }

      .chart-controls {
        .el-radio-group {
          .el-radio-button__inner {
            padding: 6px 16px;
            border-color: #e8e8e8;
            box-shadow: none !important;
            font-size: 13px;
            
            &:hover {
              color: #1890ff;
              border-color: #1890ff;
            }
          }
          
          .el-radio-button__original-radio:checked + .el-radio-button__inner {
            background-color: #1890ff;
            border-color: #1890ff;
            box-shadow: -1px 0 0 0 #1890ff;
          }
        }
      }
    }
    
    .chart-content {
      padding: 0;
      
      .chart {
        height: 300px;
        width: 100%;
      }

      .el-table {
        --el-table-header-bg-color: #fafafa;
        --el-table-border-color: #f0f0f0;
        --el-table-header-text-color: #262626;
        --el-table-row-hover-bg-color: #f5f5f5;
        
        :deep(th) {
          font-weight: 600;
          font-size: 13px;
          background-color: var(--el-table-header-bg-color);
          padding: 12px 0;
          border-bottom: 1px solid var(--el-table-border-color);
        }
        
        :deep(td) {
          font-size: 13px;
          padding: 12px 0;
          color: #595959;
          border-bottom: 1px solid var(--el-table-border-color);
        }

        :deep(.cell) {
          padding: 0 12px;
        }

        :deep(tr:hover) td {
          background-color: var(--el-table-row-hover-bg-color);
        }

        :deep(.el-table__empty-block) {
          min-height: 60px;
          .el-table__empty-text {
            color: #8c8c8c;
          }
        }
      }
    }
  }
}

.primary .icon-box { color: #409EFF; }
.success .icon-box { color: #67C23A; }
.warning .icon-box { color: #E6A23C; }
.info .icon-box { color: #909399; }
</style> 