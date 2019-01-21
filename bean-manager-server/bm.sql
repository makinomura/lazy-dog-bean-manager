SET
NAMES
utf8mb4;
SET
FOREIGN_KEY_CHECKS
=
0;

-- ----------------------------
-- Table structure for application_t
-- ----------------------------
DROP TABLE IF EXISTS `application_t`;
CREATE TABLE `application_t`
(
  `id`       int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'APP名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for environment_t
-- ----------------------------
DROP TABLE IF EXISTS `environment_t`;
CREATE TABLE `environment_t`
(
  `id`               int(11) NOT NULL AUTO_INCREMENT,
  `environment_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for version_t
-- ----------------------------
DROP TABLE IF EXISTS `version_t`;
CREATE TABLE `version_t`
(
  `id`             int(11) NOT NULL AUTO_INCREMENT,
  `app_id`         int(11) NULL DEFAULT NULL COMMENT 'APP_ID',
  `environment_id` int(11) NULL DEFAULT NULL COMMENT '环境ID',
  `num`            int(11) NULL DEFAULT NULL COMMENT '序号',
  `version_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本名称',
  `publish_time`   datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS
=
1;
