package com.example.service.conclusion;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ConclusionDO {
    private Long id;

    private String name;

    private String idNumber;

    private String householdId;

    private String isFamiliar;

    private String negativeReason;

    private String remark;

    private String outWork;

    private String houseValue;

    private String carValue;

    private String mainBusiness;

    private String scale;

    private String income;

    private String payout;

    private String creditAmount;

    private String borrower;

    private String surveyType;

    @NotNull(message = "客户经理不能为空")
    private Long userId;

    private String isValid;

    private String date;

    private Long createdAt;

    private Long updatedAt;

    // 白名单导入使用
    @NotBlank(message = "网格不能为空")
    private String gridCode;

    @NotBlank(message = "网格不能为空")
    private String gridName;

    @NotBlank(message = "客户经理不能为空")
    private String userName;

    @NotBlank(message = "机构不能为空")
    private String orgCode;

    @NotBlank(message = "机构不能为空")
    private String orgName;
}