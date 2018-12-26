package com.example.service.grid;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class GridReviewDO implements Serializable{
   
	private static final long serialVersionUID = -1338383457806704348L;

	private Long id;
	@NotNull(message = "网格编号不能为空！")
	@Size(max = 20, message = "网格编号最长20位！")
    private String gridCode;
	@NotNull(message = "网格名称不能为空！")
    @Size(max = 20, message = "网格名称最长20位！")
    private String gridName;

    private String phone;

    private String gridReviewName;

    private String idNumber;

    private String duties;

    private String description;

    private String address;

    private Long createdAt;

    private Long updatedAt;

    private String type;

    private String status;

    
}