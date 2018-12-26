package com.example.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.black.CustomerBlackDO;
import com.example.service.black.CustomerBlackDOService;
import com.example.service.grid.GridInfoDO;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Created by W.S.T on 2018-12-14
 */
@RestController
@RequestMapping("/customer")
public class CustomerBlackDOController {
	private final
    CustomerBlackDOService customerBlackDOService;
	 @Autowired
	 public CustomerBlackDOController(CustomerBlackDOService customerBlackDOService) {
	      this.customerBlackDOService = customerBlackDOService;
	 }
	 /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/black/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  customerBlackDOService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  /**
	     * 修改
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/black/{id}", method = RequestMethod.PUT)
	    public ResultBean update(@RequestBody CustomerBlackDO customerBlackDO) throws Exception {
		  customerBlackDOService.updateByPrimaryKeySelective(customerBlackDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/black/{id}", method = RequestMethod.GET)
	    public ResultBean select(@RequestBody @PathVariable Long id) throws Exception {
	        return new ResultBean().success(  customerBlackDOService.selectByPrimaryKey(id));
	    } 
	  /**
	     * 分页
	     * @param 
	     * @param pageNum
	     * @param pageSize
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/black/list", method = RequestMethod.GET)
	    public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
	       PageInfo<CustomerBlackDO> pageInfo = new PageInfo<>(customerBlackDOService.getList(map));
	       List<CustomerBlackDO> list=pageInfo.getList();
	       return new ResultBean().success(pageInfo.getTotal(), list);
	    }
	    /**
	     * 新建
	     * @param CustomerBlackDO
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/black", method = RequestMethod.POST)
	    public ResultBean insert(@RequestBody CustomerBlackDO customerBlackDO) throws Exception {
		  customerBlackDOService.insertSelective(customerBlackDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 批量删除
	     * @param roleId orgCode
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/black/bath", method = RequestMethod.DELETE)
	    public ResultBean deleteBatch(Long roleId,String orgCode) throws Exception {
		  customerBlackDOService.deleteByOrgCode(roleId, orgCode);
	        return new ResultBean().success();
	    }
}
