#
# XXL-JOB v2.3.0
# Copyright (c) 2015-present, xuxueli.

drop database IF EXISTS `pet-job`;
CREATE DATABASE `pet-job` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

use `pet-job`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;




DROP TABLE IF EXISTS `atomic_algorithm`;

CREATE TABLE `atomic_algorithm` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `update_time` datetime NOT NULL COMMENT '修改时间',
                                    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '插件名称',
                                    `jobhandler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Jobhandler beanName',
                                    `state` tinyint(1) DEFAULT NULL COMMENT '状态值：0 禁用 1 启用',
                                    `block_strategy` tinyint(1) DEFAULT NULL COMMENT '阻塞处理策略：0：单机串行1：单机并行',
                                    `category_id` bigint(11) NOT NULL COMMENT '编目分类表id',
                                    `registry_id` int(11) DEFAULT NULL COMMENT '集群id',
                                    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '插件备注',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_tree_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='插件信息表';


--
-- Dumping data for table `atomic_algorithm`
--


--
-- Table structure for table `atomic_algorithm_param`
--

DROP TABLE IF EXISTS `atomic_algorithm_param`;

CREATE TABLE `atomic_algorithm_param` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `param_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名称',
                                          `param_description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数描述',
                                          `param_type` tinyint(4) NOT NULL COMMENT '参数类型：1 string 2 date  3 枚举',
                                          `dictionary_category_id` bigint(11) DEFAULT NULL COMMENT '枚举字典分组id',
                                          `algo_id` bigint(20) NOT NULL,
                                          `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `isshow` tinyint(1) NOT NULL COMMENT '0:不显示 1：显示',
                                          `isredo` tinyint(1) NOT NULL COMMENT '0:不是重做参数 1:重做参数',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `idx_algoid` (`algo_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='插件参数信息表';


--
-- Dumping data for table `atomic_algorithm_param`
--



--
-- Table structure for table `job_handle_log`
--

DROP TABLE IF EXISTS `job_handle_log`;

CREATE TABLE `job_handle_log` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `log_id` bigint(11) NOT NULL,
                                  `algo_id` bigint(20) DEFAULT NULL COMMENT '算法id',
                                  `execute_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '执行参数',
                                  `handle_start_time` datetime DEFAULT NULL COMMENT '开始处理时间',
                                  `handle_end_time` datetime DEFAULT NULL COMMENT '结束处理时间',
                                  `handle_cost_time` bigint(20) DEFAULT NULL COMMENT '耗时',
                                  `handle_code` int(11) DEFAULT NULL COMMENT '处理标识 200成功、500错误',
                                  `handle_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理信息',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `idx_logid` (`log_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='子任务日志表';


--
-- Dumping data for table `job_handle_log`
--



--
-- Table structure for table `job_param`
--

DROP TABLE IF EXISTS `job_param`;

CREATE TABLE `job_param` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `job_id` bigint(20) DEFAULT NULL COMMENT '任务id',
                             `param_id` bigint(20) NOT NULL COMMENT '算法参数id',
                             `param_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数值',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_jobId` (`job_id`) USING BTREE,
                             KEY `idx_paramId` (`param_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务参数表';


--
-- Dumping data for table `job_param`
--



--
-- Table structure for table `menu_category`
--

DROP TABLE IF EXISTS `menu_category`;

CREATE TABLE `menu_category` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `name` varchar(50) NOT NULL COMMENT '分类名称',
                                 `parent_id` bigint(11) NOT NULL COMMENT '父级id,没有父级,默认为0',
                                 `sort_id` int(11) NOT NULL COMMENT '排序id',
                                 `type` tinyint(4) NOT NULL COMMENT '类型： 1字典管理 2 插件分类 3资源管理 4 数据汇集 5 数据预处理 6 数据归档 7产品生产 8其他',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_pts` (`parent_id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='编目管理表';


--
-- Dumping data for table `menu_category`
--


--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;

CREATE TABLE `sequence` (
                            `category` varchar(50) DEFAULT NULL COMMENT '种类',
                            `next_val` bigint(20) DEFAULT NULL COMMENT '序列',
                            `version` bigint(20) DEFAULT NULL COMMENT '版本',
                            `step` bigint(20) DEFAULT NULL COMMENT '步调',
                            `update_date` datetime DEFAULT NULL COMMENT '更新时间',
                            `create_date` datetime DEFAULT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生成序列表';


--
-- Dumping data for table `sequence`
--

--
-- Table structure for table `xxl_job_group`
--

DROP TABLE IF EXISTS `xxl_job_group`;

CREATE TABLE `xxl_job_group` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
                                 `title` varchar(20) NOT NULL COMMENT '执行器名称',
                                 `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
                                 `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4;


--
-- Dumping data for table `xxl_job_group`
--


--
-- Table structure for table `xxl_job_info`
--

DROP TABLE IF EXISTS `xxl_job_info`;

CREATE TABLE `xxl_job_info` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
                                `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
                                `job_desc` varchar(255) NOT NULL,
                                `add_time` datetime DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                `author` varchar(64) DEFAULT NULL COMMENT '作者',
                                `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
                                `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
                                `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
                                `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
                                `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
                                `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
                                `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
                                `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
                                `glue_source` mediumtext COMMENT 'GLUE源代码',
                                `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
                                `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
                                `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
                                `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
                                `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
                                `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
                                `job_type` int(11) DEFAULT NULL COMMENT '任务类型',
                                `algo_id` bigint(20) DEFAULT NULL COMMENT '算法id',
                                `category_id` bigint(20) DEFAULT NULL COMMENT '分组类型',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;


--
-- Dumping data for table `xxl_job_info`
--


--
-- Table structure for table `xxl_job_lock`
--

DROP TABLE IF EXISTS `xxl_job_lock`;

CREATE TABLE `xxl_job_lock` (
                                `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
                                PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Dumping data for table `xxl_job_lock`
--



--
-- Table structure for table `xxl_job_log`
--

DROP TABLE IF EXISTS `xxl_job_log`;

CREATE TABLE `xxl_job_log` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
                               `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
                               `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
                               `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
                               `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
                               `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
                               `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
                               `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
                               `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
                               `trigger_msg` text COMMENT '调度-日志',
                               `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
                               `handle_code` int(11) NOT NULL COMMENT '执行-状态',
                               `handle_msg` text COMMENT '执行-日志',
                               `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
                               PRIMARY KEY (`id`),
                               KEY `I_trigger_time` (`trigger_time`),
                               KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Dumping data for table `xxl_job_log`
--


--
-- Table structure for table `xxl_job_log_report`
--

DROP TABLE IF EXISTS `xxl_job_log_report`;

CREATE TABLE `xxl_job_log_report` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
                                      `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
                                      `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
                                      `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4;


--
-- Dumping data for table `xxl_job_log_report`
--

--
-- Table structure for table `xxl_job_logglue`
--

DROP TABLE IF EXISTS `xxl_job_logglue`;

CREATE TABLE `xxl_job_logglue` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
                                   `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
                                   `glue_source` mediumtext COMMENT 'GLUE源代码',
                                   `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
                                   `add_time` datetime DEFAULT NULL,
                                   `update_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



DROP TABLE IF EXISTS `xxl_job_registry`;

CREATE TABLE `xxl_job_registry` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `registry_group` varchar(50) NOT NULL,
                                    `registry_key` varchar(255) NOT NULL,
                                    `registry_value` varchar(255) NOT NULL,
                                    `update_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



DROP TABLE IF EXISTS `xxl_job_user`;

CREATE TABLE `xxl_job_user` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `username` varchar(50) NOT NULL COMMENT '账号',
                                `password` varchar(50) NOT NULL COMMENT '密码',
                                `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
                                `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



