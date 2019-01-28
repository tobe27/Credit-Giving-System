package com.example.web.controller;

import com.example.service.interview.CustomerInterviewDO;
import com.example.service.interview.CustomerInterviewDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-15
 */
@RestController
@RequestMapping("/customer")
public class CustomerInterviewDOController {
	private final
	CustomerInterviewDOService customerInterviewDOService;
	@Autowired
	public CustomerInterviewDOController(CustomerInterviewDOService customerInterviewDOService) {
		this.customerInterviewDOService = customerInterviewDOService;
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/{id}", method = RequestMethod.DELETE)
	public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		return new ResultBean().success( customerInterviewDOService.deleteByPrimaryKey(id));
	}

	/**
	 * 修改
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/{id}", method = RequestMethod.PUT)
	public ResultBean update(@RequestBody CustomerInterviewDO customerInterviewDO) throws Exception {
		customerInterviewDOService.updateByPrimaryKeySelective(customerInterviewDO);
		return new ResultBean().success();
	}


	/**
	 * 修改借款主体
	 * ==面签
	 * ==客户
	 * ==白名单与标签库
	 * @param customerInterviewDO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview", method = RequestMethod.PUT)
	public ResultBean modify(@RequestBody CustomerInterviewDO customerInterviewDO) throws Exception {
		customerInterviewDOService.modifyBorrower(customerInterviewDO);
		return new ResultBean().success();
	}


	/**
	 * 查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/{id}", method = RequestMethod.GET)
	public ResultBean select(@RequestBody @PathVariable Long id) throws Exception {
		return new ResultBean().success(  customerInterviewDOService.selectByPrimaryKey(id));
	}

	/**
	 * 作废面签
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/ban/{id}", method = RequestMethod.PUT)
	public ResultBean ban(@RequestBody CustomerInterviewDO customerInterviewDO) throws Exception {
		customerInterviewDOService.banInterview(customerInterviewDO);
		return new ResultBean().success();
	}

	/**
	 * 分页
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/list", method = RequestMethod.GET)
	public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
		PageInfo<CustomerInterviewDO> pageInfo = new PageInfo<>(customerInterviewDOService.getList(map));
		List<CustomerInterviewDO> list=pageInfo.getList();
		return new ResultBean().success(pageInfo.getTotal(), list);
	}

	/**
	 * 分页
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview/ban/list", method = RequestMethod.GET)
	public ResultBean listBanPage(@RequestParam Map<String,Object> map) throws Exception {
		PageInfo<CustomerInterviewDO> pageInfo = new PageInfo<>(customerInterviewDOService.listBanInterview(map));
		List<CustomerInterviewDO> list=pageInfo.getList();
		return new ResultBean().success(pageInfo.getTotal(), list);
	}

	/**
	 * 新增
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interview", method = RequestMethod.POST)
	public ResultBean add(@RequestBody List<CustomerInterviewDO> list) throws Exception {
		if(list.isEmpty()) {
			return new ResultBean().fail("没有数据");
		}else {
			for(CustomerInterviewDO ivo:list) {
				customerInterviewDOService.insertSelective(ivo);
			}
		}
		return new ResultBean().success();

	}
}
