package com.example.service.interview;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerInterviewDO implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7369703972200882016L;

	private Long id;

    private Long customerId;

    private String customerName;

    private String idNumber;

    private String sex;

    private String isMarried;

    private String education;

    private String phone;

    private String familySize;

    private String nativeAddress;

    private String residenceAddress;

    private String cardHolder;

    private String cardNumber;

    private String appraiseSum;

    private String appliedSum;

    private String timeLimit;

    private String provide;

    private String repayment;

    private String interestSettlement;

    private String type;

    private Long userId;

    private String userName;

    private String orgCode;

    private String orgName;

    private String gridName;

    private String gridCode;

    private String comment;

    private String approvalStatus;

    private String status;

    private String attachFlag;

    private Long createdAt;

    private Long updatedAt;

    private String purpose;
    private String householdId;
    private String photo;

    // 变更借款主体使用
    private Long oldCustomerId; // 客户信息
    private String oldIdNumber; // 白名单
    private String oldCustomerName; // 白名单
    private Integer option; // 作废时选择要进入的名单，1-黑， 2-灰， 3-贫困户， 4-蓝名单

}