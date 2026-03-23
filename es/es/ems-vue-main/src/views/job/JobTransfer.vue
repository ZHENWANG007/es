<template>
    <div class="job-transfer">
        <!-- 搜索区域 -->
        <div class="search-area">
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item label="员工姓名">
                    <el-input id="empName" v-model="searchForm.empName" placeholder="请输入员工姓名" clearable style="width: 180px;" />
                </el-form-item>
                <el-form-item label="原岗位">
                    <el-select id="oldJobId" v-model="searchForm.oldJobId" placeholder="请选择原岗位" clearable style="width: 180px;">
                        <el-option v-for="job in jobList" :key="job.id" :label="job.name" :value="job.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="新岗位">
                    <el-select id="newJobId" v-model="searchForm.newJobId" placeholder="请选择新岗位" clearable style="width: 180px;">
                        <el-option v-for="job in jobList" :key="job.id" :label="job.name" :value="job.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="审批状态">
                    <el-select id="status" v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 180px;">
                        <el-option label="待审批" value="0" />
                        <el-option label="已通过" value="1" />
                        <el-option label="已拒绝" value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon>查询
                    </el-button>
                    <el-button @click="handleReset">
                        <el-icon><Refresh /></el-icon>重置
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 操作按钮区域 -->
        <div class="operation-area">
            <el-button type="primary" @click="handleAdd" v-permission="['ems:jobTransfer:add']">
                <el-icon><Plus /></el-icon>新增调岗
            </el-button>
            <el-button type="warning" @click="handleExport" v-permission="['ems:jobTransfer:export']">
                <el-icon><Download /></el-icon>导出
            </el-button>
        </div>

        <!-- 表格区域 -->
        <el-table
            :data="transferList"
            border
            stripe
            v-loading="loading"
        >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="empName" label="员工姓名" min-width="100" show-overflow-tooltip align="center" />
            <el-table-column prop="oldJobName" label="原岗位" min-width="120" show-overflow-tooltip align="center" />
            <el-table-column prop="newJobName" label="新岗位" min-width="120" show-overflow-tooltip align="center" />
            <el-table-column prop="transferDate" label="调岗日期" width="100" show-overflow-tooltip align="center" />
            <el-table-column prop="reason" label="调岗原因" min-width="200" show-overflow-tooltip align="center" />
            <el-table-column prop="status" label="审批状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                        {{ getStatusText(row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="approvedBy" label="审批人" min-width="100" show-overflow-tooltip align="center" />
            <el-table-column prop="createTime" label="申请时间" width="160" show-overflow-tooltip align="center" />
            <el-table-column label="操作" width="220" fixed="right" align="center">
                <template #default="{ row }">
                    <div class="operation-buttons">
                        <el-button
                            v-if="row.status === '0'"
                            type="success"
                            link
                            @click="handleApprove(row)"
                            v-permission="['ems:jobTransfer:approve']"
                        >
                            <el-icon><Check /></el-icon>审批
                        </el-button>
                        <el-button
                            type="primary"
                            link
                            @click="handleView(row)"
                            v-permission="['ems:jobTransfer:view']"
                        >
                            <el-icon><View /></el-icon>查看
                        </el-button>
                        <el-button
                            v-if="row.status === '0'"
                            type="danger"
                            link
                            @click="handleDelete(row)"
                            v-permission="['ems:jobTransfer:delete']"
                        >
                            <el-icon><Delete /></el-icon>删除
                        </el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页区域 -->
        <div class="pagination-area">
            <el-pagination
                v-model:current-page="page"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 新增/查看对话框 -->
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
                <el-form-item label="员工" prop="empId">
                    <el-select
                        id="form-empId"
                        v-model="form.empId"
                        placeholder="请选择员工"
                        style="width: 100%;"
                        :disabled="!!form.id || isView"
                        @change="handleEmpChange"
                    >
                        <el-option v-for="emp in empList" :key="emp.id" :label="emp.name" :value="emp.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="当前岗位">
                    <el-input id="form-oldJobName" v-model="form.oldJobName" disabled />
                </el-form-item>
                <el-form-item label="调往岗位" prop="newJobId">
                    <el-select
                        id="form-newJobId"
                        v-model="form.newJobId"
                        placeholder="请选择调往岗位"
                        style="width: 100%;"
                        :disabled="isView"
                    >
                        <el-option
                            v-for="job in jobList"
                            :key="job.id"
                            :label="job.name"
                            :value="job.id"
                            :disabled="job.id === form.oldJobId || job.currentCount >= job.maxCapacity"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="调岗日期" prop="transferDate">
                    <el-date-picker
                        id="form-transferDate"
                        v-model="form.transferDate"
                        type="date"
                        placeholder="请选择调岗日期"
                        value-format="YYYY-MM-DD"
                        style="width: 100%;"
                        :disabled="isView"
                    />
                </el-form-item>
                <el-form-item label="调岗原因" prop="reason">
                    <el-input
                        id="form-reason"
                        v-model="form.reason"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入调岗原因"
                        :disabled="isView"
                    />
                </el-form-item>
                <template v-if="isView && form.status !== '0'">
                    <el-form-item label="审批结果">
                        <el-tag :type="getStatusType(form.status)">
                            {{ getStatusText(form.status) }}
                        </el-tag>
                    </el-form-item>
                    <el-form-item label="审批意见" v-if="form.approveRemark">
                        <el-input
                            v-model="form.approveRemark"
                            type="textarea"
                            :rows="2"
                            disabled
                        />
                    </el-form-item>
                    <el-form-item label="审批人">
                        <el-input v-model="form.approvedBy" disabled />
                    </el-form-item>
                    <el-form-item label="审批时间">
                        <el-input v-model="form.approveTime" disabled />
                    </el-form-item>
                </template>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting" v-if="!isView">
                    确 定
                </el-button>
            </template>
        </el-dialog>

        <!-- 审批对话框 -->
        <el-dialog
            title="调岗审批"
            v-model="approveDialogVisible"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form
                ref="approveFormRef"
                :model="approveForm"
                :rules="approveRules"
                label-width="100px"
            >
                <el-form-item label="审批结果" prop="status">
                    <el-radio-group id="approve-status" v-model="approveForm.status">
                        <el-radio label="1">通过</el-radio>
                        <el-radio label="2">拒绝</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="审批意见" prop="approveRemark">
                    <el-input
                        id="approve-remark"
                        v-model="approveForm.approveRemark"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入审批意见"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="approveDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleApproveSubmit" :loading="approveSubmitting">
                    确 定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download, View, Check } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = ref({
    empName: '',
    oldJobId: '',
    newJobId: '',
    status: ''
})

// 表格数据
const loading = ref(false)
const transferList = ref([
    {
        id: 1,
        empName: '张三',
        oldJobName: '前端工程师',
        newJobName: '前端组长',
        transferDate: '2025-01-15',
        reason: '工作表现优秀，晋升为组长',
        status: '1',
        approvedBy: '李总',
        createTime: '2025-01-10 14:30:00'
    }
])
const page = ref(1)
const pageSize = ref(10)
const total = ref(1)

// 岗位列表
const jobList = ref([
    { id: 1, name: '前端工程师', currentCount: 3, maxCapacity: 5 },
    { id: 2, name: '前端组长', currentCount: 1, maxCapacity: 2 }
])

// 员工列表
const empList = ref([
    { id: 1, name: '张三', jobId: 1, jobName: '前端工程师' }
])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => isView.value ? '查看调岗申请' : (form.value.id ? '编辑调岗申请' : '新增调岗申请'))
const isView = ref(false)
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    id: '',
    empId: '',
    oldJobId: '',
    oldJobName: '',
    newJobId: '',
    transferDate: '',
    reason: '',
    status: '0',
    approveRemark: '',
    approvedBy: '',
    approveTime: ''
})

// ���单校验规则
const rules = {
    empId: [{ required: true, message: '请选择员工', trigger: 'change' }],
    newJobId: [{ required: true, message: '请选择调往岗位', trigger: 'change' }],
    transferDate: [{ required: true, message: '请选择调岗日期', trigger: 'change' }],
    reason: [{ required: true, message: '请输入调岗原因', trigger: 'blur' }]
}

// 审批对话框
const approveDialogVisible = ref(false)
const approveSubmitting = ref(false)
const approveFormRef = ref(null)
const approveForm = ref({
    id: '',
    status: '1',
    approveRemark: ''
})

// 审批表单校验规则
const approveRules = {
    status: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
    approveRemark: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

// 获取状态类型
const getStatusType = (status) => {
    const typeMap = {
        '0': 'info',
        '1': 'success',
        '2': 'danger'
    }
    return typeMap[status]
}

// 获取状态文本
const getStatusText = (status) => {
    const textMap = {
        '0': '待审批',
        '1': '已通过',
        '2': '已拒绝'
    }
    return textMap[status]
}

// 搜索
const handleSearch = () => {
    page.value = 1
    loadData()
}

// 重置搜索
const handleReset = () => {
    searchForm.value = {
        empName: '',
        oldJobId: '',
        newJobId: '',
        status: ''
    }
    handleSearch()
}

// 加载数据
const loadData = () => {
    loading.value = true
    // TODO: 调用后端API
    setTimeout(() => {
        loading.value = false
    }, 500)
}

// 员工选择变化
const handleEmpChange = (empId) => {
    const emp = empList.value.find(item => item.id === empId)
    if (emp) {
        form.value.oldJobId = emp.jobId
        form.value.oldJobName = emp.jobName
    }
}

// 新增
const handleAdd = () => {
    isView.value = false
    form.value = {
        id: '',
        empId: '',
        oldJobId: '',
        oldJobName: '',
        newJobId: '',
        transferDate: '',
        reason: '',
        status: '0'
    }
    dialogVisible.value = true
}

// 查看
const handleView = (row) => {
    isView.value = true
    form.value = { ...row }
    dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除该调岗申请吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        // TODO: 调用删除API
        ElMessage.success('删除成功')
        loadData()
    }).catch(() => {})
}

// 导出
const handleExport = () => {
    // TODO: 实现导出功能
    ElMessage.success('导出成功')
}

// 审批
const handleApprove = (row) => {
    approveForm.value = {
        id: row.id,
        status: '1',
        approveRemark: ''
    }
    approveDialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate()

    try {
        submitting.value = true
        // TODO: 调用新增或更新API
        ElMessage.success(form.value.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadData()
    } finally {
        submitting.value = false
    }
}

// 提交审批
const handleApproveSubmit = async () => {
    if (!approveFormRef.value) return
    await approveFormRef.value.validate()

    try {
        approveSubmitting.value = true
        // TODO: 调用审批API
        ElMessage.success('审批成功')
        approveDialogVisible.value = false
        loadData()
    } finally {
        approveSubmitting.value = false
    }
}

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val
    loadData()
}

// 页码改变
const handleCurrentChange = (val) => {
    page.value = val
    loadData()
}

// 初始化
loadData()
</script>

<style lang="scss" scoped>
.job-transfer {
    padding: 20px;
    background-color: #f5f7fa;

    .search-area {
        background-color: #fff;
        padding: 20px;
        border-radius: 4px;
        margin-bottom: 20px;
    }

    .operation-area {
        margin-bottom: 20px;
        display: flex;
        gap: 10px;
    }

    .el-table {
        margin-bottom: 20px;
    }

    .pagination-area {
        background-color: #fff;
        padding: 15px 20px;
        border-radius: 4px;
        display: flex;
        justify-content: flex-end;
    }
}
</style>
