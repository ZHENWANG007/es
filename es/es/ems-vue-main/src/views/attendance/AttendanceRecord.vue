<template>
    <div class="attendance-record">
        <!-- 搜索区域 -->
        <div class="search-area">
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item label="员工姓名">
                    <el-input v-model="searchForm.empName" placeholder="请输入员工姓名" clearable style="width: 180px;" />
                </el-form-item>
                <el-form-item label="部门名称">
                    <el-tree-select
                        v-model="searchForm.deptId"
                        :data="deptTreeData"
                        :props="{ label: 'name', value: 'id', children: 'children' }"
                        placeholder="请选择部门"
                        clearable
                        style="width: 180px;"
                        @change="handleDeptChange"
                    />
                </el-form-item>
                <el-form-item label="考勤状态">
                    <el-select v-model="searchForm.status" placeholder="请选择考勤状态" clearable style="width: 150px;">
                        <el-option label="正常" :value="0" />
                        <el-option label="迟到" :value="1" />
                        <el-option label="早退" :value="2" />
                        <el-option label="旷工" :value="3" />
                        <el-option label="请假" :value="4" />
                    </el-select>
                </el-form-item>
                <el-form-item label="考勤日期">
                    <el-date-picker
                        v-model="searchForm.attendanceDate"
                        type="date"
                        placeholder="请选择考勤日期"
                        value-format="YYYY-MM-DD"
                        style="width: 180px;"
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
            <el-button type="primary" @click="handleAdd" v-permission="['ems:attendance:add']">
                <el-icon><Plus /></el-icon>新增记录
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length" v-permission="['ems:attendance:delete']">
                <el-icon><Delete /></el-icon>批量删除
            </el-button>
        </div>

        <!-- 表格区域 -->
        <el-table 
            ref="tableRef"
            v-loading="loading"
            :data="attendanceList" 
            border 
            stripe
            @selection-change="handleSelectionChange"
        >
            <template #empty>
                <div class="empty-block">
                    {{ loading ? '加载中...' : error ? '加载失败' : '暂无数据' }}
                    <el-button v-if="error" type="primary" link @click="getAttendanceList">
                        <el-icon><Refresh /></el-icon>重试
                    </el-button>
                </div>
            </template>
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="empName" label="员工姓名" min-width="100" show-overflow-tooltip />
            <el-table-column prop="deptName" label="所属部门" min-width="120" show-overflow-tooltip />
            <el-table-column prop="attendanceDate" label="考勤日期" min-width="120" align="center" />
            <el-table-column label="上班打卡" min-width="160" align="center">
                <template #default="{ row }">
                    {{ row.checkInTime ? formatDateTime(row.checkInTime) : '未打卡' }}
                </template>
            </el-table-column>
            <el-table-column label="下班打卡" min-width="160" align="center">
                <template #default="{ row }">
                    {{ row.checkOutTime ? formatDateTime(row.checkOutTime) : '未打卡' }}
                </template>
            </el-table-column>
            <el-table-column prop="isOvertime" label="是否加班" width="80" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.overtimeFlag === 1 ? 'success' : 'info'">
                        {{ row.overtimeFlag === 1 ? '是' : '否' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="考勤状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row)">
                        {{ getStatusText(row) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="180" fixed="right" align="center">
                <template #default="{ row }">
                    <div class="operation-buttons">
                        <el-button 
                            type="primary" 
                            link 
                            @click="handleEdit(row)" 
                            v-permission="['ems:attendance:edit']"
                        >
                            <el-icon><Edit /></el-icon>修改
                        </el-button>
                        <el-button 
                            v-if="row.status !== 0"
                            type="warning" 
                            link 
                            @click="handleAbnormal(row)" 
                            v-permission="['ems:attendance:handle']"
                        >
                            <el-icon><Warning /></el-icon>处理异常
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

        <!-- 新增/编辑对话框 -->
        <el-dialog
            :title="dialogTitle"
            v-model="dialogVisible"
            width="600px"
            :close-on-click-modal="false"
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="100px"
            >
                <el-form-item label="考勤日期" prop="attendanceDate">
                    <el-date-picker
                        v-model="form.attendanceDate"
                        type="date"
                        placeholder="请选择考勤日期"
                        value-format="YYYY-MM-DD"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="上班打卡" prop="checkInTime">
                    <el-time-picker
                        v-model="form.checkInTime"
                        placeholder="请选择上班打卡时间"
                        value-format="HH:mm:ss"
                        format="HH:mm:ss"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="下班打卡" prop="checkOutTime">
                    <el-time-picker
                        v-model="form.checkOutTime"
                        placeholder="请选择下班打卡时间"
                        value-format="HH:mm:ss"
                        format="HH:mm:ss"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="是否迟到">
                    <el-switch v-model="form.lateFlag" :active-value="1" :inactive-value="0" />
                    <el-input-number 
                        v-if="form.lateFlag === 1"
                        v-model="form.lateMinutes"
                        :min="0"
                        :max="480"
                        placeholder="迟到分钟数"
                        style="margin-left: 10px; width: 120px;"
                    />
                </el-form-item>
                <el-form-item label="是否早退">
                    <el-switch v-model="form.earlyFlag" :active-value="1" :inactive-value="0" />
                    <el-input-number 
                        v-if="form.earlyFlag === 1"
                        v-model="form.earlyMinutes"
                        :min="0"
                        :max="480"
                        placeholder="早退分钟数"
                        style="margin-left: 10px; width: 120px;"
                    />
                </el-form-item>
                <el-form-item label="是否加班">
                    <el-switch v-model="form.overtimeFlag" :active-value="1" :inactive-value="0" />
                    <el-input-number 
                        v-if="form.overtimeFlag === 1"
                        v-model="form.overtimeMinutes"
                        :min="0"
                        :max="720"
                        placeholder="加班分钟数"
                        style="margin-left: 10px; width: 120px;"
                    />
                </el-form-item>
                <el-form-item label="是否请假">
                    <el-switch v-model="form.leaveFlag" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="是否旷工">
                    <el-switch v-model="form.absentFlag" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="form.remark"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入备注信息"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting">
                    确 定
                </el-button>
            </template>
        </el-dialog>

        <!-- 异常处理对话框 -->
        <el-dialog
            title="处理异常考勤"
            v-model="abnormalDialogVisible"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form
                ref="abnormalFormRef"
                :model="abnormalForm"
                :rules="abnormalRules"
                label-width="100px"
            >
                <el-form-item label="处理结果" prop="result">
                    <el-radio-group v-model="abnormalForm.result">
                        <el-radio :label="1">确认异常</el-radio>
                        <el-radio :label="2">更正为正常</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="处理说明" prop="remark">
                    <el-input
                        v-model="abnormalForm.remark"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入处理说明"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="abnormalDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAbnormalSubmit" :loading="abnormalSubmitting">
                    确 定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Warning } from '@element-plus/icons-vue'
import { useUserInfoStore } from '@/stores/userInfo'
import {
    getAttendanceListService,
    addAttendanceService,
    updateAttendanceService,
    deleteAttendanceService,
    batchDeleteAttendanceService,
    handleAbnormalAttendanceService
} from '@/api/attendance'
import { getDeptTreeService } from '@/api/dept'

// 搜索表单
const searchForm = ref({
    empName: '',           // 员工姓名
    deptId: '',           // 部门ID
    status: '',           // 考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假）
    attendanceDate: '',   // 考勤日期
    pageNum: 1,           // 当前页码
    pageSize: 10          // 每页条数
})

// 日期范围
const dateRange = ref([])

// 监听日期范围变化
watch(dateRange, (val) => {
    searchForm.value.beginDate = val ? val[0] : undefined
    searchForm.value.endDate = val ? val[1] : undefined
}, { deep: true })

// 表格数据
const loading = ref(false)
const attendanceList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])
const tableRef = ref(null)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑考勤' : '新增考勤')
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    id: '',
    empId: '',
    empName: '',
    deptName: '',
    attendanceDate: '',    // 考勤日期
    checkInTime: '',       // 上班打卡
    checkOutTime: '',      // 下班打卡
    lateFlag: 0,          // 是否迟到
    lateMinutes: 0,       // 迟到分钟数
    earlyFlag: 0,         // 是否早退
    earlyMinutes: 0,      // 早退分钟数
    overtimeFlag: 0,      // 是否加班
    overtimeMinutes: 0,   // 加班分钟数
    leaveFlag: 0,         // 是否请假
    leaveRequestId: null, // 关联请假表ID
    absentFlag: 0,        // 是否旷工
    remark: ''            // 备注
})

// 表单校验规则
const rules = {
    attendanceDate: [
        { required: true, message: '请选择考勤日期', trigger: 'change' }
    ],
    checkInTime: [
        { required: true, message: '请选择上班打卡时间', trigger: 'change' }
    ],
    checkOutTime: [
        { required: true, message: '请选择下班打卡时间', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择考勤状态', trigger: 'change' }
    ]
}

// 异常处理对话框相关
const abnormalDialogVisible = ref(false)
const abnormalSubmitting = ref(false)
const abnormalFormRef = ref(null)
const abnormalForm = ref({
    id: '',           // 考勤记录ID
    result: 1,        // 处理结果：1-确认异常，2-更正为正常
    remark: ''        // 处理说明
})

// 异常处理表单校验规则
const abnormalRules = {
    result: [
        { required: true, message: '请选择处理结果', trigger: 'change' }
    ],
    remark: [
        { required: true, message: '请输入处理说明', trigger: 'blur' }
    ]
}

// 获取状态标签类型
const getStatusType = (row) => {
    if (row.absentFlag === 1) {
        return 'danger'
    } else if (row.leaveFlag === 1) {
        return 'info'
    } else if (row.lateFlag === 1) {
        return 'warning'
    } else if (row.earlyFlag === 1) {
        return 'warning'
    } else {
        return 'success'
    }
}

// 获取状态文本
const getStatusText = (row) => {
    if (row.absentFlag === 1) {
        return '旷工'
    } else if (row.leaveFlag === 1) {
        return '请假'
    } else if (row.lateFlag === 1) {
        return '迟到'
    } else if (row.earlyFlag === 1) {
        return '早退'
    } else {
        return '正常'
    }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return ''
    const dateTime = new Date(dateTimeStr)
    return dateTime.toLocaleTimeString('zh-CN', { hour12: false })
}

// 部门树数据
const deptTreeData = ref([])
const deptOptions = ref([])

// 将树形数据转换为扁平结构
const flattenDeptTree = (data) => {
    const result = []
    const flatten = (items, parentName = '') => {
        items.forEach(item => {
            const fullName = parentName ? `${parentName}/${item.name}` : item.name
            result.push({
                id: item.id,
                name: fullName
            })
            if (item.children && item.children.length > 0) {
                flatten(item.children, fullName)
            }
        })
    }
    flatten(data)
    return result
}

// 获取部门树数据
const getDeptTree = async () => {
    try {
        const res = await getDeptTreeService()
        if (res.code === 1) {
            deptTreeData.value = res.data
            deptOptions.value = flattenDeptTree(res.data)
        } else {
            ElMessage.error(res.message || '获取部门列表失败')
        }
    } catch (error) {
        console.error('获取部门列表失败:', error)
        ElMessage.error('获取部门列表失败')
    }
}

// 处理部门选择变化
const handleDeptChange = (value) => {
    searchForm.value.deptId = value
}

// 搜索
const handleSearch = () => {
    page.value = 1
    getAttendanceList()
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        empName: '',
        deptId: '',
        status: '',
        attendanceDate: '',
        pageNum: 1,
        pageSize: 10
    }
    handleSearch()
}

// 获取考勤列表
const error = ref(false)

const getAttendanceList = async () => {
    try {
        loading.value = true
        error.value = false
        const params = {
            ...searchForm.value,
            status: searchForm.value.status !== '' ? Number(searchForm.value.status) : undefined
        }
        const res = await getAttendanceListService(params)
        if (res.code === 1) {
            attendanceList.value = res.data.records
            total.value = res.data.total
        } else {
            error.value = true
            ElMessage.error(res.message || '获取考勤列表失败')
        }
    } catch (error) {
        console.error('获取考勤列表失败:', error)
        error.value = true
        ElMessage.error('获取考勤列表失败')
    } finally {
        loading.value = false
    }
}

// 选择行变化
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}

// 新增考勤
const handleAdd = () => {
    form.value = {
        id: '',
        attendanceDate: '',
        checkInTime: '',
        checkOutTime: '',
        isOvertime: false,
        status: '正常'
    }
    dialogVisible.value = true
}

// 编辑考勤
const handleEdit = (row) => {
    form.value = { ...row }
    dialogVisible.value = true
}

// 处理异常考勤
const handleAbnormal = (row) => {
    abnormalForm.value = {
        id: row.id,
        result: 1,
        remark: ''
    }
    abnormalDialogVisible.value = true
}

// 批量删除
const handleBatchDelete = () => {
    if (!selectedRows.value.length) {
        ElMessage.warning('请选择要删除的记录')
        return
    }
    ElMessageBox.confirm(
        `确定要删除选中的 ${selectedRows.value.length} 条记录吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedRows.value.map(row => row.id)
            const res = await batchDeleteAttendanceService(ids)
            if (res.code === 1) {
                ElMessage.success('删除成功')
                getAttendanceList()
            } else {
                ElMessage.error(res.message || '删除失败')
            }
        } catch (error) {
            console.error('删除失败:', error)
            ElMessage.error('删除失败')
        }
    }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        submitting.value = true
        
        const submitService = form.value.id ? updateAttendanceService : addAttendanceService
        const res = await submitService(form.value)
        if (res.code === 1) {
            ElMessage.success(form.value.id ? '更新成功' : '新增成功')
            dialogVisible.value = false
            getAttendanceList()
        } else {
            ElMessage.error(res.message || (form.value.id ? '更新失败' : '新增失败'))
        }
    } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(form.value.id ? '更新失败' : '新增失败')
    } finally {
        submitting.value = false
    }
}

// 提交异常处理
const handleAbnormalSubmit = async () => {
    if (!abnormalFormRef.value) return
    
    try {
        await abnormalFormRef.value.validate()
        abnormalSubmitting.value = true
        
        const res = await handleAbnormalAttendanceService(abnormalForm.value)
        if (res.code === 1) {
            ElMessage.success('处理成功')
            abnormalDialogVisible.value = false
            getAttendanceList()
        } else {
            ElMessage.error(res.message || '处理失败')
        }
    } catch (error) {
        console.error('处理失败:', error)
        ElMessage.error('处理失败')
    } finally {
        abnormalSubmitting.value = false
    }
}

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val
    getAttendanceList()
}

// 页码改变
const handleCurrentChange = (val) => {
    page.value = val
    getAttendanceList()
}

// 初始化
onMounted(() => {
    getDeptTree()
    getAttendanceList()
})
</script>

<style lang="scss" scoped>
.attendance-record {
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
            flex-wrap: wrap;
            gap: 10px;
            
            .el-form-item {
                margin-bottom: 0;
                margin-right: 0;
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