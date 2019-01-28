package com.example.service.resident;

import com.alibaba.fastjson.JSON;
import com.example.common.enums.DatePatternEnum;
import com.example.common.util.DateConvertUtil;
import com.example.common.util.StringUtil;
import com.example.service.black.CustomerBlackDOMapper;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.conclusion.ConclusionDOMapper;
import com.example.service.customer.*;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDO;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.interview.CustomerInterviewDO;
import com.example.service.interview.CustomerInterviewDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.poverty.CustomerPovertyDOMapper;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import com.example.service.thread.AsyncTaskCustomerSave;
import com.example.service.thread.AsyncTaskResidentSave;
import com.example.service.white.CustomerWhiteDOMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author CREATED BY L.C.Y on 2018/12/11
 */

@Service
public class ResidentDOServiceImpl implements ResidentDOService {
    @Resource
    private AsyncTaskResidentSave asyncTaskResidentSave;
    @Resource
    private AsyncTaskCustomerSave asyncTaskCustomerSave;
    private final ResidentDOMapper residentDOMapper;
    private final OrgDOMapper orgDOMapper;
    private final CustomerDOMapper customerDOMapper;
    private final CustomerGreyDOMapper customerGreyDOMapper;
    private final CustomerBlackDOMapper customerBlackDOMapper;
    private final CustomerPovertyDOMapper customerPovertyDOMapper;
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
    private final ODSCustomerDOMapper odsCustomerDOMapper;
    private final GridInfoDOMapper gridInfoDOMapper;
    private final CustomerInterviewDOMapper customerInterviewDOMapper;
    private final CustomerWhiteDOMapper customerWhiteDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final ConclusionDOMapper conclusionDOMapper;

    @Autowired
    public ResidentDOServiceImpl(ResidentDOMapper residentDOMapper, OrgDOMapper orgDOMapper, CustomerDOMapper customerDOMapper, CustomerGreyDOMapper customerGreyDOMapper, CustomerBlackDOMapper customerBlackDOMapper, CustomerPovertyDOMapper customerPovertyDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper, ODSCustomerDOMapper odsCustomerDOMapper, GridInfoDOMapper gridInfoDOMapper, CustomerInterviewDOMapper customerInterviewDOMapper, CustomerWhiteDOMapper customerWhiteDOMapper, SurveyDOMapper surveyDOMapper, ConclusionDOMapper conclusionDOMapper) {
        this.residentDOMapper = residentDOMapper;
        this.orgDOMapper = orgDOMapper;
        this.customerDOMapper = customerDOMapper;
        this.customerGreyDOMapper=customerGreyDOMapper;
        this.customerBlackDOMapper=customerBlackDOMapper;
        this.customerPovertyDOMapper=customerPovertyDOMapper;
        this.customerTagRelationDOMapper=customerTagRelationDOMapper;
        this.odsCustomerDOMapper = odsCustomerDOMapper;
        this.gridInfoDOMapper=gridInfoDOMapper;
        this.customerInterviewDOMapper = customerInterviewDOMapper;
        this.customerWhiteDOMapper = customerWhiteDOMapper;
        this.surveyDOMapper = surveyDOMapper;
        this.conclusionDOMapper = conclusionDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(ResidentDOServiceImpl.class);

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) throws Exception {
        try {
            return residentDOMapper.deleteByPrimaryKey(id) == 1;
        } catch (Exception e) {
            logger.info("删除户籍异常:" + e.getMessage());
            throw new ServiceException("删除户籍异常");
        }
    }

    /**
     * 新建
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public long insertSelective(ResidentDO record) throws Exception {
        // 身份证格式校验
        if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号！");
        }

        // 身份证唯一校验
        ResidentDO residentDO;
        try {
            residentDO = residentDOMapper.getByIdNumber(record.getIdNumber());
        } catch (Exception e) {
            logger.info("身份证校验异常:" + e.getMessage());
            throw new ServiceException("身份证校验异常");
        }
        if (StringUtil.isNotBlank(residentDO)) {
            throw new ServiceException("该户籍已存在列表中！");
        }

        // 创建时间
        long now = System.currentTimeMillis();
        record.setCreatedAt(now).setUpdatedAt(now);
        int count;
        try {
            count = residentDOMapper.insertSelective(record);
        } catch (Exception e) {
            logger.info("新建户籍异常:" + e.getMessage());
            throw new ServiceException("新建户籍异常");
        }

        // 导入到客户基本信息
        long customerId = insertCustomerByResident(record);


        // 如果该人是黑、贫客户则将家人流程全部作废
        List<CustomerTagRelationDO> customerTagRelationDOList = customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(record.getHouseholdId());
        boolean isBlackOrPoverty = false;
        boolean isWhite = false;
        String whiteIdNumber = "";
        if (customerTagRelationDOList != null && customerTagRelationDOList.size() != 0) {
            for (CustomerTagRelationDO customerTagRelationDO : customerTagRelationDOList) {
                if (customerTagRelationDO.getTagId() == 1 || customerTagRelationDO.getTagId() == 3) {
                    isBlackOrPoverty = true;
                }

                if (customerTagRelationDO.getTagId() == 5) {
                    whiteIdNumber = customerTagRelationDO.getIdNumber();
                    isWhite = true;
                }
            }
        }


        // 流程作废
        if (isBlackOrPoverty) {
            // 作废客户信息
            // 同时客户信息-授信额度=0，有效调查次数=0，是否下结论=否，是否借款主体=否
            customerDOMapper.updateByHouseholdIdSelective(
                    new CustomerDO()
                            .setHouseholdId(record.getHouseholdId())
                            .setAmount(new BigDecimal("0"))
                            .setIsConcluded("否")
                            .setIsBorrower("否")
                            .setValidTime(0));

            // 作废调查表和结论
            surveyDOMapper.updateByHouseholdIdSelective(new SurveyDO().setHouseholdId(record.getHouseholdId()).setIsValid("0"));
            conclusionDOMapper.updateByHouseholdIdSelective(new ConclusionDO().setHouseholdId(record.getHouseholdId()).setIsValid("0"));

            // 如果有白名单面签流程作废
            if (isWhite) {

                List<CustomerInterviewDO> customerInterviewDOList = customerInterviewDOMapper.getListByIdNumber(
                        new CustomerInterviewDO().setIdNumber(whiteIdNumber)
                );

                if (customerInterviewDOList != null && customerInterviewDOList.size() != 0) {
                    for (CustomerInterviewDO customerInterviewDO : customerInterviewDOList) {
                        // 删除白名单
                        // 删除白名单关联信息
                        customerTagRelationDOMapper.deleteByIdNumberAndTagId(new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId(5L));
                        // 删除白名单库信息
                        customerWhiteDOMapper.deleteByIdNumber(record.getIdNumber());

                        // 作废面签
                        customerInterviewDOMapper.deleteByPrimaryKey(customerInterviewDO.getId());
                        customerInterviewDOMapper.insertBanSelective(customerInterviewDO.setUpdatedAt(customerInterviewDO.getId()));

                    }
                }
            }

        }

        return customerId;
    }

    /**
     * 户籍转换成客户
     * @param record
     * @return
     */
    private long insertCustomerByResident(ResidentDO record) {
        // 如果纳入名单库为否，则返回0
        if ("否".equals(record.getIsInList())) {
            return 0;
        }

        // 如果客户列表中已存在该客户，则返回0
        CustomerDO customerDO1 = null;
        try {
            customerDO1 = customerDOMapper.getByIdNumber(record.getIdNumber());
        } catch (Exception e) {
            logger.info("户籍转换客户异常" + e.getMessage());
            throw new ServiceException("户籍转换客户异常");
        }
        if (StringUtil.isNotBlank(customerDO1)) {
            return 0;
        }

        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;

        // 户籍转换成客户信息
        int idNumber17th = Integer.valueOf(record.getIdNumber().substring(16, 17));
        String year = record.getIdNumber().substring(6, 10);
        String month = record.getIdNumber().substring(10, 12);
        String day = record.getIdNumber().substring(12, 14);
        String birthday = year + "-" + month + "-" + day;
        CustomerDO customerDO = new CustomerDO();
        customerDO.setName(record.getName()) // 姓名
                .setType("其他自然人") // 类型
                .setIdType("身份证") // 身份证类型
                .setIdNumber(record.getIdNumber()) //身份证
                .setBirthday(birthday) // 出生日期
                .setGender(idNumber17th % 2 == 0 ? "2" : "1") // 性别，1-男，2-女
                .setNativeAddress(record.getCounty() + record.getTownship() + record.getVillage() + record.getGroup()) //户籍地址
                .setPhoneNumber(record.getContact())
                .setHouseholdId(record.getHouseholdId())
                .setIsBorrower("否") //借款主体
                .setStatus("1")
                .setRelationship(record.getRelationship()) // 与户主的关系
                .setValidTime(record.getValidTime()) // 有效次数
                .setIsConcluded(record.getIsConcluded()) // 是否有调查结论
                .setAge(age) // 年龄
                .setGridCode(record.getGridCode())
                .setMainOrgName(record.getOrgName())
                .setRegisterPerson(record.getUserName())
                .setLastModifyPerson(record.getUserName())
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis());

        try {
            customerDOMapper.insertSelective(customerDO);
        } catch (Exception e) {
            logger.info("户籍转换客户异常:" + e.getMessage());
            throw new ServiceException("户籍转换客户异常");
        }
        return customerDO.getId();
    }

    /**
     * 从ODS获取数据补全信息
     * @param customerDO
     * @return
     */
    private CustomerDO getCustomerFromODS(CustomerDO customerDO) {
        List<ODSCustomerDO> odsCustomerDOList;
        try {
            odsCustomerDOList = odsCustomerDOMapper.listODSByIdNumber(customerDO.getIdNumber());
        } catch (Exception e) {
            logger.info("获取存量客户异常:" + e.getMessage());
            throw new ServiceException("获取存量客户异常");
        }
        if (odsCustomerDOList != null && odsCustomerDOList.size() !=0) {
            ODSCustomerDO odsCustomerDO = odsCustomerDOList.get(0);
            customerDO.setCreditId(odsCustomerDO.getCreditId())
                    .setSignOrg(odsCustomerDO.getSignOrg())
                    .setIsLongTerm(odsCustomerDO.getIsLongTerm())
                    .setSignDate(odsCustomerDO.getSignDate())
                    .setDueDate(odsCustomerDO.getDueDate())
                    .setDetailAddress(odsCustomerDO.getDetailAddress())
                    .setPhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getPhoneNumber())
                            ? odsCustomerDO.getPhoneNumber() : null)
                    .setNationality(odsCustomerDO.getNationality())
                    .setNation(odsCustomerDO.getNation())
                    .setPoliticsStatus(odsCustomerDO.getPoliticsStatus())
                    .setEducationBackground(odsCustomerDO.getEducationBackground())
                    .setPhysicalCondition(odsCustomerDO.getPhysicalCondition())
                    .setMaritalStatus(odsCustomerDO.getMaritalStatus())
                    .setSpouseName(odsCustomerDO.getSpouseName())
                    .setSpouseIdNumber(StringUtil.isIdNumber(odsCustomerDO.getSpouseIdNumber())
                            ? odsCustomerDO.getSpouseIdNumber() : null)
                    .setSpousePhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getSpousePhoneNumber())
                            ? odsCustomerDO.getSpousePhoneNumber() : null)
                    .setSpouseCompanyName(odsCustomerDO.getSpouseCompanyName())
                    .setCareerType(odsCustomerDO.getCareerType())
                    .setMainBusiness(odsCustomerDO.getMainBusiness())
                    .setIsStaff(odsCustomerDO.getIsStaff())
                    .setIsStockholder(odsCustomerDO.getIsStockholder())
                    .setIsCivilServant(odsCustomerDO.getIsCivilServant())
                    .setIsHouseOwner(odsCustomerDO.getIsHouseOwner());
        }
        return customerDO;
    }

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public ResidentDO getByPrimaryKey(Long id) throws Exception {
        try {
            return residentDOMapper.getByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("查看户籍异常:" + e.getMessage());
            throw new ServiceException("查看户籍异常");
        }
    }

    /**
     * 分页
     *
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<ResidentDO> listByNameOrIdNumberOrHouseholdId(ResidentDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询异常:" + e.getMessage());
                throw new ServiceException("查询异常");
            }
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            return residentDOMapper.listByNameOrIdNumberOrHouseholdId(record);
        } catch (Exception e) {
            logger.info("分页查询户籍异常:" + e.getMessage());
            throw new ServiceException("分页查询户籍异常");
        }
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateByPrimaryKeySelective(ResidentDO record) throws Exception {
        // 身份证格式校验
        if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号！");
        }

        // 身份证唯一校验
        ResidentDO residentDO;
        try {
            residentDO = residentDOMapper.getByIdNumber(record.getIdNumber());
        } catch (Exception e) {
            logger.info("身份证校验异常:" + e.getMessage());
            throw new ServiceException("身份证校验异常");
        }

        // 时间
        long now = System.currentTimeMillis();
        record.setUpdatedAt(now);
        try {
            return residentDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑户籍异常:" + e.getMessage());
            if (StringUtil.isNotBlank(residentDO)) {
                throw new ServiceException("该户籍已存在列表中！");
            }
            throw new ServiceException("编辑户籍异常");
        }
    }


    /**
     * 户籍转客户
     * @param list
     */
    public void residentToCustomer(List<ResidentDO> list){
        List<CustomerDO> customerList=new ArrayList<>();
        for(ResidentDO record:list) {


            long now = System.currentTimeMillis();
            // 计算年龄
            Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
            Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
            Integer age = nowYear - birthYear;

            // 户籍转换成客户信息
            int idNumber17th = Integer.valueOf(record.getIdNumber().substring(16, 17));
            String year = record.getIdNumber().substring(6, 10);
            String month = record.getIdNumber().substring(10, 12);
            String day = record.getIdNumber().substring(12, 14);
            String birthday = year + "-" + month + "-" + day;
            CustomerDO customerDO = new CustomerDO();
            customerDO.setName(record.getName()) // 姓名
                    .setType("其他自然人") // 类型
                    .setIdType("身份证") // 身份证类型
                    .setIdNumber(record.getIdNumber()) //身份证
                    .setBirthday(birthday) // 出生日期
                    .setGender(idNumber17th % 2 == 0 ? "2" : "1") // 性别，1-男，2-女
                    .setNativeAddress(record.getCounty() + record.getTownship() + record.getVillage() + record.getGroup()) //户籍地址
                    .setPhoneNumber(record.getContact())
                    .setHouseholdId(record.getHouseholdId())
                    .setIsBorrower("否") //借款主体
                    .setStatus("1")
                    .setRelationship(record.getRelationship()) // 与户主的关系
                    .setValidTime(0) // 有效次数
                    .setIsConcluded("否") // 是否有调查结论
                    .setAge(age) // 年龄
                    .setGridCode(record.getGridCode())
                    .setMainOrgName(record.getOrgName())
                    .setRegisterPerson(record.getUserName())
                    .setLastModifyPerson(record.getUserName())
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());

            customerList.add(customerDO);

        }
        customerDOMapper.batchSave(customerList);
    }
    /**
     * 从excel导入
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Transactional
    @Override
    public int importFromExcel(List<Map<String, Object>> list,Map<String,Object> paramMap) throws Exception {
        long now=System.currentTimeMillis();
        if(list.size()>5000) {
            throw new ServiceException("单次上传最多上传5000条户籍信息");
        }else if(list.isEmpty()) {
            throw new ServiceException("文件中没有有效的户籍信息");
        }
        //遍历数据，取出身份证号
        List<String> idNumberList=new ArrayList<>();
        List<ResidentDO> residentDOList=new ArrayList<>();
        //查询出选择的网格的数据
        Map<String,Object> mapGrid=new HashMap<>();
        mapGrid.put("gridCode", paramMap.get("gridCode").toString());
        List<GridInfoDO> gf=gridInfoDOMapper.getList(mapGrid);
        //如果网格的行政村和自然村是同一个名字 ，则置空自然村
        if(gf.get(0).getVillage().equals(gf.get(0).getGroup())) {
            gf.get(0).setGroup("");
        }
        //遍历出户号
        Set<String> houseHoldId=new HashSet<>();
        //存是户主的户号
        Set<String> master=new HashSet<>();
        for(Map<String, Object> map:list) {

            if(map.get("7")==null||"".equals(map.get("7").toString())) {
                throw new ServiceException("身份证号不能为空");
            }

            String idNumber=map.get("7").toString();
            if(idNumberList.contains(idNumber)) {
                throw new ServiceException("文件中存在重复身份证号:"+idNumber);
            }

            if (StringUtil.isNotIdNumber(idNumber)) {
                throw new ServiceException("身份证号:"+idNumber+"不是有效的中国居民身份证号！");
            }
            if(map.get("6")==null||"".equals(map.get("6").toString())) {
                throw new ServiceException("身份证号:"+idNumber+" 的姓名为空");
            }else if(map.get("6").toString().length()>10) {
                throw new ServiceException("身份证号:"+idNumber+"的姓名长度超过了10位");
            }
            if(map.get("10")==null||"".equals(map.get("10").toString())||map.get("10").toString().length()>20) {
                throw new ServiceException("身份证号:"+idNumber+" 的户号为空或者长度超过20位");
            }

            //如果是户主记录下户号
            if((map.get("9")!=null)&&("户主".equals(map.get("9").toString().replace(" ", "")))) {
                master.add(map.get("10").toString().replace(" ", ""));
            }
            if(map.get("9")!=null&&map.get("9").toString().length()>110) {
                throw new ServiceException("身份证号:"+idNumber+" 的与户主关系长度超过20位");
            }
            houseHoldId.add(map.get("10").toString().replace(" ", ""));
            idNumberList.add(idNumber);
            String phone="";
            if(map.get("11")!=null) {
                phone=map.get("11").toString();
                if(phone.length()>11) {
                    throw new ServiceException("身份证号:"+idNumber+"的联系方式长度超过了11位");
                }
            }
            String ship="";
            if(map.get("9")!=null) {
                ship=map.get("9").toString();
            }
            String country="";
            if(map.get("2")!=null) {
                country=map.get("2").toString();
                if(country.length()>10) {
                    throw new ServiceException("身份证号:"+idNumber+"的县级名称长度超过了10位");
                }
            }
            ResidentDO residentDO=new ResidentDO();
            residentDO.setName(map.get("6").toString()).setIdNumber(idNumber.toUpperCase()).setGridName(paramMap.get("gridName").toString()).setOrgCode(paramMap.get("orgCode").toString()).setOrgName(paramMap.get("orgName").toString())
                    .setGridCode(paramMap.get("gridCode").toString()).setUserName(paramMap.get("name").toString()).setUserId(Long.parseLong(paramMap.get("userId").toString())).setHouseholdId(map.get("10").toString()).setContact(phone)
                    .setCounty(country).setTownship(gf.get(0).getTownship()).setVillage(gf.get(0).getVillage()).setGroup(gf.get(0).getGroup()).setName(map.get("6").toString()).setRelationship(ship).setCreatedAt(now).setUpdatedAt(now).setIsInList("是");
            residentDOList.add(residentDO);
        }
        //检查表中的户号和户主的户号是否相等
        for(String hz:houseHoldId) {
            int i=0;
            for(Map<String, Object> map:list) {
                if((map.get("9")!=null)&&("户主".equals(map.get("9").toString()))&&hz.equals(map.get("10").toString())) {
                    i++;
                }
            }
            if(i==0) {
                throw new ServiceException("户号:"+hz.toString()+"没有户主");
            }else if(i>1) {
                throw new ServiceException("户号:"+hz.toString()+"有多个户主");
            }
        }

        // 检查同一户中是否姓名唯一
        List<String> nameAndHouseholdIdOnlyOneList = new ArrayList<>();
        for (ResidentDO residentDO : residentDOList) {
            nameAndHouseholdIdOnlyOneList.add(residentDO.getName() + ";" + residentDO.getHouseholdId());
        }
        for (String nameAndHouseholdIdOut : nameAndHouseholdIdOnlyOneList) {
            int count = 0;
            for (String nameAndHouseholdIdIn : nameAndHouseholdIdOnlyOneList) {
                if (nameAndHouseholdIdIn.equals(nameAndHouseholdIdOut)) {
                    count ++;
                }
            }
            if (count > 1) {
                throw new ServiceException("户号:" + nameAndHouseholdIdOut.split(";")[1] + "存在同一家庭姓名重复");
            }
        }

        // 检查每一家庭年龄是否不符，即年龄都不在18-57岁中间
//        List<CustomerGreyDO> customerGreyDOList = new ArrayList<>();
//        List<CustomerTagRelationDO> customerTagRelationDOList = new ArrayList<>();
//        for (String hhid : master) {
//            // 汇总家庭成员，统一判断年龄是否不符
//            List<ResidentDO> family = new ArrayList<>();
//            for (ResidentDO residentDO : residentDOList) {
//                if (hhid.equals(residentDO.getHouseholdId())) {
//                    family.add(residentDO);
//                }
//            }
//
//            // 判断家庭年龄是否不符
//            boolean isNotMatched = true;
//            for (ResidentDO residentDO : family) {
//                // 计算年龄
//                Integer birthYear = Integer.valueOf(residentDO.getIdNumber().substring(6, 10));
//                Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
//                int age = nowYear - birthYear;
//                isNotMatched = (age < 18 || age > 57) && isNotMatched;
//            }
//
//            // 将不符合年龄的客户统一加入到灰名单库及标签关联表中
//            if (isNotMatched) {
//                for (ResidentDO residentDO : family) {
//                    // 灰名单
//                    CustomerGreyDO customerGreyDO = new CustomerGreyDO();
//                    customerGreyDO
//                            .setCustomerName(residentDO.getName())
//                            .setHouseholdId(residentDO.getHouseholdId())
//                            .setIdNumber(residentDO.getIdNumber())
//                            .setReason("14年龄不符合")
//                            .setPhone(residentDO.getContact())
//                            .setUserId(residentDO.getUserId())
//                            .setUserName(residentDO.getUserName())
//                            .setGridCode(residentDO.getGridCode())
//                            .setGridName(residentDO.getGridName())
//                            .setOrgCode(residentDO.getOrgCode())
//                            .setOrgName(residentDO.getOrgName())
//                            .setCreatedAt(System.currentTimeMillis())
//                            .setUpdatedAt(System.currentTimeMillis());
//                    customerGreyDOList.add(customerGreyDO);
//
//                    // 标签库
//                    CustomerTagRelationDO customerTagRelationDO = new CustomerTagRelationDO();
//                    customerTagRelationDO.setTagId(6L)
//                            .setIdNumber(residentDO.getIdNumber());
//                    customerTagRelationDOList.add(customerTagRelationDO);
//                }
//            }
//        }
//
//        // 年龄不符的批量导入灰名单库和标签关联库
//        try {
////            customerGreyDOMapper.batchSave(customerGreyDOList);
////            customerTagRelationDOMapper.batchSave(customerTagRelationDOList);
//            for (CustomerTagRelationDO customerTagRelationDO : customerTagRelationDOList) {
//                customerTagRelationDOMapper.insertSelective(customerTagRelationDO);
//            }
//            for (CustomerGreyDO customerGreyDO : customerGreyDOList) {
//                customerGreyDOMapper.insertSelective(customerGreyDO);
//            }
//        } catch (Exception e) {
//            logger.info("批量导入年龄不符到灰名单库异常：" + e.getMessage());
//            throw new ServiceException("批量导入年龄不符到灰名单库异常");
//        }

        //检查系统中是否有重复的身份证号
        Map<String,Object> map=new HashMap<>();
        map.put("idNumberList", idNumberList);
        List<ResidentDO> doubleList=residentDOMapper.getByIdNumers(map);
        if(doubleList !=null && !doubleList.isEmpty()) {
           /* String doubleIdNumber="";
            for(ResidentDO re:doubleList) {
                doubleIdNumber=doubleIdNumber+","+re.getIdNumber();
            }*/
            throw new ServiceException("系统中已存在身份证号："+doubleList.get(0).getIdNumber());
        }
        long now1=System.currentTimeMillis();
        System.out.println("到户籍多线程前 ======="+(now1-now));
        //启用多线程插入户籍
        List<ResidentDO> saveReList=new ArrayList<>();
        for(int i=0;i<residentDOList.size();i++) {

            saveReList.add(residentDOList.get(i));
            if(saveReList.size()>=200) {
                try {
                    asyncTaskResidentSave.executeAsyncTask(saveReList, residentDOMapper);
                } catch (Exception e) {
                    logger.info("批量导入户籍异常" + e.getMessage());
                    throw new ServiceException("批量导入户籍异常");
                }
                saveReList=new ArrayList<>();
            }
            if(i==residentDOList.size()-1) {
                try {
                    asyncTaskResidentSave.executeAsyncTask(saveReList, residentDOMapper);
                } catch (Exception e) {
                    logger.info("批量导入户籍异常" + e.getMessage());
                    throw new ServiceException("批量导入户籍异常");
                }
            }
        }
        long now2=System.currentTimeMillis();
        System.out.println("到客户多线程前 ======="+(now2-now1));
        //插入客户前过滤掉客户表中已存在的
        List<CustomerDO> customerList=customerDOMapper.getByIdNumbers(map);
        List<ResidentDO> saveList=new ArrayList<>();
        if(customerList.size()>0) {
            Set<String> set=new HashSet<>();

            for(CustomerDO cdo:customerList) {
                set.add(cdo.getIdNumber());
            }
            for(ResidentDO rdo:residentDOList) {
                if(set.contains(rdo.getIdNumber())) {
                    continue;
                }else {
                    saveList.add(rdo);
                }
            }
            if(saveList.size()>0) {
                //启用多线程插入客户
                List<ResidentDO> saveCuList=new ArrayList<>();
                for(int i=0;i<saveList.size();i++) {
                    saveCuList.add(saveList.get(i));
                    if(saveCuList.size()>=200) {
                        try {
                            asyncTaskCustomerSave.executeAsyncTask(saveCuList, customerDOMapper,odsCustomerDOMapper);
                        } catch (Exception e) {
                            logger.info("批量导入客户异常" + e.getMessage());
                            throw new ServiceException("批量导入客户异常");
                        }
                        saveCuList=new ArrayList<>();
                    }
                    if(i==saveList.size()-1) {
                        try {
                            asyncTaskCustomerSave.executeAsyncTask(saveCuList, customerDOMapper,odsCustomerDOMapper);
                        } catch (Exception e) {
                            logger.info("批量导入客户异常" + e.getMessage());
                            throw new ServiceException("批量导入客户异常");
                        }
                    }
                }
            }
        }else {
            //启用多线程插入客户
            List<ResidentDO> saveCuList=new ArrayList<>();
            for(int i=0;i<residentDOList.size();i++) {
                saveCuList.add(residentDOList.get(i));
                if(saveCuList.size()>=200) {
                    asyncTaskCustomerSave.executeAsyncTask(saveCuList, customerDOMapper,odsCustomerDOMapper);
                    saveCuList=new ArrayList<>();
                }
                if(i==residentDOList.size()-1) {
                    asyncTaskCustomerSave.executeAsyncTask(saveCuList, customerDOMapper,odsCustomerDOMapper);
                }
            }
        }
        long now3=System.currentTimeMillis();
        System.out.println("到结束前 ======="+(now3-now2));
        return residentDOList.size();
    }

    /**
     * 更新户籍、客户、调查表、客户标签关联表、面签、灰白名单库信息
     * 身份证号、户号、网格
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean updateIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record) throws Exception {
        // 判断新旧身份证号是否存在黑/贫/蓝名单中
        int countInList = residentDOMapper.countInBlackAndPovertyAndBlueByIdNumberAndOldIdNumber(record);
        if (countInList > 0) {
            throw new ServiceException("身份证号已经存在黑名单或贫困户或蓝名单库中，不可变更");
        }
        // 修改判断新名字是否存在该户中
        if (!record.getName().equals(record.getOldName())) {
            int countExistResidentName = residentDOMapper.countInResidentByHouseholdIdAndName(record);
            if (countExistResidentName > 0) {
                throw new ServiceException("新姓名已存在该户中，不可变更");
            }
        }

        // 修改户籍、客户身份证号和姓名
        residentDOMapper.modifyResidentIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(record);
        residentDOMapper.modifyCustomerIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(record);

        // 修改调查表姓名，结论表的姓名和身份证号
        residentDOMapper.modifyNameByNameAndHouseholdId(record);
        residentDOMapper.modifyConclusionNameAndIdNumberByOldIdNumber(record);

        // 修改面签及作废表的姓名和身份证号
        residentDOMapper.modifyInterviewNameAndIdNumberByOldIdNumber(record);
        residentDOMapper.modifyInterviewBanNameAndIdNumberByOldIdNumber(record);

        // 修改白灰名单的身份证号
        residentDOMapper.modifyCustomerTagIdNumberByOldIdNumber(record);
        residentDOMapper.modifyWhiteNameAndIdNumberByOldIdNumber(record);
        residentDOMapper.modifyGreyNameAndIdNumberByOldIdNumber(record);

        return true;
    }

}
