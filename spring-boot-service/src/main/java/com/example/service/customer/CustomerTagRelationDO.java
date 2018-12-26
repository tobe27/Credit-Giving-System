package com.example.service.customer;

import com.example.service.tag.TagDO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class CustomerTagRelationDO {
    private Long id;

    private Long tagId;

    @NotBlank(message = "身份证号不能为空！")
    private String idNumber;

    // 联查
    private String tagName;

    @NotNull(message = "标签编号列表不能为空！")
    private List<Long> tagIds;

    private List<String> idNumberList;

}