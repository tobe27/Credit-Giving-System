package com.example.service.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class TagDO {
    private Long id;

    @NotBlank(message = "标签名称不能为空！")
    @Size(max = 20, message = "标签名称最长10位！")
    private String name;

    @NotBlank(message = "标签描述不能为空！")
    @Size(max = 20, message = "标签描述最长20位！")
    private String description;

    private String type;

    @Size(max = 40, message = "备注最长40字！")
    private String remark;

    private Long createdAt;

    private Long updatedAt;
}