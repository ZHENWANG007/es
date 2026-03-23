<template>
    <div class="leave-record">
        <!-- 搜索区域 -->
        <div class="search-area">
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item label="员工姓名" v-if="showNameSearch">
                    <el-input v-model="searchForm.empName" placeholder="请输入员工姓名" clearable style="width: 180px;" />
                </el-form-item>
                <el-form-item label="请假类型">
                    <el-select v-model="searchForm.leaveType" placeholder="请选择请假类型" clearable style="width: 150px;">
                        <el-option :label="'事假'" :value="1" />
                        <el-option :label="'病假'" :value="2" />
                        <el-option :label="'公假'" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="审批状态">
                    <el-select v-model="searchForm.overallStatus" placeholder="请选择审批状态" clearable style="width: 150px;">
                        <el-option :label="'待审批'" :value="0" />
                        <el-option :label="'已批准'" :value="1" />
                        <el-option :label="'已驳回'" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="申请时间">
                    <el-date-picker
                        v-model="dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                        style="width: 220px;"
                    />
                </el-form-item>
                <el-form-item>
                    <div class="search-buttons">
                        <el-button type="primary" @click="handleSearch">
                            <el-icon><Search /></el-icon>查询
                        </el-button>
                        <el-button @click="resetSearch">
                            <el-icon><Refresh /></el-icon>重置
                        </el-button>
                    </div>
                </el-form-item>
            </el-form>
        </div>

        <!-- 操作按钮区域 -->
        <div class="operation-area">
            <el-button type="primary" @click="handleAdd" v-permission="['ems:leave:add']">
                <el-icon><Plus /></el-icon>请假申请
            </el-button>
        </div>

        <!-- 表格区域 -->
        <el-table 
            ref="tableRef"
            v-loading="loading"
            :data="leaveList" 
            border 
            stripe
        >
            <template #empty>
                <div class="empty-block">
                    {{ loading ? '加载中...' : error ? '加载失败' : '暂无数据' }}
                    <el-button v-if="error" type="primary" link @click="getLeaveList">
                        <el-icon><Refresh /></el-icon>重试
                    </el-button>
                </div>
            </template>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="empName" label="员工姓名" min-width="100" show-overflow-tooltip />
            <el-table-column prop="leaveType" label="请假类型" min-width="100" align="center">
                <template #default="{ row }">
                    {{ row.leaveType === 1 ? '事假' : row.leaveType === 2 ? '病假' : '公假' }}
                </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始日期" min-width="120" align="center" />
            <el-table-column prop="endDate" label="结束日期" min-width="120" align="center" />
            <el-table-column prop="reason" label="请假原因" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="申请时间" min-width="160" align="center">
                <template #default="{ row }">
                    {{ formatDateTime(row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="leaderStatus" label="直属领导审批" width="140" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.leaderStatus === 0 ? 'warning' : row.leaderStatus === 1 ? 'success' : 'danger'">
                        {{ row.leaderStatus === 0 ? '待审批' : row.leaderStatus === 1 ? '已通过' : '已驳回' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="leaderApprovedByName" label="领导审批人" min-width="100" show-overflow-tooltip />
            <el-table-column prop="leaderApprovedTime" label="领导审批时间" min-width="160" align="center">
                <template #default="{ row }">
                    {{ formatDateTime(row.leaderApprovedTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="hrStatus" label="人事审批" width="140" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.hrStatus === 0 ? 'warning' : row.hrStatus === 1 ? 'success' : 'danger'">
                        {{ row.hrStatus === 0 ? '待审批' : row.hrStatus === 1 ? '已通过' : '已驳回' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="hrApprovedByName" label="人事审批人" min-width="100" show-overflow-tooltip />
            <el-table-column prop="hrApprovedTime" label="人事审批时间" min-width="160" align="center">
                <template #default="{ row }">
                    {{ formatDateTime(row.hrApprovedTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="overallStatus" label="总状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.overallStatus === 0 ? 'warning' : row.overallStatus === 1 ? 'success' : 'danger'">
                        {{ row.overallStatus === 0 ? '待审批' : row.overallStatus === 1 ? '已批准' : '已驳回' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right" align="center">
                <template #default="{ row }">
                    <div class="operation-buttons">
                        <el-button type="info" link @click="handleView(row)">
                            <el-icon><View /></el-icon>查看
                        </el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页区域 -->
        <div class="pagination-area">
            <el-pagination
                v-model:current-page="pageNum"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 查看对话框 -->
        <el-dialog
            title="查看请假详情"
            v-model="viewDialogVisible"
            width="600px"
        >
            <el-descriptions :column="2" border>
                <el-descriptions-item label="员工姓名">{{ viewForm.empName }}</el-descriptions-item>
                <el-descriptions-item label="请假类型">
                    {{ viewForm.leaveType === 1 ? '事假' : viewForm.leaveType === 2 ? '病假' : '公假' }}
                </el-descriptions-item>
                <el-descriptions-item label="开始日期">{{ viewForm.startDate }}</el-descriptions-item>
                <el-descriptions-item label="结束日期">{{ viewForm.endDate }}</el-descriptions-item>
                <el-descriptions-item label="请假天数">{{ viewForm.days }}天</el-descriptions-item>
                <el-descriptions-item label="申请时间">{{ formatDateTime(viewForm.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="请假原因" :span="2">{{ viewForm.reason }}</el-descriptions-item>
                <el-descriptions-item label="审批状态" :span="2">
                    <el-tag :type="viewForm.overallStatus === 0 ? 'warning' : viewForm.overallStatus === 1 ? 'success' : 'danger'">
                        {{ viewForm.overallStatus === 0 ? '待审批' : viewForm.overallStatus === 1 ? '已批准' : '已驳回' }}
                    </el-tag>
                </el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <el-button @click="viewDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>

        <!-- 新增/编辑对话框 -->
        <el-dialog
            :title="dialogTitle"
            v-model="dialogVisible"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="100px"
            >
                <el-form-item label="请假类型" prop="leaveType">
                    <el-select v-model="form.leaveType" placeholder="请选择请假类型" style="width: 100%;">
                        <el-option :label="'事假'" :value="1" />
                        <el-option :label="'病假'" :value="2" />
                        <el-option :label="'公假'" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="请假时间" prop="startDate">
                    <el-date-picker
                        v-model="leaveDateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="请假天数">
                    <span>{{ form.days }}天</span>
                </el-form-item>
                <el-form-item label="请假原因" prop="reason">
                    <el-input
                        v-model="form.reason"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入请假原因"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting">
                    确 定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Plus } from '@element-plus/icons-vue'
import { useUserInfoStore } from '@/stores/userInfo'
import { getLeaveListService, applyLeaveService } from '@/api/leave'
import { formatDateTime } from '@/utils/format'

// 获取用户信息
const userInfoStore = useUserInfoStore()
const userInfo = computed(() => userInfoStore.info)

// 判断用户角色
const isAdmin = computed(() => userInfo.value.roleKey === 'admin')
const isDeptHead = computed(() => userInfo.value.roleKey === 'dept_head')

// 搜索表单
const searchForm = ref({
    empName: '',       // 员工姓名
    leaveType: '',     // 请假类型
    overallStatus: '', // 总审批状态
    startDate: '',     // 开始时间
    endDate: '',       // 结束时间
    pageNum: 1,        // 当前页码
    pageSize: 10       // 每页条数
})

// 日期范围
const dateRange = ref([])

// 监听日期范围变化
watch(dateRange, (val) => {
    searchForm.value.startDate = val ? val[0] : undefined
    searchForm.value.endDate = val ? val[1] : undefined
}, { deep: true })

// 表格数据
const loading = ref(false)
const error = ref(false)
const leaveList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableRef = ref(null)

// 查看对话框相关
const viewDialogVisible = ref(false)
const viewForm = ref({})

// 新增请假对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => '请假申请')
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    leaveType: '',
    startDate: '',
    endDate: '',
    days: 0,
    reason: ''
})

// 请假时间范围
const leaveDateRange = ref([])

// 监听请假时间范围变化
watch(leaveDateRange, (val) => {
    form.value.startDate = val ? val[0] : ''
    form.value.endDate = val ? val[1] : ''
    calculateDays(val)
}, { deep: true })

// 表单校验规则
const rules = {
    leaveType: [
        { required: true, message: '请选择请假类型', trigger: 'change' }
    ],
    startDate: [
        { required: true, message: '请选择开始日期', trigger: 'change' }
    ],
    endDate: [
        { required: true, message: '请选择结束日期', trigger: 'change' }
    ],
    reason: [
        { required: true, message: '请输入请假原因', trigger: 'blur' },
        { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
    ]
}

// 新增请假
const handleAdd = () => {
    form.value = {
        leaveType: '',
        startDate: '',
        endDate: '',
        days: 0,
        reason: ''
    }
    leaveDateRange.value = [] // 清空日期范围
    dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        submitting.value = true
        
        // 构造请假申请数据
        const leaveData = {
            leaveType: form.value.leaveType,
            startDate: form.value.startDate,
            endDate: form.value.endDate,
            reason: form.value.reason
        }
        
        await applyLeaveService(leaveData)
        ElMessage.success('请假申请提交成功')
        dialogVisible.value = false
        getLeaveList()
    } finally {
        submitting.value = false
    }
}

// 计算请假天数
const calculateDays = (dates) => {
    if (!dates || dates.length !== 2) {
        form.value.days = 0
        return
    }
    const start = new Date(dates[0])
    const end = new Date(dates[1])
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1
    form.value.days = days
}

// 搜索
const handleSearch = () => {
    pageNum.value = 1
    getLeaveList()
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        empName: '',
        leaveType: '',
        overallStatus: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 10
    }
    dateRange.value = []
    handleSearch()
}

// 获取请假列表
const getLeaveList = async () => {
    try {
        loading.value = true
        error.value = false
        const params = {
            empName: searchForm.value.empName,
            leaveType: searchForm.value.leaveType,
            overallStatus: searchForm.value.overallStatus,
            startDate: searchForm.value.startDate,
            endDate: searchForm.value.endDate,
            pageNum: pageNum.value,
            pageSize: pageSize.value
        }

        // 根据角色添加不同的查询条件
        if (!isAdmin.value) {
            if (isDeptHead.value) {
                // 部门主管可以看到本部门的请假记录
                params.deptId = userInfo.value.deptId
            } else {
                // 普通员工只能看到自己的请假记录
                params.empId = userInfo.value.id
            }
        }

        const res = await getLeaveListService(params)
        if (res.code === 1) {
            leaveList.value = res.data.records
            total.value = res.data.total
        } else {
            error.value = true
            ElMessage.error(res.message || '获取请假列表失败')
        }
    } catch (error) {
        console.error('获取请假列表失败:', error)
        error.value = true
        ElMessage.error('获取请假列表失败')
    } finally {
        loading.value = false
    }
}

// 选择行变化
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}

// 编辑请假
const handleEdit = () => {}

// 查看请假
const handleView = (row) => {
    viewForm.value = {
        id: row.id,
        empName: row.empName,
        leaveType: row.leaveType,
        startDate: row.startDate,
        endDate: row.endDate,
        days: row.days,
        reason: row.reason,
        overallStatus: row.overallStatus,
        createTime: row.createTime
    }
    viewDialogVisible.value = true
}

// 批量删除
const handleBatchDelete = () => {}

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val
    getLeaveList()
}

// 页码改变
const handleCurrentChange = (val) => {
    pageNum.value = val
    getLeaveList()
}

// 修改搜索表单，根据角色显示不同的搜索条件
const showNameSearch = computed(() => isAdmin.value || isDeptHead.value)

// 初始化
onMounted(() => {
    getLeaveList()
})
</script>

<style lang="scss" scoped>
.leave-record {
    padding: 10px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 84px);

    .search-area {
        background-color: #fff;
        padding: 15px;
        border-radius: 4px;
        margin-bottom: 15px;

        .el-form {
            display: flex;
            flex-wrap: nowrap;
            align-items: center;
            gap: 8px;
            
            .el-form-item {
                margin-bottom: 0;
                margin-right: 0;
                flex-shrink: 0;

                &:last-child {
                    margin-left: auto;
                }
            }

            .search-buttons {
                display: flex;
                gap: 8px;
            }
        }
    }

    .operation-area {
        margin-bottom: 15px;
        display: flex;
        gap: 10px;
    }

    .el-table {
        background-color: #fff;
        border-radius: 4px;
        margin-bottom: 15px;
    }

    .pagination-area {
        background-color: #fff;
        padding: 12px 15px;
        border-radius: 4px;
        display: flex;
        justify-content: flex-end;
    }

    .operation-buttons {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 8px;
        
        .el-button {
            margin-left: 0;
            padding: 4px 8px;
        }
    }

    .empty-block {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #909399;
        padding: 32px 0;
        
        .el-button {
            margin-top: 12px;
        }
    }
}
</style> 