--��ʼ����ǩ
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('������', '������', 'ϵͳ����ǩ', '������', 1544784588758, 1544784588758);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('������', '������', 'ϵͳ����ǩ', '������', 1544784613762, 1544784613762);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('ƶ����', 'ƶ����', 'ϵͳ����ǩ', 'ƶ�໧', 1544784639547, 1544784639547);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('������', '������', 'ϵͳ����ǩ', '������', 1544784660891, 1544784660891);
INSERT INTO DB_TAG (NAME, DESCRIPTION, TYPE, REMARK, CREATED_AT, UPDATED_AT) VALUES ('������', '������', 'ϵͳ����ǩ', '������', 1544784660892, 1544784660892);

--��ʼ����ɫ
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ( 'manager', '�ͻ�����', 1544756042943, 1544756042943);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subpresident', '֧�г�', 1544756062642, 1544756062642);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subsearch', '֧�в�ѯԱ', 1544756069720, 1544756069720);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('subadmin', '���˻�������Ա', 1544756076254, 1544756076254);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('search', '���в�ѯԱ', 1544756086378, 1544756086378);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('president', '���й�����Ա', 1544756094141, 1544756094141);
INSERT INTO DB_ROLE (ROLE_EN, ROLE_CH, CREATED_AT, UPDATED_AT) VALUES ('admin', '��������Ա', 1544756102514, 1544756102514);


insert into task_status values('touch-task',   to_char(current timestamp, 'yyyymmdd'), 'notstart', 'by ningdb @ 20181106', 'N', 'Y');



--������

delete from DB_ORG where level in ('0','1');

-- ����������
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('1409', '14', '�˳���ũ�����ú�����������', '', '0', '1', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));
-- ���������˻���
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140901', '652000', 'ɽ���˳�ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));		 
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140902', '653000', 'ɽ������ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));		 
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140903', '654000', 'ɽ���ǳ�ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140904', '655102', 'ɽ�����ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140905', '656000', 'ɽ������ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140906', '657000', 'ɽ�����ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140907', '658000', 'ɽ���ɽũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140908', '659000', 'ɽ���ӽ�ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140909', '660000', 'ɽ����ϲũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140910', '661000', 'ɽ������ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140911', '662000', 'ɽ�����ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140912', '663000', 'ɽ��ƽ½ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	
insert into  DB_ORG(org_code, core_org_code, org_name, parent_org_code, corp_org_code, level, remark, status, created_at, updated_at)
values ('140913', '664000', 'ɽ��ԫ��ũ����ҵ���йɷ����޹�˾', '1409', '', '2', '��ʼ������', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));	


--�û�-���������Ա
delete from DB_USER where USERNAME like 'LS%';

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('LS140900', 'b12a532eb751ef76be193488bba7ed2a', '�˳�������ϵͳ����Ա', 'LS140900','1409', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));


--��ɫ: ���������Ա
delete from DB_USER_ROLE_RELATION where user_id in (select id from DB_USER where USERNAME like 'LS%');


INSERT INTO DB_USER_ROLE_RELATION (USER_ID, ROLE_ID, CREATED_AT, UPDATED_AT) VALUES (1, 7, 1544177090265, 1544177090265);


--�û�-���˻�������Ա
delete DB_USER where USERNAME like 'FR%'

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140901', 'b12a532eb751ef76be193488bba7ed2a', '�˳�ũ����ϵͳ����Ա', 'FR140901','140901', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140902', 'b12a532eb751ef76be193488bba7ed2a', '����ũ����ϵͳ����Ա', 'FR140902','140902', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140903', 'b12a532eb751ef76be193488bba7ed2a', '�ǳ�ũ����ϵͳ����Ա', 'FR140903','140903', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140904', 'b12a532eb751ef76be193488bba7ed2a', '���ũ����ϵͳ����Ա', 'FR140904','140904', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140905', 'b12a532eb751ef76be193488bba7ed2a', '����ũ����ϵͳ����Ա', 'FR140905','140905', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140906', 'b12a532eb751ef76be193488bba7ed2a', '���ũ����ϵͳ����Ա', 'FR140906','140906', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140907', 'b12a532eb751ef76be193488bba7ed2a', '�ɽũ����ϵͳ����Ա', 'FR140907','140907', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140908', 'b12a532eb751ef76be193488bba7ed2a', '�ӽ�ũ����ϵͳ����Ա', 'FR140908','140908', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140909', 'b12a532eb751ef76be193488bba7ed2a', '��ϲũ����ϵͳ����Ա', 'FR140909','140909', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140910', 'b12a532eb751ef76be193488bba7ed2a', '����ũ����ϵͳ����Ա', 'FR140910','140910', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140911', 'b12a532eb751ef76be193488bba7ed2a', '���ũ����ϵͳ����Ա', 'FR140911','140911', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140912', 'b12a532eb751ef76be193488bba7ed2a', 'ƽ½ũ����ϵͳ����Ա', 'FR140912','140912', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

insert into DB_USER (USERNAME, PASSWORD, NAME, work_id, ORG_CODE, corp_name, STATUS, LAST_LOGIN_AT, CREATED_AT, UPDATED_AT)
VALUES ('FR140913', 'b12a532eb751ef76be193488bba7ed2a', 'ԫ��ũ����ϵͳ����Ա', 'FR140913','140913', '', '1', cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint), cast(char('1544928601624') as bigint));

--��ɫ�����˻�������Ա
delete from DB_USER_ROLE_RELATION where user_id in (select id from DB_USER where USERNAME like 'FR%');

insert into DB_USER_ROLE_RELATION(user_id, ROLE_ID, CREATED_AT, UPDATED_AT)
select id as user_id, cast(char('4') as int) as role_id, cast(char('1544928601624') as bigint) as CREATED_AT, cast(char('1544928601624') as bigint) as UPDATED_AT from DB_USER where username like 'FR%'
