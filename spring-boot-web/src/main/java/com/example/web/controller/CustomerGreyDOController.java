package com.example.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Created by W.S.T on 2018-12-14
 */
@RestController
@RequestMapping("/customer")
public class CustomerGreyDOController {
	private final
    CustomerGreyDOService customerGreyDOService;
	 @Autowired
	 public CustomerGreyDOController(CustomerGreyDOService customerGreyDOService) {
	      this.customerGreyDOService = customerGreyDOService;
	 }
	 /**
	     * 删除
	     * @param map
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grey/delete", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestParam Map<String,String> map) throws Exception {
		  customerGreyDOService.deleteGrey(map);
		  return new ResultBean().success();
	    }
	  /**
	     * 修改
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grey/{id}", method = RequestMethod.PUT)
	    public ResultBean update(@RequestBody CustomerGreyDO customerGreyDO) throws Exception {
		  customerGreyDOService.updateByPrimaryKeySelective(customerGreyDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grey/{id}", method = RequestMethod.GET)
	    public ResultBean select(@RequestBody @PathVariable Long id) throws Exception {
	        return new ResultBean().success(  customerGreyDOService.selectByPrimaryKey(id));
	    } 
	  /**
	     * 分页
	     * @param CustomerWhiteDO
	     * @param pageNum
	     * @param pageSize
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grey/list", method = RequestMethod.GET)
	    public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
	       PageInfo<CustomerGreyDO> pageInfo = new PageInfo<>(customerGreyDOService.getList(map));
	       List<CustomerGreyDO> list=pageInfo.getList();
	       return new ResultBean().success(pageInfo.getTotal(), list);
	    }
}
