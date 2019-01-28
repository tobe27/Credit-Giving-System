package com.example.service.business;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@Mapper
@Repository
public interface BusinessDOMapper {

    BusinessDO getBusinessByIdNumber(String idNumber);

    List<ODSAssureDO> listAssureByIdNumber(String householdId);

    List<ODSDepositDO> listDepositByIdNumber(String householdId);

    List<ODSLoanDO> listLoanByIdNumber(String householdId);
}
