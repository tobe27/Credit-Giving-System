package com.example.service.business;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
public interface BusinessDOService {
    /**
     * 获取家庭业务信息
     * @param idNumber
     * @return
     * @throws Exception
     */
    BusinessDO getBusinessByIdNumber(String idNumber) throws Exception;

    /**
     * 家庭担保
     * @param householdId
     * @return
     */
    List<ODSAssureDO> listAssureByIdNumber(String householdId) throws Exception;

    /**
     * 家庭存款
     * @param householdId
     * @return
     */
    List<ODSDepositDO> listDepositByIdNumber(String householdId) throws Exception;

    /**
     * 家庭贷款
     * @param householdId
     * @return
     */
    List<ODSLoanDO> listLoanByIdNumber(String householdId) throws Exception;
}
