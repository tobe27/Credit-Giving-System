package com.example.service.business;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ODSLoanDO {
    private String creditId;

    private String name;

    private String idNumber;

    private String account;

    private String loanType;

    private String contNo;

    private String billNo;

    private BigDecimal amount;

    private BigDecimal balance;

    private String orgno;

    // 关联使用
    private String startDate;
    private String endDate;
    private String gridCode;
    private String gridName;
    private Long tagId;
    private Long userId;
    private Long roleId;
    private String userName;
    private String orgCode;
    private String orgName;
    private List<String> orgCodeList;
    private String creditAmount;

}