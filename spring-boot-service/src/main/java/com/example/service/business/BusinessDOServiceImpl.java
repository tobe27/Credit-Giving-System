package com.example.service.business;

import com.example.service.customer.CustomerDOMapper;
import com.example.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@Service
public class BusinessDOServiceImpl implements BusinessDOService {
    private final BusinessDOMapper businessDOMapper;
    private final CustomerDOMapper customerDOMapper;

    @Autowired
    public BusinessDOServiceImpl(BusinessDOMapper businessDOMapper, CustomerDOMapper customerDOMapper) {
        this.businessDOMapper = businessDOMapper;
        this.customerDOMapper = customerDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(BusinessDOServiceImpl.class);

    /**
     * 获取家庭业务信息
     *
     * @param idNumber
     * @return
     * @throws Exception
     */
    @Override
    public BusinessDO getBusinessByIdNumber(String idNumber) throws Exception {
        if ("Y".equals(customerDOMapper.getODSIsYes())) {
            try {
                return businessDOMapper.getBusinessByIdNumber(idNumber);
            } catch (Exception e) {
                logger.info("获取业务信息异常:" + e.getMessage());
                throw new ServiceException("获取业务信息异常");
            }
        } else {
            throw new ServiceException("数据更新中，请稍后再试");
        }
    }

    /**
     * 家庭担保
     *
     * @param householdId
     * @return
     */
    @Override
    public List<ODSAssureDO> listAssureByIdNumber(String householdId) throws Exception {
        if ("Y".equals(customerDOMapper.getODSIsYes())) {
            try {
                return businessDOMapper.listAssureByIdNumber(householdId);
            } catch (Exception e) {
                logger.info("获取业务信息异常:" + e.getMessage());
                throw new ServiceException("获取业务信息异常");
            }
        } else {
            throw new ServiceException("数据更新中，请稍后再试");
        }
    }

    /**
     * 家庭存款
     *
     * @param householdId
     * @return
     */
    @Override
    public List<ODSDepositDO> listDepositByIdNumber(String householdId) throws Exception {
        if ("Y".equals(customerDOMapper.getODSIsYes())) {
            try {
                return businessDOMapper.listDepositByIdNumber(householdId);
            } catch (Exception e) {
                logger.info("获取业务信息异常:" + e.getMessage());
                throw new ServiceException("获取业务信息异常");
            }
        } else {
            throw new ServiceException("数据更新中，请稍后再试");
        }
    }

    /**
     * 家庭贷款
     *
     * @param householdId
     * @return
     */
    @Override
    public List<ODSLoanDO> listLoanByIdNumber(String householdId) throws Exception {
        if ("Y".equals(customerDOMapper.getODSIsYes())) {
            try {
                return businessDOMapper.listLoanByIdNumber(householdId);
            } catch (Exception e) {
                logger.info("获取业务信息异常:" + e.getMessage());
                throw new ServiceException("获取业务信息异常");
            }
        } else {
            throw new ServiceException("数据更新中，请稍后再试");
        }
    }
}
