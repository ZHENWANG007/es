<template>
    <div class="employee-manage">
        <!-- 左侧部门树 -->
        <div class="dept-tree">
            <div class="dept-title">部门列表</div>
            <el-tree
                ref="deptTreeRef"
                :data="deptList"
                :props="{ label: 'name', children: 'children' }"
                default-expand-all
                highlight-current
                node-key="id"
                :expand-on-click-node="false"
                @node-click="handleDeptClick"
                @node-contextmenu="handleNodeRightClick"
            />
        </div>

        <!-- 右侧内容区 -->
        <div class="content-area">
            <!-- 搜索区域 -->
            <div class="search-area">
                <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                    <el-form-item label="员工姓名">
                        <el-input v-model="searchForm.name" placeholder="请输入员工姓名" clearable style="width: 200px;" />
                    </el-form-item>
                    <el-form-item label="职位名称">
                        <el-select v-model="searchForm.jobTitle" placeholder="请选择职位名称" clearable style="width: 200px;">
                            <el-option
                                v-for="item in jobList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.name"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="权限标识">
                        <el-input v-model="searchForm.roleKey" placeholder="请输入权限标识" clearable style="width: 200px;" />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px;">
                            <el-option label="正常" :value="0" />
                            <el-option label="禁用" :value="1" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="入职时间">
                        <el-date-picker
                            v-model="dateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="YYYY-MM-DD"
                            style="width: 240px;"
                        />
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
            </div>

            <!-- 操作按钮区域 -->
            <div class="operation-area">
                <el-button type="primary" @click="handleAdd" v-permission="['ems:employee:add']">
                    <el-icon><Plus /></el-icon>新增
                </el-button>
                <el-button type="success" @click="handleImport" v-permission="['ems:employee:import']">
                    <el-icon><Upload /></el-icon>导入
                </el-button>
                <el-button type="warning" @click="handleExport" v-permission="['ems:employee:export']">
                    <el-icon><Download /></el-icon>导出
                </el-button>
                <el-button type="info" @click="handleResetPassword" :disabled="!selectedRows.length" v-permission="['ems:employee:edit']">
                    <el-icon><Key /></el-icon>重置密码
                </el-button>
                <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length" v-permission="['ems:employee:delete']">
                    <el-icon><Delete /></el-icon>批量删除
                </el-button>
            </div>

            <!-- 表格区域 -->
            <el-table 
                ref="tableRef"
                v-loading="loading"
                :data="employeeList" 
                border 
                stripe
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="name" label="员工姓名" min-width="120" show-overflow-tooltip />
                <el-table-column prop="jobTitle" label="职位名称" min-width="120" show-overflow-tooltip />
                <el-table-column prop="deptName" label="部门名称" min-width="120" show-overflow-tooltip />
                <el-table-column prop="entryDate" label="入职时间" min-width="130" align="center" />
                <el-table-column prop="roleKey" label="权限标识" min-width="120" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="80" align="center">
                    <template #default="{ row }">
                        <el-switch
                            v-model="row.status"
                            :active-value="0"
                            :inactive-value="1"
                            @change="(val) => handleStatusChange(row.id, val)"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="{ row }">
                        <div class="operation-buttons">
                            <el-button type="primary" link @click="handleEdit(row)" v-permission="['ems:employee:edit']">
                                <el-icon><Edit /></el-icon>修改
                            </el-button>
                            <el-button type="success" link @click="handleRole(row)" v-permission="['ems:employee:role']">
                                <el-icon><UserFilled /></el-icon>角色
                            </el-button>
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
                    <el-form-item label="用户名" prop="username" v-if="!form.id">
                        <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isView" />
                    </el-form-item>
                    <el-form-item label="员工姓名" prop="name">
                        <el-input v-model="form.name" placeholder="请输入员工姓名" :disabled="isView" />
                    </el-form-item>
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="form.gender" :disabled="isView">
                            <el-radio :label="1">男</el-radio>
                            <el-radio :label="2">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="年龄" prop="age">
                        <el-input-number v-model="form.age" :min="18" :max="100" placeholder="请输入年龄" :disabled="isView" />
                    </el-form-item>
                    <el-form-item label="所属部门" prop="deptId">
                        <el-tree-select
                            v-model="form.deptId"
                            :data="deptList"
                            :props="{ label: 'name', value: 'id', children: 'children' }"
                            placeholder="请选择所属部门"
                            :disabled="isView"
                        />
                    </el-form-item>
                    <el-form-item label="职位名称" prop="jobTitle">
                        <el-select v-model="form.jobTitle" placeholder="请选择职位名称" style="width: 100%;" :disabled="isView">
                            <el-option
                                v-for="item in jobList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.name"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="手机号码" prop="phone">
                        <el-input v-model="form.phone" placeholder="请输入手机号码" :disabled="isView" />
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="form.email" placeholder="请输入邮箱" :disabled="isView" />
                    </el-form-item>
                    <el-form-item label="入职日期" prop="entryDate">
                        <el-date-picker
                            v-model="form.entryDate"
                            type="date"
                            placeholder="请选择入职日期"
                            value-format="YYYY-MM-DD"
                            :disabled="isView"
                        />
                    </el-form-item>
                    <el-form-item label="离职日期" prop="leaveDate">
                        <el-date-picker
                            v-model="form.leaveDate"
                            type="date"
                            placeholder="请选择离职日期"
                            value-format="YYYY-MM-DD"
                            :disabled="isView"
                        />
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="form.status" :disabled="isView">
                            <el-radio :label="0">正常</el-radio>
                            <el-radio :label="1">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
                    <el-button type="primary" @click="handleSubmit" :loading="submitting" v-if="!isView">
                        确 定
                    </el-button>
                </template>
            </el-dialog>

            <!-- 导入对话框 -->
            <el-dialog
                title="导入员工"
                v-model="importDialogVisible"
                width="400px"
                :close-on-click-modal="false"
                destroy-on-close
            >
                <el-upload
                    class="upload-demo"
                    drag
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :on-success="handleUploadSuccess"
                    :on-error="handleUploadError"
                    :before-upload="beforeUpload"
                    :show-file-list="true"
                    :limit="1"
                    accept=".xlsx,.xls"
                >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                        将文件拖到此处，或<em>点击上传</em>
                    </div>
                    <template #tip>
                        <div class="el-upload__tip">
                            只能上传xlsx/xls文件，且不超过5MB
                            <el-link 
                                type="primary" 
                                :underline="false"
                                style="margin-left: 10px"
                                @click="downloadTemplate"
                            >
                                下载模板
                            </el-link>
                        </div>
                    </template>
                </el-upload>
            </el-dialog>

            <!-- 部门表单对话框 -->
            <el-dialog
                :title="deptDialogTitle"
                v-model="deptDialogVisible"
                width="500px"
                :close-on-click-modal="false"
            >
                <el-form
                    ref="deptFormRef"
                    :model="deptForm"
                    :rules="deptRules"
                    label-width="100px"
                >
                    <el-form-item label="上级部门">
                        <el-tree-select
                            v-model="deptForm.parentId"
                            :data="deptList"
                            :props="{ label: 'name', value: 'id', children: 'children' }"
                            placeholder="请选择上级部门"
                            check-strictly
                            :disabled="!!deptForm.id"
                        />
                    </el-form-item>
                    <el-form-item label="部门名称" prop="deptName">
                        <el-input v-model="deptForm.deptName" placeholder="请输入部门名称" />
                    </el-form-item>
                    <el-form-item label="显示排序" prop="orderNum">
                        <el-input-number v-model="deptForm.orderNum" :min="0" :max="999" controls-position="right" />
                    </el-form-item>
                    <el-form-item label="部门状态">
                        <el-radio-group v-model="deptForm.status">
                            <el-radio :label="0">正常</el-radio>
                            <el-radio :label="1">停用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="部门描述">
                        <el-input v-model="deptForm.description" type="textarea" placeholder="请输入部门描述" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="deptDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="handleDeptSubmit">确 定</el-button>
                </template>
            </el-dialog>

            <!-- 角色分配对话框 -->
            <el-dialog
                title="分配角色"
                v-model="roleDialogVisible"
                width="500px"
                :close-on-click-modal="false"
                class="role-dialog"
            >
                <el-form ref="roleFormRef" :model="roleForm" label-width="80px">
                    <el-form-item label="员工姓名" class="employee-name">
                        <span>{{ roleForm.name }}</span>
                    </el-form-item>
                    <el-form-item label="角色列表">
                        <div class="role-list">
                            <el-radio-group v-model="roleForm.roleId">
                                <el-radio
                                    v-for="role in roleList"
                                    :key="role.id"
                                    :label="role.id"
                                    :disabled="role.status === '1'"
                                    class="role-item"
                                >
                                    <div class="role-info">
                                        <span class="role-name">{{ role.name }}</span>
                                        <span class="role-key">({{ role.roleKey }})</span>
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </div>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="roleDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="handleRoleSubmit" :loading="roleSubmitting">确 定</el-button>
                </template>
            </el-dialog>

            <!-- 右键菜单 -->
            <div 
                v-show="contextMenuVisible" 
                class="context-menu"
                :style="{ left: contextMenuPosition.x + 'px', top: contextMenuPosition.y + 'px' }"
            >
                <div class="menu-item menu-item-primary" @click="handleAddDept(currentNode)">
                    <el-icon><Plus /></el-icon>新增部门
                </div>
                <div class="menu-item menu-item-primary" @click="handleEditDept(currentNode.data)">
                    <el-icon><Edit /></el-icon>编辑部门
                </div>
                <div class="menu-item menu-item-danger" @click="handleDeleteDept(currentNode.data)">
                    <el-icon><Delete /></el-icon>删除部门
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Upload, Download, UploadFilled, View, Key, UserFilled, InfoFilled } from '@element-plus/icons-vue'
import { useTokenStore } from '@/stores/token'
import { getDeptTreeService, addDeptService, updateDeptService, deleteDeptService, hasEmpService } from '@/api/dept'
import {
    getEmpListService,
    addEmpService,
    updateEmpService,
    deleteEmpService,
    batchDeleteEmpService,
    importEmpService,
    exportEmpService,
    updateEmpStatusService,
    resetPasswordService
} from '@/api/emp'
import { getJobNameListService } from '@/api/job'
import { getRoleListService, updateEmpRoleService, getAllRoleListService } from '@/api/role'

// 部门树数据
const deptList = ref([])
// 添加当前选中的部门ID
const currentDeptId = ref(null)

// 获取部门树数据
const getDeptTree = async () => {
    try {
        const res = await getDeptTreeService()
        if (res.code === 1) {
            deptList.value = res.data
        } else {
            ElMessage.error(res.message || '获取部门列表失败')
        }
    } catch (error) {
        console.error('获取部门列表失败:', error)
    }
}

// 点击部门树节点
const handleDeptClick = (data, node, event) => {
    // 如果右键菜单是显示状态，先关闭它
    if (contextMenuVisible.value) {
        closeContextMenu()
        // 阻止事件继续传播
        event.stopPropagation()
        return
    }
    currentDeptId.value = data.id
    searchForm.value.deptId = data.id
    handleSearch()
}

// 日期范围
const dateRange = ref([])

// 监听日期范围变化
watch(dateRange, (val) => {
    searchForm.value.beginDate = val ? val[0] : undefined
    searchForm.value.endDate = val ? val[1] : undefined
}, { deep: true })

// 搜索表单
const searchForm = ref({
    name: '',         // 员工姓名
    jobTitle: '',     // 职位名称
    roleKey: '',      // 权限标识
    status: '',       // 状态
    deptId: null,     // 部门ID
    beginDate: '',    // 入职开始时间
    endDate: '',      // 入职结束时间
    pageNum: 1,       // 当前页码
    pageSize: 10      // 每页条数
})

// 表格数据
const loading = ref(false)
const employeeList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => {
    if (isView.value) return '查看用户'
    return form.value.id ? '编辑用户' : '新增用户'
})
const importDialogVisible = ref(false)
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
    id: '',
    username: '',     // 用户名
    name: '',         // 员工姓名
    gender: 1,        // 性别
    age: '',         // 年龄
    deptId: null,     // 部门ID
    jobTitle: '',     // 职位名称
    phone: '',        // 手机号码
    email: '',        // 邮箱
    entryDate: '',    // 入职时间
    leaveDate: '',    // 离职时间
    status: 0         // 状态，默认正常
})

// 表单校验规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入员工姓名', trigger: 'blur' }
    ],
    gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ],
    age: [
        { required: true, message: '请输入年龄', trigger: 'blur' },
        { type: 'number', message: '年龄必须为数字', trigger: 'blur' }
    ],
    deptId: [
        { required: true, message: '请选择所属部门', trigger: 'change' }
    ],
    jobTitle: [
        { required: true, message: '请选择职位名称', trigger: 'change' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    entryDate: [
        { required: true, message: '请选择入职日期', trigger: 'change' }
    ]
}

// 导入相关
const uploadUrl = '/api/emp/import'
const uploadHeaders = computed(() => {
    const tokenStore = useTokenStore()
    return {
        Authorization: tokenStore.token
    }
})

// 上传前校验
const beforeUpload = (file) => {
    const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                    file.type === 'application/vnd.ms-excel'
    const isLt5M = file.size / 1024 / 1024 < 5

    if (!isExcel) {
        ElMessage.error('只能上传 Excel 文件!')
        return false
    }
    if (!isLt5M) {
        ElMessage.error('文件大小不能超过 5MB!')
        return false
    }
    return true
}

// 上传成功
const handleUploadSuccess = (response) => {
    if (response.code === 1) {
        ElMessage.success(response.message || '导入成功')
        importDialogVisible.value = false
        getEmployeeList()
    } else {
        ElMessage.error(response.message || '导入失败')
    }
}

// 上传失败
const handleUploadError = (error) => {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请稍后重试')
}

// 下载模板
const downloadTemplate = async () => {
    try {
        const response = await fetch('/api/emp/template', {
            method: 'GET',
            headers: {
                'Authorization': useTokenStore().token
            }
        })
        if (!response.ok) throw new Error('下载失败')
        
        const blob = await response.blob()
        if (blob.size === 0) {
            throw new Error('下载的文件为空')
        }

        // 从响应头中获取文件名
        const contentDisposition = response.headers.get('content-disposition')
        let filename = '员工导入模板.xlsx'
        if (contentDisposition) {
            const filenameMatch = contentDisposition.match(/filename\*=utf-8''(.+)/)
            if (filenameMatch) {
                filename = decodeURIComponent(filenameMatch[1])
            }
        }

        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        ElMessage.success('下载成功')
    } catch (error) {
        console.error('下载模板失败:', error)
        ElMessage.error('下载模板失败，请重试')
    }
}

// 获取员工列表
const getEmployeeList = async () => {
    try {
        loading.value = true
        const params = {
            ...searchForm.value,
            pageNum: page.value,
            pageSize: pageSize.value
        }
        const res = await getEmpListService(params)
        employeeList.value = res.data.records
        total.value = res.data.total
    } finally {
        loading.value = false
    }
}

// 搜索
const handleSearch = () => {
    page.value = 1
    getEmployeeList()
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        name: '',         // 员工姓名
        jobTitle: '',     // 职位名称
        roleKey: '',      // 权限标识
        status: '',       // 状态
        deptId: currentDeptId.value,  // 保持当前选中的部门ID
        beginDate: '',    // 入职开始时间
        endDate: '',      // 入职结束时间
        pageNum: 1,       // 当前页码
        pageSize: 10      // 每页条数
    }
    dateRange.value = []  // 清空日期范围
    handleSearch()        // 执行搜索
}

// 选择行变化
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}

// 批量删除
const handleBatchDelete = () => {
    if (!selectedRows.value.length) {
        ElMessage.warning('请选择要删除的用户')
        return
    }
    ElMessageBox.confirm(
        `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedRows.value.map(row => row.id)
            const res = await batchDeleteEmpService(ids)
            if (res.code === 1) {
                ElMessage.success('删除成功')
                getEmployeeList()
            } else {
                ElMessage.error(res.message || '删除失败')
            }
        } catch (error) {
            console.error('删除失败:', error)
            ElMessage.error('删除失败')
        }
    }).catch(() => {})
}

// 导入按钮点击
const handleImport = () => {
    importDialogVisible.value = true
}

const handleExport = () => {
    // TODO: 调用导出API
    ElMessage.success('导出成功')
}

// 状态变更
const handleStatusChange = async (id, status) => {
    try {
        await updateEmpService({
            id,
            status
        })
        ElMessage.success(`${status === 0 ? '启用' : '禁用'}成功`)
    } catch {
        // 如果失败，回滚状态
        getEmployeeList()
    }
}

// 是否为查看模式
const isView = ref(false)

// 添加用户
const handleAdd = () => {
    isView.value = false
    form.value = {
        id: '',
        username: '',
        name: '',
        gender: 1,
        age: '',
        deptId: currentDeptId.value || null,
        jobTitle: '',
        phone: '',
        email: '',
        entryDate: '',
        leaveDate: '',
        status: 0
    }
    dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
    isView.value = false
    form.value = { ...row }
    // 根据部门名称查找对应的部门ID
    const findDeptId = (deptList, deptName) => {
        for (const dept of deptList) {
            if (dept.name === deptName) {
                return dept.id
            }
            if (dept.children) {
                const id = findDeptId(dept.children, deptName)
                if (id) return id
            }
        }
        return null
    }
    // 设置部门ID
    form.value.deptId = findDeptId(deptList.value, row.deptName)
    dialogVisible.value = true
}

// 查看用户
const handleView = (row) => {
    isView.value = true
    form.value = { ...row }
    // 根据部门名称查找对应的部门ID
    const findDeptId = (deptList, deptName) => {
        for (const dept of deptList) {
            if (dept.name === deptName) {
                return dept.id
            }
            if (dept.children) {
                const id = findDeptId(dept.children, deptName)
                if (id) return id
            }
        }
        return null
    }
    // 设置部门ID
    form.value.deptId = findDeptId(deptList.value, row.deptName)
    dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除用户 ${row.username} 吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        // TODO: 调用删除API
        ElMessage.success('删除成功')
        getEmployeeList()
    }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    if (isView.value) {
        dialogVisible.value = false
        return
    }
    
    try {
        await formRef.value.validate()
        submitting.value = true
        
        const submitService = form.value.id ? updateEmpService : addEmpService
        const res = await submitService(form.value)
        
        if (res.code === 1) {
            ElMessage.success(form.value.id ? '更新成功' : '新增成功')
            dialogVisible.value = false
            getEmployeeList()
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

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val
    getEmployeeList()
}

// 页码改变
const handleCurrentChange = (val) => {
    page.value = val
    getEmployeeList()
}

// 右键菜单相关
const contextMenuRef = ref(null)
const contextMenuVisible = ref(false)
const contextMenuPosition = ref({ x: 0, y: 0 })
const currentNode = ref(null)

// 部门对话框相关
const deptDialogVisible = ref(false)
const deptDialogTitle = computed(() => deptForm.value.id ? '编辑部门' : '新增部门')
const deptFormRef = ref(null)
const deptForm = ref({
    id: '',
    parentId: null,
    deptName: '',
    orderNum: 0,
    status: 0,
    description: ''
})

// 部门表单校验规则
const deptRules = {
    deptName: [
        { required: true, message: '请输入部门名称', trigger: 'blur' }
    ],
    orderNum: [
        { required: true, message: '请输入显示排序', trigger: 'blur' }
    ]
}

// 右键菜单事件处理
const handleNodeRightClick = (event, data, node) => {
    // 先关闭已经打开的右键菜单（如果有）
    closeContextMenu()
    
    event.preventDefault()
    event.stopPropagation()
    
    // 设置新的右键菜单位置和数据
    contextMenuVisible.value = true
    contextMenuPosition.value = {
        x: event.clientX,
        y: event.clientY
    }
    currentNode.value = { data, node }
}

// 关闭右键菜单
const closeContextMenu = () => {
    contextMenuVisible.value = false
    currentNode.value = null
}

// 添加部门
const handleAddDept = (parentNode = null) => {
    deptForm.value = {
        id: '',
        parentId: parentNode ? parentNode.data.id : null,
        deptName: '',
        orderNum: 0,
        status: 0,
        description: ''
    }
    deptDialogVisible.value = true
    closeContextMenu()
}

// 编辑部门
const handleEditDept = (data) => {
    deptForm.value = {
        id: data.id,
        parentId: data.parentId,
        deptName: data.name,
        orderNum: data.orderNum || 0,
        status: data.status || 0,
        description: data.description
    }
    deptDialogVisible.value = true
    closeContextMenu()
}

// 删除部门
const handleDeleteDept = (data) => {
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
            const hasEmpRes = await hasEmpService(data.id)
            if (hasEmpRes.code === 1 && hasEmpRes.data) {
                ElMessage.warning('该部门下存在员工或子部门，无法删除')
                return
            }
            
            const res = await deleteDeptService(data.id)
            if (res.code === 1) {
                ElMessage.success('删除成功')
                // 重新加载部门树
                getDeptTree()
            } else {
                ElMessage.error(res.message || '删除失败')
            }
        } catch (error) {
            console.error('删除部门失败:', error)
            ElMessage.error('删除失败')
        }
    }).catch(() => {})
    closeContextMenu()
}

// 提交部门表单
const handleDeptSubmit = async () => {
    if (!deptFormRef.value) return
    try {
        await deptFormRef.value.validate()
        const submitService = deptForm.value.id ? updateDeptService : addDeptService
        const res = await submitService({
            id: deptForm.value.id,
            parentId: deptForm.value.parentId,
            name: deptForm.value.deptName,
            orderNum: deptForm.value.orderNum,
            status: deptForm.value.status,
            description: deptForm.value.description
        })
        
        if (res.code === 1) {
            ElMessage.success(deptForm.value.id ? '更新成功' : '新增成功')
            deptDialogVisible.value = false
            // 重新加载部门树
            getDeptTree()
        } else {
            ElMessage.error(res.message || (deptForm.value.id ? '更新失败' : '新增失败'))
        }
    } catch (error) {
        console.error('保存部门失败:', error)
        ElMessage.error(deptForm.value.id ? '更新失败' : '新增失败')
    }
}

// 重置密码
const handleResetPassword = () => {
    if (!selectedRows.value.length) {
        ElMessage.warning('请选择要重置密码的用户')
        return
    }
    ElMessageBox.confirm(
        `确定要重置选中的 ${selectedRows.value.length} 个用户的密码吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedRows.value.map(row => row.id)
            const res = await resetPasswordService(ids)
            if (res.code === 1) {
                ElMessage.success('密码重置成功')
                // 清空选中状态
                selectedRows.value = []
                // 清空表格的选中状态
                if (tableRef.value) {
                    tableRef.value.clearSelection()
                }
            } else {
                ElMessage.error(res.message || '密码重置失败')
            }
        } catch (error) {
            console.error('密码重置失败:', error)
            ElMessage.error('密码重置失败')
        }
    }).catch(() => {})
}

// 全局点击事件处理函数
const handleGlobalClick = (event) => {
    const contextMenu = document.querySelector('.context-menu')
    const treeNode = event.target.closest('.el-tree-node')
    
    // 如果点击的是树节点，且右键菜单正在显示，则关闭右键菜单
    if (treeNode && contextMenuVisible.value) {
        closeContextMenu()
        return
    }
    
    // 如果点击的不是右键菜单内部元素，则关闭右键菜单
    if (!contextMenu || !contextMenu.contains(event.target)) {
        closeContextMenu()
    }
}

// 添加职位列表数据
const jobList = ref([])

// 获取职位列表
const getJobList = async () => {
    try {
        const res = await getJobNameListService()
        if (res.code === 1) {
            jobList.value = res.data
        } else {
            ElMessage.error(res.message || '获取职位列表失败')
        }
    } catch (error) {
        console.error('获取职位列表失败:', error)
        ElMessage.error('获取职位列表失败')
    }
}

// 添加表格ref
const tableRef = ref(null)

// 角色相关
const roleDialogVisible = ref(false)
const roleSubmitting = ref(false)
const roleForm = ref({
    id: '',
    name: '',
    roleId: null
})
const roleList = ref([])

// 获取角色列表
const getRoleList = async () => {
    try {
        const res = await getAllRoleListService()
        if (res.code === 1) {
            roleList.value = res.data
        } else {
            ElMessage.error(res.message || '获取角色列表失败')
        }
    } catch (error) {
        console.error('获取角色列表失败:', error)
        ElMessage.error('获取角色列表失败')
    }
}

// 打开角色分配对话框
const handleRole = (row) => {
    roleForm.value = {
        id: row.id,
        name: row.name,
        roleId: null // 先置空，等待下面根据roleKey匹配
    }
    roleDialogVisible.value = true
    // 确保角色列表已加载
    if (roleList.value.length === 0) {
        getRoleList().then(() => {
            // 根据roleKey查找对应的角色ID
            const role = roleList.value.find(role => role.roleKey === row.roleKey)
            if (role) {
                roleForm.value.roleId = role.id
            }
        })
    } else {
        // 如果角色列表已加载，直接查找匹配
        const role = roleList.value.find(role => role.roleKey === row.roleKey)
        if (role) {
            roleForm.value.roleId = role.id
        }
    }
}

// 提交角色分配
const handleRoleSubmit = async () => {
    if (!roleForm.value.roleId) {
        ElMessage.warning('请选择要分配的角色')
        return
    }
    
    try {
        roleSubmitting.value = true
        await updateEmpRoleService({
            empId: roleForm.value.id,
            roleId: roleForm.value.roleId
        })
        ElMessage.success('角色分配成功')
        roleDialogVisible.value = false
        getEmployeeList() // 刷新列表
    } finally {
        roleSubmitting.value = false
    }
}

// 初始化
onMounted(() => {
    getDeptTree()
    getEmployeeList()
    getJobList()
    getRoleList()
    // 添加全局点击事件监听器来关闭右键菜单
    document.addEventListener('click', handleGlobalClick, true) // 使用捕获阶段
})

// 组件卸载时移除事件监听器
onUnmounted(() => {
    document.removeEventListener('click', handleGlobalClick, true) // 使用捕获阶段
})
</script>

<style lang="scss" scoped>
.employee-manage {
    padding: 10px;
    background-color: #f5f7fa;
    display: flex;
    gap: 15px;
    min-height: calc(100vh - 84px);
    position: relative;

    .dept-tree {
        background-color: #fff;
        padding: 15px;
        border-radius: 4px;
        width: 200px;
        position: sticky;
        top: 10px;
        height: calc(100vh - 104px);
        overflow-y: overlay;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

        // 自定义滚动条样式
        &::-webkit-scrollbar {
            width: 6px;
            height: 6px;
            background: transparent;
        }

        &::-webkit-scrollbar-thumb {
            border-radius: 3px;
            background-color: transparent;
        }

        &:hover {
            &::-webkit-scrollbar-thumb {
                background-color: rgba(144, 147, 153, 0.3);
            }
        }

        .dept-title {
            font-size: 16px;
            font-weight: bold;
            color: #303133;
            margin-bottom: 12px;
            padding-bottom: 8px;
            border-bottom: 1px solid #ebeef5;
            position: sticky;
            top: 15px;
            background-color: #fff;
            z-index: 1;
        }

        :deep(.el-tree) {
            background: none;
            height: calc(100% - 42px);
            overflow-y: overlay;
            
            // 为树组件也添加相同的滚动条样式
            &::-webkit-scrollbar {
                width: 6px;
                height: 6px;
                background: transparent;
            }

            &::-webkit-scrollbar-thumb {
                border-radius: 3px;
                background-color: transparent;
            }

            &:hover {
                &::-webkit-scrollbar-thumb {
                    background-color: rgba(144, 147, 153, 0.3);
                }
            }
            
            .el-tree-node__content {
                height: 32px;
                
                &:hover {
                    background-color: #f5f7fa;
                }
            }
            
            .el-tree-node.is-current > .el-tree-node__content {
                background-color: #ecf5ff;
                color: #409EFF;
            }
        }
    }

    .content-area {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-width: 0;
        
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

            // 为表格添加相同的滚动条样式
            :deep(.el-table__body-wrapper) {
                overflow-y: overlay !important;

                &::-webkit-scrollbar {
                    width: 6px;
                    height: 6px;
                    background: transparent;
                }

                &::-webkit-scrollbar-thumb {
                    border-radius: 3px;
                    background-color: transparent;
                }

                &:hover {
                    &::-webkit-scrollbar-thumb {
                        background-color: rgba(144, 147, 153, 0.3);
                    }
                }
            }
        }

        .pagination-area {
            background-color: #fff;
            padding: 12px 15px;
            border-radius: 4px;
            display: flex;
            justify-content: flex-end;
        }
    }

    .upload-demo {
        text-align: center;
        
        .el-upload__text {
            margin-top: 10px;
            color: #606266;
            
            em {
                color: #409EFF;
                font-style: normal;
            }
        }
    }

    .operation-buttons {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 4px;
        
        .el-button {
            margin-left: 0;
            padding: 4px 6px;
            
            .el-icon {
                margin-right: 2px;
            }
        }
    }

    .role-dialog {
        :deep(.el-dialog__body) {
            padding: 20px 30px;
        }

        .employee-name {
            span {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
            }
        }

        .role-list {
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;

            .el-radio-group {
                display: flex;
                flex-direction: column;
                gap: 12px;

                .role-item {
                    margin-right: 0;
                    padding: 12px 16px;
                    border-radius: 4px;
                    transition: all 0.3s;
                    width: 100%;
                    
                    :deep(.el-radio__input) {
                        margin-top: 2px;
                    }

                    :deep(.el-radio__label) {
                        padding-left: 12px;
                        width: calc(100% - 24px);
                    }
                    
                    &:hover {
                        background-color: #ecf5ff;
                    }

                    &.is-checked {
                        background-color: #ecf5ff;
                    }

                    &.is-disabled {
                        background-color: #f5f7fa;
                        cursor: not-allowed;

                        &:hover {
                            background-color: #f5f7fa;
                        }

                        .role-name, .role-key {
                            color: #c0c4cc;
                        }
                    }

                    .role-info {
                        display: flex;
                        align-items: center;
                        gap: 8px;

                        .role-name {
                            font-size: 14px;
                            color: #303133;
                            min-width: 80px;
                        }

                        .role-key {
                            font-size: 13px;
                            color: #909399;
                            font-family: Consolas, monospace;
                        }
                    }
                }
            }
        }
    }
}

.context-menu {
    position: fixed;
    z-index: 9999;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 5px 0;

    .menu-item {
        padding: 8px 16px;
        cursor: pointer;
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #606266;
        
        .el-icon {
            margin-right: 8px;
            font-size: 16px;
        }

        &.menu-item-primary {
            color: #409EFF;
            
            &:hover {
                background-color: #ecf5ff;
            }
            
            .el-icon {
                color: #409EFF;
            }
        }

        &.menu-item-danger {
            color: #F56C6C;
            
            &:hover {
                background-color: #fef0f0;
            }
            
            .el-icon {
                color: #F56C6C;
            }
        }
    }
}

// 添加点击空白处关闭右键菜单的处理
:deep(body) {
    &::after {
        content: '';
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 9998;
        display: none;
    }

    &.show-context-menu {
        &::after {
            display: block;
        }
    }
}
</style> 