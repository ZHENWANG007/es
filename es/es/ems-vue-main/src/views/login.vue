<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import logo from '@/assets/logo.png'
import { loginService } from '@/api/emp.js'
import { useTokenStore } from '@/stores/token.js'

const tokenStore = useTokenStore()
const loginForm = ref({
  username: '',
  password: ''
})

// 登录表单校验规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^\w{5,20}$/, message: '用户名必须是5-20位的字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\w{5,20}$/, message: '密码必须是5-20位的字母、数字或下划线', trigger: 'blur' }
  ]
}

const router = useRouter()
const loginFormRef = ref(null)

// 登录方法
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await loginService(loginForm.value)
        if (res.code === 1) {
          tokenStore.setToken(res.data.token)
          console.log('token:'+res.data.token);
          ElMessage.success('登录成功')
          router.push('/')
        }
      } catch (error) {
        console.error('登录失败：', error)
      }
    }
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-content">
        <!-- 左侧 Logo 区域 -->
        <div class="logo-section">
          <div class="logo-wrapper">
            <img :src="logo" alt="Logo" class="logo">
          </div>
          <h2 class="welcome-text">鱼跃人力资源管理系统</h2>
          <p class="slogan">Enterprise Employee Management System</p>
          <div class="decoration-circles">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>

        <!-- 右侧表单区域 -->
        <div class="form-section">
          <h2 class="title">账号登录</h2>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                :prefix-icon="User"
                placeholder="请输入用户名"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                :prefix-icon="Lock"
                type="password"
                placeholder="请输入密码"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                class="submit-button"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="copyright">
            © 2025 鱼跃人力资源管理系统 All Rights Reserved
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    background: url('@/assets/login-bg.jpg') center/cover no-repeat;
    filter: blur(2px);
    opacity: 0.15;
  }

  .login-box {
    position: relative;
    width: 1000px;
    min-height: 500px;
    background: rgba(255, 255, 255, 0.98);
    border-radius: 20px;
    box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2);
    overflow: hidden;
    backdrop-filter: blur(10px);
    animation: fade-in 0.8s ease-out;

    .login-content {
      display: flex;
      min-height: 500px;

      .logo-section {
        flex: 1;
        position: relative;
        background: linear-gradient(135deg, #1e3c72, #2a5298);
        padding: 40px 50px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: white;
        text-align: center;
        overflow: hidden;

        .logo-wrapper {
          position: relative;
          width: 200px;
          height: 200px;
          margin-bottom: 30px;
          animation: float 6s ease-in-out infinite;
          margin-top: -20px;

          .logo {
            width: 100%;
            height: 100%;
            object-fit: contain;
            filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.2));
          }
        }

        .welcome-text {
          font-size: 32px;
          font-weight: 600;
          margin-bottom: 15px;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
          letter-spacing: 1px;
        }

        .slogan {
          font-size: 18px;
          opacity: 0.9;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
          letter-spacing: 0.5px;
        }

        .decoration-circles {
          position: absolute;
          bottom: 40px;
          display: flex;
          gap: 12px;

          span {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.5);
            animation: pulse 2s infinite;

            &:nth-child(2) {
              animation-delay: 0.4s;
            }

            &:nth-child(3) {
              animation-delay: 0.8s;
            }
          }
        }
      }

      .form-section {
        flex: 1;
        padding: 40px 60px;
        background: white;
        display: flex;
        flex-direction: column;
        justify-content: center;
        position: relative;

        .title {
          font-size: 28px;
          color: #1e3c72;
          margin-bottom: 40px;
          text-align: center;
          font-weight: 600;
          position: relative;

          &::after {
            content: '';
            position: absolute;
            bottom: -15px;
            left: 50%;
            transform: translateX(-50%);
            width: 40px;
            height: 3px;
            background: linear-gradient(to right, #1e3c72, #2a5298);
            border-radius: 2px;
          }
        }

        .el-form {
          width: 85%;
          margin: 0 auto;
        }

        .el-form-item {
          margin-bottom: 30px;

          :deep(.el-input__wrapper) {
            box-shadow: 0 0 0 1px #dcdfe6;
            transition: all 0.3s ease;
            border-radius: 8px;

            &:hover {
              box-shadow: 0 0 0 1px #b4bccc;
            }

            &.is-focus {
              box-shadow: 0 0 0 1px #409eff;
            }
          }

          :deep(.el-input__inner) {
            height: 48px;
          }
        }

        .submit-button {
          margin-top: 10px;
          width: 100%;
          height: 48px;
          font-size: 18px;
          font-weight: 500;
          background: linear-gradient(45deg, #1e3c72, #2a5298);
          border: none;
          border-radius: 8px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(30, 60, 114, 0.3);
          }

          &:active {
            transform: translateY(1px);
          }
        }

        .copyright {
          position: absolute;
          bottom: 20px;
          left: 0;
          right: 0;
          text-align: center;
          color: #909399;
          font-size: 12px;
          opacity: 0.8;
        }
      }
    }
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 0.5;
  }
}

// 响应式设计
@media screen and (max-width: 768px) {
  .login-box {
    width: 95% !important;
    margin: 20px;

    .login-content {
      flex-direction: column;

      .logo-section {
        padding: 30px 20px;

        .logo-wrapper {
          width: 130px;
          height: 130px;
          margin-bottom: 20px;
        }

        .welcome-text {
          font-size: 28px;
        }
      }

      .form-section {
        padding: 30px 20px;

        .title {
          font-size: 22px;
          margin-bottom: 25px;
        }
      }
    }
  }
}
</style>
