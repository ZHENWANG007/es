<template>
    <div class="menu-manage">
        <!-- 工具栏 -->
        <div class="toolbar">
            <el-button type="primary" @click="handleAdd(null)">
                <el-icon><Plus /></el-icon>新增
            </el-button>
        </div>

        <!-- 菜单表格 -->
        <el-table
            v-loading="loading"
            :data="tableData"
            row-key="id"
            border
            :default-expand-all="false"
            :tree-props="{ children: 'children' }"
        >
            <el-table-column prop="menuName" label="菜单名称" width="200" show-overflow-tooltip />
            <el-table-column prop="icon" label="图标" width="80" align="center">
                <template #default="{ row }">
                    <el-icon v-if="row.type === 'M' && row.icon">
                        <component :is="ElementPlusIcons[row.icon]" />
                    </el-icon>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column prop="orderNum" label="排序" width="60" align="center" />
            <el-table-column prop="perms" label="权限标识" min-width="180" show-overflow-tooltip />
            <el-table-column prop="path" label="路由地址" width="150" show-overflow-tooltip>
                <template #default="{ row }">
                    {{ row.type === 'M' ? row.path : '-' }}
                </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.type === 'M' ? 'primary' : 'warning'" size="small">
                        {{ row.type === 'M' ? '菜单' : '按钮' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="visible" label="显示状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.visible === 0 ? 'success' : 'info'" size="small">
                        {{ row.visible === 0 ? '显示' : '隐藏' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                        {{ row.status === 0 ? '正常' : '停用' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" fixed="right">
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

        <!-- 新/编辑对话框 -->
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
                <el-form-item label="上级菜单">
                    <el-tree-select
                        v-model="form.parentId"
                        :data="menuOptions"
                        :props="{ label: 'menuName', value: 'id', children: 'children' }"
                        placeholder="请选择上级菜单"
                        check-strictly
                        clearable
                    />
                </el-form-item>
                <el-form-item label="菜单类型" prop="type">
                    <el-radio-group v-model="form.type" @change="handleTypeChange">
                        <el-radio label="M">菜单</el-radio>
                        <el-radio label="B">按钮</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="菜单名称" prop="menuName">
                    <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
                </el-form-item>
                <el-form-item v-if="form.type === 'M'" label="排序" prop="orderNum">
                    <el-input-number v-model="form.orderNum" :min="0" :max="999" controls-position="right" />
                </el-form-item>
                <template v-if="form.type === 'M'">
                    <el-form-item label="路由地址" prop="path">
                        <el-input v-model="form.path" placeholder="请输入路由地址" />
                    </el-form-item>
                    <el-form-item label="组件路径" prop="component">
                        <el-input v-model="form.component" placeholder="请输入组件路径" />
                    </el-form-item>
                    <el-form-item label="菜单图标" prop="icon">
                        <el-select v-model="form.icon" placeholder="请选择图标" clearable filterable>
                            <el-option
                                v-for="(icon, name) in ElementPlusIcons"
                                :key="name"
                                :label="name"
                                :value="name"
                            >
                                <el-icon>
                                    <component :is="icon" />
                                </el-icon>
                                <span style="margin-left: 8px">{{ name }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </template>
                <el-form-item label="权限标识" prop="perms">
                    <el-input v-model="form.perms" placeholder="请输入权限标识" />
                </el-form-item>
                <el-form-item label="显示状态" prop="visible">
                    <el-radio-group v-model="form.visible">
                        <el-radio :label="0">显示</el-radio>
                        <el-radio :label="1">隐藏</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="菜单状态" prop="status">
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
import * as ElementPlusIcons from '@element-plus/icons-vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
    getMenuListService,
    addMenuService,
    updateMenuService,
    deleteMenuService
} from '@/api/menu'

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 获取菜单列表
const getMenuList = async () => {
    try {
        loading.value = true
        const res = await getMenuListService()
        tableData.value = res.data
    } finally {
        loading.value = false
    }
}

// 菜单选项
const menuOptions = computed(() => {
    const options = [{ id: 0, menuName: '主目录', children: [] }]
    if (tableData.value.length) {
        options[0].children = tableData.value
    }
    return options
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑菜单' : '新增菜单')
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    id: '',
    parentId: 0,
    menuName: '',
    path: '',
    component: '',
    perms: '',
    icon: '',
    orderNum: 0,
    type: 'M',
    visible: 0,
    status: 0
})

// 表单校验规则
const rules = {
    menuName: [
        { required: true, message: '请输入菜单名称', trigger: 'blur' }
    ],
    type: [
        { required: true, message: '请选择菜单类型', trigger: 'change' }
    ],
    orderNum: [
        { required: true, message: '请输入排序号', trigger: 'blur' }
    ],
    path: [
        { required: true, message: '请输入路由地址', trigger: 'blur', when: (form) => form.type === 'M' }
    ]
}

// 监听菜单类型变化
const handleTypeChange = () => {
    if (form.value.type === 'B') {
        form.value.path = ''
        form.value.component = ''
        form.value.icon = ''
        form.value.orderNum = 0
    }
}

// 新增菜单
const handleAdd = (row) => {
    form.value = {
        id: '',
        parentId: row ? row.id : 0,
        menuName: '',
        path: '',
        component: '',
        perms: '',
        icon: '',
        orderNum: 0,
        type: 'M',
        visible: 0,
        status: 0
    }
    dialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row) => {
    form.value = {
        id: row.id,
        parentId: row.parentId,
        menuName: row.menuName,
        path: row.path,
        component: row.component,
        perms: row.perms,
        icon: row.icon,
        orderNum: row.type === 'M' ? (row.orderNum || 0) : undefined,
        type: row.type,
        visible: row.visible,
        status: row.status
    }
    dialogVisible.value = true
}

// 删除菜单
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除菜单"${row.menuName}"吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        await deleteMenuService(row.id)
        ElMessage.success('删除成功')
        getMenuList()
    })
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate()
    
    try {
        submitting.value = true
        // 创建一个新的表单数据对象
        const submitData = { ...form.value }
        
        // 如果是按钮类型，删除orderNum字段
        if (submitData.type === 'B') {
            delete submitData.orderNum
        }
        
        if (submitData.id) {
            await updateMenuService(submitData)
            ElMessage.success('更新成功')
        } else {
            await addMenuService(submitData)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getMenuList()
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    getMenuList()
})
</script>

<style scoped>
.menu-manage {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 84px);
}

.toolbar {
    margin-bottom: 20px;
}

:deep(.el-table) {
    margin-bottom: 20px;
    border-radius: 4px;
}

:deep(.el-button--link) {
    padding: 4px 8px;
    .el-icon {
        margin-right: 4px;
    }
}

:deep(.el-button + .el-button) {
    margin-left: 8px;
}

:deep(.el-tree-select) {
    width: 100%;
}

:deep(.el-tag) {
    min-width: 60px;
}
</style> 