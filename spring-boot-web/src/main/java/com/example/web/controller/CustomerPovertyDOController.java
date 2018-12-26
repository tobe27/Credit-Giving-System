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

import com.example.service.black.CustomerBlackDO;
import com.example.service.poverty.CustomerPovertyDO;
import com.example.service.poverty.CustomerPovertyDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Created by W.S.T on 2018-12-14
 */
@RestController
@RequestMapping("/customer")
public class CustomerPovertyDOController {
	private final
    CustomerPovertyDOService customerPovertyDOService;
	 @Autowired
	 public CustomerPovertyDOController(CustomerPovertyDOService customerPovertyDOService) {
	      this.customerPovertyDOService = customerPovertyDOService;
	 }
	 /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/poverty/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  customerPovertyDOService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  /**
	     * 修改
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/poverty/{id}", method = RequestMethod.PUT)
	    public ResultBean update(@RequestBody CustomerPovertyDO customerPovertyDO) throws Exception {
		  customerPovertyDOService.updateByPrimaryKeySelective(customerPovertyDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/poverty/{id}", method = RequestMethod.GET)
	    public ResultBean select(@RequestBody @PathVariable Long id) throws Exception {
	        return new ResultBean().success(  customerPovertyDOService.selectByPrimaryKey(id));
	    } 
	  /**
	     * 分页
	     * @param CustomerWhiteDO
	     * @param pageNum
	     * @param pageSize
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/poverty/list", method = RequestMethod.GET)
	    public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
	       PageInfo<CustomerPovertyDO> pageInfo = new PageInfo<>(customerPovertyDOService.getList(map));
	       List<CustomerPovertyDO> list=pageInfo.getList();
	       return new ResultBean().success(pageInfo.getTotal(), list);
	    }
	    /**
	     * 新建
	     * @param CustomerBlackDO
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/poverty", method = RequestMethod.POST)
	    public ResultBean insert(@RequestBody CustomerPovertyDO customerPovertyDO) throws Exception {
		  customerPovertyDOService.insertSelective(customerPovertyDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 批量删除
	     * @param roleId orgCode
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/poverty/bath", method = RequestMethod.DELETE)
	    public ResultBean deleteBatch(Long roleId,String orgCode) throws Exception {
		  customerPovertyDOService.deleteByOrgCode(roleId, orgCode);
	        return new ResultBean().success();
	    }
}
