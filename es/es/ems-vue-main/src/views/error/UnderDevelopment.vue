<template>
    <div class="under-development">
        <el-empty :image-size="240" description="页面开发中，敬请期待...">
            <template #image>
                <el-icon :size="100" color="#909399"><Box /></el-icon>
            </template>
            <template #description>
                <p class="description">页面开发中，敬请期待...</p>
            </template>
            <el-button type="primary" size="large" @click="$router.go(-1)">返回上一页</el-button>
        </el-empty>
    </div>
</template>

<script setup>
import { Box } from '@element-plus/icons-vue'
</script>

<style scoped>
.under-development {
    width: 100%;
    height: calc(100vh - 84px);
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f7fa;
    padding: 20px;
    box-sizing: border-box;
    overflow: hidden;
}

:deep(.el-empty) {
    padding: 30px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.description {
    margin-top: 16px;
    font-size: 16px;
    color: #909399;
}

:deep(.el-button) {
    margin-top: 16px;
    padding: 12px 24px;
    font-size: 16px;
}
</style> 