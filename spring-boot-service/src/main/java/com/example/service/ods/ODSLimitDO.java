package com.example.service.ods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ODSLimitDO {
    private String creditId;

    private String name;

    private String idNumber;

    private String lmtAmount;

    private String startDate;

    private String endDate;

    private String lmtType;

    // 关联使用
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