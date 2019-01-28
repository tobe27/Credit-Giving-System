package com.example.service.customer;

import com.example.common.util.StringUtil;
import com.example.common.validation.DraftGroup;
import com.example.common.validation.InsertGroup;
import com.example.service.survey.SurveyDO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class CustomerDO {
    private Long id;

    private String creditId;

    @NotBlank(message = "请输入姓名！", groups = {InsertGroup.class, DraftGroup.class})
    @Size(max = 10, message = "姓名最长10位！", groups = {InsertGroup.class, DraftGroup.class})
    private String name;

    private String type;

    private String idType;

    @NotBlank(message = "请输入身份证号！", groups = {InsertGroup.class, DraftGroup.class})
    @Size(min = 18, max = 18, message = "身份证号必须是18位！", groups = {InsertGroup.class, DraftGroup.class})
    private String idNumber;

    @NotBlank(message = "请输入签发机构！", groups = {InsertGroup.class})
    @Size(max = 20, message = "签发机构最长20位！", groups = {InsertGroup.class, DraftGroup.class})
    private String signOrg;

    @NotBlank(message = "请选择是否长期有效！", groups = {InsertGroup.class})
    private String isLongTerm;

    @NotBlank(message = "请选择签发日期！", groups = {InsertGroup.class})
    private String signDate;

    private String dueDate;

    private String districtAddress;

    @NotBlank(message = "请选择出生日期！", groups = {InsertGroup.class})
    private String birthday;

    @NotBlank(message = "请选择性别！", groups = {InsertGroup.class})
    private String gender;

    @Size(max = 100, message = "详细地址最长100字", groups = {InsertGroup.class, DraftGroup.class})
    private String detailAddress;

    @NotBlank(message = "请输入邮政编码！", groups = {InsertGroup.class})
    @Pattern(regexp = StringUtil.ALPHABET_NUMERIC, message = "邮政编码只能是数字！", groups = {InsertGroup.class, DraftGroup.class})
    @Size(min = 6, max = 6, message = "邮政编码为6位数字！", groups = {InsertGroup.class, DraftGroup.class})
    private String postcode;

    @NotBlank(message = "请输入户籍地址！", groups = {InsertGroup.class})
    @Size(max = 100, message = "户籍地址最长100位", groups = {InsertGroup.class, DraftGroup.class})
    private String nativeAddress;

    @NotBlank(message = "请输入手机号！")
    @Pattern(regexp = StringUtil.PHONE_NUMBER, message = "请输入11位有效手机号！", groups = {InsertGroup.class, DraftGroup.class})
    private String phoneNumber;

    @NotBlank(message = "请选择国籍！", groups = {InsertGroup.class})
    private String nationality;

    @NotBlank(message = "请选择民族！", groups = {InsertGroup.class})
    private String nation;

    @NotBlank(message = "请选择政治面貌！", groups = {InsertGroup.class})
    private String politicsStatus;

    @NotBlank(message = "请选择学历！", groups = {InsertGroup.class})
    private String educationBackground;

    @NotBlank(message = "请选择身体状况！", groups = {InsertGroup.class})
    private String physicalCondition;

    @NotBlank(message = "请选择婚姻状况！", groups = {InsertGroup.class})
    private String maritalStatus;

    @Size(max = 10, message = "配偶姓名最长10位！", groups = {InsertGroup.class, DraftGroup.class})
    private String spouseName;

    @Size(min = 18, max = 18, message = "配偶身份证号必须18位！", groups = {InsertGroup.class, DraftGroup.class})
    private String spouseIdNumber;

    @Pattern(regexp = StringUtil.PHONE_NUMBER, message = "请输入11位有效配偶手机号！", groups = {InsertGroup.class, DraftGroup.class})
    private String spousePhoneNumber;

    @Size(max = 20, message = "配偶工作单位最长20位！", groups = {InsertGroup.class, DraftGroup.class})
    private String spouseCompanyName;

    @NotBlank(message = "请选择职业！", groups = {InsertGroup.class})
    private String careerType;

    @NotBlank(message = "请选择客户类型(税收)！", groups = {InsertGroup.class})
    private String taxType;

    @NotBlank(message = "请输入主营业务！", groups = {InsertGroup.class})
    @Size(max = 20, message = "主营业务最长20位！", groups = {InsertGroup.class, DraftGroup.class})
    private String mainBusiness;

    @NotBlank(message = "请选择从属行业！", groups = {InsertGroup.class})
    private String industryType;

    @NotBlank(message = "请选择是否本行员工！", groups = {InsertGroup.class})
    private String isStaff;

    @NotBlank(message = "请选择是否股东！", groups = {InsertGroup.class})
    private String isStockholder;

    @NotBlank(message = "请选择是否国家公职人员！", groups = {InsertGroup.class})
    private String isCivilServant;

    private String isHouseOwner;

    @NotBlank(message = "户号不能为空！", groups = {InsertGroup.class})
    private String householdId;

    @Size(max = 20, message = "单位名称最长20位！", groups = {InsertGroup.class, DraftGroup.class})
    private String companyName;

    @Pattern(regexp = StringUtil.NUMERIC, message = "工作年限只能是数字！", groups = {InsertGroup.class, DraftGroup.class})
    @Size(max = 2, message = "工作年限最长2位！", groups = {InsertGroup.class, DraftGroup.class})
    private String workYear;

    @Size(max = 100, message = "单位地址最长100位！", groups = {InsertGroup.class, DraftGroup.class})
    private String companyAddress;

    private String isFarmer;

    private String isBuyHome;

    @Size(max = 20, message = "小区名称最长20位！", groups = {InsertGroup.class, DraftGroup.class})
    private String cellName;

    @Size(max = 100, message = "小区地址最长100位！", groups = {InsertGroup.class, DraftGroup.class})
    private String cellAddress;

    @NotBlank(message = "请选择居住状况！", groups = {InsertGroup.class})
    private String livingCondition;

    @NotBlank(message = "请选择是否企业主！", groups = {InsertGroup.class})
    private String isBusinessOwner;

    @NotBlank(message = "请选择是否借款主体！", groups = {InsertGroup.class})
    private String isBorrower;

    @NotBlank(message = "网格代码不能为空！", groups = {InsertGroup.class, DraftGroup.class})
    private String gridCode;

    @NotBlank(message = "请选择客户经理！", groups = {InsertGroup.class})
    private String customerManager;

    @NotBlank(message = "请选择管户经理！", groups = {InsertGroup.class})
    private String supportManager;

    @NotBlank(message = "主办机构不能为空！", groups = {InsertGroup.class, DraftGroup.class})
    private String mainOrgName;

    @NotBlank(message = "登记人不能为空！", groups = {InsertGroup.class, DraftGroup.class})
    private String registerPerson;

    @NotBlank(message = "最后修改人员不能为空！", groups = {InsertGroup.class, DraftGroup.class})
    private String lastModifyPerson;

    @Pattern(regexp = "^[012]$", message = "状态只能是0、1、2！", groups = {InsertGroup.class, DraftGroup.class})
    private String status;

    // 面谈面签
    private Long interviewId;

    private BigDecimal amount;

    private Long createdAt;

    private Long updatedAt;

    private String relationship;

    // 调查信息关联
    private Integer validTime;

    private String isConcluded;

    private Integer age;

    private BigDecimal outEnsureAmount;

    // 关联使用
    private String gridName;
    private Long roleId;
    private Long userId;
    private String orgCode;
    private List<CustomerTagRelationDO> tags;
    private List<SurveyDO> surveyDOList;
    private List<String> orgCodeList;
    private Long tagId;
    private Integer calType;
    private String senator;
}