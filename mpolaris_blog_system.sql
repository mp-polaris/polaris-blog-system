/*
 Navicat Premium Data Transfer

 Source Server         : aliyun
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 218.244.138.206:3306
 Source Schema         : mpolaris_blog_system

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 01/01/2021 22:02:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `category_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类ID',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '文章内容',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型（0表示富文本，1表示markdown）',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0表示已发布，1表示草稿，2表示删除）',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '摘要',
  `labels` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签',
  `cover` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面',
  `view_count` int(11) DEFAULT 0 COMMENT '阅读数量',
  `create_time` datetime(0) NOT NULL COMMENT '发布时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_article_on_user_id`(`user_id`) USING BTREE,
  INDEX `fk_category_article_on_category_id`(`category_id`) USING BTREE,
  CONSTRAINT `fk_user_article_on_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_article
-- ----------------------------
INSERT INTO `tb_article` VALUES ('794592365120585728', 'Linux必会的基本命令', '794586816966557696', '794592481311195136', '## Linux必会的基本命令\n\n```java\n# 进入根目录\ncd /\n# 创建文件夹 test\nmkdir test\n# 创建test.txt文件\ntouch test.txt\n# 解压v2.26.2.tar.gz\ntar -zxvf v2.26.2.tar.gz\n# 解压解压v2.26.2.tar.gz 到当前目录\ntar -zxvf v2.26.2.tar.gz ./\n#解压解压v2.26.2.tar.gz 到/usr/local/\ntar -zxvf v2.26.2.tar.gz  ./usr/local\n#查看ip\nip addr\n#分配一个ip地址\ndhclient\n#解压ruoyi-ui.zip\nunzip ruoyi-ui.zip\n#删除压缩文件ruoyi-ui.zip\nrm -rf ruoyi-ui.zip\n#查看所有文件，包括隐藏文件\nls -al \n#移动文件或目录\nmv \n#返回上一级目录\ncd ..\n# 当前目录\n./\n#显示当前所在目录\npwd\n#创建多级目录\nmkdir -p test1/test2/test3\n#删除目录,只能删除空文件夹\nrmdir test1\n#删除目录，不为空也能删除n\nrmdir -p test2\n#复制文件\ncp \n#tab键自动补全\n#查看网络配置\nifconfig\n#查看剩余内存\nfree -h\n\n```\n![GitHub.png](/portal/image/1609487059431_794591848273281024.png)', '1', '1', 'Linux必须要会的基本命令', 'Linux-操作系统-Shell-Java-Linux命令', '1609487059431_794591848273281024.png', 6, '2021-01-01 07:46:23', '2021-01-01 13:03:04');
INSERT INTO `tb_article` VALUES ('794675771078606848', '测试代码块', '794586816966557696', '794590813794336768', '## 测试代码块\n\n```java\npublic void test () {\n	system.out.println(\"hello world\");\n}\n```', '1', '1', '测试代码块', 'java-spring-vue-test-gg', '1609507044366_794675671166091264.png', 2, '2021-01-01 13:17:48', '2021-01-01 13:17:48');

-- ----------------------------
-- Table structure for tb_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `pinyin` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '拼音',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `order` int(11) NOT NULL COMMENT '顺序',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态：0表示不使用，1表示正常',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_category
-- ----------------------------
INSERT INTO `tb_category` VALUES ('794590714330611712', 'Vue', 'vue', 'vue技术交流', 1, '1', '2021-01-01 07:39:49', '2021-01-01 07:39:49');
INSERT INTO `tb_category` VALUES ('794590813794336768', 'SpringBoot', 'SpringBoot', 'SpringBoot技术交流', 2, '1', '2021-01-01 07:40:13', '2021-01-01 07:40:13');
INSERT INTO `tb_category` VALUES ('794592481311195136', 'Linux', 'Linux', 'Linux', 3, '1', '2021-01-01 07:46:50', '2021-01-01 07:46:50');

-- ----------------------------
-- Table structure for tb_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `parent_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '父内容',
  `article_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章ID',
  `comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论用户的ID',
  `user_avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评论用户的头像',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评论用户的名称',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0表示删除，1表示正常）',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_comment_on_user_id`(`user_id`) USING BTREE,
  INDEX `fk_article_comment_on_article_id`(`article_id`) USING BTREE,
  CONSTRAINT `fk_article_comment_on_article_id` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_comment_on_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_comment
-- ----------------------------
INSERT INTO `tb_comment` VALUES ('794628627869204480', '', '794592365120585728', '可以啊！', '794586816966557696', '/portal/image/1609492357817_794614071323262976.png', 'mpolaris', '1', '2021-01-01 10:10:28', '2021-01-01 10:10:28');

-- ----------------------------
-- Table structure for tb_daily_view_count
-- ----------------------------
DROP TABLE IF EXISTS `tb_daily_view_count`;
CREATE TABLE `tb_daily_view_count`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `view_count` int(11) NOT NULL DEFAULT 0 COMMENT '每天浏览量',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_friend_link
-- ----------------------------
DROP TABLE IF EXISTS `tb_friend_link`;
CREATE TABLE `tb_friend_link`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '友情链接名称',
  `logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '友情链接logo',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '友情链接',
  `order` int(11) NOT NULL DEFAULT 0 COMMENT '顺序',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '友情链接状态:0表示不可用，1表示正常',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_friend_link
-- ----------------------------
INSERT INTO `tb_friend_link` VALUES ('794590504124678144', 'GitHub', '1609486734679_794590486164668416.png', 'https://github.com/', 1, '1', '2021-01-01 07:38:59', '2021-01-01 07:38:59');

-- ----------------------------
-- Table structure for tb_image
-- ----------------------------
DROP TABLE IF EXISTS `tb_image`;
CREATE TABLE `tb_image`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路径',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储路径配置',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片类型',
  `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原名称',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0表示删除，1表正常）',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片分类',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_images_on_user_id`(`user_id`) USING BTREE,
  INDEX `path`(`path`) USING BTREE,
  INDEX `url`(`url`) USING BTREE,
  CONSTRAINT `fk_user_images_on_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_image
-- ----------------------------
INSERT INTO `tb_image` VALUES ('794589167139946496', '794586816966557696', '1609486420182_794589167139946496.png', '/root/polaris_blog/upload/2021_01_01/png/794589167139946496.png', 'image/png', '雪.png', '1', 'carousel', '2021-01-01 07:33:40', '2021-01-01 07:33:40');
INSERT INTO `tb_image` VALUES ('794589513199386624', '794586816966557696', '1609486502706_794589513199386624.png', '/root/polaris_blog/upload/2021_01_01/png/794589513199386624.png', 'image/png', '星空.png', '1', 'carousel', '2021-01-01 07:35:03', '2021-01-01 07:35:03');
INSERT INTO `tb_image` VALUES ('794590486164668416', '794586816966557696', '1609486734679_794590486164668416.png', '/root/polaris_blog/upload/2021_01_01/png/794590486164668416.png', 'image/png', 'file.png', '1', 'friend_link', '2021-01-01 07:38:55', '2021-01-01 07:38:55');
INSERT INTO `tb_image` VALUES ('794591848273281024', '794586816966557696', '1609487059431_794591848273281024.png', '/root/polaris_blog/upload/2021_01_01/png/794591848273281024.png', 'image/png', 'GitHub.png', '1', 'article', '2021-01-01 07:44:19', '2021-01-01 07:44:19');
INSERT INTO `tb_image` VALUES ('794614071323262976', '794586816966557696', '1609492357817_794614071323262976.png', '/root/polaris_blog/upload/2021_01_01/png/794614071323262976.png', 'image/png', 'file.png', '1', 'avatar', '2021-01-01 09:12:38', '2021-01-01 09:12:38');
INSERT INTO `tb_image` VALUES ('794664951162601472', '794586816966557696', '1609504488518_794664951162601472.jpg', '/root/polaris_blog/upload/2021_01_01/jpg/794664951162601472.jpg', 'image/jpeg', '公众号.jpg', '1', 'article', '2021-01-01 12:34:49', '2021-01-01 12:34:49');
INSERT INTO `tb_image` VALUES ('794666920317026304', '794586816966557696', '1609504958001_794666920317026304.jpg', '/root/polaris_blog/upload/2021_01_01/jpg/794666920317026304.jpg', 'image/jpeg', '微信.jpg', '1', 'article', '2021-01-01 12:42:38', '2021-01-01 12:42:38');
INSERT INTO `tb_image` VALUES ('794675671166091264', '794586816966557696', '1609507044366_794675671166091264.png', '/root/polaris_blog/upload/2021_01_01/png/794675671166091264.png', 'image/png', 'Snipaste_2021-01-01_21-17-18.png', '1', 'article', '2021-01-01 13:17:24', '2021-01-01 13:17:24');

-- ----------------------------
-- Table structure for tb_label
-- ----------------------------
DROP TABLE IF EXISTS `tb_label`;
CREATE TABLE `tb_label`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称',
  `count` int(11) NOT NULL DEFAULT 0 COMMENT '数量',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_label
-- ----------------------------
INSERT INTO `tb_label` VALUES ('794592370938085376', 'Linux', 1, '2021-01-01 07:46:24', '2021-01-01 07:46:24');
INSERT INTO `tb_label` VALUES ('794592370950668288', '操作系统', 1, '2021-01-01 07:46:24', '2021-01-01 07:46:24');
INSERT INTO `tb_label` VALUES ('794592370963251200', 'Shell', 1, '2021-01-01 07:46:24', '2021-01-01 07:46:24');
INSERT INTO `tb_label` VALUES ('794592370975834112', 'Java', 2, '2021-01-01 07:46:24', '2021-01-01 07:46:24');
INSERT INTO `tb_label` VALUES ('794592370984222720', 'Linux命令', 1, '2021-01-01 07:46:24', '2021-01-01 07:46:24');
INSERT INTO `tb_label` VALUES ('794675771334459392', 'spring', 1, '2021-01-01 13:17:48', '2021-01-01 13:17:48');
INSERT INTO `tb_label` VALUES ('794675771468677120', 'vue', 1, '2021-01-01 13:17:48', '2021-01-01 13:17:48');
INSERT INTO `tb_label` VALUES ('794675771477065728', 'test', 1, '2021-01-01 13:17:48', '2021-01-01 13:17:48');
INSERT INTO `tb_label` VALUES ('794675771485454336', 'gg', 1, '2021-01-01 13:17:48', '2021-01-01 13:17:48');

-- ----------------------------
-- Table structure for tb_looper
-- ----------------------------
DROP TABLE IF EXISTS `tb_looper`;
CREATE TABLE `tb_looper`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '轮播图标题',
  `order` int(11) NOT NULL DEFAULT 0 COMMENT '顺序',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态：0表示不可用，1表示正常',
  `target_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '目标URL',
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片地址',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_image_looper_on_url`(`image_url`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_looper
-- ----------------------------
INSERT INTO `tb_looper` VALUES ('794589277978624000', '雪', 1, '1', 'https://www.vcg.com/creative/', '/portal/image/1609486420182_794589167139946496.png', '2021-01-01 07:34:07', '2021-01-01 07:34:07');
INSERT INTO `tb_looper` VALUES ('794589527539712000', '星空', 2, '1', 'https://www.vcg.com/creative/', '/portal/image/1609486502706_794589513199386624.png', '2021-01-01 07:35:06', '2021-01-01 07:35:06');

-- ----------------------------
-- Table structure for tb_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_refresh_token`;
CREATE TABLE `tb_refresh_token`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `refresh_token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `token_key` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mobile_token_key` varchar(34) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_refesh_token_on_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `fk_user_refesh_token_on_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_setting
-- ----------------------------
DROP TABLE IF EXISTS `tb_setting`;
CREATE TABLE `tb_setting`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '键',
  `value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '值',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_setting
-- ----------------------------
INSERT INTO `tb_setting` VALUES ('794586817360822272', 'manager_account_init_state', '1', '2021-01-01 07:24:20', '2021-01-01 07:24:20');
INSERT INTO `tb_setting` VALUES ('794587072978485248', 'web_size_view_count', '1', '2021-01-01 07:25:21', '2021-01-01 07:25:21');
INSERT INTO `tb_setting` VALUES ('794589995888279552', 'web_size_title', 'Poalris博客', '2021-01-01 07:36:58', '2021-01-01 07:36:58');
INSERT INTO `tb_setting` VALUES ('794589995892473856', 'web_size_keywords', 'java,erp,crm,oa', '2021-01-01 07:36:58', '2021-01-01 07:36:58');
INSERT INTO `tb_setting` VALUES ('794589995938611200', 'web_size_description', '技术交流', '2021-01-01 07:36:58', '2021-01-01 07:36:58');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `roles` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像地址',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱地址',
  `sign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '签名',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态：0表示删除，1表示正常',
  `reg_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册ip',
  `login_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录Ip',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('794586816966557696', 'mpolaris', '$2a$10$5S.SUCJ2VtcpdhcLDIeQjeCEhVmtRdPgj/GrKZ5fCHMZS/xtioqdi', 'role_admin', '/portal/image/1609492357817_794614071323262976.png', 'polaris424@163.com', '整一个签名？', '1', '139.207.156.94', '139.207.156.94', '2021-01-01 07:24:20', '2021-01-01 09:12:41');
INSERT INTO `tb_user` VALUES ('794671522433204224', 'rose', '$2a$10$IiNBm8/EoZV4RNSENhWf6u06/tDk1dxw0l2gQ8VMkPRQPZ50ufmoe', 'role_normal', 'https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=289446672,1693173871&fm=26&gp=0.jpg', 'polaris424@foxmail.com', NULL, '1', '172.19.0.2', '172.19.0.2', '2021-01-01 13:00:55', '2021-01-01 13:00:55');

SET FOREIGN_KEY_CHECKS = 1;
