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
    userStatus   int      default 0                 not null comment '状态 0 - 正
常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0
- 普通用户 1 - 管理员',
    userCode     varchar(512)                       null comment '用户编号',
    tags         varchar(1024)                      null comment '标签列表',
    friendIds       varchar(512)                       null comment '添加的好友'
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
    `fromId`     bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息发送的用户id',
    `toId`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息接收的用户id',
    `data`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
    `isRead`     tinyint(4)                                                    NULL DEFAULT 0 COMMENT '已读-0 未读 ,1 已读',
    `createTime` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint(4)                                                    NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;

CREATE TABLE `follow`
(
    `id`             bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`        bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
    `followUserId` bigint(20) UNSIGNED NOT NULL COMMENT '关注的用户id',
    `createTime`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`      tinyint(4)          NULL     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;
CREATE TABLE `blog`
(
    `id`           bigint(20) UNSIGNED                                            NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`      bigint(20) UNSIGNED                                            NOT NULL COMMENT '用户id',
    `title`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '标题',
    `images`       varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '图片，最多9张，多张以\",\"隔开',
    `content`      varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章',
    `likedNum`    int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '点赞数量',
    `commentsNum` int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '评论数量',
    `createTime`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = COMPACT;
CREATE TABLE `blog_like`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `blogId`     bigint(20) NOT NULL COMMENT '博文id',
    `userId`     bigint(20) NOT NULL COMMENT '用户id',
    `createTime` datetime   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime   NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = COMPACT;
CREATE TABLE `chat`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '聊天记录id',
    `fromId`     bigint(20)                                                    NOT NULL COMMENT '发送消息id',
    `toId`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '接收消息id',
    `text`        varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `chatType`   tinyint(4)                                                    NOT NULL COMMENT '聊天类型 1-私聊 2-群聊',
    `createTime` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP,
    `teamId`     bigint(20)                                                    NULL DEFAULT NULL,
    `isDelete`   tinyint(4)                                                    NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;
CREATE TABLE `friends`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '好友申请id',
    `fromId`     bigint(20)                                                    NOT NULL COMMENT '发送申请的用户id',
    `receiveId`  bigint(20)                                                    NULL     DEFAULT NULL COMMENT '接收申请的用户id ',
    `isRead`     tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '是否已读(0-未读 1-已读)',
    `status`      tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '申请状态 默认0 （0-未通过 1-已同意 2-已过期 3-不同意）',
    `createTime` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP,
    `isDelete`   tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '是否删除',
    `remark`      varchar(214) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '好友申请备注信息',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
