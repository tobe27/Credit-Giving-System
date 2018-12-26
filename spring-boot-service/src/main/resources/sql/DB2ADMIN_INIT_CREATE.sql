create table CUSTOMER_BLACK
(
  ID            BIGINT generated always as identity
    constraint CUSTOMER_BLACK_PK
      primary key,
  CUSTOMER_ID   BIGINT,
  CUSTOMER_NAME VARCHAR(20),
  HOUSEHOLD_ID  VARCHAR(20),
  ID_NUMBER     VARCHAR(18),
  REASON        VARCHAR(150),
  PHONE         VARCHAR(20),
  GRID_CODE     VARCHAR(20),
  GRID_NAME     VARCHAR(20),
  ORG_CODE      VARCHAR(20),
  ORG_NAME      VARCHAR(20),
  USER_ID       BIGINT,
  USER_NAME     VARCHAR(20),
  STATUS        VARCHAR(5),
  UPDATED_AT    BIGINT not null,
  CREATED_AT    BIGINT not null
) in griddataspace1 index in gridindexspace1;

create table CUSTOMER_GREY
(
  ID            BIGINT generated always as identity
    constraint CUSTOMER_GREY_PK
      primary key,
  CUSTOMER_ID   BIGINT,
  CUSTOMER_NAME VARCHAR(20),
  HOUSEHOLD_ID  VARCHAR(20),
  ID_NUMBER     VARCHAR(18) not null,
  REASON        VARCHAR(50),
  PHONE         VARCHAR(20),
  GRID_CODE     VARCHAR(20),
  GRID_NAME     VARCHAR(20),
  ORG_CODE      VARCHAR(20),
  ORG_NAME      VARCHAR(20),
  USER_ID       BIGINT,
  USER_NAME     VARCHAR(20),
  STATUS        VARCHAR(5),
  CREATED_AT    BIGINT      not null,
  UPDATED_AT    BIGINT      not null
) in griddataspace1 index in gridindexspace1;

create table CUSTOMER_IMAGE
(
  ID             BIGINT generated always as identity
    constraint CUSTOMER_IMAGE_PK
      primary key,
  CUSTOMER_NAME  VARCHAR(50),
  ID_NUMBER      VARCHAR(20) not null,
  GRID_CODE      VARCHAR(20) not null,
  ORIGINAL_NAME  VARCHAR(150),
  SYSTEM_NAME    VARCHAR(150),
  PATH           VARCHAR(200),
  UPLOAD_USER_ID BIGINT,
  TYPE           VARCHAR(10),
  DELETE_FLAG    VARCHAR(10),
  DELETE_PATH    VARCHAR(150),
  DELETE_TIME    BIGINT,
  CREATED_AT     BIGINT,
  UPDATED_AT     BIGINT
) in griddataspace1 index in gridindexspace1;

comment on table CUSTOMER_IMAGE is '客户影像资料表';

create table CUSTOMER_POVERTY
(
  ID               BIGINT generated always as identity
    constraint customer_poverty_pk
      primary key,
  CUSTOMER_ID      BIGINT,
  CUSTOMER_NAME VARCHAR(20),
  HOUSEHOLD_ID     VARCHAR(20),
  ID_NUMBER        VARCHAR(20),
  REASON           VARCHAR(150),
  PHONE            VARCHAR(20),
  GRID_CODE        VARCHAR(20),
  GRID_NAME        VARCHAR(20),
  ORG_CODE         VARCHAR(20),
  ORG_NAME         VARCHAR(20),
  USER_ID          BIGINT,
  USER_NAME        VARCHAR(20),
  STATUS           VARCHAR(5),
  CREATED_AT       BIGINT not null,
  UPDATED_AT       BIGINT not null
) in griddataspace1 index in gridindexspace1;

create table CUSTOMER_WHITE
(
  ID            BIGINT generated always as identity
    constraint CUSTOMER_WHITE_PK
      primary key,
  CUSTOMER_ID   BIGINT,
  CUSTOMER_NAME VARCHAR(20),
  ID_NUMBER     VARCHAR(18) not null,
  LIMIT         DECIMAL(10, 4),
  PHONE         VARCHAR(20),
  GRID_CODE     VARCHAR(20) not null,
  GRID_NAME     VARCHAR(20),
  ORG_CODE      VARCHAR(20) not null,
  ORG_NAME      VARCHAR(20),
  USER_ID       BIGINT      not null,
  USER_NAME     VARCHAR(20),
  STATUS        VARCHAR(5) default '0',
  CREATED_AT    BIGINT      not null,
  UPDATED_AT    BIGINT      not null,
  HOUSEHOLD_ID  VARCHAR(20)
) in griddataspace1 index in gridindexspace1;

create table DB_CUSTOMER_TAG_RELATION
(
  ID        BIGINT generated always as identity
    constraint PK_ID
      primary key,
  TAG_ID    BIGINT      not null,
  ID_NUMBER VARCHAR(18) not null
) in griddataspace1 index in gridindexspace1;

create table DB_ORG
(
  ID              BIGINT generated always as identity
    constraint PK_ID
      primary key,
  ORG_CODE        VARCHAR(20)  not null,
  CORE_ORG_CODE   VARCHAR(20)  not null,
  ORG_NAME        VARCHAR(200) not null,
  PARENT_ORG_CODE VARCHAR(20)  not null,
  CORP_ORG_CODE   VARCHAR(20),
  LEVEL           VARCHAR(2)   not null,
  REMARK          VARCHAR(400),
  STATUS          VARCHAR(2)   not null,
  CREATED_AT      BIGINT       not null,
  UPDATED_AT      BIGINT       not null
) in griddataspace1 index in gridindexspace1;

create unique index UK_CORE_ORG_CODE
  on DB_ORG (CORE_ORG_CODE);

create unique index UK_ORG_CODE
  on DB_ORG (ORG_CODE);

create unique index UK_ORG_NAME
  on DB_ORG (ORG_NAME);

create table DB_PERMISSION
(
  ID             BIGINT generated always as identity
    primary key,
  PERMISSION_CH  VARCHAR(20)  not null,
  PERMISSION_URL VARCHAR(100) not null,
  CREATED_AT     BIGINT       not null,
  UPDATED_AT     BIGINT       not null
) in griddataspace1 index in gridindexspace1;

create table DB_ROLE
(
  ID         BIGINT generated always as identity
    primary key,
  ROLE_EN    VARCHAR(20) not null,
  ROLE_CH    VARCHAR(40) not null,
  CREATED_AT BIGINT      not null,
  UPDATED_AT BIGINT      not null
) in griddataspace1 index in gridindexspace1;

comment on column DB_ROLE.ROLE_EN is '角色英文名';

comment on column DB_ROLE.ROLE_CH is '角色中文名';

create table DB_ROLE_PERMISSION_RELATION
(
  ID            BIGINT generated always as identity
    primary key,
  ROLE_ID       BIGINT not null,
  PERMISSION_ID BIGINT not null,
  CREATED_AT    BIGINT not null,
  UPDATED_AT    BIGINT not null
) in griddataspace1 index in gridindexspace1;

create table DB_TAG
(
  ID          BIGINT generated always as identity
    constraint PK_ID
      primary key,
  NAME        VARCHAR(20) not null,
  DESCRIPTION VARCHAR(40) not null,
  TYPE        VARCHAR(40) not null,
  REMARK      VARCHAR(80),
  CREATED_AT  BIGINT      not null,
  UPDATED_AT  BIGINT      not null
) in griddataspace1 index in gridindexspace1;

create unique index UK_NAME
  on DB_TAG (NAME);

create table DB_USER
(
  ID            BIGINT generated always as identity
    constraint PK_ID
      primary key,
  USERNAME      VARCHAR(20)  not null,
  PASSWORD      VARCHAR(100) not null,
  NAME          VARCHAR(40)  not null,
  WORK_ID       VARCHAR(20)  not null,
  ID_NUMBER     VARCHAR(18),
  GENDER        VARCHAR(2),
  CONTACT       VARCHAR(30),
  EMAIL         VARCHAR(50),
  ORG_CODE      VARCHAR(50)  not null,
  CORP_NAME     VARCHAR(20)  not null,
  STATUS        VARCHAR(2)   not null,
  LAST_LOGIN_AT BIGINT,
  CREATED_AT    BIGINT       not null,
  UPDATED_AT    BIGINT       not null
) in griddataspace1 index in gridindexspace1;

create unique index UK_USERNAME
  on DB_USER (USERNAME);

create table DB_USER_ROLE_RELATION
(
  ID         BIGINT generated always as identity
    primary key,
  USER_ID    BIGINT not null,
  ROLE_ID    BIGINT not null,
  CREATED_AT BIGINT not null,
  UPDATED_AT BIGINT not null
) in griddataspace1 index in gridindexspace1;


create table GRID_IMAGE
(
  ID            BIGINT generated always as identity
    primary key,
  GRID_CODE     VARCHAR(20),
  ORIGINAL_NAME VARCHAR(60),
  SYSTEM_NAME   VARCHAR(150),
  PATH          VARCHAR(255),
  USER_ID       BIGINT,
  TYPE          VARCHAR(10),
  STATUS        VARCHAR(10) default '1' not null,
  DELETE_PATH   VARCHAR(255),
  DELETE_TIME   BIGINT,
  CREATED_AT    BIGINT                  not null,
  UPDATED_AT    BIGINT                  not null,
  COMMENT       VARCHAR(255),
  IMAGE_NUM     VARCHAR(40)
) in griddataspace1 index in gridindexspace1;

create table GRID_INFO
(
  ID                BIGINT generated always as identity
    primary key,
  GRID_CODE         VARCHAR(100),
  GRID_NAME         VARCHAR(100),
  ORG_CODE          VARCHAR(20),
  GRID_TYPE         VARCHAR(5),
  DESCRIPTION       VARCHAR(255),
  USER_ID           BIGINT                  not null,
  ASSIST_MANAGER    VARCHAR(50),
  SUPERVISE_MANAGER VARCHAR(50),
  QR_CODE           VARCHAR(255),
  GRID_MAP          VARCHAR(255),
  CREATED_AT        BIGINT,
  UPDATED_AT        BIGINT,
  STATUS            VARCHAR(10) default '1' not null
) in griddataspace1 index in gridindexspace1;

create table GRID_REVIEW
(
  ID               BIGINT generated always as identity
    primary key,
  GRID_CODE        VARCHAR(100),
  GRID_NAME        VARCHAR(200),
  PHONE            VARCHAR(20),
  GRID_REVIEW_NAME VARCHAR(50),
  ID_NUMBER        VARCHAR(20),
  DUTIES           VARCHAR(20),
  DESCRIPTION      VARCHAR(255),
  ADDRESS          VARCHAR(255),
  CREATED_AT       BIGINT not null,
  UPDATED_AT       BIGINT not null,
  TYPE             VARCHAR(10),
  STATUS           VARCHAR(5) default '1'
) in griddataspace1 index in gridindexspace1;


create table USER_CONCLUSION
(
  ID              BIGINT generated always as identity
    primary key,
  NAME            VARCHAR(20),
  ID_NUMBER       VARCHAR(18) not null,
  HOUSEHOLD_ID    VARCHAR(20),
  IS_FAMILIAR     VARCHAR(4),
  NEGATIVE_REASON VARCHAR(40),
  REMARK          VARCHAR(900),
  OUT_WORK        VARCHAR(300),
  HOUSE_VALUE     VARCHAR(20),
  CAR_VALUE       VARCHAR(20),
  MAIN_BUSINESS   VARCHAR(100),
  SCALE           VARCHAR(100),
  INCOME          VARCHAR(20),
  PAYOUT          VARCHAR(20),
  CREDIT_AMOUNT   VARCHAR(20),
  BORROWER        VARCHAR(20),
  SURVEY_TYPE     VARCHAR(20),
  USER_ID         BIGINT,
  IS_VALID        VARCHAR(4),
  DATE            VARCHAR(20),
  CREATED_AT      BIGINT      not null,
  UPDATED_AT      BIGINT      not null
) in griddataspace1 index in gridindexspace1;

create table USER_CUSTOMER
(
  ID                   BIGINT generated always as identity
    constraint PK_ID
      primary key,
  CREDIT_ID            VARCHAR(20),
  NAME                 VARCHAR(40) not null,
  TYPE                 VARCHAR(20) not null,
  ID_TYPE              VARCHAR(20) not null,
  ID_NUMBER            VARCHAR(18) not null,
  SIGN_ORG             VARCHAR(40),
  IS_LONG_TERM         VARCHAR(4),
  SIGN_DATE            VARCHAR(20),
  DUE_DATE             VARCHAR(20),
  DISTRICT_ADDRESS     VARCHAR(200),
  BIRTHDAY             VARCHAR(20),
  GENDER               VARCHAR(4),
  DETAIL_ADDRESS       VARCHAR(200),
  POSTCODE             VARCHAR(6),
  NATIVE_ADDRESS       VARCHAR(200),
  PHONE_NUMBER         VARCHAR(11),
  NATIONALITY          VARCHAR(2),
  NATION               VARCHAR(2),
  POLITICS_STATUS      VARCHAR(2),
  EDUCATION_BACKGROUND VARCHAR(2),
  PHYSICAL_CONDITION   VARCHAR(2),
  MARITAL_STATUS       VARCHAR(2),
  SPOUSE_NAME          VARCHAR(40),
  SPOUSE_ID_NUMBER     VARCHAR(18),
  SPOUSE_PHONE_NUMBER  VARCHAR(11),
  SPOUSE_COMPANY_NAME  VARCHAR(20),
  CAREER_TYPE          VARCHAR(2),
  TAX_TYPE             VARCHAR(40),
  MAIN_BUSINESS        VARCHAR(40),
  INDUSTRY_TYPE        VARCHAR(60),
  IS_STAFF             VARCHAR(4),
  IS_STOCKHOLDER       VARCHAR(4),
  IS_CIVIL_SERVANT     VARCHAR(4),
  IS_HOUSE_OWNER       VARCHAR(4),
  HOUSEHOLD_ID         VARCHAR(20),
  COMPANY_NAME         VARCHAR(40),
  WORK_YEAR            VARCHAR(2),
  COMPANY_ADDRESS      VARCHAR(200),
  IS_FARMER            VARCHAR(4),
  IS_BUY_HOME          VARCHAR(4),
  CELL_NAME            VARCHAR(40),
  CELL_ADDRESS         VARCHAR(200),
  LIVING_CONDITION     VARCHAR(40),
  IS_BUSINESS_OWNER    VARCHAR(4),
  IS_BORROWER          VARCHAR(4),
  GRID_CODE            VARCHAR(20),
  CUSTOMER_MANAGER     VARCHAR(20),
  SUPPORT_MANAGER      VARCHAR(20),
  MAIN_ORG_NAME        VARCHAR(200),
  REGISTER_PERSON      VARCHAR(20),
  LAST_MODIFY_PERSON   VARCHAR(20),
  STATUS               VARCHAR(2)  not null,
  CREATED_AT           BIGINT      not null,
  UPDATED_AT           BIGINT      not null,
  INTERVIEW_ID         BIGINT,
  AMOUNT               VARCHAR(18),
  VALID_TIME           INTEGER default 0,
  IS_CONCLUDED         VARCHAR(4),
  RELATIONSHIP         VARCHAR(20),
  AGE                  INTEGER
) in griddataspace1 index in gridindexspace1;

create unique index UK_ID_NUMBER
  on USER_CUSTOMER (ID_NUMBER);

create table USER_RESIDENT
(
  ID           BIGINT generated always as identity
    primary key,
  NAME         VARCHAR(20),
  ID_NUMBER    VARCHAR(20) not null
    unique,
  CONTACT      VARCHAR(20),
  COUNTY       VARCHAR(20),
  TOWNSHIP     VARCHAR(20),
  VILLAGE      VARCHAR(20),
  GROUP        VARCHAR(20),
  HOUSEHOLD_ID VARCHAR(20),
  RELATIONSHIP VARCHAR(20),
  IS_IN_LIST   VARCHAR(20),
  REMARK       VARCHAR(200),
  USER_ID      BIGINT,
  USER_NAME    VARCHAR(20),
  ORG_NAME     VARCHAR(200),
  GRID_CODE    VARCHAR(20),
  CREATED_AT   BIGINT      not null,
  UPDATED_AT   BIGINT      not null
) in griddataspace1 index in gridindexspace1;

create table USER_SURVEY
(
  ID              BIGINT generated always as identity
    primary key,
  NAME            VARCHAR(20),
  ID_NUMBER       VARCHAR(18) not null,
  HOUSEHOLD_ID    VARCHAR(20),
  IS_FAMILIAR     VARCHAR(4),
  NEGATIVE_REASON VARCHAR(40),
  REMARK          VARCHAR(300),
  OUT_WORK        VARCHAR(100),
  HOUSE_VALUE     VARCHAR(20),
  CAR_VALUE       VARCHAR(20),
  MAIN_BUSINESS   VARCHAR(40),
  SCALE           VARCHAR(20),
  INCOME          VARCHAR(20),
  PAYOUT          VARCHAR(20),
  CREDIT_AMOUNT   VARCHAR(20),
  BORROWER        VARCHAR(20),
  SURVEY_TYPE     VARCHAR(20),
  SENATOR         VARCHAR(20),
  IS_VALID        VARCHAR(4),
  DATE            VARCHAR(20),
  CREATED_AT      BIGINT      not null,
  UPDATED_AT      BIGINT      not null
) in griddataspace1 index in gridindexspace1;
