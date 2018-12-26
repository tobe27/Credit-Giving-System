package com.example.service.org;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrgDO {

    private Long id;

    @NotBlank(message = "机构代码不能为空！")
    @Size(max = 20, message = "机构代码最长20位！")
    private String orgCode;

    @NotBlank(message = "核心机构代码不能为空！")
    @Size(max = 20, message = "核心机构代码最长20位！")
    private String coreOrgCode;

    @NotBlank(message = "机构名称不能为空！")
    @Size(max = 30, message = "机构名称最长30位！")
    private String orgName;

    @NotBlank(message = "上级机构代码不能为空！")
    @Size(max = 20, message = "上级机构代码最长20位！")
    private String parentOrgCode;

    private String corpOrgCode;

    @NotBlank(message = "机构等级不能为空！")
    private String level;

    @Size(max = 200, message = "备注超长！")
    private String remark;

    @NotBlank(message = "状态不能为空！")
    private String status;

    private Long createdAt;

    private Long updatedAt;

    // 查询使用
    private List<String> orgCodeList;
}