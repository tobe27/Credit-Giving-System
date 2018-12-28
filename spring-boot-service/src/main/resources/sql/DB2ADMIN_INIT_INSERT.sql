--初始化标签
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('黑名单', '黑名单', '系统级标签', '黑名单', 1544784588758, 1544784588758);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('灰名单', '灰名单', '系统级标签', '灰名单', 1544784613762, 1544784613762);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('贫困户', '贫困户', '系统级标签', '贫苦户', 1544784639547, 1544784639547);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('蓝名单', '蓝名单', '系统级标签', '蓝名单', 1544784660891, 1544784660891);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('白名单', '白名单', '系统级标签', '白名单', 1544784660892, 1544784660892);

--初始化角色
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ( 'manager', '客户经理', 1544756042943, 1544756042943);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subpresident', '支行长', 1544756062642, 1544756062642);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subsearch', '支行查询员', 1544756069720, 1544756069720);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subadmin', '法人机构管理员', 1544756076254, 1544756076254);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('search', '总行查询员', 1544756086378, 1544756086378);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('president', '总行管理人员', 1544756094141, 1544756094141);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('admin', '超级管理员', 1544756102514, 1544756102514);


--机构：

delete from DB_ORG where level in ('0','1');

-- 机构：联社
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('1409', '14', '运城市农村信用合作社联合社', '', '0', '1', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));
-- 机构：法人机构
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140901', '652000', '山西运城农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));		 
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140902', '653000', '山西永济农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));		 
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140903', '654000', '山西芮城农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140904', '655102', '山西临猗农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140905', '656000', '山西万荣农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140906', '657000', '山西新绛农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140907', '658000', '山西稷山农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140908', '659000', '山西河津农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140909', '660000', '山西闻喜农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140910', '661000', '山西夏县农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140911', '662000', '山西绛县农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140912', '663000', '山西平陆农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140913', '664000', '山西垣曲农村商业银行股份有限公司', '1409', '', '2', '初始化数据', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	


--用户-市联社管理员
delete from DB_USER where USERNAME like 'LS%';

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('LS140900', 'b12a532eb751ef76be193488bba7ed2a', '运城市联社系统管理员', 'LS140900','1409', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));


--角色: 市联社管理员
delete from DB_USER_ROLE_RELATION where user_id in (select id from DB_USER where USERNAME like 'LS%');

insert into DB_USER_ROLE_RELATION(user_id, ROLE_ID, CREATED_AT, UPDATED_AT)
select id as user_id, cast(char('7') as int) as role_id, cast(char('1544928601624') as bigint) as CREATED_AT, cast(char('1544928601624') as bigint) as UPDATED_AT from DB_USER where username like 'LS%'


--用户-法人机构管理员
delete DB_USER where USERNAME like 'FR%'

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140901', 'b12a532eb751ef76be193488bba7ed2a', '运城农商行系统管理员', 'FR140901','140901', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140902', 'b12a532eb751ef76be193488bba7ed2a', '永济农商行系统管理员', 'FR140902','140902', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140903', 'b12a532eb751ef76be193488bba7ed2a', '芮城农商行系统管理员', 'FR140903','140903', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140904', 'b12a532eb751ef76be193488bba7ed2a', '临猗农商行系统管理员', 'FR140904','140904', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140905', 'b12a532eb751ef76be193488bba7ed2a', '万荣农商行系统管理员', 'FR140905','140905', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140906', 'b12a532eb751ef76be193488bba7ed2a', '新绛农商行系统管理员', 'FR140906','140906', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140907', 'b12a532eb751ef76be193488bba7ed2a', '稷山农商行系统管理员', 'FR140907','140907', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140908', 'b12a532eb751ef76be193488bba7ed2a', '河津农商行系统管理员', 'FR140908','140908', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140909', 'b12a532eb751ef76be193488bba7ed2a', '闻喜农商行系统管理员', 'FR140909','140909', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140910', 'b12a532eb751ef76be193488bba7ed2a', '夏县农商行系统管理员', 'FR140910','140910', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140911', 'b12a532eb751ef76be193488bba7ed2a', '绛县农商行系统管理员', 'FR140911','140911', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140912', 'b12a532eb751ef76be193488bba7ed2a', '平陆农商行系统管理员', 'FR140912','140912', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140913', 'b12a532eb751ef76be193488bba7ed2a', '垣曲农商行系统管理员', 'FR140913','140913', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

--角色：法人机构管理员
delete from DB_USER_ROLE_RELATION where user_id in (select id from DB_USER where USERNAME like 'FR%');

insert into DB_USER_ROLE_RELATION(user_id, ROLE_ID, CREATED_AT, UPDATED_AT)
select id as user_id, cast(char('4') as int) as role_id, cast(char('1544928601624') as bigint) as CREATED_AT, cast(char('1544928601624') as bigint) as UPDATED_AT from DB_USER where username like 'FR%'
