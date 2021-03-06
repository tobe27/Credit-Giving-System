package com.example.service.grey;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerGreyDO implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5968004747031430668L;

	private Long id;

    private Long customerId;
    @NotNull(message = "客户姓名不能为空！")
   	@Size(max = 20, message = "客户姓名最长20位！")
    private String customerName;
    @NotNull(message = "户籍号不能为空！")
   	@Size(max = 20, message = "户籍号最长20位！")
    private String householdId;
    @NotNull(message = "客户身份证号不能为空！")
   	@Size(max = 20, message = "客户身份证号最长20位！")
    private String idNumber;

    private String reason;

    private String phone;
    @NotNull(message = "网格编号不能为空！")
   	@Size(max = 20, message = "网格编号最长20位！")
    private String gridCode;
    @NotNull(message = "网格名称不能为空！")
	@Size(max = 20, message = "网格名称最长20位！")
    private String gridName;
    @NotNull(message = "机构号不能为空！")
   	@Size(max = 20, message = "机构号最长20位！")
    private String orgCode;
    @NotNull(message = "机构名称不能为空！")
   	@Size(max = 20, message = "机构名称最长20位！")
    private String orgName;
    @NotNull(message = "客户经理不能为空！")
    private Long userId;
    @NotNull(message = "客户经理名称不能为空！")
   	@Size(max = 20, message = "客户经理名称最长20位！")
    private String userName;

    private String status;

    private Long createdAt;

    private Long updatedAt;


}