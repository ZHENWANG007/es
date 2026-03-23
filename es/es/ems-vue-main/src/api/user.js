// 导入request.js请求工具
import request from '@/utils/request.js'

//登录
export const loginService = (loginData) => {
    return request.post('/user/login', loginData)
}

// 获取个人信息
export const userInfoGetService = () => {
    return request.get('/user/user/1');
}

// 登出
export const logoutService = () => {
    return request.post('/user/logout');
}

// 获取用户信息
export const getUserInfoService = () => {
  return request.get('/user')
}

// 更新用户信息
export const updateUserInfoService = (userInfo) => {
  return request.post('/user', userInfo)
}

// 修改密码
export const updatePasswordService = (passwordData) => {
  return request.post('/user/updatePassword', passwordData)
}

// 上传头像
export const uploadAvatarService = (file) => {
  const formData = new FormData()
  formData.append('imgfile', file)
  return request.post('/user/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

