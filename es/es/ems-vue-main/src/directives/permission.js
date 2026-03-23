import { usePermissionStore } from '@/stores/permission'

export const permission = {
    mounted(el, binding) {
        const permissionStore = usePermissionStore()
        const { value } = binding
        const buttons = permissionStore.buttons

        if (value && value.length > 0) {
            // 检查是否拥有任一所需权限
            const hasPermission = value.some(permission => buttons.includes(permission))
            if (!hasPermission) {
                el.parentNode?.removeChild(el)
            }
        }
    }
}