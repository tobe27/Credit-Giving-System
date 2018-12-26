package com.example.service.business;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@Data
@Accessors(chain = true)
public class BusinessDO {
    private String idNumber;
    private String householdId;
    // 担保信息
    private List<ODSAssureDO> assureDOList;
    // 存款信息
    private List<ODSDepositDO> depositDOList;
    // 贷款信息
    private List<ODSLoanDO> loanDOList;
}
