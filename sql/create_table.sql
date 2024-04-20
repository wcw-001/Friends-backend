-- 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    userPassword varchar(512)                       not null comment '密码',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    profile      varchar(512)                       null comment '个人简介',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userIds       varchar(512)                       null comment '添加的好友',
    userStatus   int      default 0                 not null comment '状态 0 - 正
常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0
- 普通用户 1 - 管理员',
    userCode     varchar(512)                       null comment '用户编号',
    tags         varchar(1024)                      null comment '标签列表'
)
    comment '用户' engine = InnoDB;

-- 队伍表
create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    name   varchar(256)                   not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum    int      default 1                 not null comment '最大人数',
    expireTime    datetime  null comment '过期时间',
    userId            bigint comment '用户id',
    status    int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password varchar(512)                       null comment '密码',

    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';
-- 用户队伍关系表
create table user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId            bigint comment '用户id',
    teamId            bigint comment '队伍id',
    joinTime datetime  null comment '加入时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';
CREATE TABLE `message`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        tinyint(4)                                                    NULL DEFAULT NULL COMMENT '类型-1 点赞',
    `from_id`     bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息发送的用户id',
    `to_id`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息接收的用户id',
    `data`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
    `is_read`     tinyint(4)                                                    NULL DEFAULT 0 COMMENT '已读-0 未读 ,1 已读',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4)                                                    NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB

CREATE TABLE `follow`
(
    `id`             bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`        bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
    `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关注的用户id',
    `create_time`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`      tinyint(4)          NULL     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
CREATE TABLE `blog`
(
    `id`           bigint(20) UNSIGNED                                            NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      bigint(20) UNSIGNED                                            NOT NULL COMMENT '用户id',
    `title`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '标题',
    `images`       varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '图片，最多9张，多张以\",\"隔开',
    `content`      varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章',
    `liked_num`    int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '点赞数量',
    `comments_num` int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '评论数量',
    `create_time`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = COMPACT;
CREATE TABLE `blog_like`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `blog_id`     bigint(20) NOT NULL COMMENT '博文id',
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `create_time` datetime   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = COMPACT;
