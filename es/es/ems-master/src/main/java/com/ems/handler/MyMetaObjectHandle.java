package com.ems.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ems.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MP公共字段自动填充，支持表只有创建时间和修改时间字段时的填充
 */
@Component
@Slf4j
public class MyMetaObjectHandle implements MetaObjectHandler {

    // 插入操作时执行
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert 公共字段自动填充...{}", metaObject.getOriginalObject());

        // 如果存在 createTime 字段，填充当前时间
        if (metaObject.hasSetter("createTime")) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }

        // 如果存在 updateTime 字段，填充当前时间
        if (metaObject.hasSetter("updateTime")) {
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }

        // 如果存在 createUser 字段，填充当前用户ID
        if (metaObject.hasSetter("createUser")) {
            this.strictInsertFill(metaObject, "createUser", Long.class, BaseContext.getCurrentId());
        }

        // 如果存在 updateUser 字段，填充当前用户ID
        if (metaObject.hasSetter("updateUser")) {
            this.strictInsertFill(metaObject, "updateUser", Long.class, BaseContext.getCurrentId());
        }
    }

    // 更新操作时执行
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update 公共字段自动填充...{}", metaObject.getOriginalObject());

        // 如果存在 updateTime 字段，填充当前时间
        if (metaObject.hasSetter("updateTime")) {
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }

        // 如果存在 updateUser 字段，填充当前用户ID
        if (metaObject.hasSetter("updateUser")) {
            this.strictUpdateFill(metaObject, "updateUser", Long.class, BaseContext.getCurrentId());
        }
    }
}
