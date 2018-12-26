--初始化标签
INSERT INTO DB2ADMIN.DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('黑名单', '黑名单', '系统级标签', '黑名单', 1544784588758, 1544784588758);
INSERT INTO DB2ADMIN.DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('灰名单', '灰名单', '系统级标签', '灰名单', 1544784613762, 1544784613762);
INSERT INTO DB2ADMIN.DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('贫困户', '贫困户', '系统级标签', '贫困户', 1544784639547, 1544784639547);
INSERT INTO DB2ADMIN.DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('蓝名单', '蓝名单', '系统级标签', '蓝名单', 1544784660891, 1544784660891);
INSERT INTO DB2ADMIN.DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('白名单', '白名单', '系统级标签', '白名单', 1544784660892, 1544784660892);
--初始化用户
INSERT INTO DB2ADMIN.DB_USER (USERNAME, PASSWORD, NAME, WORK_ID, ID_NUMBER, GENDER, CONTACT, EMAIL, ORG_CODE, CORP_NAME, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT) VALUES ('root', 'b12a532eb751ef76be193488bba7ed2a', '超级管理员', '4901001', null, null, null, null, '1409', '3655789966', '1', null, 1544783262037, 1544783262037);
--初始化角色
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ( 'manager', '客户经理', 1544756042943, 1544756042943);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subpresident', '支行行长', 1544756062642, 1544756062642);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subsearch', '支行查询员', 1544756069720, 1544756069720);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subadmin', '法人机构管理员', 1544756076254, 1544756076254);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('search', '总行查询员', 1544756086378, 1544756086378);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('president', '总行管理人员', 1544756094141, 1544756094141);
INSERT INTO DB2ADMIN.DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('admin', '超级管理员', 1544756102514, 1544756102514);
--初始化用户角色关联
INSERT INTO DB2ADMIN.DB_USER_ROLE_RELATION (USER_ID, ROLE_ID, CREATED_AT, UPDATED_AT) VALUES (1, 7, 1544177090265, 1544177090265);
--初始化机构
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('1409', '1409', '运城市农村信用合作社联合社', '0', '1', '1', '运城市农村信用合作社联合社1', '1', 1544172835370, 1545289035924);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('140901', '652000', '山西运城农村商业银行股份有限公司', '1409', '0', '2', '1112221', '1', 1545187788965, 1545289218334);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('140903', '654000', '山西芮城农村商业银行股份有限公司', '1409', '', '2', '初始化数据11', '1', 1544928601624, 1545289268275);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('140905', '656000', '万荣县农村信用合作联社', '1409', '0', '2', '111122', '1', 1545143014524, 1545289400884);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500100', '656121', '万荣县农村信用合作联社城关信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1545288735598);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500200', '656131', '万荣县农村信用合作联社万泉信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1545287951566);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500300', '656141', '万荣县农村信用合作联社西村信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500400', '656151', '万荣县农村信用合作联社里望信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500500', '656161', '万荣县农村信用合作联社汉薛信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500600', '656171', '万荣县农村信用合作联社皇甫信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500700', '656181', '万荣县农村信用合作联社三文信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500800', '656191', '万荣县农村信用合作联社埝底信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090500900', '656211', '万荣县农村信用合作联社贾村信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501000', '656221', '万荣县农村信用合作联社高村信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501002', '656222', '万荣县农村信用合作联社闫井分社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501100', '656231', '万荣县农村信用合作联社王显信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501200', '656241', '万荣县农村信用合作联社南张信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501300', '656251', '万荣县农村信用合作联社通化信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501400', '656261', '万荣县农村信用合作联社裴庄信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501500', '656271', '万荣县农村信用合作联社光华信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501600', '656281', '万荣县农村信用合作联社荣河信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
INSERT INTO DB2ADMIN.DB_ORG ( ORG_CODE, CORE_ORG_CODE, ORG_NAME, PARENT_ORG_CODE, CORP_ORG_CODE, LEVEL, REMARK, STATUS, CREATED_AT, UPDATED_AT) VALUES ('14090501700', '656291', '万荣县农村信用合作联社宝井信用社', '140905', '', '3', '初始化数据', '1', 1544928601624, 1544928601624);
