package com.example.service.customer;

import com.example.common.enums.DatePatternEnum;
import com.example.common.util.DateConvertUtil;
import com.example.common.util.StringUtil;
import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDOMapper;
import com.github.pagehelper.PageHelper;
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

    @Autowired
    public CustomerDOServiceImpl(CustomerDOMapper customerDOMapper, OrgDOMapper orgDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper) {
        this.customerDOMapper = customerDOMapper;
        this.orgDOMapper = orgDOMapper;
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
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
     * 返回导出Excel数据
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listMapList(CustomerDO record) throws Exception {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> houseOwnerMapList = new ArrayList<>();

        // 一次查询出需要导出的所有数据,根据次数和
        List<CustomerDO> surveyFamilyList;
        try {
            surveyFamilyList = customerDOMapper.listByHouseholdIdAndGridCode(new CustomerDO()
                    .setGridCode(record.getGridCode())
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
            int minAge = houseOwner.getAge();
            int maxAge = houseOwner.getAge();

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
                    minAge = member.getAge() > minAge ? minAge : member.getAge();
                    maxAge = member.getAge() < maxAge ? maxAge :member.getAge();
                }
            }
            houseOwnerMap.put("memberList", memberMapList);
            houseOwnerMap.put("validTime", validTime);
            houseOwnerMap.put("outEnsureAmount", outEnsureAmount);


            // 如果大于3，直接下一个循环,或者大于57岁
            if (minAge > 57 || maxAge < 18 || validTime >= 3) {
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
