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
import com.example.service.white.CustomerWhiteDO;
import com.example.service.white.CustomerWhiteDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Created by W.S.T on 2018-12-12
 */
@RestController
@RequestMapping("/customer")
public class CustomerWhiteDOController {
	private final
    CustomerWhiteDOService customerWhiteDOService;
	 @Autowired
	 public CustomerWhiteDOController(CustomerWhiteDOService customerWhiteDOService) {
	      this.customerWhiteDOService = customerWhiteDOService;
	 }
	 /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/white/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  customerWhiteDOService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/white/{id}", method = RequestMethod.GET)
	    public ResultBean select(@RequestBody @PathVariable Long id) throws Exception {
	        return new ResultBean().success(  customerWhiteDOService.selectByPrimaryKey(id));
	    }
	  /**
	     * 分页
	     * @param CustomerWhiteDO
	     * @param pageNum
	     * @param pageSize
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/white/list", method = RequestMethod.GET)
	    public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
	       PageInfo<CustomerWhiteDO> pageInfo = new PageInfo<>(customerWhiteDOService.getList(map));
	       List<CustomerWhiteDO> list=pageInfo.getList();
	       return new ResultBean().success(pageInfo.getTotal(), list);
	    }
}
