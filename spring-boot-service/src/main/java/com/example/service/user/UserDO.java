package com.example.service.user;

import com.example.common.util.StringUtil;
import com.example.common.validation.InsertGroup;
import com.example.common.validation.LoginGroup;
import com.example.common.validation.PartGroup;
import com.example.common.validation.UpdateGroup;
import com.example.service.role.RoleDO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Accessors(chain = true)
@Data
public class UserDO implements Serializable {

    private static final long serialVersionUID = 4382629624053154693L;

    private Long id;


    @NotNull(message = "登录代码能为空！", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    @Pattern(regexp = StringUtil.ALPHABET_NUMERIC, message = "登录代码只能是英文、数字、下划线",groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    @Size(max = 20, message = "登录代码最长20位！", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    private String username;

    @NotNull(message = "密码不能为空！", groups = {InsertGroup.class, LoginGroup.class})
    @Pattern(regexp = StringUtil.ALPHABET_NUMERIC_SYMBOL, message = "密码只能是英文、数字、下划线，及+-.@",groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class, PartGroup.class})
    @Size(min = 6, max = 20, message = "密码必须6-20位！", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class, PartGroup.class})
    private String password;

    @NotNull(message = "姓名不能为空！", groups = {InsertGroup.class, UpdateGroup.class})
    private String name;

    @NotNull(message = "工号不能为空！", groups = {InsertGroup.class, UpdateGroup.class})
    private String workId;

    @Size(min = 18, max = 18, message = "身份证必须是18位！", groups = {InsertGroup.class, UpdateGroup.class, PartGroup.class})
    private String idNumber;

    @Pattern(regexp = "^[012]$", message = "性别只能是0、1或2!", groups = {InsertGroup.class, UpdateGroup.class, PartGroup.class})
    private String gender;

    private String contact;

    @Pattern(regexp = StringUtil.EMAIL, message = "邮箱格式不正确！", groups = {InsertGroup.class, UpdateGroup.class, PartGroup.class})
    private String email;

    @NotBlank(message = "机构代码不能为空！", groups = {InsertGroup.class, UpdateGroup.class})
    private String orgCode;

    private String corpName;

    @NotBlank(message = "状态不能为空！", groups = {InsertGroup.class, UpdateGroup.class})
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1!", groups = {InsertGroup.class, UpdateGroup.class, PartGroup.class})
    private String status;

    private Long lastLoginAt;

    private Long createdAt;

    private Long updatedAt;

    private List<RoleDO> roles;

    // 关联使用
    private String orgName;
    private Long roleId;
    private String gridCode;
    private List<String> orgCodeList;
    private String taskDate;

}