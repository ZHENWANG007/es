<template>
  <div class="role-manage">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" ref="searchFormRef" :inline="true" class="search-form">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
      </el-form-item>
      <el-form-item label="权限标识" prop="roleKey">
        <el-input v-model="searchForm.roleKey" placeholder="请输入权限标识" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="正常" :value="0" />
          <el-option label="停用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格工具栏 -->
    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增角色</el-button>
      <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      @selection-change="handleSelectionChange"
      border
      style="width: 100%"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="name" label="角色名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="roleKey" label="权限标识" min-width="120" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="'0'"
            :inactive-value="'1'"
            @change="(val) => handleStatusChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>编辑
          </el-button>
          <el-button type="primary" link @click="handleAssignMenus(row)">
            <el-icon><Setting /></el-icon>分配权限
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 新增/编辑角色对话框 -->
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
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
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

    <!-- 分配权限对话框 -->
    <el-dialog
      title="分配权限"
      v-model="menuDialogVisible"
      width="400px"
      :close-on-click-modal="false"
      class="menu-dialog"
    >
      <div class="menu-tree-toolbar">
        <el-button type="primary" link @click="handleCheckAll">全选</el-button>
        <el-button type="primary" link @click="handleUncheckAll">全不选</el-button>
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuList"
        :props="{
          label: 'menuName',
          children: 'children'
        }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        check-strictly
        class="menu-tree"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleAssignSubmit" :loading="menuSubmitting">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRoleListService, 
  addRoleService, 
  updateRoleService, 
  deleteRoleService,
  getRoleMenusService,
  assignRoleMenusService
} from '@/api/role'
import { getMenuTreeSelectService } from '@/api/menu'
import { Edit, Delete, Setting } from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils/format'

// 搜索表单
const searchForm = ref({
  roleName: '',    // 角色名称
  roleKey: '',     // 权限标识
  status: '',      // 状态
  pageNum: 1,      // 当前页码
  pageSize: 10     // 每页条数
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])

// 获取角色列表
const getRoleList = async () => {
  try {
    loading.value = true
    const params = {
      name: searchForm.value.roleName,
      roleKey: searchForm.value.roleKey,
      status: searchForm.value.status,
      pageNum: searchForm.value.pageNum,
      pageSize: searchForm.value.pageSize
    }
    const res = await getRoleListService(params)
    if (res.code === 1) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  getRoleList()
}

// 重置
const searchFormRef = ref()
const handleReset = () => {
  searchFormRef.value?.resetFields()
  handleSearch()
}

// 表格多选
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val
  getRoleList()
}

const handleCurrentChange = (val) => {
  searchForm.value.pageNum = val
  getRoleList()
}

// 表单对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑角色' : '新增角色')
const submitting = ref(false)

// 表单对象
const formRef = ref(null)
const form = ref({
  id: '',
  name: '',
  roleKey: '',
  description: '',
  status: '0'
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限标识', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 新增角色
const handleAdd = () => {
  form.value = {
    id: '',
    name: '',
    roleKey: '',
    description: '',
    status: '0'
  }
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除角色
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除角色"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await deleteRoleService([row.id])
    ElMessage.success('删除成功')
    getRoleList()
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedIds.value.length) {
    ElMessage.warning('请选择要删除的角色')
    return
  }
  const names = tableData.value
    .filter(item => selectedIds.value.includes(item.id))
    .map(item => item.name)
    .join('、')
  
  ElMessageBox.confirm(
    `确定要删除角色"${names}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await deleteRoleService(selectedIds.value)
    ElMessage.success('删除成功')
    getRoleList()
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    submitting.value = true
    if (form.value.id) {
      await updateRoleService(form.value)
      ElMessage.success('更新成功')
    } else {
      await addRoleService(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getRoleList()
  } finally {
    submitting.value = false
  }
}

// 权限分配相关
const menuDialogVisible = ref(false)
const menuSubmitting = ref(false)
const menuTreeRef = ref(null)
const menuList = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)

// 分配权限
const handleAssignMenus = async (row) => {
  try {
    currentRoleId.value = row.id
    // 重置选中状态
    checkedMenuIds.value = []
    // 获取菜单树数据
    const menuRes = await getMenuTreeSelectService()
    menuList.value = menuRes.data
    // 获取角色已有权限
    const roleRes = await getRoleMenusService(row.id)
    checkedMenuIds.value = roleRes.data || []
    // 重置树的展开状态
    nextTick(() => {
      if (menuTreeRef.value) {
        // 展开第一级菜单
        menuList.value.forEach(item => {
          menuTreeRef.value.store.nodesMap[item.id].expanded = true
        })
      }
    })
    menuDialogVisible.value = true
  } catch (error) {
    console.error('获取菜单树失败:', error)
    ElMessage.error('获取菜单树失败')
  }
}

// 监听对话框关闭
watch(menuDialogVisible, (newVal) => {
  if (!newVal) {
    // 对话框关闭时重置状态
    checkedMenuIds.value = []
    menuList.value = []
    currentRoleId.value = null
  }
})

// 提交权限分配
const handleAssignSubmit = async () => {
    try {
        menuSubmitting.value = true
        // 获取所有选中的节点（包括父节点和子节点）
        const checkedKeys = menuTreeRef.value.getCheckedKeys()
        
        await assignRoleMenusService({
            roleId: currentRoleId.value,
            menuIds: checkedKeys
        })
        ElMessage.success('权限分配成功')
        menuDialogVisible.value = false
    } catch (error) {
        console.error('分配权限失败:', error)
        ElMessage.error('分配权限失败')
    } finally {
        menuSubmitting.value = false
    }
}

// 状态变更
const handleStatusChange = async (row, status) => {
  try {
    const res = await updateRoleService({
      id: row.id,
      status
    })
    if (res.code === 1) {
      ElMessage.success(`${status === '0' ? '启用' : '禁用'}成功`)
    } else {
      ElMessage.error(res.message || '操作失败')
      // 如果失败，回滚状态
      row.status = status === '0' ? '1' : '0'
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('操作失败')
    // 如果失败，回滚状态
    row.status = status === '0' ? '1' : '0'
  }
}

onMounted(() => {
  getRoleList()
})
</script>

<style scoped>
.role-manage {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.search-form {
  background-color: #fff;
  padding: 15px 20px 0;
  border-radius: 4px;
  margin-bottom: 15px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 15px;
  margin-right: 20px;
}

.search-form :deep(.el-input),
.search-form :deep(.el-select) {
  width: 220px;
}

.table-toolbar {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.status-tag {
  min-width: 60px;
}

.pagination {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #fff;
  border-radius: 4px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
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

.menu-dialog :deep(.el-dialog__body) {
  padding: 10px 20px;
  max-height: 500px;
  overflow-y: auto;
}

.menu-tree-toolbar {
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
}

.menu-tree {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px 0;
}
</style> 