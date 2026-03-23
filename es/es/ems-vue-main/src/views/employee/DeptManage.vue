<template>
    <div class="dept-manage">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <el-form :inline="true" :model="searchForm">
                <el-form-item label="部门名称">
                    <el-input v-model="searchForm.name" placeholder="请输入部门名称" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon>查询
                    </el-button>
                    <el-button @click="resetSearch">
                        <el-icon><Refresh /></el-icon>重置
                    </el-button>
                </el-form-item>
            </el-form>
            <el-button type="primary" @click="handleAdd(null)">
                <el-icon><Plus /></el-icon>新增部门
            </el-button>
        </div>

        <!-- 部门表格 -->
        <el-table
            v-loading="loading"
            :data="tableData"
            row-key="id"
            border
            :tree-props="{ children: 'children' }"
            style="width: 100%"
            :expand-row-keys="expandKeys"
            default-expand-all
        >
            <el-table-column prop="name" label="部门名称" min-width="140" />
            <el-table-column prop="orderNum" label="排序" width="80" align="center" />
            <el-table-column prop="description" label="部门描述" min-width="180" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="{ row }">
                    <el-switch
                        v-model="row.status"
                        :active-value="0"
                        :inactive-value="1"
                        @change="(val) => handleStatusChange(row.id, val)"
                    />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center">
                <template #default="{ row }">
                    <el-button type="primary" link @click="handleEdit(row)">
                        <el-icon><Edit /></el-icon>编辑
                    </el-button>
                    <el-button type="danger" link @click="handleDelete(row)">
                        <el-icon><Delete /></el-icon>删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

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
                <el-form-item label="上级部门">
                    <el-tree-select
                        v-model="form.parentId"
                        :data="tableData"
                        :props="{ label: 'name', value: 'id', children: 'children' }"
                        placeholder="请选择上级部门"
                        check-strictly
                        :disabled="!!form.id"
                    />
                </el-form-item>
                <el-form-item label="部门名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入部门名称" />
                </el-form-item>
                <el-form-item label="显示排序" prop="orderNum">
                    <el-input-number v-model="form.orderNum" :min="0" :max="999" controls-position="right" />
                </el-form-item>
                <el-form-item label="部门状态">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="0">正常</el-radio>
                        <el-radio :label="1">停用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="部门描述" prop="description">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入部门描述" />
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDeptListService, addDeptService, updateDeptService, deleteDeptService, hasEmpService } from '@/api/dept'
import { formatDateTime } from '@/utils/format'

// 搜索表单
const searchForm = ref({
    name: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const originalData = ref([]) // 保存原始数据

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑部门' : '新增部门')
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    id: '',
    parentId: null,
    name: '',
    orderNum: 0,
    status: 0,
    description: ''
})

// 表单校验规则
const rules = {
    name: [
        { required: true, message: '请输入部门名称', trigger: 'blur' }
    ],
    orderNum: [
        { required: true, message: '请输入显示排序', trigger: 'blur' }
    ]
}

// 展开的行keys
const expandKeys = ref([])

// 获取部门树列表
const getDeptList = async () => {
    try {
        loading.value = true
        const res = await getDeptListService()
        if (res.code === 1) {
            originalData.value = res.data
            tableData.value = res.data
        } else {
            ElMessage.error(res.message || '获取部门列表失败')
        }
    } catch (error) {
        console.error('获取部门列表失败:', error)
        ElMessage.error('获取部门列表失败')
    } finally {
        loading.value = false
    }
}

// 递归搜索部门
const filterDeptTree = (data, keyword) => {
    const result = []
    for (const item of data) {
        if (item.name.toLowerCase().includes(keyword.toLowerCase())) {
            // 如果当前节点匹配，直接添加整个节点及其子节点
            result.push({ ...item })
        } else if (item.children && item.children.length) {
            // 如果当前节点不匹配，递归搜索子节点
            const filteredChildren = filterDeptTree(item.children, keyword)
            if (filteredChildren.length) {
                result.push({
                    ...item,
                    children: filteredChildren
                })
            }
        }
    }
    return result
}

// 搜索
const handleSearch = () => {
    if (!searchForm.value.name.trim()) {
        tableData.value = originalData.value
        return
    }
    tableData.value = filterDeptTree(originalData.value, searchForm.value.name.trim())
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        name: ''
    }
    tableData.value = originalData.value
}

// 新增部门
const handleAdd = (row) => {
    form.value = {
        id: '',
        parentId: row ? row.id : null,
        name: '',
        orderNum: 0,
        status: 0,
        description: ''
    }
    dialogVisible.value = true
}

// 编辑部门
const handleEdit = (row) => {
    form.value = {
        id: row.id,
        parentId: row.parentId,
        name: row.name,
        orderNum: row.orderNum,
        status: row.status,
        description: row.description
    }
    dialogVisible.value = true
}

// 删除部门
const handleDelete = (row) => {
    ElMessageBox.confirm(
        '是否确认删除该部门？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            // 先检查部门是否有员工或子部门
            const hasEmpRes = await hasEmpService(row.id)
            if (hasEmpRes.code === 1 && hasEmpRes.data) {
                ElMessage.warning('该部门下存在员工或子部门，无法删除')
                return
            }
            
            const res = await deleteDeptService(row.id)
            if (res.code === 1) {
                ElMessage.success('删除成功')
                getDeptList()
            } else {
                ElMessage.error(res.message || '删除失败')
            }
        } catch (error) {
            console.error('删除部门失败:', error)
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
        
        const submitService = form.value.id ? updateDeptService : addDeptService
        const res = await submitService(form.value)
        
        if (res.code === 1) {
            ElMessage.success(form.value.id ? '更新成功' : '新增成功')
            dialogVisible.value = false
            getDeptList()
        } else {
            ElMessage.error(res.message || (form.value.id ? '更新失败' : '新增失败'))
        }
    } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error(form.value.id ? '更新失败' : '新增失败')
    } finally {
        submitting.value = false
    }
}

// 添加状态切换处理函数
const handleStatusChange = async (id, status) => {
    try {
        const res = await updateDeptService({
            id,
            status
        })
        if (res.code === 1) {
            ElMessage.success(status === 0 ? '启用成功' : '禁用成功')
            getDeptList()
        } else {
            ElMessage.error(res.message || '操作失败')
            // 如果失败，回滚状态
            getDeptList()
        }
    } catch (error) {
        console.error('更新状态失败:', error)
        ElMessage.error('操作失败')
        // 如果失败，回滚状态
        getDeptList()
    }
}

// 初始化
onMounted(() => {
    getDeptList()
})
</script>

<style lang="scss" scoped>
.dept-manage {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 84px);

    .toolbar {
        background-color: #fff;
        padding: 20px;
        border-radius: 4px;
        margin-bottom: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    :deep(.el-table) {
        margin-bottom: 20px;
        background-color: #fff;
        border-radius: 4px;
    }

    :deep(.el-button--link) {
        padding: 4px 8px;
        .el-icon {
            margin-right: 4px;
        }
    }
}
</style> 