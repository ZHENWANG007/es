<template>
    <div class="leave-approve">
        <!-- 搜索表单 -->
        <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="员工姓名">
                <el-input v-model="searchForm.empName" placeholder="请输入员工姓名" clearable />
            </el-form-item>
            <el-form-item label="请假类型">
                <el-select v-model="searchForm.leaveType" placeholder="请选择请假类型" clearable style="width: 180px">
                    <el-option label="事假" :value="1" />
                    <el-option label="病假" :value="2" />
                    <el-option label="公假" :value="3" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="batch-operation">
                <el-button type="success" @click="handleBatchApprove(1)" :disabled="selectedLeaves.length === 0">
                    <el-icon><Check /></el-icon>批量通过
                </el-button>
                <el-button type="danger" @click="handleBatchApprove(2)" :disabled="selectedLeaves.length === 0">
                    <el-icon><Close /></el-icon>批量驳回
                </el-button>
            </div>
        </div>

        <!-- 数据表格 -->
        <el-table 
            :data="leaveList" 
            border 
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="empName" label="员工姓名" width="100" align="center" />
            <el-table-column prop="leaveType" label="请假类型" width="120" align="center">
                <template #default="{ row }">
                    {{ getLeaveTypeName(row.leaveType) }}
                </template>
            </el-table-column>
            <el-table-column prop="days" label="请假天数" width="100" align="center">
                <template #default="{ row }">
                    {{ calculateDays(row.startDate, row.endDate) }}天
                </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始日期" width="120" align="center" />
            <el-table-column prop="endDate" label="结束日期" width="120" align="center" />
            <el-table-column prop="reason" label="请假原因" min-width="200" align="center" show-overflow-tooltip />
            <el-table-column prop="leaderStatus" label="直属领导审批" width="120" align="center">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.leaderStatus)">
                        {{ getStatusName(row.leaderStatus) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="hrStatus" label="HR审批" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.hrStatus)">
                        {{ getStatusName(row.hrStatus) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="overallStatus" label="总审批状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.overallStatus)">
                        {{ getStatusName(row.overallStatus) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
                <template #default="{ row }">
                    <div class="operation-buttons">
                        <el-button type="primary" link @click="handleView(row)">
                            <el-icon><View /></el-icon>查看
                        </el-button>
                        <template v-if="row.overallStatus === 0">
                            <el-button type="success" link @click="handleApprove(row, 1)">
                                <el-icon><Check /></el-icon>通过
                            </el-button>
                            <el-button type="danger" link @click="handleApprove(row, 2)">
                                <el-icon><Close /></el-icon>驳回
                            </el-button>
                        </template>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 查看详情对话框 -->
        <el-dialog v-model="dialogVisible" title="请假详情" width="600px">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="员工姓名">{{ currentLeave.empName }}</el-descriptions-item>
                <el-descriptions-item label="部门">{{ currentLeave.deptName }}</el-descriptions-item>
                <el-descriptions-item label="请假类型">{{ getLeaveTypeName(currentLeave.leaveType) }}</el-descriptions-item>
                <el-descriptions-item label="请假天数">{{ currentLeave.days }}天</el-descriptions-item>
                <el-descriptions-item label="开始日期">{{ currentLeave.startDate }}</el-descriptions-item>
                <el-descriptions-item label="结束日期">{{ currentLeave.endDate }}</el-descriptions-item>
                <el-descriptions-item label="请假原因" :span="2">{{ currentLeave.reason }}</el-descriptions-item>
                <el-descriptions-item label="直属领导审批">
                    <el-tag :type="getStatusType(currentLeave.leaderStatus)">
                        {{ getStatusName(currentLeave.leaderStatus) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="领导审批人">{{ currentLeave.leaderApprovedByName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="领导审批时间" :span="2">{{ currentLeave.leaderApprovedTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="HR审批">
                    <el-tag :type="getStatusType(currentLeave.hrStatus)">
                        {{ getStatusName(currentLeave.hrStatus) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="HR审批人">{{ currentLeave.hrApprovedByName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="HR审批时间" :span="2">{{ currentLeave.hrApprovedTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="总审批状态">
                    <el-tag :type="getStatusType(currentLeave.overallStatus)">
                        {{ getStatusName(currentLeave.overallStatus) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ currentLeave.createTime }}</el-descriptions-item>
            </el-descriptions>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Check, Close } from '@element-plus/icons-vue'
import { getPendingApprovals, approveLeaveService } from '@/api/leave'
import { getRoleKeyService } from '@/api/emp'

// 搜索表单数据
const searchForm = ref({
    empName: '',
    leaveType: ''
})

// 表格数据
const leaveList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedLeaves = ref([])

// 对话框数据
const dialogVisible = ref(false)
const currentLeave = ref({})

// 选择变化处理
const handleSelectionChange = (selection) => {
    selectedLeaves.value = selection
}

// 获取审批类型
const getApproveType = () => {
    const empInfo = userStore.empInfo
    console.log('当前用户信息:', empInfo)
    
    // 如果是部门经理，返回直属领导审批类型
    if (empInfo?.jobTitle?.includes('经理')) {
        return 1
    }
    // 如果是HR，返回HR审批类型
    if (empInfo?.jobTitle?.includes('HR')) {
        return 2
    }
    return 0 // 无权限
}

// 处理审批
const handleApprove = async (row, status) => {
    try {
        // 获取用户权限标识
        const res = await getRoleKeyService()
        if (res.code !== 1) {
            ElMessage.error('获取权限信息失败')
            return
        }
        
        const roleKey = res.data
        // 根据权限标识判断审批类型
        let type = 0
        if (roleKey === 'hr') {
            type = 2 // HR审批
        } else if (roleKey === 'manager') {
            type = 1 // 直属领导审批
        } else {
            ElMessage.error('您没有审批权限')
            return
        }

        const statusText = status === 1 ? '通过' : '驳回'
        ElMessageBox.confirm(
            `确定要${statusText}该请假申请吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        ).then(async () => {
            try {
                await approveLeaveService({
                    ids: [row.id],
                    status,
                    type
                })
                ElMessage.success(`${statusText}成功`)
                getLeaveList()
            } catch (error) {
                console.error('审批失败:', error)
                ElMessage.error(error.response?.data?.message || '审批失败')
            }
        }).catch(() => {})
    } catch (error) {
        console.error('获取权限信息失败:', error)
        ElMessage.error('获取权限信息失败')
    }
}

// 批量审批处理
const handleBatchApprove = async (status) => {
    if (selectedLeaves.value.length === 0) {
        ElMessage.warning('请选择要审批的请假申请')
        return
    }

    try {
        // 获取用户权限标识
        const res = await getRoleKeyService()
        if (res.code !== 1) {
            ElMessage.error('获取权限信息失败')
            return
        }
        
        const roleKey = res.data
        // 根据权限标识判断审批类型
        let type = 0
        if (roleKey === 'hr') {
            type = 2 // HR审批
        } else if (roleKey === 'manager') {
            type = 1 // 直属领导审批
        } else {
            ElMessage.error('您没有审批权限')
            return
        }

        const statusText = status === 1 ? '通过' : '驳回'
        ElMessageBox.confirm(
            `确定要批量${statusText}选中的${selectedLeaves.value.length}条请假申请吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        ).then(async () => {
            try {
                const leaveIds = selectedLeaves.value.map(item => item.id)
                await approveLeaveService({
                    ids: leaveIds,
                    status,
                    type
                })
                ElMessage.success(`批量${statusText}成功`)
                getLeaveList()
            } catch (error) {
                console.error('批量审批失败:', error)
                ElMessage.error(error.response?.data?.message || '批量审批失败')
            }
        }).catch(() => {})
    } catch (error) {
        console.error('获取权限信息失败:', error)
        ElMessage.error('获取权限信息失败')
    }
}

// 获取请假列表
const getLeaveList = async () => {
    try {
        const res = await getPendingApprovals({
            page: currentPage.value,
            pageSize: pageSize.value,
            empName: searchForm.value.empName,
            leaveType: searchForm.value.leaveType
        })
        if (res.code === 1) {
            leaveList.value = res.data.records
            total.value = res.data.total
        } else {
            ElMessage.error(res.message || '获取请假列表失败')
        }
    } catch (error) {
        console.error('获取请假列表失败:', error)
        ElMessage.error('获取请假列表失败')
    }
}

// 获取请假类型名称
const getLeaveTypeName = (type) => {
    const typeMap = {
        1: '事假',
        2: '病假',
        3: '公假'
    }
    return typeMap[type] || '未知'
}

// 获取状态名称
const getStatusName = (status) => {
    const statusMap = {
        0: '待审批',
        1: '已通过',
        2: '已驳回'
    }
    return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
    const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
    }
    return typeMap[status] || 'info'
}

// 计算请假天数
const calculateDays = (startDate, endDate) => {
    if (!startDate || !endDate) return 0
    const start = new Date(startDate)
    const end = new Date(endDate)
    // 将时间设置为当天的0点，避免时区问题
    start.setHours(0, 0, 0, 0)
    end.setHours(0, 0, 0, 0)
    // 计算天数差值（包含开始和结束日期）
    const days = Math.floor((end - start) / (1000 * 60 * 60 * 24)) + 1
    return days > 0 ? days : 0
}

// 查看详情
const handleView = (row) => {
    currentLeave.value = { 
        ...row,
        days: calculateDays(row.startDate, row.endDate)
    }
    dialogVisible.value = true
}

// 搜索
const handleSearch = () => {
    currentPage.value = 1
    getLeaveList()
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        empName: '',
        leaveType: ''
    }
    handleSearch()
}

// 分页相关方法
const handleSizeChange = (val) => {
    pageSize.value = val
    getLeaveList()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    getLeaveList()
}

onMounted(() => {
    getLeaveList()
})
</script>

<style lang="scss" scoped>
.leave-approve {
    padding: 20px;

    .search-form {
        margin-bottom: 20px;
    }

    .toolbar {
        margin-bottom: 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .batch-operation {
            .el-button {
                margin-right: 8px;
            }
        }
    }

    .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }

    .operation-buttons {
        display: flex;
        align-items: center;
        white-space: nowrap;
        
        .el-button {
            padding: 4px 8px;
            & + .el-button {
                margin-left: 8px;
            }
        }
    }
}
</style> 