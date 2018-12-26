package com.example.service.conclusion;

import com.example.common.util.StringUtil;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import com.example.service.white.CustomerWhiteDO;
import com.example.service.white.CustomerWhiteDOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/12
 */
@Service
public class ConclusionDOServiceImpl implements ConclusionDOService {
    private final ConclusionDOMapper conclusionDOMapper;
    private final CustomerDOMapper customerDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final CustomerWhiteDOService whiteDOService;

    @Autowired
    public ConclusionDOServiceImpl(ConclusionDOMapper conclusionDOMapper, CustomerDOMapper customerDOMapper, SurveyDOMapper surveyDOMapper, CustomerWhiteDOService whiteDOService) {
        this.conclusionDOMapper = conclusionDOMapper;
        this.customerDOMapper = customerDOMapper;
        this.surveyDOMapper = surveyDOMapper;
        this.whiteDOService = whiteDOService;
    }

    private static Logger logger = LoggerFactory.getLogger(ConclusionDOServiceImpl.class);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) {
        try {
            return conclusionDOMapper.deleteByPrimaryKey(id) == 1;
        } catch (Exception e) {
            logger.info("删除调查结论异常:" + e.getMessage());
            throw new ServiceException("删除调查结论异常");
        }
    }

    /**
     * 新建
     *
     * @param record
     * @return
     */
    @Override
    @Transactional
    public boolean insertSelective(ConclusionDO record) throws Exception {

        // 询该家庭的身份证号进行校验
        checkFamilyIdNumber(record.getHouseholdId());

        // 查询该家庭之前的有效结论，置为无效状态
        setIsValid(record.getHouseholdId());

        // 通过借款主体和户号查询出借款主体的身份证号,并把record的身份证号替换为借款主体的身份证号
        String borrowerIdNumber;
        try {
            borrowerIdNumber = customerDOMapper.getIdNumberByNameAndHouseholdId(
                    new CustomerDO().setName(record.getBorrower()).setHouseholdId(record.getHouseholdId()));
        } catch (Exception e) {
            logger.info("查询借款主体客户信息异常:" + e.getMessage());
            throw new ServiceException("查询借款主体客户信息异常");
        }
        if (StringUtil.isNotBlank(borrowerIdNumber)) {
            record.setName(record.getBorrower()).setIdNumber(borrowerIdNumber);
        }

        // 保证授信额度要小于最小值
        ensureIsMinAmount(borrowerIdNumber, record.getCreditAmount());

        long now = System.currentTimeMillis();
        record.setIsFamiliar("是")
                .setNegativeReason("无")
                .setDate(String.valueOf(now))
                .setSurveyType("客户经理结论")
                .setIsValid("是")
                .setCreatedAt(now)
                .setUpdatedAt(now);
        int count;
        try {
            count = conclusionDOMapper.insertSelective(record);
        } catch (Exception e) {
            logger.info("新建调查结论异常:" + e.getMessage());
            throw new ServiceException("新建调查结论异常");
        }

        // 同时更新客户-是否为借款主体-是，是否有调查结论-是,家庭人员置为非借款主体，调查结论为否
        List<CustomerDO> customerDOList;
        try {
            customerDOList = customerDOMapper.listByHouseholdIdAndGridCode(new CustomerDO().setHouseholdId(record.getHouseholdId()));
        } catch (Exception e) {
            logger.info("查询家庭成员异常" + e.getMessage());
            throw new ServiceException("查询家庭成员异常");
        }

        // 此时的身份证号已经更换为借款人的身份证号
        for (CustomerDO customerDO : customerDOList) {
            if (record.getIdNumber().equals(customerDO.getIdNumber())) {
                // 当前客户，是否为借款主体-是，是否有调查结论-是
                setCustomerIsConcluded(customerDO.getId(), "是", "是");
            } else {
                // 其他成员客户，是否为借款主体-否，是否有调查结论-否
                setCustomerIsConcluded(customerDO.getId(), "否", "是");
            }
        }

        // 同时设置为白名单
        CustomerWhiteDO customerWhiteDO = new CustomerWhiteDO();
        customerWhiteDO.setId(null)
                .setCustomerName(record.getName())
                .setIdNumber(record.getIdNumber())
                .setGridCode(record.getGridCode())
                .setGridName(record.getGridName())
                .setOrgCode(record.getOrgCode())
                .setOrgName(record.getOrgName())
                .setUserId(record.getUserId())
                .setUserName(record.getUserName())
                .setLimit(new BigDecimal(record.getCreditAmount()))
                .setHouseholdId(record.getHouseholdId());

        whiteDOService.insertSelective(customerWhiteDO);

        return count == 1;
    }


    /**
     * 有效调查次数小于三次，不能下结论
     * 授信额度应小于最小值
     * @param idNumber
     * @param creditAmount
     */
    private void ensureIsMinAmount(String idNumber, String creditAmount) {
        // 有效调查次数小于三次，不能下结论
        List<SurveyDO> surveyDOList;
        try {
            surveyDOList = surveyDOMapper
                    .listByIdNumberAndIsValid(new SurveyDO().setIdNumber(idNumber));
        } catch (Exception e) {
            logger.info("查询有效调查信息异常:" + e.getMessage());
            throw new ServiceException("查询有效调查信息异常");
        }
        if (surveyDOList == null || surveyDOList.size() < 3) {
            throw new ServiceException("有效调查次数小于三次，不能下结论！");
        }

        // 授信额度应小于最小值
        double minCreditAmount = Double.valueOf(creditAmount);
        for (SurveyDO surveyDO : surveyDOList) {
            if (StringUtil.isNotBlank(surveyDO.getCreditAmount()) && minCreditAmount > Double.valueOf(surveyDO.getCreditAmount())) {
                throw new ServiceException("授信额度应小于最小值");
            }
        }
    }


    /**
     * 查询结论汇总信息,调查信息必须大于三次，前端调用后回传到新建
     * @param idNumber
     * @return
     * @throws Exception
     */
    public ConclusionDO getConclusionInfo(String idNumber, String isValid) throws Exception{
        List<SurveyDO> surveyDOList;
        ConclusionDO conclusionDO = new ConclusionDO();
        try {
            surveyDOList = surveyDOMapper
                    .listByIdNumberAndIsValid(new SurveyDO().setIdNumber(idNumber));
        } catch (Exception e) {
            logger.info("查询有效调查信息异常:" + e.getMessage());
            throw new ServiceException("查询有效调查信息异常");
        }
        if (surveyDOList == null || surveyDOList.size() < 3) {
            throw new ServiceException("有效调查次数小于三次，不能下结论！");
        }

        String remark = ""; // 汇总 + 序号
        String mainBusiness = ""; // 汇总
        String scale = ""; // 汇总
        boolean haveHouse = false;
        boolean haveCar = false;
        double income = 0.0; // 平均
        double payout = 0.0; // 平均
        Double creditAmount = 10000000000.0; // 取小
        for (int i = 0; i < surveyDOList.size(); i ++) {
            SurveyDO surveyDO = surveyDOList.get(i);
            remark += i + ":" + surveyDO.getRemark() + ";";
            mainBusiness += surveyDO.getMainBusiness();
            scale += surveyDO.getScale();
            haveHouse = haveHouse || "有".equals(surveyDO.getHouseValue());
            haveCar = haveCar || "有".equals(surveyDO.getCarValue());
            income += Double.parseDouble(StringUtil.isBlank(surveyDO.getIncome()) ? "0" : surveyDO.getIncome());
            payout += Double.parseDouble(StringUtil.isBlank(surveyDO.getPayout()) ? "0" : surveyDO.getPayout());
            creditAmount = creditAmount > Double.valueOf(StringUtil.isBlank(surveyDO.getCreditAmount()) ? "0" : surveyDO.getCreditAmount())
                    ? Double.valueOf(StringUtil.isBlank(surveyDO.getCreditAmount()) ? "0" : surveyDO.getCreditAmount()) : creditAmount;
        }

        int count = surveyDOList.size();

        // 控制为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        SurveyDO surveyDO = surveyDOList.get(0);
        conclusionDO.setName(surveyDO.getName())
                .setIdNumber(surveyDO.getIdNumber())
                .setHouseholdId(surveyDO.getHouseholdId())
                .setRemark(remark)
                .setMainBusiness(mainBusiness)
                .setScale(scale)
                .setHouseValue(haveHouse ? "有" : "无")
                .setCarValue(haveCar ? "有" : "无")
                .setIncome(decimalFormat.format(income/count))
                .setPayout(decimalFormat.format(payout/count))
                .setCreditAmount(decimalFormat.format(creditAmount));

        return conclusionDO;
    }

    /**
     * 同时更新客户-是否为借款主体-是/否，是否有调查结论-是/否
     * @param
     */
    private void setCustomerIsConcluded(Long id, String isBorrower, String isConcluded) {
        CustomerDO customerDO = new CustomerDO();
        customerDO.setId(id)
                .setIsBorrower(isBorrower)
                .setIsConcluded(isConcluded);
        try {
            customerDOMapper.updateByPrimaryKeySelective(customerDO);
        } catch (Exception e) {
            logger.info("新建调查结论异常:" + e.getMessage());
            throw new ServiceException("新建调查结论异常");
        }
    }


    /**
     * 查询该家庭的身份证号进行校验
     * @param householdId
     */
    private void checkFamilyIdNumber(String householdId) {
        List<CustomerDO> familyList;
        try {
            familyList = customerDOMapper.listByHouseholdIdAndGridCode(new CustomerDO().setHouseholdId(householdId));
        } catch (Exception e) {
            logger.info("查询家庭身份证号异常:" + e.getMessage());
            throw new ServiceException("查询家庭身份证号异常");
        }
        String idNumbers = "";
        for (CustomerDO customerDO : familyList) {
            if (StringUtil.isNotIdNumber(customerDO.getIdNumber())) {
                idNumbers = customerDO.getIdNumber() +";";
            }
        }
        if (StringUtil.isNotBlank(idNumbers)) {
            throw new ServiceException("该家庭中有以下身份证号无效，请先修改："+ idNumbers);
        }


    }

    /**
     * 查询该家庭之前的有效结论，置为无效状态
     * @param householdId
     */
    private void setIsValid(String householdId) {
        // 查询该家庭之前的有效结论，置为无效状态
        List<ConclusionDO> conclusionDOList;
        try {
            conclusionDOList = conclusionDOMapper.listByHouseholdId(householdId);
        } catch (Exception e) {
            logger.info("查看调查结论异常:" + e.getMessage());
            throw new ServiceException("查看调查结论异常");
        }
        if (conclusionDOList != null && conclusionDOList.size() != 0) {
            for (ConclusionDO conclusionDO : conclusionDOList) {
                ConclusionDO updateIsValid = new ConclusionDO();
                updateIsValid.setId(conclusionDO.getId()).setIsValid("否");
                try {
                    conclusionDOMapper.updateByPrimaryKeySelective(updateIsValid);
                } catch (Exception e) {
                    logger.info("更新之前调查结论为无效异常:" + e.getMessage());
                    throw new ServiceException("更新之前调查结论为无效异常");
                }
            }
        }
    }

    /**
     * 列表
     *
     * @param idNumber
     * @return
     */
    @Override
    public List<ConclusionDO> listByIdNumber(String idNumber) throws Exception{
        try {
            return conclusionDOMapper.listByIdNumber(idNumber);
        } catch (Exception e) {
            logger.info("查看调查结论异常:" + e.getMessage());
            throw new ServiceException("查看调查结论异常");
        }
    }

    /**
     * 户号列表
     *
     * @param householdId
     * @return
     * @throws Exception
     */
    @Override
    public List<ConclusionDO> listByHouseholdId(String householdId) throws Exception {
        try {
            return conclusionDOMapper.listByHouseholdId(householdId);
        } catch (Exception e) {
            logger.info("查看调查结论异常:" + e.getMessage());
            throw new ServiceException("查看调查结论异常");
        }
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     */
    @Override
    public boolean updateByPrimaryKeySelective(ConclusionDO record) {
        return false;
    }
}
