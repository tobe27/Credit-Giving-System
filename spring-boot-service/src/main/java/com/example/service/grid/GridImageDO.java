package com.example.service.grid;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GridImageDO implements Serializable {
  
	private static final long serialVersionUID = 5717768292398813434L;

	private Long id;
	@NotNull(message = "网格编号不能为空！")
	@Size(max = 20, message = "网格编号最长20位！")
    private String gridCode;

    private String originalName;

    private String systemName;

    private String path;
    @NotNull(message = "客户经理不能为空！")
    private Long userId;

    private String type;

    private String status;

    private String deletePath;

    private Long deleteTime;

    private Long createdAt;

    private Long updatedAt;

    private String comment;

    private String imageNum;

   
}