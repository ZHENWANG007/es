<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userInfo'
import { updateUserInfoService, getEmpDetailInfoService, updatePasswordService, uploadFileService, updateAvatarService } from '@/api/emp'
import defaultAvatar from '@/assets/default.png'
import { EditPen } from '@element-plus/icons-vue'

const userInfoStore = useUserInfoStore()

// 文件输入框ref
const fileInput = ref(null)

// 用户头像
const userAvatar = ref(defaultAvatar)

// 用户详细信息
const userDetail = ref({})

// 获取用户详细信息
const getUserDetail = async () => {
  try {
    const res = await getEmpDetailInfoService()
    if (res.code === 1) {
      userDetail.value = res.data
      // 更新头像
      if (res.data.avatar) {
        userAvatar.value = res.data.avatar
      }
      // 更新表单数据
      formData.value = {
        username: res.data.username || '',
        name: res.data.name || '',
        phone: res.data.phone || '',
        email: res.data.email || '',
        gender: res.data.gender || null,
        age: res.data.age || '',
        entryDate: res.data.entryDate || ''
      }
    }
  } catch (error) {
    console.error('获取用户详细信息失败:', error)
  }
}

// 表单数据
const formData = ref({
  username: '',
  name: '',
  phone: '',
  email: '',
  gender: null,
  age: '',
  entryDate: ''
})

// 密码表单数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 性别选项
const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 }
]

// 性别显示文本
const getGenderLabel = (value) => {
  const option = genderOptions.find(opt => opt.value === value)
  return option ? option.label : ''
}

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { pattern: /^\d+$/, message: '年龄必须为数字', trigger: 'blur' }
  ],
  entryDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ]
}

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 表单ref
const formRef = ref()
const passwordFormRef = ref()

// 保存修改
const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    // 构造提交的数据对象
    const submitData = {
      id: userDetail.value.id,  // 添加用户ID
      name: formData.value.name,
      gender: formData.value.gender,
      age: formData.value.age,
      phone: formData.value.phone,
      email: formData.value.email
    }
    
    const res = await updateUserInfoService(submitData)
    if (res.code === 1) {
      ElMessage.success('保存成功')
      // 更新store中的用户信息
      userInfoStore.setInfo({
        ...userInfoStore.info,
        ...submitData
      })
      // 重新获取用户信息
      getUserDetail()
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('保存失败')
  }
}

// 修改密码
const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()
  try {
    const res = await updatePasswordService({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword  // 添加确认密码字段
    })
    if (res.code === 1) {
      ElMessage.success('密码修改成功')
      // 清空表单
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      // 重置表单校验状态
      passwordFormRef.value.resetFields()
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  }
}

// 处理头像点击
const handleAvatarClick = () => {
  // 使用ref来触发文件选择器
  if (fileInput.value) {
    fileInput.value.click()
  }
}

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('只能上传 JPG/PNG/GIF 格式的图片！')
    return
  }
  
  // 验证文件大小（2MB）
  const maxSize = 2 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 2MB！')
    return
  }

  try {
    // 上传文件
    const uploadRes = await uploadFileService(file)
    if (uploadRes.code === 1) {
      // 更新用户头像
      const updateRes = await updateAvatarService(uploadRes.data)
      if (updateRes.code === 1) {
        ElMessage.success('头像更新成功')
        // 更新显示的头像
        userAvatar.value = uploadRes.data
        // 更新store中的用户信息
        const newUserInfo = {
          ...userInfoStore.info,
          avatar: uploadRes.data
        }
        userInfoStore.setInfo(newUserInfo)
        // 重新获取用户信息以确保数据同步
        await getUserDetail()
      }
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  } finally {
    // 清空文件输入框，以便可以重复上传同一个文件
    event.target.value = ''
  }
}

// 初始化加载数据
onMounted(() => {
  getUserDetail()
})
</script>

<template>
  <div class="account-info">
    <!-- 头部背景区域 -->
    <div class="info-header">
      <div class="user-profile">
        <div class="avatar-wrapper" @click="handleAvatarClick">
          <el-avatar 
            :src="userAvatar" 
            :size="100"
            class="user-avatar"
          />
          <div class="avatar-hover">
            <el-icon><EditPen /></el-icon>
            <span>更换头像</span>
          </div>
          <input
            type="file"
            ref="fileInput"
            accept="image/jpeg,image/png,image/gif"
            class="hidden-upload"
            @change="handleAvatarUpload"
          />
        </div>
        <h2 class="user-name">{{ userDetail.name || userDetail.username }}</h2>
        <div class="user-meta">
          <span class="dept-name">{{ userDetail.deptName }}</span>
          <el-divider direction="vertical" />
          <span class="job-title">{{ userDetail.jobTitle }}</span>
        </div>
      </div>
    </div>

    <!-- 选项卡区域 -->
    <div class="info-tabs">
      <el-tabs>
        <el-tab-pane label="基本资料">
          <el-card class="info-card" shadow="never">
            <el-form 
              ref="formRef"
              :model="formData"
              :rules="rules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="formData.username" disabled />
              </el-form-item>
              
              <el-form-item label="姓名" prop="name">
                <el-input v-model="formData.name" placeholder="请输入姓名" />
              </el-form-item>

              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="formData.gender">
                  <el-radio 
                    v-for="option in genderOptions" 
                    :key="option.value" 
                    :label="option.value"
                  >
                    {{ option.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="年龄" prop="age">
                <el-input-number 
                  v-model="formData.age" 
                  :min="18" 
                  :max="100"
                  placeholder="请输入年龄"
                />
              </el-form-item>

              <el-form-item label="入职日期" prop="entryDate">
                <el-input 
                  v-model="formData.entryDate" 
                  disabled
                />
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入手机号" />
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="formData.email" placeholder="请输入邮箱" />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSubmit">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="修改密码">
          <el-card class="info-card" shadow="never">
            <el-form 
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input 
                  v-model="passwordForm.oldPassword" 
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password"
                  placeholder="请确认新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.account-info {
  .info-header {
    height: 85px;
    background: linear-gradient(45deg, #409EFF, #36D1DC);
    margin: -20px -20px 0;
    padding: 20px;
    position: relative;
    
    .user-profile {
      position: absolute;
      left: 50%;
      bottom: 0;
      transform: translate(-50%, 50%);
      text-align: center;
      
      .avatar-wrapper {
        position: relative;
        display: inline-block;
        cursor: pointer;
        
        .user-avatar {
          border: 4px solid #fff;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
        }

        .avatar-hover {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          border-radius: 50%;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          color: #fff;
          opacity: 0;
          transition: opacity 0.3s ease;
          z-index: 1;
        }

        &:hover {
          .avatar-hover {
            opacity: 1;
          }
        }

        .el-icon {
          font-size: 20px;
          margin-bottom: 5px;
        }

        span {
          font-size: 12px;
        }
      }

      .hidden-upload {
        display: none;
      }
      
      .user-name {
        color: #333;
        margin: 15px 0 5px;
        font-size: 20px;
      }
      
      .user-meta {
        color: #666;
        font-size: 14px;
        
        .el-divider {
          margin: 0 10px;
        }
        
        .dept-name,
        .job-title {
          color: #666;
        }
      }
    }
  }
  
  .info-tabs {
    margin-top: 100px;
    padding: 0 20px;
    
    .info-card {
      margin-top: 20px;
      
      .info-form {
        max-width: 600px;
        margin: 0 auto;
        padding: 20px 0;
        
        :deep(.el-input),
        :deep(.el-input-number),
        :deep(.el-date-picker) {
          width: 300px;
        }
        
        :deep(.el-radio-group) {
          width: 300px;
          display: flex;
          gap: 30px;
        }
      }
    }
  }
}
</style> 