package com.example.service.resident;

import com.example.common.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
public class ResidentDO {
    private Long id;

    @NotBlank(message = "姓名不能为空！")
    private String name;

    @NotBlank(message = "身份证号码不能为空！")
    @Size(min = 18, max = 18, message = "身份证号码必须18位！")
    private String idNumber;

    @NotBlank(message = "联系方式不能为空！")
    @Pattern(regexp = StringUtil.PHONE_NUMBER, message = "联系方式必须是11位手机号！")
    private String contact;

    @NotBlank(message = "县级名称不能为空！")
    private String county;

    @NotBlank(message = "乡镇不能为空！")
    private String township;

    @NotBlank(message = "行政村不能为空！")
    private String village;

    @NotBlank(message = "自然村不能为空！")
    private String group;

    @NotBlank(message = "户号不能为空！")
    private String householdId;

    @NotBlank(message = "与户主关系不能为空！")
    private String relationship;

    @NotBlank(message = "是否纳入名单库不能为空！")
    private String isInList;

    @Size(max = 100, message = "备注最长100字！")
    private String remark;

    @NotNull(message = "客户经理编号不能为空！")
    private Long userId;

    @NotBlank(message = "客户经理姓名不能为空！")
    private String userName;

    @NotBlank(message = "机构名称不能为空！")
    private String orgName;

    @NotBlank(message = "网格代码不能为空！")
    private String gridCode;

    private Long createdAt;

    private Long updatedAt;

//    关联使用字段
    private String gridName;
    private Long roleId;
    private String orgCode;
    private List<String> orgCodeList;
    private String oldIdNumber;
    private String oldHouseholdId;
    private String oldName;
    private String borrower;
    private String isConcluded;
    private Integer validTime;

}