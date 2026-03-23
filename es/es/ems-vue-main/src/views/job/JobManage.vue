<template>
    <div class="job-manage">
        <!-- 搜索区域 -->
        <div class="search-area">
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item label="岗位名称">
                    <el-input v-model="searchForm.name" placeholder="请输入岗位名称" clearable />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="岗位状态" clearable style="width: 180px;">
                        <el-option label="正常" :value="0" />
                        <el-option label="停用" :value="1" />
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
            <el-button type="primary" @click="handleAdd" v-permission="['ems:job:add']">
                <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button type="danger" @click="handleDelete()" :disabled="!selectedRows.length" v-permission="['ems:job:delete']">
                <el-icon><Delete /></el-icon>批量删除
            </el-button>
        </div>

        <!-- 表格区域 -->
        <el-table
            :data="tableData"
            border
            stripe
            @selection-change="handleSelectionChange"
            @row-click="handleRowClick"
            v-loading="loading"
        >
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="name" label="岗位名称" min-width="120" show-overflow-tooltip align="center" />
            <el-table-column prop="description" label="岗位描述" min-width="180" show-overflow-tooltip />
            <el-table-column label="薪资范围" min-width="150" align="center">
                <template #default="{ row }">
                    {{ row.minSalary }} - {{ row.maxSalary }}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
                <template #default="{ row }">
                    <div style="display: flex; justify-content: center; align-items: center; height: 100%;">
                        <el-switch
                            v-model="row.status"
                            :active-value="0"
                            :inactive-value="1"
                            @change="(val) => handleStatusChange(row.id, val)"
                        />
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
                <template #default="{ row }">
                    <div class="operation-buttons">
                        <el-button type="primary" link @click="handleEdit(row)" v-permission="['ems:job:edit']">
                            <el-icon><Edit /></el-icon>编辑
                        </el-button>
                        <el-button type="danger" link @click="handleDelete(row)" v-permission="['ems:job:delete']">
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
                <el-form-item label="岗位名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入岗位名称" />
                </el-form-item>
                <el-form-item label="岗位描述" prop="description">
                    <el-input 
                        v-model="form.description" 
                        type="textarea" 
                        :rows="3"
                        placeholder="请输入岗位描述" 
                    />
                </el-form-item>
                <el-form-item label="最低薪资" prop="minSalary">
                    <el-input-number 
                        v-model="form.minSalary" 
                        :min="0" 
                        :max="999999" 
                        :precision="2"
                        :step="1000"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="最高薪资" prop="maxSalary">
                    <el-input-number 
                        v-model="form.maxSalary" 
                        :min="0" 
                        :max="999999" 
                        :precision="2"
                        :step="1000"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="0">正常</el-radio>
                        <el-radio :label="1">停用</el-radio>
                    </el-radio-group>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import { getJobListService, addJobService, updateJobService, deleteJobService, hasEmpService } from '@/api/job'
import { formatDateTime } from '@/utils/format'

// 搜索表单
const searchForm = ref({
    name: '',
    status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 分页
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载数据
const loadData = async () => {
    try {
        loading.value = true
        const params = {
            ...searchForm.value,
            pageNum: page.value,
            pageSize: pageSize.value
        }
        const res = await getJobListService(params)
        tableData.value = res.data.records
        total.value = res.data.total
    } finally {
        loading.value = false
    }
}

// 搜索
const handleSearch = () => {
    page.value = 1
    loadData()
}

// 重置搜索
const handleReset = () => {
    searchForm.value = {
        name: '',
        status: ''
    }
    handleSearch()
}

// 表单
const formRef = ref(null)
const form = ref({
    id: '',
    name: '',
    description: '',
    minSalary: '',
    maxSalary: '',
    status: 0
})

// 表单校验规则
const rules = {
    name: [
        { required: true, message: '请输入岗位名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    minSalary: [
        { required: true, message: '请输入最低薪资', trigger: 'blur' }
    ],
    maxSalary: [
        { required: true, message: '请输入最高薪资', trigger: 'blur' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ]
}

// 选择行
const selectedRows = ref([])
const selectedRow = ref(null)

// 选择行变化
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}

// 点击行
const handleRowClick = (row) => {
    selectedRow.value = row
}

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑岗位' : '新增岗位')
const submitting = ref(false)

// 新增
const handleAdd = () => {
    form.value = {
        id: '',
        name: '',
        description: '',
        minSalary: '',
        maxSalary: '',
        status: 0
    }
    dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
    form.value = { ...row }
    dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
    const rows = row ? [row] : selectedRows.value
    if (!rows.length) {
        ElMessage.warning('请选择要删除的岗位')
        return
    }

    try {
        // 先检查是否有员工
        const ids = rows.map(item => item.id)
        const hasEmpRes = await hasEmpService(ids)
        
        if (hasEmpRes.data) {
            ElMessage.warning('选中的岗位中存在关联员工，无法删除')
            return
        }

        await ElMessageBox.confirm(
            `确定要删除选中的 ${rows.length} 个岗位吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await deleteJobService(ids)
        ElMessage.success('删除成功')
        // 删除成功后，清空选中状态
        selectedRows.value = []
        loadData()
    } catch (error) {
        // 只处理非取消操作的错误
        if (error !== 'cancel') {
            // 错误已由响应拦截器处理，这里不需要额外处理
            return
        }
    }
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    
    try {
        await formRef.value.validate()
        submitting.value = true
        
        const submitData = {
            id: form.value.id,
            name: form.value.name,
            description: form.value.description,
            minSalary: form.value.minSalary,
            maxSalary: form.value.maxSalary,
            status: form.value.status
        }
        
        const service = form.value.id ? updateJobService : addJobService
        await service(submitData)
        ElMessage.success(form.value.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadData()
    } finally {
        submitting.value = false
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

// 状态切换
const handleStatusChange = async (id, status) => {
    try {
        await updateJobService({
            id,
            status
        })
        ElMessage.success(status === 0 ? '启用成功' : '禁用成功')
    } catch {
        // 如果失败，回滚状态（错误消息已由响应拦截器处理）
        loadData()
    }
}

// 初始化
onMounted(() => {
    loadData()
})
</script>

<style lang="scss" scoped>
.job-manage {
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