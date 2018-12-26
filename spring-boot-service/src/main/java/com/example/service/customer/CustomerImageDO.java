package com.example.service.customer;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class CustomerImageDO implements Serializable{
  
	private static final long serialVersionUID = 5579767100709993671L;

	private Long id;
    @NotNull(message = "客户姓名不能为空！")
	@Size(max = 18, message = "客户姓名最长18位！")
    private String customerName;
    @NotNull(message = "客户身份证号不能为空！")
   	@Size(max = 18, message = "身份证号最长18位！")
    private String idNumber;
    @NotNull(message = "网格编号不能为空！")
	@Size(max = 20, message = "网格编号最长20位！")
    private String gridCode;

    private String originalName;

    private String systemName;

    private String path;
    @NotNull(message = "图片上传人不能为空")
 
    private Long uploadUserId;
    @NotNull(message = "图片类型不能为空！")
   	@Size(max = 5, message = "类型长度最长5位")
    private String type;

    private String deleteFlag;

    private String deletePath;

    private Long deleteTime;

    private Long createdAt;

    private Long updatedAt;

    
}