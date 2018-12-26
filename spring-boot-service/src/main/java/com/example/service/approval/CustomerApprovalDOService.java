package com.example.service.approval;

import java.util.List;

/**
 * @author Created by W.S.T on 2018-12-16
 */
public interface CustomerApprovalDOService {
	    /**
	     * 新增
	     * @param record
	     * @return
	     * @throws Exception
	     */
	int insertSelective(List<CustomerApprovalDO> recordList)throws Exception;
	  /**
	     * 查询某条面谈面签数据的审批记录
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  List<CustomerApprovalDO> getListByInterviewId(Long interviewId)throws Exception;

}
