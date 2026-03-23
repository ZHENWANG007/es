/*
 Navicat Premium Data Transfer

 Source Server         : hdm
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : etest

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 17/06/2025 21:27:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emp_id` bigint UNSIGNED NOT NULL COMMENT '员工ID',
  `emp_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工姓名',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门名称',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '上班打卡时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '下班打卡时间',
  `late_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否迟到（0-否，1-是）',
  `late_minutes` int NULL DEFAULT 0 COMMENT '迟到分钟数',
  `early_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否早退（0-否，1-是）',
  `early_minutes` int NULL DEFAULT 0 COMMENT '早退分钟数',
  `overtime_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否加班（0-否，1-是）',
  `overtime_minutes` int NULL DEFAULT 0 COMMENT '加班分钟数',
  `leave_flag` tinyint NULL DEFAULT NULL COMMENT '是否请假（0-否，1-是）',
  `leave_request_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联请假表ID',
  `absent_flag` tinyint NULL DEFAULT 0 COMMENT '是否旷工（0-否，1-是）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `attendance__page`(`emp_name` ASC, `dept_name` ASC, `attendance_date` ASC) USING BTREE COMMENT '分页索引'
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of attendance
-- ----------------------------
INSERT INTO `attendance` VALUES (1, 1, '王五', '深圳研发部门', '2025-06-17', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 0, 0, 0, NULL, 0, '正常出勤', '2024-01-15 18:00:00', 1, '2024-01-15 18:00:00', 1);
INSERT INTO `attendance` VALUES (2, 2, '张三', '深圳测试部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 30, 0, 0, 0, 0, 0, NULL, 0, '迟到30分钟', '2024-01-16 18:00:00', 1, '2024-01-16 18:00:00', 1);
INSERT INTO `attendance` VALUES (3, 3, '李四', '深圳财务部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 1, 60, 0, 0, 0, NULL, 0, '早退1小时', '2024-01-17 18:00:00', 1, '2024-01-17 18:00:00', 1);
INSERT INTO `attendance` VALUES (4, 4, '蒋银辉', '长沙销售部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '未打卡，旷工一天', '2024-01-18 18:00:00', 1, '2024-01-18 18:00:00', 1);
INSERT INTO `attendance` VALUES (5, 4, '蒋银辉', '长沙销售部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 0, 0, 1, 4, 0, '事假（家人陪护）', '2024-04-02 18:00:00', 1, '2024-04-02 18:00:00', 1);
INSERT INTO `attendance` VALUES (6, 5, '王二狗', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 1, 180, 0, NULL, 0, '加班3小时', '2024-01-19 21:00:00', 1, '2024-01-19 21:00:00', 1);
INSERT INTO `attendance` VALUES (7, 1, '王五', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 1, 299, 0, NULL, 0, '加班4小时59分钟', '2025-01-01 21:01:39', 1, '2025-01-01 21:01:39', 1);
INSERT INTO `attendance` VALUES (8, 1, '王五', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 1, 198, 0, NULL, 0, '加班3小时18分钟', '2025-01-02 21:01:39', 1, '2025-01-01 21:01:39', 1);
INSERT INTO `attendance` VALUES (9, 3, '李四', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 307, 0, 0, 0, 0, 0, NULL, 0, '迟到5小时7分钟', '2025-01-02 14:07:21', 3, '2025-01-02 14:07:21', 3);
INSERT INTO `attendance` VALUES (10, 1, '王五', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 173, 1, 365, 0, 0, 0, NULL, 0, '早退6小时5分钟', '2025-01-03 11:53:20', 1, '2025-01-03 11:53:20', 1);
INSERT INTO `attendance` VALUES (11, 4, '蒋银辉', '长沙销售部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 178, 0, 0, 0, 0, 0, NULL, 0, '迟到2小时58分钟', '2025-01-03 12:00:33', 4, '2025-01-03 12:00:33', 4);
INSERT INTO `attendance` VALUES (12, 3, '李四', '深圳财务部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 182, 1, 354, 0, 0, 0, NULL, 0, '早退5小时54分钟', '2025-01-03 12:02:16', 3, '2025-01-03 12:02:16', 3);
INSERT INTO `attendance` VALUES (13, 1, '王五', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 0, 0, 0, 0, 0, 0, 0, NULL, 0, NULL, '2025-01-04 08:48:52', 1, '2025-01-04 08:48:52', 1);
INSERT INTO `attendance` VALUES (14, 1, '王五', '深圳研发部门', '2025-01-05', '2025-06-17 18:00:00', '2025-06-17 18:00:00', 1, 784, 0, 0, 1, 198, 0, NULL, 0, '加班3小时18分钟', '2025-01-05 22:04:38', 1, '2025-01-05 22:04:38', 1);
INSERT INTO `attendance` VALUES (15, 7, '文浩州', '深圳财务部门', '2025-06-17', '2025-06-17 18:18:21', '2025-06-17 18:18:22', 1, 558, 0, 0, 0, 0, 0, NULL, 0, '迟到9小时18分钟', '2025-06-17 18:18:21', 7, '2025-06-17 18:18:21', 7);

-- ----------------------------
-- Table structure for dept
-- ----------------------------
DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父部门ID（支持多级部门）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门描述',
  `order_num` tinyint NULL DEFAULT NULL COMMENT '排序',
  `status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dept
-- ----------------------------
INSERT INTO `dept` VALUES (1, '莫灰心科技', NULL, '公司总部，负责整体战略规划和决策', 0, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (2, '深圳总公司', 1, '负责深圳地区的业务运营和管理', 1, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (3, '深圳研发部门', 2, '负责新产品的研发和技术创新', 1, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (4, '深圳市场部门', 2, '负责深圳地区市场调研、品牌推广和销售策略', 2, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (5, '深圳测试部门', 2, '负责产品质量测试和确保产品符合标准', 4, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (6, '深圳财务部门', 2, '负责公司的财务管理、预算和审计', 3, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (7, '深圳运维部门', 2, '负责公司IT基础设施的维护和技术支持', 5, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (8, '长沙分公司', 1, '负责长沙地区的业务拓展和客户关系管理', 2, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (16, '长沙销售部门', 8, '负责长沙地区的销售活动，包括客户开发、销售策略执行和销售目标达成', 1, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (17, '长沙客服部门', 8, '提供客户服务和支持，处理客户咨询、投诉和反馈，提升客户满意度', 2, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (18, '长沙物流部门', 8, '管理长沙地区的物流和配送，确保产品及时、准确地送达客户手中', 3, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (19, '长沙行政部门', 8, '负责长沙分公司的日常行政工作，包括办公环境维护、设备管理和后勤支持', 4, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (20, '长沙人力资源部门', 8, '负责长沙分公司的人力资源管理，包括招聘、培训、绩效评估和员工关系', 5, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (21, '长沙研发支持部门', 8, '为长沙分公司提供研发支持，包括产品本地化改进和技术创新', 6, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (22, '长沙市场部门', 8, '负责长沙地区的市场调研和市场活动，提升品牌知名度和市场占有率1', 7, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);
INSERT INTO `dept` VALUES (25, '长沙测试部门', 8, '测试1', 7, 0, '2025-06-17 18:00:00', 1, '2025-06-17 18:00:00', 1);

-- ----------------------------
-- Table structure for emp
-- ----------------------------
DROP TABLE IF EXISTS `emp`;
CREATE TABLE `emp`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '部门ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` tinyint UNSIGNED NOT NULL COMMENT '性别, 说明: 1 男, 2 女',
  `age` tinyint NULL DEFAULT NULL COMMENT '年龄',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图像',
  `job_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位名称',
  `entry_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `leave_date` date NULL DEFAULT NULL COMMENT '离职时间',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `emp__page`(`name` ASC, `job_title` ASC, `status` ASC, `entry_date` ASC) USING BTREE COMMENT '分页复合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of emp
-- ----------------------------
INSERT INTO `emp` VALUES (1, 3, 'admin', '$2a$10$9fZl3fbJKPFMzchVNX4OreEYMyyCXkQqfQx2ebC01GuNcZFsdjUHq', '王五', 1, 32, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/00505e50-996f-4bb0-870b-999fbbea3cb2.jpg', '系统管理员', '2000-01-01', NULL, '15356456856', '15356456856@163.com', 0, '2024-02-05 20:32:32', 1, '2025-01-06 03:20:31', 1);
INSERT INTO `emp` VALUES (2, 5, 'hr001', '$2a$10$JIiSSw7G79GrTfO5hg.BkO73A.Nh7jaoY4Gp.yTvXObQ0nRLVSCOC', '张三', 1, 34, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '人事经理', '2015-01-01', NULL, '12356485235', '12356485235@163.com', 0, '2024-02-05 20:32:32', 1, '2024-02-05 20:32:32', 1);
INSERT INTO `emp` VALUES (3, 6, 'manager001', '$2a$10$JIiSSw7G79GrTfO5hg.BkO73A.Nh7jaoY4Gp.yTvXObQ0nRLVSCOC', '李四', 1, 45, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '部门经理', '2015-01-01', NULL, '12356485235', '12356485235@163.com', 0, '2024-02-05 20:32:32', 1, '2024-02-05 20:32:32', 1);
INSERT INTO `emp` VALUES (4, 6, 'emp001', '$2a$10$.cCRtKo9qt1wKB1Ue197LOTbFQ12C8x213bOPQS0upyzonQmjkmGK', '蒋银辉', 1, 18, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '后端开发工程师', '2015-01-01', NULL, '15356485235', '15356485235@163.com', 0, '2024-02-05 20:32:32', 1, '2025-01-06 02:09:10', 1);
INSERT INTO `emp` VALUES (5, 3, 'wangergou', '$2a$10$.cCRtKo9qt1wKB1Ue197LOTbFQ12C8x213bOPQS0upyzonQmjkmGK', '王二狗', 1, 18, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '产品经理', '2024-12-18', '2025-01-02', '15235623542', '15235623542@qq.com', 1, '2024-12-31 21:27:02', 1, '2024-12-31 21:39:17', 1);
INSERT INTO `emp` VALUES (6, 6, 'emp002', '$2a$10$.cCRtKo9qt1wKB1Ue197LOTbFQ12C8x213bOPQS0upyzonQmjkmGK', '秦波', 1, 18, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '后端开发工程师', '2025-01-02', NULL, '15235685635', '15235685635@qq.com', 0, '2025-01-02 10:41:34', 1, '2025-01-02 10:41:39', 1);
INSERT INTO `emp` VALUES (7, 6, 'emp003', '$2a$10$.cCRtKo9qt1wKB1Ue197LOTbFQ12C8x213bOPQS0upyzonQmjkmGK', '文浩州', 1, 18, 'https://jiang2003-travel.oss-cn-beijing.aliyuncs.com/hand.jpg', '后端开发工程师', '2025-01-02', NULL, '15235878786', '15235878786@qq.com', 0, '2025-01-02 10:41:45', 1, '2025-01-02 10:41:47', 1);
INSERT INTO `emp` VALUES (12, 5, 'sales001', '$2a$10$GrR3mcdOr3G.KhA0G8800eJHIYHM7iQmIO767P3vWjD3bZTWo/wwC', '张伟', 1, 28, NULL, '销售经理', '2015-02-11', NULL, '13800138000', 'zhangwei@example.com', 0, '2025-01-06 09:32:58', 1, '2025-01-06 09:32:58', 1);
INSERT INTO `emp` VALUES (13, 5, 'tech002', '$2a$10$Ed5T5xHJBvawzjrftGDInuZsCIgLiaknOwm/ECJZg80L/uvoCeAiK', '李娜', 2, 32, NULL, '高级开发工程师', '2015-02-11', NULL, '13900139000', 'linan@example.com', 0, '2025-01-06 09:32:58', 1, '2025-01-06 09:32:58', 1);

-- ----------------------------
-- Table structure for emp_role
-- ----------------------------
DROP TABLE IF EXISTS `emp_role`;
CREATE TABLE `emp_role`  (
  `emp_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '员工id',
  `role_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色id',
  PRIMARY KEY (`emp_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of emp_role
-- ----------------------------
INSERT INTO `emp_role` VALUES (1, 1);
INSERT INTO `emp_role` VALUES (2, 2);
INSERT INTO `emp_role` VALUES (3, 3);
INSERT INTO `emp_role` VALUES (4, 4);
INSERT INTO `emp_role` VALUES (5, 4);
INSERT INTO `emp_role` VALUES (6, 4);
INSERT INTO `emp_role` VALUES (7, 4);
INSERT INTO `emp_role` VALUES (12, 4);
INSERT INTO `emp_role` VALUES (13, 4);

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '岗位职责描述',
  `min_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低薪资',
  `max_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高薪资',
  `status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_job_name`(`name` ASC) USING BTREE,
  INDEX `job__page`(`name` ASC, `status` ASC) USING BTREE COMMENT '岗位分页索引'
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of job
-- ----------------------------
INSERT INTO `job` VALUES (1, '软件工程师', '负责软件开发、测试及部署', 8000.00, 15000.00, 0, '2024-01-01 10:00:00', 1, '2025-01-04 08:48:33', 1);
INSERT INTO `job` VALUES (2, '项目经理', '负责项目规划和团队管理', 12000.00, 25000.00, 0, '2024-01-02 11:00:00', 1, '2024-01-02 11:00:00', 1);
INSERT INTO `job` VALUES (3, '数据分析师', '负责数据收集、分析与建模', 9000.00, 18000.00, 0, '2024-01-03 12:00:00', 1, '2024-01-03 12:00:00', 1);
INSERT INTO `job` VALUES (4, '前端开发工程师', '负责前端页面开发与优化', 7000.00, 14000.00, 0, '2024-01-04 13:00:00', 1, '2024-01-04 13:00:00', 1);
INSERT INTO `job` VALUES (5, '后端开发工程师', '负责后端接口开发与维护', 8000.00, 16000.00, 0, '2024-01-05 14:00:00', 1, '2024-01-05 14:00:00', 1);
INSERT INTO `job` VALUES (6, 'UI设计师', '负责用户界面设计与优化', 6000.00, 12000.00, 0, '2024-01-06 15:00:00', 1, '2024-01-06 15:00:00', 1);
INSERT INTO `job` VALUES (7, '运维工程师', '负责系统的日常维护与支持', 7000.00, 13000.00, 0, '2024-01-07 16:00:00', 1, '2024-01-07 16:00:00', 1);
INSERT INTO `job` VALUES (8, '产品经理', '负责产品规划和需求分析', 10000.00, 20000.00, 0, '2024-01-08 17:00:00', 1, '2024-01-08 17:00:00', 1);
INSERT INTO `job` VALUES (9, '测试工程师', '负责产品功能测试与质量保证', 6000.00, 11000.00, 0, '2024-01-09 18:00:00', 1, '2024-01-09 18:00:00', 1);
INSERT INTO `job` VALUES (14, '部门经理', '负责项目规划和员工管理', 10000.00, 12000.00, 1, '2024-12-31 18:10:03', 1, '2025-01-05 23:43:33', 1);
INSERT INTO `job` VALUES (15, '人事经理', '负责公司人事管理', 10000.00, 12000.00, 0, '2024-12-31 18:11:05', 1, '2024-12-31 18:11:05', 1);
INSERT INTO `job` VALUES (16, '系统管理员', '负责管理公司系统', 6000.00, 8000.00, 0, '2024-12-31 18:12:19', 1, '2024-12-31 18:12:19', 1);
INSERT INTO `job` VALUES (18, '软件工程师1', '开发软件', 2000.00, 2000.00, 0, '2025-01-05 23:32:19', 1, '2025-01-05 23:39:31', 1);
INSERT INTO `job` VALUES (19, '243', '43242', 2000.00, 3000.00, 0, '2025-06-17 18:17:31', 1, '2025-06-17 18:17:31', 1);

-- ----------------------------
-- Table structure for leave_request
-- ----------------------------
DROP TABLE IF EXISTS `leave_request`;
CREATE TABLE `leave_request`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emp_id` bigint UNSIGNED NOT NULL COMMENT '员工ID',
  `emp_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工姓名',
  `leave_type` tinyint UNSIGNED NOT NULL COMMENT '请假类型（1-事假、2-病假、3-公假）',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请假原因',
  `leader_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '直属领导审批状态（0-待审批，1-通过，2-驳回）',
  `leader_approved_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '直属领导审批人ID',
  `leader_approved_time` datetime NULL DEFAULT NULL COMMENT '直属领导审批时间',
  `hr_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '人事经理审批状态（0-待审批，1-通过，2-驳回）',
  `hr_approved_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '人事经理审批人ID',
  `hr_approved_time` datetime NULL DEFAULT NULL COMMENT '人事经理审批时间',
  `overall_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '请假总状态（0-待审批，1-已批准，2-已驳回）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `leave_request__page`(`leave_type` ASC, `create_time` ASC, `overall_status` ASC, `emp_name` ASC) USING BTREE COMMENT '分页索引'
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请假表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of leave_request
-- ----------------------------
INSERT INTO `leave_request` VALUES (1, 1, '王五', 1, '2025-06-17', '2025-06-17', '家中有急事需处理', 0, NULL, NULL, 0, NULL, NULL, 0, '2025-06-17 10:00:00', 1, '2024-01-10 10:00:00', 1);
INSERT INTO `leave_request` VALUES (2, 2, '张三', 2, '2025-06-17', '2025-06-17', '身体不适需休息', 1, 3, '2024-01-12 14:00:00', 2, 2, '2025-01-03 20:13:20', 2, '2024-01-10 11:00:00', 2, '2025-01-03 20:13:20', 2);
INSERT INTO `leave_request` VALUES (3, 3, '李四', 3, '2025-06-17', '2025-06-17', '需参加公司外部活动', 2, 3, '2024-01-13 15:00:00', 0, NULL, NULL, 2, '2024-01-10 12:00:00', 3, '2024-01-13 15:00:00', 3);
INSERT INTO `leave_request` VALUES (4, 4, '蒋银辉', 1, '2025-06-17', '2025-06-17', '家人陪护', 1, 3, '2024-01-14 16:00:00', 1, 2, '2024-01-15 10:00:00', 1, '2024-01-10 13:00:00', 4, '2024-01-15 10:00:00', 2);
INSERT INTO `leave_request` VALUES (5, 5, '王二狗', 2, '2025-06-17', '2025-06-17', '身体复查', 1, 3, '2024-01-16 10:00:00', 2, 2, '2024-01-17 11:00:00', 2, '2024-01-10 14:00:00', 5, '2024-01-17 11:00:00', 2);
INSERT INTO `leave_request` VALUES (7, 4, '蒋银辉', 1, '2025-06-17', '2025-06-17', '家人陪护', 1, 6, '2025-01-02 10:49:39', 2, 2, '2025-01-06 02:11:19', 2, '2025-01-02 10:47:52', 4, '2025-01-06 02:11:19', 2);
INSERT INTO `leave_request` VALUES (8, 6, '秦波', 2, '2025-06-17', '2025-06-17', '身体不适需休息', 2, 3, '2025-01-03 20:12:06', 0, NULL, NULL, 2, '2025-01-02 10:47:53', 6, '2025-01-03 20:12:06', 3);
INSERT INTO `leave_request` VALUES (9, 7, '文浩州', 3, '2025-06-17', '2025-06-17', '需参加公司外部活动', 0, NULL, NULL, 0, NULL, NULL, 0, '2025-01-02 10:47:54', 7, NULL, NULL);
INSERT INTO `leave_request` VALUES (10, 4, '蒋银辉', 2, '2025-06-17', '2025-06-17', '病假', 1, 3, '2025-01-03 19:55:08', 1, 2, '2025-01-03 20:12:45', 1, '2025-01-03 17:56:33', 4, '2025-01-03 20:12:45', 2);
INSERT INTO `leave_request` VALUES (11, 4, '蒋银辉', 3, '2025-06-17', '2025-06-17', '出差', 1, 3, '2025-01-06 02:10:09', 1, 2, '2025-01-06 02:11:05', 1, '2025-01-06 02:06:11', 4, '2025-01-06 02:11:05', 2);
INSERT INTO `leave_request` VALUES (12, 7, '文浩州', 2, '2025-06-13', '2025-07-18', '3242', 0, NULL, NULL, 0, NULL, NULL, 0, '2025-06-17 18:18:30', 7, '2025-06-17 18:18:30', 7);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父菜单ID（支持多级菜单）',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '菜单名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `visible` tinyint NULL DEFAULT 0 COMMENT '菜单状态(0显示 1隐藏)',
  `status` tinyint NULL DEFAULT 0 COMMENT '菜单状态(0正常 1禁用)',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识( 如user:read )',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` tinyint NULL DEFAULT NULL COMMENT '显示顺序',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单类型（\'M\'-菜单 \'B\'-按钮）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, 0, '员工管理', '/employee', 'employee/EmployeeManage', 0, 0, 'ems:employee', 'User', 2, 'M', '2024-12-27 15:00:33', 1, '2024-12-27 15:00:33', 1);
INSERT INTO `menu` VALUES (2, 1, '员工列表', NULL, NULL, 0, 0, 'ems:employee:list', NULL, NULL, 'B', '2024-12-27 15:00:33', 1, '2024-12-27 15:00:33', 1);
INSERT INTO `menu` VALUES (3, 1, '添加员工', NULL, NULL, 0, 0, 'ems:employee:add', NULL, NULL, 'B', '2024-12-27 15:00:33', 1, '2024-12-27 15:00:33', 1);
INSERT INTO `menu` VALUES (4, 1, '编辑员工', NULL, NULL, 0, 0, 'ems:employee:edit', NULL, NULL, 'B', '2024-12-27 15:00:33', 1, '2024-12-27 15:00:33', 1);
INSERT INTO `menu` VALUES (5, 1, '删除员工', NULL, NULL, 0, 0, 'ems:employee:delete', NULL, NULL, 'B', '2024-12-27 15:00:33', 1, '2024-12-27 15:00:33', 1);
INSERT INTO `menu` VALUES (6, 0, '数据统计', '/dashboard', 'dashboard/index', 0, 0, 'ems:dashboard:view', 'DataLine', 1, 'M', '2024-12-27 17:58:58', 1, '2024-12-27 17:59:00', 1);
INSERT INTO `menu` VALUES (7, 0, '岗位管理', '/job', 'job/JobManage', 0, 0, 'ems:job', 'Briefcase', 3, 'M', '2024-12-27 22:12:47', 1, '2024-12-27 22:12:51', 1);
INSERT INTO `menu` VALUES (8, 7, '岗位列表', NULL, NULL, 0, 0, 'ems:job:list', NULL, NULL, 'B', '2024-12-27 22:16:29', 1, '2024-12-27 22:16:34', 1);
INSERT INTO `menu` VALUES (9, 7, '编辑岗位', NULL, NULL, 0, 0, 'ems:job:edit', NULL, NULL, 'B', '2024-12-27 22:16:30', 1, '2024-12-27 22:16:34', 1);
INSERT INTO `menu` VALUES (10, 7, '添加岗位', NULL, NULL, 0, 0, 'ems:job:add', NULL, NULL, 'B', '2024-12-27 22:16:31', 1, '2024-12-27 22:16:36', 1);
INSERT INTO `menu` VALUES (11, 7, '删除岗位', NULL, NULL, 0, 0, 'ems:job:delete', NULL, NULL, 'B', '2024-12-27 22:16:32', 1, '2024-12-27 22:16:36', 1);
INSERT INTO `menu` VALUES (12, 1, '导入员工', NULL, NULL, 0, 0, 'ems:employee:import', NULL, NULL, 'B', '2024-12-27 23:27:01', 1, '2024-12-27 23:27:10', 1);
INSERT INTO `menu` VALUES (13, 1, '导出员工', NULL, NULL, 0, 0, 'ems:employee:export', NULL, NULL, 'B', '2024-12-27 23:27:03', 1, '2024-12-27 23:27:13', 1);
INSERT INTO `menu` VALUES (14, 0, '调岗管理', '/jobTransfer', 'job/jobTransfer', 0, 0, 'ems:jobTransfer', 'Promotion', 4, 'M', '2024-12-27 23:35:32', 1, '2024-12-27 23:35:36', 1);
INSERT INTO `menu` VALUES (15, 14, '新增调岗', NULL, NULL, 0, 0, 'ems:jobTransfer:add', NULL, NULL, 'B', '2024-12-27 23:38:23', 1, '2024-12-27 23:38:29', 1);
INSERT INTO `menu` VALUES (16, 14, '查看详情', NULL, NULL, 0, 0, 'ems:jobTransfer:view', NULL, NULL, 'B', '2024-12-27 23:38:24', 1, '2024-12-27 23:38:30', 1);
INSERT INTO `menu` VALUES (17, 14, '删除申请', NULL, NULL, 0, 0, 'ems:jobTransfer:delete', NULL, NULL, 'B', '2024-12-27 23:38:25', 1, '2024-12-27 23:38:31', 1);
INSERT INTO `menu` VALUES (18, 14, '审批', NULL, NULL, 0, 0, 'ems:jobTransfer:approve', NULL, NULL, 'B', '2024-12-27 23:38:26', 1, '2024-12-27 23:38:31', 1);
INSERT INTO `menu` VALUES (19, 14, '导出', NULL, NULL, 0, 0, 'ems:jobTransfer:export', NULL, NULL, 'B', '2024-12-27 23:38:26', 1, '2024-12-27 23:38:32', 1);
INSERT INTO `menu` VALUES (20, 0, '权限管理', '/permission', '', 0, 0, 'ems:permission', 'Setting', 6, 'M', '2024-12-28 00:27:35', 1, '2025-01-01 01:45:04', 1);
INSERT INTO `menu` VALUES (21, 20, '角色管理', '/role', 'permission/RoleManage', 0, 0, 'ems:role', 'UserFilled', 1, 'M', '2024-12-28 00:29:44', 1, '2024-12-28 00:29:47', 1);
INSERT INTO `menu` VALUES (22, 21, '角色列表', NULL, NULL, 0, 0, 'ems:role:list', NULL, NULL, 'B', '2024-12-28 00:34:16', 1, '2024-12-28 00:34:20', 1);
INSERT INTO `menu` VALUES (23, 21, '添加角色', NULL, NULL, 0, 0, 'ems:role:add', NULL, NULL, 'B', '2024-12-28 00:34:17', 1, '2024-12-28 00:34:23', 1);
INSERT INTO `menu` VALUES (24, 21, '编辑角色', NULL, NULL, 0, 0, 'ems:role:edit', NULL, NULL, 'B', '2024-12-28 00:34:18', 1, '2024-12-28 00:34:22', 1);
INSERT INTO `menu` VALUES (25, 21, '删除角色', NULL, NULL, 0, 0, 'ems:role:delete', NULL, NULL, 'B', '2024-12-28 00:34:18', 1, '2024-12-28 00:34:25', 1);
INSERT INTO `menu` VALUES (26, 21, '分配权限', NULL, NULL, 0, 0, 'ems:role:permission', NULL, NULL, 'B', '2024-12-28 00:34:19', 1, '2024-12-28 00:34:26', 1);
INSERT INTO `menu` VALUES (27, 20, '菜单管理', '/menu', 'permission/MenuManage', 0, 0, 'ems:menu', 'Menu', 2, 'M', '2024-12-28 00:37:08', 1, '2024-12-28 00:37:16', 1);
INSERT INTO `menu` VALUES (28, 27, '菜单列表', NULL, NULL, 0, 0, 'ems:menu:list', NULL, NULL, 'B', '2024-12-28 00:39:21', 1, '2024-12-28 00:39:25', 1);
INSERT INTO `menu` VALUES (29, 27, '添加菜单', NULL, NULL, 0, 0, 'ems:menu:add', NULL, NULL, 'B', '2024-12-28 00:39:23', 1, '2024-12-28 00:39:26', 1);
INSERT INTO `menu` VALUES (30, 27, '编辑菜单', NULL, NULL, 0, 0, 'ems:menu:edit', NULL, NULL, 'B', '2024-12-28 00:39:23', 1, '2024-12-28 00:39:26', 1);
INSERT INTO `menu` VALUES (31, 27, '删除菜单', NULL, NULL, 0, 0, 'ems:menu:delete', NULL, NULL, 'B', '2024-12-28 00:39:24', 1, '2024-12-28 00:39:27', 1);
INSERT INTO `menu` VALUES (37, 0, '部门管理', '/employee/dept', 'employee/DeptManage', 0, 0, 'ems:dept', 'OfficeBuilding', 2, 'M', '2024-12-29 14:51:13', 1, '2024-12-29 14:51:21', 1);
INSERT INTO `menu` VALUES (38, 37, '部门列表', NULL, NULL, 0, 0, 'ems:dept:list', NULL, NULL, 'B', '2024-12-29 14:48:02', 1, '2024-12-29 14:48:09', 1);
INSERT INTO `menu` VALUES (39, 37, '添加部门', '', '', 0, 0, 'ems:dept:add', '', NULL, 'B', '2024-12-29 16:29:51', 1, '2024-12-29 16:29:51', 1);
INSERT INTO `menu` VALUES (40, 37, '编辑部门', '', '', 0, 0, 'ems:dept:edit', '', NULL, 'B', '2024-12-29 16:30:28', 1, '2024-12-29 16:30:28', 1);
INSERT INTO `menu` VALUES (41, 37, '删除部门', '', '', 0, 0, 'ems:dept:delete', '', NULL, 'B', '2024-12-29 16:30:58', 1, '2024-12-29 16:30:58', 1);
INSERT INTO `menu` VALUES (42, 0, '考勤管理', '/attendance', '', 0, 0, 'ems:attendance', 'Calendar', 5, 'M', '2025-01-01 01:26:55', 1, '2025-01-03 22:27:16', 1);
INSERT INTO `menu` VALUES (43, 42, '考勤记录', '/record', 'attendance/AttendanceRecord', 0, 0, 'ems:attendance:record', 'Clock', 2, 'M', '2025-01-01 01:26:55', 1, '2025-01-01 15:36:45', 1);
INSERT INTO `menu` VALUES (44, 42, '考勤统计', '/statistics', 'attendance/AttendanceStatistics', 0, 0, 'ems:attendance:statistics', 'DataLine', 3, 'M', '2025-01-01 01:26:55', 1, '2025-01-01 15:37:03', 1);
INSERT INTO `menu` VALUES (45, 43, '考勤列表', '', '', 0, 0, 'ems:attendance:list', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (46, 43, '添加考勤', '', '', 0, 0, 'ems:attendance:add', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (47, 43, '编辑考勤', '', '', 0, 0, 'ems:attendance:edit', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (48, 43, '删除考勤', '', '', 0, 0, 'ems:attendance:delete', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (49, 43, '异常处理', '', '', 0, 0, 'ems:attendance:handle', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (50, 0, '请假管理', '/leave', '', 0, 0, 'ems:leave', 'Timer', 5, 'M', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (52, 58, '请假列表', '', '', 0, 0, 'ems:leave:list', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (53, 58, '添加请假', '', '', 0, 0, 'ems:leave:add', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (54, 58, '编辑请假', '', '', 0, 0, 'ems:leave:edit', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (55, 58, '删除请假', '', '', 0, 0, 'ems:leave:delete', '', NULL, 'B', '2025-01-01 01:26:55', 1, '2025-01-01 01:26:55', 1);
INSERT INTO `menu` VALUES (57, 42, '我的考勤', '/my', 'attendance/MyAttendance', 0, 0, 'ems:attendance:my', 'UserFilled', 1, 'M', '2025-01-01 15:30:22', 1, '2025-01-01 15:36:55', 1);
INSERT INTO `menu` VALUES (58, 50, '请假记录', '/record', '/leave/LeaveRecord', 0, 0, 'ems:leave:list', 'Clock', 1, 'M', '2025-01-02 09:39:22', 1, '2025-01-02 09:39:26', 1);
INSERT INTO `menu` VALUES (59, 50, '请假审批', '/approve', '/leave/LeaveApprove', 0, 0, 'ems:leave:approve', 'Checked', 2, 'M', '2025-01-02 09:40:53', 1, '2025-01-02 09:40:56', 1);
INSERT INTO `menu` VALUES (62, 1, '修改角色', '', '', 0, 0, 'ems:employee:role', '', NULL, 'B', '2025-01-02 20:29:12', 1, '2025-01-02 20:29:12', 1);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色权限标识（如ADMIN）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 0 COMMENT '角色状态(0正常 1停用)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role__page`(`name` ASC, `role_key` ASC, `status` ASC) USING BTREE COMMENT '分页索引'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '系统管理员', 'admin', '系统管理员,拥有所有权限', 0, '2024-12-27 14:51:29', 1, '2024-12-28 23:33:29', 1);
INSERT INTO `role` VALUES (2, '人事经理', 'hr', '人事部门经理,管理员工信息', 0, '2024-12-27 14:51:30', 1, '2024-12-27 14:51:39', 1);
INSERT INTO `role` VALUES (3, '部门经理', 'manager', '部门经理,管理部门员工', 0, '2024-12-27 14:51:31', 1, '2024-12-27 14:51:40', 1);
INSERT INTO `role` VALUES (4, '普通员工', 'employer', '普通员工', 0, '2024-12-27 14:51:32', 1, '2024-12-27 14:51:41', 1);
INSERT INTO `role` VALUES (5, '董事长', '888', '老板', 1, '2024-12-28 23:34:54', 1, '2025-01-02 20:53:12', 1);

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `menu_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单id',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (1, 1);
INSERT INTO `role_menu` VALUES (1, 2);
INSERT INTO `role_menu` VALUES (1, 3);
INSERT INTO `role_menu` VALUES (1, 4);
INSERT INTO `role_menu` VALUES (1, 5);
INSERT INTO `role_menu` VALUES (1, 6);
INSERT INTO `role_menu` VALUES (1, 7);
INSERT INTO `role_menu` VALUES (1, 8);
INSERT INTO `role_menu` VALUES (1, 9);
INSERT INTO `role_menu` VALUES (1, 10);
INSERT INTO `role_menu` VALUES (1, 11);
INSERT INTO `role_menu` VALUES (1, 12);
INSERT INTO `role_menu` VALUES (1, 13);
INSERT INTO `role_menu` VALUES (1, 14);
INSERT INTO `role_menu` VALUES (1, 15);
INSERT INTO `role_menu` VALUES (1, 16);
INSERT INTO `role_menu` VALUES (1, 17);
INSERT INTO `role_menu` VALUES (1, 18);
INSERT INTO `role_menu` VALUES (1, 19);
INSERT INTO `role_menu` VALUES (1, 20);
INSERT INTO `role_menu` VALUES (1, 21);
INSERT INTO `role_menu` VALUES (1, 22);
INSERT INTO `role_menu` VALUES (1, 23);
INSERT INTO `role_menu` VALUES (1, 24);
INSERT INTO `role_menu` VALUES (1, 25);
INSERT INTO `role_menu` VALUES (1, 26);
INSERT INTO `role_menu` VALUES (1, 27);
INSERT INTO `role_menu` VALUES (1, 28);
INSERT INTO `role_menu` VALUES (1, 29);
INSERT INTO `role_menu` VALUES (1, 30);
INSERT INTO `role_menu` VALUES (1, 31);
INSERT INTO `role_menu` VALUES (1, 33);
INSERT INTO `role_menu` VALUES (1, 35);
INSERT INTO `role_menu` VALUES (1, 37);
INSERT INTO `role_menu` VALUES (1, 38);
INSERT INTO `role_menu` VALUES (1, 39);
INSERT INTO `role_menu` VALUES (1, 40);
INSERT INTO `role_menu` VALUES (1, 41);
INSERT INTO `role_menu` VALUES (1, 42);
INSERT INTO `role_menu` VALUES (1, 43);
INSERT INTO `role_menu` VALUES (1, 44);
INSERT INTO `role_menu` VALUES (1, 45);
INSERT INTO `role_menu` VALUES (1, 46);
INSERT INTO `role_menu` VALUES (1, 47);
INSERT INTO `role_menu` VALUES (1, 48);
INSERT INTO `role_menu` VALUES (1, 49);
INSERT INTO `role_menu` VALUES (1, 50);
INSERT INTO `role_menu` VALUES (1, 52);
INSERT INTO `role_menu` VALUES (1, 53);
INSERT INTO `role_menu` VALUES (1, 54);
INSERT INTO `role_menu` VALUES (1, 55);
INSERT INTO `role_menu` VALUES (1, 57);
INSERT INTO `role_menu` VALUES (1, 58);
INSERT INTO `role_menu` VALUES (1, 59);
INSERT INTO `role_menu` VALUES (1, 62);
INSERT INTO `role_menu` VALUES (2, 1);
INSERT INTO `role_menu` VALUES (2, 2);
INSERT INTO `role_menu` VALUES (2, 3);
INSERT INTO `role_menu` VALUES (2, 4);
INSERT INTO `role_menu` VALUES (2, 5);
INSERT INTO `role_menu` VALUES (2, 6);
INSERT INTO `role_menu` VALUES (2, 7);
INSERT INTO `role_menu` VALUES (2, 8);
INSERT INTO `role_menu` VALUES (2, 9);
INSERT INTO `role_menu` VALUES (2, 10);
INSERT INTO `role_menu` VALUES (2, 11);
INSERT INTO `role_menu` VALUES (2, 12);
INSERT INTO `role_menu` VALUES (2, 13);
INSERT INTO `role_menu` VALUES (2, 14);
INSERT INTO `role_menu` VALUES (2, 15);
INSERT INTO `role_menu` VALUES (2, 16);
INSERT INTO `role_menu` VALUES (2, 17);
INSERT INTO `role_menu` VALUES (2, 18);
INSERT INTO `role_menu` VALUES (2, 19);
INSERT INTO `role_menu` VALUES (2, 37);
INSERT INTO `role_menu` VALUES (2, 38);
INSERT INTO `role_menu` VALUES (2, 39);
INSERT INTO `role_menu` VALUES (2, 40);
INSERT INTO `role_menu` VALUES (2, 41);
INSERT INTO `role_menu` VALUES (2, 42);
INSERT INTO `role_menu` VALUES (2, 43);
INSERT INTO `role_menu` VALUES (2, 44);
INSERT INTO `role_menu` VALUES (2, 45);
INSERT INTO `role_menu` VALUES (2, 46);
INSERT INTO `role_menu` VALUES (2, 47);
INSERT INTO `role_menu` VALUES (2, 48);
INSERT INTO `role_menu` VALUES (2, 49);
INSERT INTO `role_menu` VALUES (2, 50);
INSERT INTO `role_menu` VALUES (2, 52);
INSERT INTO `role_menu` VALUES (2, 53);
INSERT INTO `role_menu` VALUES (2, 54);
INSERT INTO `role_menu` VALUES (2, 55);
INSERT INTO `role_menu` VALUES (2, 57);
INSERT INTO `role_menu` VALUES (2, 58);
INSERT INTO `role_menu` VALUES (2, 59);
INSERT INTO `role_menu` VALUES (2, 62);
INSERT INTO `role_menu` VALUES (3, 1);
INSERT INTO `role_menu` VALUES (3, 2);
INSERT INTO `role_menu` VALUES (3, 4);
INSERT INTO `role_menu` VALUES (3, 6);
INSERT INTO `role_menu` VALUES (3, 8);
INSERT INTO `role_menu` VALUES (3, 22);
INSERT INTO `role_menu` VALUES (3, 38);
INSERT INTO `role_menu` VALUES (3, 42);
INSERT INTO `role_menu` VALUES (3, 43);
INSERT INTO `role_menu` VALUES (3, 44);
INSERT INTO `role_menu` VALUES (3, 45);
INSERT INTO `role_menu` VALUES (3, 50);
INSERT INTO `role_menu` VALUES (3, 52);
INSERT INTO `role_menu` VALUES (3, 57);
INSERT INTO `role_menu` VALUES (3, 58);
INSERT INTO `role_menu` VALUES (3, 59);
INSERT INTO `role_menu` VALUES (4, 2);
INSERT INTO `role_menu` VALUES (4, 6);
INSERT INTO `role_menu` VALUES (4, 8);
INSERT INTO `role_menu` VALUES (4, 38);
INSERT INTO `role_menu` VALUES (4, 42);
INSERT INTO `role_menu` VALUES (4, 45);
INSERT INTO `role_menu` VALUES (4, 46);
INSERT INTO `role_menu` VALUES (4, 50);
INSERT INTO `role_menu` VALUES (4, 52);
INSERT INTO `role_menu` VALUES (4, 53);
INSERT INTO `role_menu` VALUES (4, 54);
INSERT INTO `role_menu` VALUES (4, 55);
INSERT INTO `role_menu` VALUES (4, 57);
INSERT INTO `role_menu` VALUES (4, 58);
INSERT INTO `role_menu` VALUES (5, 6);
INSERT INTO `role_menu` VALUES (5, 14);
INSERT INTO `role_menu` VALUES (5, 20);
INSERT INTO `role_menu` VALUES (5, 21);
INSERT INTO `role_menu` VALUES (5, 27);

SET FOREIGN_KEY_CHECKS = 1;
