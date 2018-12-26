package com.example.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.approval.CustomerApprovalDO;
import com.example.service.approval.CustomerApprovalDOService;
import com.example.web.entity.ResultBean;

/**
 * @author Created by W.S.T on 2018-12-16
 */
@RestController
@RequestMapping("/customer")
public class CustomerApprovalDOController {
	private final
    CustomerApprovalDOService customerApprovalDOService;
	 @Autowired
	 public CustomerApprovalDOController(CustomerApprovalDOService customerApprovalDOService) {
	      this.customerApprovalDOService = customerApprovalDOService;
	 }
	 /**
	     * 新增
	     * @param list
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/approval", method = RequestMethod.POST)
	    public ResultBean add(@RequestBody List<CustomerApprovalDO> list) throws Exception {
		  if(list.isEmpty()) {
			  return new ResultBean().fail("没有数据");
		  }else {
			  return new ResultBean().success( customerApprovalDOService.insertSelective(list));
		  }
		
	       
	    }
	  /**
	     * 查询
	     * @param interviewId
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/approval/{interviewId}", method = RequestMethod.GET)
	    public ResultBean select(@RequestBody @PathVariable Long interviewId) throws Exception {
	        return new ResultBean().success(  customerApprovalDOService.getListByInterviewId(interviewId));
	    }
	  
}
