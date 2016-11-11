/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50716
Source Host           : 127.0.0.1:3306
Source Database       : essh

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2016-11-11 16:24:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_base_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_base_organ`;
CREATE TABLE `t_base_organ` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `CODE` varchar(36) DEFAULT NULL,
  `FAX` varchar(64) DEFAULT NULL,
  `MANAGER_USER_ID` bigint(20) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `ORDER_NO` int(11) DEFAULT NULL,
  `PHONE` varchar(64) DEFAULT NULL,
  `SYS_CODE` varchar(36) DEFAULT NULL,
  `TYPE` int(11) DEFAULT NULL,
  `PARENT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  KEY `FKFE2373CE3E535456` (`PARENT_ID`),
  CONSTRAINT `FKFE2373CE3E535456` FOREIGN KEY (`PARENT_ID`) REFERENCES `t_base_organ` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_organ
-- ----------------------------
INSERT INTO `t_base_organ` VALUES ('7', '2014-03-29 21:43:39', 'admin', '0', null, null, '0', '', '', '', null, '尔演科技', '1', '', '00', '0', null);
INSERT INTO `t_base_organ` VALUES ('8', '2014-03-29 21:43:51', 'admin', '0', null, null, '0', '', '', '', null, '软件部门', '2', '', '001', '1', '7');

-- ----------------------------
-- Table structure for t_base_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_base_resource`;
CREATE TABLE `t_base_resource` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CODE` varchar(36) DEFAULT NULL,
  `ICON` varchar(255) DEFAULT NULL,
  `ICON_CLS` varchar(255) DEFAULT NULL,
  `MARK_URL` varchar(2000) DEFAULT NULL,
  `NAME` varchar(20) NOT NULL,
  `ORDER_NO` int(11) DEFAULT NULL,
  `TYPE` int(11) DEFAULT NULL,
  `URL` varchar(255) DEFAULT NULL,
  `PARENT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKFCD7B111A886F349` (`PARENT_ID`),
  CONSTRAINT `FKFCD7B111A886F349` FOREIGN KEY (`PARENT_ID`) REFERENCES `t_base_resource` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_resource
-- ----------------------------
INSERT INTO `t_base_resource` VALUES ('1', null, null, '0', '2013-11-12 22:12:42', 'admin', '1', '', null, 'icon-application', '', '权限管理', '1', '0', '', null);
INSERT INTO `t_base_resource` VALUES ('2', null, null, '0', '2013-12-28 16:17:38', 'admin', '6', '', null, 'icon-folder', '', '资源管理', '2', '0', '/base/resource.action', '1');
INSERT INTO `t_base_resource` VALUES ('3', '2013-11-12 22:13:42', 'admin', '0', '2013-12-22 00:01:53', 'admin', '3', '', null, 'icon-group', '', '角色管理', '3', '0', '/base/role.action', '1');
INSERT INTO `t_base_resource` VALUES ('4', '2013-11-12 22:14:10', 'admin', '0', '2013-12-28 19:25:00', 'admin', '3', '', null, 'icon-group', '', '机构管理', '4', '0', '/base/organ.action', '1');
INSERT INTO `t_base_resource` VALUES ('5', '2013-11-12 22:14:28', 'admin', '0', '2013-12-28 19:19:03', 'admin', '1', '', null, 'icon-user', '', '用户管理', '5', '0', '/base/user.action', '1');
INSERT INTO `t_base_resource` VALUES ('6', '2013-11-12 22:14:54', 'admin', '0', '2013-11-12 22:15:18', 'admin', '1', '', null, 'icon-application', '', '系统配置', '6', '0', '', null);
INSERT INTO `t_base_resource` VALUES ('7', '2013-11-12 22:15:13', 'admin', '0', '2013-12-21 21:02:05', 'admin', '2', '', null, 'icon-book', '', '字典类型', '7', '0', '/sys/dictionary-type.action', '6');
INSERT INTO `t_base_resource` VALUES ('8', '2013-11-12 22:15:40', 'admin', '0', '2013-12-28 19:25:41', 'admin', '5', '', null, 'icon-ipod', '', '数据字典', '8', '0', '/sys/dictionary.action', '6');
INSERT INTO `t_base_resource` VALUES ('9', '2013-11-12 22:15:57', 'admin', '0', '2014-03-29 18:43:23', 'admin', '2', '', null, 'icon-bug', '', '内容管理', '9', '0', '/sys/bug.action', '6');
INSERT INTO `t_base_resource` VALUES ('11', '2013-11-16 23:45:54', 'admin', '0', '2013-12-21 19:57:45', 'admin', '5', 'bug:add', null, '', '/sys/bug!input*;/sys/bug!save*', '新增', '10', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('16', '2013-11-17 00:10:23', 'admin', '0', '2013-11-17 20:16:33', 'admin', '2', 'bug:edit', null, '', '/sys/bug!save*', '编辑', '11', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('17', '2013-11-17 00:11:05', 'admin', '0', '2013-11-17 20:16:38', 'admin', '2', 'bug:importExcel', null, '', '/sys/bug!importExcel*', 'Excel导入', '12', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('18', '2013-11-17 00:13:35', 'admin', '0', '2013-11-17 20:16:41', 'admin', '1', 'bug:exportExcel', null, '', '/sys/bug!exportExcel*', 'Excel导出', '13', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('19', '2013-11-17 00:14:32', 'admin', '0', '2013-11-17 20:16:45', 'admin', '1', 'bug:remove', null, '', '/sys/bug!delete*;/sys/bug!remove*', '批量删除', '14', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('20', '2013-11-17 00:46:09', 'eryan', '0', '2013-11-17 20:16:50', 'admin', '1', 'bug:view', null, '', '/sys/bug!view*', '查看', '15', '1', '', '9');
INSERT INTO `t_base_resource` VALUES ('21', '2013-12-08 17:26:38', 'admin', '0', '2013-12-08 17:27:03', 'admin', '1', '', null, 'icon-monitor', '/sys/log*', '日志管理', '16', '0', '/sys/log.action', '6');
INSERT INTO `t_base_resource` VALUES ('22', '2014-01-05 15:07:01', 'admin', '0', '2014-03-29 21:40:06', 'admin', '1', '', null, 'icon-application', '', '演示', '17', '0', '', null);
INSERT INTO `t_base_resource` VALUES ('23', '2014-01-05 15:08:02', 'admin', '0', '2014-03-29 21:40:14', 'admin', '4', '', null, '', '', '数据字典', '18', '0', '/fileRedirect.action?toPage=demo/dictionarydemo.jsp&amp;amp;name=ok', '22');
INSERT INTO `t_base_resource` VALUES ('24', '2014-03-29 21:40:36', 'admin', '0', '2014-03-29 21:43:05', 'admin', '3', '', null, '', '', 'Quartz Cron', '19', '0', '/fileRedirect.action?toPage=demo/cron.jsp', '22');
INSERT INTO `t_base_resource` VALUES ('25', '2014-03-29 21:41:31', 'admin', '0', '2014-03-29 21:42:42', 'admin', '2', '', null, '', '', 'easyui-my97', '20', '0', '/fileRedirect.action?toPage=demo/my97demo.jsp', '22');

-- ----------------------------
-- Table structure for t_base_role
-- ----------------------------
DROP TABLE IF EXISTS `t_base_role`;
CREATE TABLE `t_base_role` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CODE` varchar(36) DEFAULT NULL,
  `NAME` varchar(100) NOT NULL,
  `REMARK` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_base_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_base_role_resource`;
CREATE TABLE `t_base_role_resource` (
  `ROLE_ID` bigint(20) NOT NULL,
  `RESOURCE_ID` bigint(20) NOT NULL,
  KEY `FK99003E94CBF981E5` (`ROLE_ID`),
  KEY `FK99003E9476B5CD65` (`RESOURCE_ID`),
  CONSTRAINT `FK99003E9476B5CD65` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `t_base_resource` (`ID`),
  CONSTRAINT `FK99003E94CBF981E5` FOREIGN KEY (`ROLE_ID`) REFERENCES `t_base_role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_base_user
-- ----------------------------
DROP TABLE IF EXISTS `t_base_user`;
CREATE TABLE `t_base_user` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(64) DEFAULT NULL,
  `LOGIN_NAME` varchar(36) NOT NULL,
  `MOBILEPHONE` varchar(36) DEFAULT NULL,
  `NAME` varchar(36) DEFAULT NULL,
  `PASSWORD` varchar(64) NOT NULL,
  `SEX` int(11) DEFAULT NULL,
  `TEL` varchar(36) DEFAULT NULL,
  `DEFAULT_ORGANID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `LOGIN_NAME` (`LOGIN_NAME`),
  KEY `FKBDE2DA4E7AEFAE74` (`DEFAULT_ORGANID`),
  CONSTRAINT `FKBDE2DA4E7AEFAE74` FOREIGN KEY (`DEFAULT_ORGANID`) REFERENCES `t_base_organ` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_user
-- ----------------------------
INSERT INTO `t_base_user` VALUES ('1', null, null, '0', '2013-11-13 08:02:20', 'admin', '3', '', '', 'admin', null, '', '5f4dcc3b5aa765d61d8327deb882cf99', '2', '', null);

-- ----------------------------
-- Table structure for t_base_user_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_base_user_organ`;
CREATE TABLE `t_base_user_organ` (
  `USER_ID` bigint(20) NOT NULL,
  `ORGAN_ID` bigint(20) NOT NULL,
  KEY `FK1F9964C01162FD8F` (`ORGAN_ID`),
  KEY `FK1F9964C0712445C5` (`USER_ID`),
  CONSTRAINT `FK1F9964C01162FD8F` FOREIGN KEY (`ORGAN_ID`) REFERENCES `t_base_organ` (`ID`),
  CONSTRAINT `FK1F9964C0712445C5` FOREIGN KEY (`USER_ID`) REFERENCES `t_base_user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_user_organ
-- ----------------------------

-- ----------------------------
-- Table structure for t_base_user_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_base_user_resource`;
CREATE TABLE `t_base_user_resource` (
  `USER_ID` bigint(20) NOT NULL,
  `RESOURCE_ID` bigint(20) NOT NULL,
  KEY `FKD8C9C2DF712445C5` (`USER_ID`),
  KEY `FKD8C9C2DF76B5CD65` (`RESOURCE_ID`),
  CONSTRAINT `FKD8C9C2DF712445C5` FOREIGN KEY (`USER_ID`) REFERENCES `t_base_user` (`ID`),
  CONSTRAINT `FKD8C9C2DF76B5CD65` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `t_base_resource` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_user_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_base_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_base_user_role`;
CREATE TABLE `t_base_user_role` (
  `USER_ID` bigint(20) NOT NULL,
  `ROLE_ID` bigint(20) NOT NULL,
  KEY `FK2A5097C7CBF981E5` (`ROLE_ID`),
  KEY `FK2A5097C7712445C5` (`USER_ID`),
  CONSTRAINT `FK2A5097C7712445C5` FOREIGN KEY (`USER_ID`) REFERENCES `t_base_user` (`ID`),
  CONSTRAINT `FK2A5097C7CBF981E5` FOREIGN KEY (`ROLE_ID`) REFERENCES `t_base_role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_base_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_bug
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_bug`;
CREATE TABLE `t_sys_bug` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CONTENT` longtext,
  `TITLE` varchar(255) DEFAULT NULL,
  `TYPE` varchar(36) DEFAULT NULL,
  `COLOR` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_bug
-- ----------------------------
INSERT INTO `t_sys_bug` VALUES ('17', '2014-03-29 20:27:44', 'admin', '0', null, null, '0', '欢迎使用', '欢迎使用', 'bug000', 'red');

-- ----------------------------
-- Table structure for t_sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionary`;
CREATE TABLE `t_sys_dictionary` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CODE` varchar(36) DEFAULT NULL,
  `NAME` varchar(100) DEFAULT NULL,
  `ORDER_NO` int(11) DEFAULT NULL,
  `REMAK` varchar(100) DEFAULT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  `DICTIONARYTYPE_CODE` varchar(20) DEFAULT NULL,
  `PARENT_CODE` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CODE` (`CODE`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `CODE_2` (`CODE`),
  UNIQUE KEY `UK_o1tl2dpxs4ogjrihj5d0aetyy` (`CODE`),
  UNIQUE KEY `UK_a537d5dca8d3425ca9dda698d10` (`CODE`),
  UNIQUE KEY `UK_f5f933daadac4883853243aad12` (`CODE`),
  UNIQUE KEY `UK_e6c25999b7314e5181998f776ba` (`CODE`),
  UNIQUE KEY `UK_8f14db30926d45208b68602823a` (`CODE`),
  UNIQUE KEY `UK_60c6a856fe064bc68cfe0aee53b` (`CODE`),
  KEY `FK79C52CB373CC8B3F` (`DICTIONARYTYPE_CODE`),
  KEY `FK79C52CB3BD49F8CB` (`PARENT_CODE`),
  CONSTRAINT `FK79C52CB373CC8B3F` FOREIGN KEY (`DICTIONARYTYPE_CODE`) REFERENCES `t_sys_dictionarytype` (`CODE`),
  CONSTRAINT `FK79C52CB3BD49F8CB` FOREIGN KEY (`PARENT_CODE`) REFERENCES `t_sys_dictionary` (`CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionary
-- ----------------------------
INSERT INTO `t_sys_dictionary` VALUES ('3', '2014-03-29 17:49:10', 'admin', '0', null, null, '0', 'bug001', '功能性', '1', '', 'bug001', 'bug', null);
INSERT INTO `t_sys_dictionary` VALUES ('4', '2014-03-29 17:49:24', 'admin', '0', null, null, '0', 'bug002', '建议', '2', '', 'bug002', 'bug', null);
INSERT INTO `t_sys_dictionary` VALUES ('5', '2014-03-29 20:27:09', 'admin', '0', null, null, '0', 'bug000', '内部新闻', '3', '', 'bug000', 'bug', null);

-- ----------------------------
-- Table structure for t_sys_dictionarytype
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionarytype`;
CREATE TABLE `t_sys_dictionarytype` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CODE` varchar(20) DEFAULT NULL,
  `NAME` varchar(100) DEFAULT NULL,
  `ORDER_NO` int(11) DEFAULT NULL,
  `REMARK` varchar(255) DEFAULT NULL,
  `GROUP_CODE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CODE` (`CODE`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `CODE_2` (`CODE`),
  UNIQUE KEY `UK_9als5rp87ewbp9egotvj1j9rg` (`CODE`),
  UNIQUE KEY `UK_753186875d0243ee833be0fb462` (`CODE`),
  UNIQUE KEY `UK_2064ba05f4df4d2cb4dd86255b5` (`CODE`),
  UNIQUE KEY `UK_2281184778e548688543cc82330` (`CODE`),
  UNIQUE KEY `UK_551c03d742b847f8a4f1d44527e` (`CODE`),
  UNIQUE KEY `UK_02b721e6df5f42169f82ffab7bf` (`CODE`),
  KEY `FK8551226D4DC80EF0` (`GROUP_CODE`),
  CONSTRAINT `FK8551226D4DC80EF0` FOREIGN KEY (`GROUP_CODE`) REFERENCES `t_sys_dictionarytype` (`CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionarytype
-- ----------------------------
INSERT INTO `t_sys_dictionarytype` VALUES ('5', '2014-03-29 17:48:24', 'admin', '0', null, null, '0', 'system', '系统字典', '1', '', null);
INSERT INTO `t_sys_dictionarytype` VALUES ('6', '2014-03-29 17:48:52', 'admin', '0', null, null, '0', 'bug', 'bug类型', '2', '', 'system');

-- ----------------------------
-- Table structure for t_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_log`;
CREATE TABLE `t_sys_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(36) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER` varchar(36) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `ACTION` varchar(255) DEFAULT NULL,
  `ACTION_TIME` varchar(20) DEFAULT NULL,
  `IP` varchar(64) DEFAULT NULL,
  `LOGIN_NAME` varchar(36) DEFAULT NULL,
  `MODULE` varchar(36) DEFAULT NULL,
  `OPER_TIME` datetime DEFAULT NULL,
  `REMARK` varchar(255) DEFAULT NULL,
  `TYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_log
-- ----------------------------
INSERT INTO `t_sys_log` VALUES ('1', '2016-09-21 09:32:24', 'admin', '0', null, null, '0', '用户登录', '0', '0:0:0:0:0:0:0:1', 'admin', 'LoginAction-login', '2016-09-21 09:32:24', null, '0');
INSERT INTO `t_sys_log` VALUES ('2', '2016-10-02 16:15:40', 'admin', '0', null, null, '0', '用户登录', '0', '0:0:0:0:0:0:0:1', 'admin', 'LoginAction-login', '2016-10-02 16:15:40', null, '0');
INSERT INTO `t_sys_log` VALUES ('3', null, null, '0', null, null, '0', '用户非正常注销', '0', '0:0:0:0:0:0:0:1', 'admin', 'LoginAction-logout', '2016-10-03 09:50:08', null, '0');
INSERT INTO `t_sys_log` VALUES ('4', '2016-10-03 10:05:25', 'admin', '0', null, null, '0', '用户登录', '0', '0:0:0:0:0:0:0:1', 'admin', 'LoginAction-login', '2016-10-03 10:05:25', null, '0');
INSERT INTO `t_sys_log` VALUES ('5', '2016-11-07 13:42:16', 'admin', '0', null, null, '0', '用户登录', '0', '0:0:0:0:0:0:0:1', 'admin', 'LoginAction-login', '2016-11-07 13:42:16', null, '0');
SET FOREIGN_KEY_CHECKS=1;
