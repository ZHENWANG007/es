<script setup>
import {
    Promotion,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom,
    HomeFilled,
    Monitor,
    Avatar,
    TrendCharts,
    SetUp,
    Setting,
    UserFilled,
    Menu
} from '@element-plus/icons-vue'
import * as ElementPlusIcons from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'
import { ref, onMounted, computed } from 'vue'
import { getAvatarService } from '@/api/emp'

// 用户头像
const userAvatar = computed(() => {
    return userInfoStore.info.avatar || avatar
})

//导入接口函数
//导入pinia
import {useUserInfoStore} from '@/stores/userInfo.js'
const userInfoStore = useUserInfoStore();

//dropDown条目被点击后，回调的函数
import {useRouter} from 'vue-router'
const router = useRouter()
import {ElMessage,ElMessageBox} from 'element-plus'
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore()
import { logoutService } from '@/api/emp.js'
import { usePermissionStore } from '@/stores/permission'
const permissionStore = usePermissionStore()

const handleCommand = (command) => {
    if (command === 'logout') {
        //退出登录
        ElMessageBox.confirm(
            '确定要退出登录吗？',
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        ).then(async () => {
            try {
                // 调用退出接口
                await logoutService()
                // 清除token
                tokenStore.clearToken()
                // 清除用户信息
                userInfoStore.clearInfo()
                // 重置路由
                permissionStore.resetRoutes()
                // 跳转到登录页
                await router.push('/login')
                ElMessage.success('退出成功')
            } catch (error) {
                console.error('退出失败:', error)
                ElMessage.error('退出失败')
            }
        }).catch(() => {})
    } else {
        //路由
        router.push('/account/' + command)
    }
}
</script>

<template>
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px" class="aside-menu">
            <div class="el-aside__logo">
                <span>后台管理系统</span>
            </div>
            <el-menu
                :default-active="$route.path"
                background-color="#304156"
                text-color="#bfcbd9"
                active-text-color="#409EFF"
                router
            >
                <!-- 动态生成菜单 -->
                <template v-for="menu in permissionStore.menus" :key="menu.id">
                    <!-- 一级菜单 -->
                    <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
                        <template #title>
                            <el-icon>
                                <component :is="ElementPlusIcons[menu.icon]" />
                            </el-icon>
                            <span>{{ menu.menuName }}</span>
                        </template>
                        <!-- 二级菜单 -->
                        <el-menu-item 
                            v-for="child in menu.children" 
                            :key="child.id"
                            :index="menu.path + '/' + child.path.replace(/^\//, '')"
                        >
                            <el-icon>
                                <component :is="ElementPlusIcons[child.icon]" />
                            </el-icon>
                            <span>{{ child.menuName }}</span>
                        </el-menu-item>
                    </el-sub-menu>
                    <!-- 没有子菜单的一级菜单 -->
                    <el-menu-item v-else :index="menu.path">
                        <el-icon>
                            <component :is="ElementPlusIcons[menu.icon]" />
                        </el-icon>
                        <span>{{ menu.menuName }}</span>
                    </el-menu-item>
                </template>
            </el-menu>
        </el-aside>

        <!-- 右侧主区域 -->
        <el-container>
            <!-- 头部区域 -->
            <el-header class="header">
                <!-- 下拉菜单 -->
                <!-- command: 条目被点击后会触发，在事件函数上可以声明一个参数，接受条目对应的指令 -->
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar 
                            :src="userAvatar" 
                            :size="40"
                        />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info" :icon="User">个人中心</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>

            <!-- 内容区域 -->
            <el-main class="main-content">
                <!-- <div class="content-display">
                    内容展示区域
                </div> -->
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;
    display: flex;

    .aside-menu {
        background-color: #2C3E50;
        padding: 10px;

        .el-aside__logo {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;

            h2 {
                font-size: 24px;
                font-weight: bold;
            }
        }

        .el-menu {
            border-right: none;

            .el-menu-item, .el-sub-menu {
                position: relative;
                transition: background-color 0.3s, color 0.3s;

                &:hover {
                    .el-icon {
                        color: #3498db;
                    }
                    span {
                        color: #3498db;
                    }
                }
            }

            .personal-center {
                .el-menu-item {
                    &.is-active {
                        background-color: #34495e;
                        color: #3498db;
                    }

                    &:hover {
                        background-color: #34495e;
                        color: #3498db;
                    }

                    .el-icon {
                        color: inherit;
                    }

                    span {
                        color: inherit;
                    }
                }
            }
        }
    }

    .header {
        background-color: #f5f5f5;
        display: flex;
        justify-content: flex-end;
        padding: 10px;

        .el-dropdown__box {
            display: flex;
            align-items: center;
            cursor: pointer;
            gap: 8px;

            .el-avatar {
                border: 2px solid #fff;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                transition: all 0.3s;

                &:hover {
                    transform: scale(1.05);
                }
            }

            .el-icon {
                color: #666;
            }
        }
    }

    .main-content {
        padding: 20px;
        background-color: #fff;
        flex: 1;
        border: 1px solid #ddd; /* 添加边框 */
        border-radius: 8px; /* 添加圆角 */
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影 */

        .content-display {
            text-align: center;
            font-size: 18px;
            color: #333;
            padding: 20px;
            height: calc(100vh - 120px); /* 调整高度，去除上方头部的高度 */
            display: flex;
            justify-content: center;
            align-items: center;
            border: 1px solid #ddd;
            border-radius: 8px;
        }
    }
}
</style>
