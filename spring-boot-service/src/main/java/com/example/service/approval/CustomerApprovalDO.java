package com.example.service.approval;

import java.io.Serializable;

import com.example.service.poverty.CustomerPovertyDO;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class CustomerApprovalDO implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1787732749567499788L;

	private Long id;

    private Long interviewId;

    private String idNumber;

    private Long customerId;

    private String approvalOpinion;

    private Long approvalRoleId;

    private String approvalUserName;

    private Long approvalUserId;

    private String approvalResult;

    private String approvalNode;

    private Long createdAt;

    private Long updatedAt;

    
}