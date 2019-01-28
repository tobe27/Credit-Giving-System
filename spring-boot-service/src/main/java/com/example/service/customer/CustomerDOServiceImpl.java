package com.example.service.customer;

import com.example.common.enums.DatePatternEnum;
import com.example.common.util.DateConvertUtil;
import com.example.common.util.StringUtil;
import com.example.service.black.CustomerBlackDO;
import com.example.service.black.CustomerBlackDOMapper;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.conclusion.ConclusionDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.resident.ResidentDO;
import com.example.service.resident.ResidentDOMapper;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import com.github.pagehelper.PageHelper;
import com.ibm.icu.text.DecimalFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Created by L.C.Y on 2018-12-5
 */
@Service
public class CustomerDOServiceImpl implements CustomerDOService {
    private final CustomerDOMapper customerDOMapper;
    private final OrgDOMapper orgDOMapper;
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
    private final GridInfoDOMapper gridInfoDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final ConclusionDOMapper conclusionDOMapper;
    private final ResidentDOMapper residentDOMapper;
    private final CustomerGreyDOMapper customerGreyDOMapper;
    private final CustomerBlackDOMapper customerBlackDOMapper;


    @Autowired
    public CustomerDOServiceImpl(CustomerDOMapper customerDOMapper, OrgDOMapper orgDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper, GridInfoDOMapper gridInfoDOMapper, SurveyDOMapper surveyDOMapper, ConclusionDOMapper conclusionDOMapper, ResidentDOMapper residentDOMapper, CustomerGreyDOMapper customerGreyDOMapper, CustomerBlackDOMapper customerBlackDOMapper) {
        this.customerDOMapper = customerDOMapper;
        this.orgDOMapper = orgDOMapper;
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
        this.gridInfoDOMapper = gridInfoDOMapper;
        this.surveyDOMapper = surveyDOMapper;
        this.conclusionDOMapper = conclusionDOMapper;
        this.residentDOMapper = residentDOMapper;
        this.customerGreyDOMapper = customerGreyDOMapper;
        this.customerBlackDOMapper = customerBlackDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(CustomerDOServiceImpl.class);

    /**
     * 删除
     * 暂存状态可删除
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) throws Exception {
        try {
            return customerDOMapper.deleteByPrimaryKey(id) == 1;
        } catch (Exception e) {
            logger.info("删除客户异常:" + e.getMessage());
            throw new ServiceException("删除客户异常");
        }
    }

    /**
     * 删除不是借款主体客户
     * 同时删除户籍信息
     *
     * @param record
     * @return
     */
    @Override
    public boolean deleteNotBorrowerByIdAndIdNumber(CustomerDO record) throws Exception {
        // 判断是否是借款主体
        CustomerDO customerDO = customerDOMapper.getByPrimaryKey(record.getId());
        if ("是".equals(customerDO.getIsBorrower())) {
            throw new ServiceException("借款主体不能删除！");
        }
        // 删除灰名单
        customerGreyDOMapper.deleteByIdNumber(record.getIdNumber());
        customerTagRelationDOMapper.deleteByIdNumberAndTagId(
                new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId(2L));

        // 删除客户
        customerDOMapper.deleteByPrimaryKey(record.getId());

        // 删除户籍
        residentDOMapper.deleteByIdNumber(record.getIdNumber());

        return true;
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
    public boolean insertSelective(CustomerDO record) throws Exception {
        if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("身份证号不是有效的中国居民身份证号！");
        }
        if (customerDOMapper.getByIdNumber(record.getIdNumber()) != null) {
            throw new ServiceException("客户已存在列表中，请勿重复添加！");
        }

        if (StringUtil.isNotBlank(record.getSpouseIdNumber()) && StringUtil.isNotIdNumber(record.getSpouseIdNumber())) {
            throw new ServiceException("配偶身份证号不是有效的中国居民身份证号！");
        }


        // 默认值
        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;
        record.setType("其他自然人")
                .setIdType("身份证")
                .setAge(age)
                .setValidTime(0)
                .setIsConcluded("否")
                .setIsBorrower("否")
                .setStatus("1")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        try {
            return customerDOMapper.insertSelective(record) == 1;
        } catch (Exception e) {
            logger.info("新建客户异常:" + e.getMessage());
            throw new ServiceException("新建客户异常");
        }
    }

    /**
     * 新建暂存
     * 1-正常，2-暂存
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean insertDraftSelective(CustomerDO record) throws Exception {
        if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("身份证号不是有效的中国居民身份证号！");
        }

        if (customerDOMapper.getByIdNumber(record.getIdNumber()) != null) {
            throw new ServiceException("客户已存在列表中，请勿重复添加！");
        }

        if (StringUtil.isNotBlank(record.getSpouseIdNumber()) && StringUtil.isNotIdNumber(record.getSpouseIdNumber())) {
            throw new ServiceException("配偶身份证号不是有效的中国居民身份证号！");
        }

        // 默认值
        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;
        record.setType("其他自然人")
                .setIdType("身份证")
                .setAge(age)
                .setValidTime(0)
                .setIsConcluded("否")
                .setIsBorrower("否")
                .setStatus("2")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        try {
            return customerDOMapper.insertSelective(record) == 1;
        } catch (Exception e) {
            logger.info("暂存客户异常:" + e.getMessage());
            throw new ServiceException("暂存客户异常");
        }
    }

    /**
     * 查看
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public CustomerDO getByPrimaryKey(Long id) throws Exception {
        try {
            return customerDOMapper.getByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("查看客户异常:" + e.getMessage());
            throw new ServiceException("查看客户异常");
        }
    }

    /**
     * 通过身份证查看客户
     *
     * @param idNumber
     * @return
     * @throws Exception
     */
    @Override
    public CustomerDO getByIdNumber(String idNumber) throws Exception {
        try {
            return customerDOMapper.getByIdNumber(idNumber);
        } catch (Exception e) {
            logger.info("查看客户异常:" + e.getMessage());
            throw new ServiceException("查看客户异常");
        }
    }

    /**
     * 分页
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     *
     * @param record 客户
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public List<CustomerDO> listCustomers(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            return customerDOMapper.listCustomers(record);
        } catch (Exception e) {
            logger.info("分页查询客户异常:" + e.getMessage());
            throw new ServiceException("分页查询客户异常");
        }
    }

    /**
     * 分页查询无标签客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     *
     * @param record   客户
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<CustomerDO> listNoTagCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            return customerDOMapper.listNoTagCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(record);
        } catch (Exception e) {
            logger.info("分页查询无标签客户异常:" + e.getMessage());
            throw new ServiceException("分页查询无标签客户异常");
        }
    }

    /**
     * 分页查询无标签客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     *
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<CustomerDO> listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            return customerDOMapper.listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(record);
        } catch (Exception e) {
            logger.info("分页查询有标签客户异常:" + e.getMessage());
            throw new ServiceException("分页查询有标签客户异常");
        }
    }

    /**
     * 导出不能评议客户到Excel
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(CustomerDO record) throws Exception {
        // 获取网格内的客户
        List<CustomerDO> customerDOList = customerDOMapper.listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(record);
        // 获取网格内的黑名单
        List<CustomerBlackDO> customerBlackDOList = customerBlackDOMapper.listByGridCode(record.getGridCode());
        // 获取网格内的灰名单
        List<CustomerGreyDO> customerGreyDOList = customerGreyDOMapper.listByGridCode(record.getGridCode());


        // 获取户主列表
        List<CustomerDO> houseOwnerList = new ArrayList<>();
        for (CustomerDO customerDO : customerDOList) {
            if ("户主".equals(customerDO.getRelationship())) {
                houseOwnerList.add(customerDO);
            }
        }

        int no = 1; // 序号
        int count = 0; // 记录总数

        List<Map<String, Object>> mainList = new ArrayList<>();

        // 获取家庭成员列表
        for (CustomerDO houseOwnerCustomer : houseOwnerList) {
            Map<String, Object> houseOwnerMap = new HashMap<>();
            int memberCount = 0; // 家庭人数
            // 家庭成员
            List<Map<String, Object>> memberList = new ArrayList<>();
            for (CustomerDO  memberCustomer : customerDOList) {
                if (memberCustomer.getHouseholdId().equals(houseOwnerCustomer.getHouseholdId())) {
                    Map<String, Object> memberMap = new HashMap<>();
                    Set<String> tagNameSet = new HashSet<>();
                    if (memberCustomer.getTags() != null && memberCustomer.getTags().size() != 0) {
                        for (CustomerTagRelationDO customerTagRelationDO : memberCustomer.getTags()) {
                            if (customerTagRelationDO.getTagId() == 1) {
                                for (CustomerBlackDO customerBlackDO : customerBlackDOList) {
                                    if (customerBlackDO.getIdNumber().equals(customerTagRelationDO.getIdNumber())) {
                                        tagNameSet.add(customerTagRelationDO.getTagName() + "原因:" + customerBlackDO.getReason());
                                    } else {
                                        tagNameSet.add(customerTagRelationDO.getTagName());
                                    }
                                }
                            } else if (customerTagRelationDO.getTagId() == 2) {
                                for (CustomerGreyDO customerGreyDO : customerGreyDOList) {
                                   if (customerGreyDO.getIdNumber().equals(customerTagRelationDO.getIdNumber())) {
                                       tagNameSet.add(customerTagRelationDO.getTagName() + "原因:" + customerGreyDO.getReason());
                                   } else {
                                       tagNameSet.add(customerTagRelationDO.getTagName());
                                   }
                                }
                            } else {
                                tagNameSet.add(customerTagRelationDO.getTagName());
                            }

                        }
                    }
                    String tagNameArr = "";
                    for (String tagName : tagNameSet) {
                        tagNameArr += tagName + ";";
                    }
                    memberMap.put("relationship", memberCustomer.getRelationship());
                    memberMap.put("name", memberCustomer.getName());
                    memberMap.put("age", memberCustomer.getAge());
                    memberMap.put("idNumber" , memberCustomer.getIdNumber());
                    memberMap.put("phoneNumber" , memberCustomer.getPhoneNumber());
                    memberMap.put("address" , memberCustomer.getNativeAddress());
                    memberMap.put("familyTags" , tagNameArr);

                    memberList.add(memberMap);
                    memberCount ++;
                }
            }
            houseOwnerMap.put("no", no);
            houseOwnerMap.put("householdId", houseOwnerCustomer.getHouseholdId());
            houseOwnerMap.put("familySize", memberCount);
            houseOwnerMap.put("memberList", memberList);

            mainList.add(houseOwnerMap);
            no ++;
            count += memberCount;

        }

        Map<String, Object> countMap = new HashMap<>();
        countMap.put("count", count);
        mainList.add(countMap);

        return mainList;

    }

    /**
     * 分页查询已评议客户，且有白名单客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     *
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<CustomerDO> listYetReviewCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            return customerDOMapper.listYetReviewCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(record);
        } catch (Exception e) {
            logger.info("分页查询已评议客户异常:" + e.getMessage());
            throw new ServiceException("分页查询已评议客户异常");
        }
    }

    /**
     * 已评议客户导出-整村授信评议预授信表
     * 计算方式 1-平均， 2-最小
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listSurveyCustomersByGridCodeAndRelationshipAndUserId(CustomerDO record, List<String> senatorList, int calType) throws Exception {

        List<CustomerDO> customerDOList = customerDOMapper.listSurveyCustomersByGridCodeAndRelationshipAndUserId(record);
        List<Map<String, Object>> houseOwnerList = new ArrayList<>();
        logger.info("预授信评议员个数==" + senatorList);
        logger.info("预授信评议员信息==" + senatorList);
        int no = 1;
        for (CustomerDO customerDO : customerDOList) {
            Map<String, Object> houseOwnerMap = new HashMap<>();
            List<SurveyDO> surveyDOList = customerDO.getSurveyDOList();
            double avgCreditAmount = 0;
            double minCreditAmount = 100000000000.0;
            int validTime = customerDO.getValidTime();
            Set<String> borrowerSet = new HashSet<>();
            if (surveyDOList != null && surveyDOList.size() != 0) {
                logger.info("预授信导出" + customerDO.getName() + "-调查次数:" + surveyDOList.size());
                logger.info("预授信导出-调查信息：" + surveyDOList);
                for (SurveyDO surveyDO : surveyDOList) {

                    if (StringUtil.isNotBlank(surveyDO.getBorrower())) {
                        borrowerSet.add(surveyDO.getBorrower());
                    }

                    for (String senator : senatorList) {
                        if (senator.equals(surveyDO.getSenator().trim())) {
                            if ("是".equals(surveyDO.getIsValid())) {
                                logger.info("评议员"+ senator + "==额度:" + surveyDO.getCreditAmount());
                                houseOwnerMap.put(senator, surveyDO.getCreditAmount());
                                avgCreditAmount += Double.valueOf(surveyDO.getCreditAmount());
                                minCreditAmount = minCreditAmount > Double.valueOf(surveyDO.getCreditAmount())
                                        ? Double.valueOf(surveyDO.getCreditAmount()) : minCreditAmount;
                            } else {
                                logger.info("评议员"+ senator + "==无效评议");
                                houseOwnerMap.put(senator, "无效");
                            }
                        }
                    }
                }
            }

            String borrowerArr = "";
            String creditAmount = "";
            DecimalFormat decimalFormat = new DecimalFormat("#.0");
            // 如果有结论，借款主体为一人,金额为固定值
            if ("是".equals(customerDO.getIsConcluded())) {
                logger.info("下结论的借款主体开始：" + borrowerArr);
                List<ConclusionDO> conclusionDOList = conclusionDOMapper.listByHouseholdId(customerDO.getHouseholdId());
                if (conclusionDOList != null && conclusionDOList.size() != 0) {
                    borrowerArr = conclusionDOList.get(0).getBorrower();
                    logger.info("下结论：" + conclusionDOList.get(0));
                    creditAmount = decimalFormat.format(Double.valueOf(conclusionDOList.get(0).getCreditAmount()));
                }
            } else {
                for (String borrower : borrowerSet) {
                    borrowerArr += borrower + ";";
                }

                if (validTime >= 3) {
                    if (calType == 1) {
                        creditAmount = decimalFormat.format(avgCreditAmount/validTime);
                    } else {
                        creditAmount =  decimalFormat.format(minCreditAmount);
                    }
                }
            }


            houseOwnerMap.put("no",  no);
            houseOwnerMap.put("name",  customerDO.getName());
            houseOwnerMap.put("idNumber",  customerDO.getIdNumber());
            houseOwnerMap.put("address",  customerDO.getNativeAddress());
            houseOwnerMap.put("phoneNumber", customerDO.getPhoneNumber());
            houseOwnerMap.put("validTime", customerDO.getValidTime());
            houseOwnerMap.put("calType",  creditAmount);
            houseOwnerMap.put("borrower",  borrowerArr);

            houseOwnerList.add(houseOwnerMap);
            no ++;
        }
        return houseOwnerList;
    }

    /**
     * 已下结论-面签通过的借款主体信息，返回导出Excel数据
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Map<String, Object> borrowerSurveyList(CustomerDO record) throws Exception {
        // 获取家庭成员
        List<CustomerDO> customerDOList = customerDOMapper.listByHouseholdId(record.getHouseholdId());
        List<SurveyDO> surveyDOList = surveyDOMapper.listByHouseholdIdAndIsValid(new SurveyDO().setHouseholdId(record.getHouseholdId()));

        int countHaveDeposit = customerDOMapper.countFamilyInDeposit(record.getHouseholdId());

        Map<String, Object> borrowerMap = new HashMap<>();
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (CustomerDO customerDO : customerDOList) {
            if ("是".equals(customerDO.getIsBorrower())) {
                borrowerMap.put("name", customerDO.getName());
                borrowerMap.put("gender", customerDO.getGender());
                borrowerMap.put("age", customerDO.getAge());
                borrowerMap.put("idNumber", customerDO.getIdNumber());
                borrowerMap.put("phoneNumber", customerDO.getPhoneNumber());
                borrowerMap.put("address", customerDO.getNativeAddress());
            } else {
                Map<String, Object> memberMap = new HashMap<>();
                memberMap.put("name", customerDO.getName());
                memberMap.put("relationship", customerDO.getRelationship());
                memberMap.put("idNumber", customerDO.getIdNumber());
                memberMap.put("age", customerDO.getAge());
                memberList.add(memberMap);
            }
        }

        for (SurveyDO surveyDO : surveyDOList) {
            borrowerMap.put("mainBusiness", surveyDO.getMainBusiness());
            borrowerMap.put("scale", surveyDO.getScale());
            borrowerMap.put("haveHouse", surveyDO.getHouseValue());
            borrowerMap.put("haveCar", surveyDO.getCarValue());
            borrowerMap.put("income", surveyDO.getIncome());
            borrowerMap.put("payout", surveyDO.getPayout());
            if (StringUtil.isBlank(surveyDO.getNegativeReason())) {
                break;
            }
        }
        borrowerMap.put("isHaveDeposit", countHaveDeposit > 0 ? "是" : "否");

        // 判断家庭成员是否大于6人
        if (memberList.size() <= 6) {
            borrowerMap.put("memberList", memberList);
        } else {
            // 大于6人时，优先去除年龄不在18-57岁的人
            // 将数据分为两组，一组是正常的，一组是优先去除的
            List<Map<String, Object>> memberCommonList = new ArrayList<>();
            List<Map<String, Object>> memberNotCommonList = new ArrayList<>();

            for (Map<String, Object> aMemberList : memberList) {
                int age = Integer.valueOf(String.valueOf(aMemberList.get("age")));
                if (age < 18 || age > 57) {
                    memberNotCommonList.add(aMemberList);
                } else {
                    memberCommonList.add(aMemberList);
                }
            }

            // 等于6正好
            if (memberCommonList.size() == 6) {
                borrowerMap.put("memberList", memberCommonList);
            } else if (memberCommonList.size() > 6) { // 大于6时缩减至6人
                List<Map<String, Object>> memberCommonSonList = new ArrayList<>();
                for (int i =0; i <= 5; i ++) {
                    memberCommonSonList.add(memberCommonList.get(i));
                }
                borrowerMap.put("memberList", memberCommonSonList);
            } else { // 小于6时添加至6人
                List<Map<String, Object>> memberCommonPlusList = new ArrayList<>(memberCommonList);
                for (int i = 0; i < 6 - memberCommonList.size(); i ++) {
                    memberCommonPlusList.add(memberNotCommonList.get(i));
                }
                borrowerMap.put("memberList", memberCommonPlusList);
            }

        }
        return borrowerMap;
    }

    /**
     * 返回导出Excel数据
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public List<Map<String, Object>> listMapList(CustomerDO record) throws Exception {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> houseOwnerMapList = new ArrayList<>();

        // 一次查询出需要导出的所有数据,根据次数和
        List<CustomerDO> surveyFamilyList;
        try {
            surveyFamilyList = customerDOMapper.listNoTagCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(
                    new CustomerDO().setGridCode(record.getGridCode())
                            .setRoleId(1L)
                            .setUserId(record.getUserId())
                            .setValidTime(record.getValidTime()));
        } catch (Exception e) {
            logger.info("查询调查表信息异常:" + e.getMessage());
            throw new ServiceException("查询调查表信息异常");
        }
        long getDataTime = System.currentTimeMillis();
        logger.info("导出总个数：" + surveyFamilyList.size());
        logger.info("客户查询耗时:" + (getDataTime - start));
        if (surveyFamilyList.size() == 0) {
            throw new ServiceException("没有数据或者有效调查次数已有3次!");
        }

        List<CustomerDO> houseOwnerList = new ArrayList<>();
        List<String> allIdNumberList = new ArrayList<>();
        for (CustomerDO houseOwnerInfo : surveyFamilyList) {
            // 获取所有身份证号
            allIdNumberList.add(houseOwnerInfo.getIdNumber());
            // 获取所有的户主
            if ("户主".equals(houseOwnerInfo.getRelationship())) {
                houseOwnerList.add(houseOwnerInfo);
            }
        }
        logger.info("户主总个数：" + houseOwnerList.size());

        // 根据所有身份证号查询出有标签的客户身份证号
        Set<String> idNumberSet = customerTagRelationDOMapper.setIdNumberByInIdNumber(new CustomerTagRelationDO().setIdNumberList(allIdNumberList));
        logger.info("获取有标签的人数：" + idNumberSet.size());

        // 根据身份证号获取户号
        Set<String> householdIdSet= new HashSet<>();
        if (!idNumberSet.isEmpty()) {
            for (CustomerDO householdIdInfo : surveyFamilyList) {
                if (idNumberSet.contains(householdIdInfo.getIdNumber())) {
                    householdIdSet.add(householdIdInfo.getHouseholdId());
                }
            }
        }
        logger.info("获取有标签的户数：" + householdIdSet.size());

        // 获取没有标签的户主
        List<CustomerDO> lastHouseOwnerList = new ArrayList<>();
        if (householdIdSet.isEmpty()) {
            lastHouseOwnerList = houseOwnerList;
        } else {
            for (CustomerDO startHouseOwner : houseOwnerList) {
                if (!householdIdSet.contains(startHouseOwner.getHouseholdId())) {
                    lastHouseOwnerList.add(startHouseOwner);
                }
            }
        }
        logger.info("获取没有标签的户主：" + lastHouseOwnerList.size());


        int i = 1; // 序号
        int count = 0; // 计数

        // 户主信息
        for (CustomerDO houseOwner : lastHouseOwnerList) {
            // 成员个数
            int memberCount = 0;
            Map<String, Object> houseOwnerMap = new HashMap<>();
            houseOwnerMap.put("name", houseOwner.getName());
            houseOwnerMap.put("age", houseOwner.getAge());
            houseOwnerMap.put("householdId", houseOwner.getHouseholdId());
            houseOwnerMap.put("idNumber", houseOwner.getIdNumber());
            houseOwnerMap.put("phoneNumber", houseOwner.getPhoneNumber());
            houseOwnerMap.put("address", houseOwner.getNativeAddress());

            // 对外担保总额,取家庭总和
            BigDecimal outEnsureAmount = new BigDecimal("0");
            outEnsureAmount = outEnsureAmount.add(houseOwner.getOutEnsureAmount());
            // 判断年龄，取最小，如果小于57则不导出
//            int minAge = houseOwner.getAge();
//            int maxAge = houseOwner.getAge();

            int validTime = houseOwner.getValidTime();

            // 获取家庭成员信息
            List<CustomerDO> memberList = new ArrayList<>();
            for (CustomerDO memberInfo : surveyFamilyList) {
                if (houseOwner.getHouseholdId().equals(memberInfo.getHouseholdId())) {
                    memberList.add(memberInfo);
                }
            }
            List<Map<String, Object>> memberMapList = new ArrayList<>();
            // 家庭成员信息
            if (memberList.size() == 1) {
                memberCount ++;
            } else {
                for (CustomerDO member : memberList) {
                    if (houseOwner.getIdNumber().equals(member.getIdNumber())) {
                        continue;
                    }
                    Map<String, Object> memberMap = new HashMap<>();
                    memberMap.put("relationship", member.getRelationship());
                    memberMap.put("name", member.getName());
                    memberMap.put("age", member.getAge());
                    memberMapList.add(memberMap);
                    memberCount ++;
                    validTime = member.getValidTime() > validTime ? member.getValidTime() : validTime;
                    outEnsureAmount = outEnsureAmount.add(member.getOutEnsureAmount());
//                    minAge = member.getAge() > minAge ? minAge : member.getAge();
//                    maxAge = member.getAge() < maxAge ? maxAge :member.getAge();
                }
            }
            houseOwnerMap.put("memberList", memberMapList);
            houseOwnerMap.put("validTime", validTime);
            houseOwnerMap.put("outEnsureAmount", outEnsureAmount);


            // 如果大于3，直接下一个循环,或者大于57岁
            if (validTime >= 3) {
                continue;
            }
            houseOwnerMap.put("no", i ++);
            houseOwnerMapList.add(houseOwnerMap);
            count += memberCount;
        }

        Map<String, Object> countMap = new HashMap<>();
        countMap.put("count", count);
        houseOwnerMapList.add(countMap);
        logger.info("总行数：" + houseOwnerMapList.get(houseOwnerMapList.size() - 1).get("count"));
        long end = System.currentTimeMillis();
        logger.info("有效户主个数:" + (houseOwnerMapList.size() - 1));
        logger.info("总耗时:" + (end - start));
        return houseOwnerMapList;
    }

    /**
     * 变更客户和户籍的与户主关系
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateRelationshipByIdNumberSelective(CustomerDO record) throws Exception {
        customerDOMapper.updateByIdNumberSelective(record);
        residentDOMapper.updateByIdNumberSelective(new ResidentDO().setIdNumber(record.getIdNumber()).setRelationship(record.getRelationship()));
        return true;
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateByPrimaryKeySelective(CustomerDO record) throws Exception {

        // 默认值
        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;
        // 默认值
        record.setType("其他自然人")
                .setIdType("身份证")
                .setAge(age)
                .setStatus("1")
                .setUpdatedAt(now);

        try {
            return customerDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑客户异常:" + e.getMessage());
            if (customerDOMapper.getByIdNumber(record.getIdNumber()) != null) {
                throw new ServiceException("客户已存在列表中，请勿重复添加！");
            }
            throw new ServiceException("编辑客户异常");
        }
    }

    /**
     * 编辑暂存
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateDraftByPrimaryKeySelective(CustomerDO record) throws Exception {
        // 默认值
        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;
        // 默认值
        record.setAge(age);

        try {
            return customerDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("暂存客户异常:" + e.getMessage());
            if (customerDOMapper.getByIdNumber(record.getIdNumber()) != null) {
                throw new ServiceException("客户已存在列表中，请勿重复添加！");
            }
            throw new ServiceException("暂存客户异常");
        }
    }

    /**
     * 编辑部分字段
     * 编辑状态等
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updatePartById(CustomerDO record) throws Exception {
        // 默认值
        long now = System.currentTimeMillis();
        // 计算年龄
        Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
        Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
        Integer age = nowYear - birthYear;
        // 默认值
        record.setType("其他自然人")
                .setIdType("身份证")
                .setAge(age)
                .setStatus("1")
                .setUpdatedAt(now);
        try {
            return customerDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑客户异常:" + e.getMessage());
            throw new ServiceException("编辑客户异常");
        }
    }
}
