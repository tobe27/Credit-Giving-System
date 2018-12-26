package com.example.service.blue;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class BlueDO {
    private String creditId;

    private String name;

    private String idNumber;

    private BigDecimal amountSum;

    private BigDecimal balanceSum;

    private String startDate;

    private String endDate;

    private BigDecimal lmtAmount;

    private String custManagerId;

    private String orgId;

    // 关联使用
    private Long userId;
    private Long roleId;
    private String orgCode;
    private List<String> orgCodeList;
}