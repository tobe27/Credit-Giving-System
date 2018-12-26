package com.example.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.customer.CustomerDO;
import com.example.service.grid.GridInfoDO;
import com.example.service.grid.GridInfoService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Created by W.S.T on 2018-12-6
 */
@RestController
@RequestMapping
public class GridInfoController {
	  private final
	    GridInfoService gridInfoService;
	  @Autowired
	  public GridInfoController( GridInfoService gridInfoService) {
	        this.gridInfoService = gridInfoService;
	    }  
	  
	  /**
	     * 新建
	     * @param gridInfoDO
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grid", method = RequestMethod.POST)
	    public ResultBean insert(@RequestBody @Validated GridInfoDO gridInfoDO) throws Exception {
		  gridInfoService.insertSelective(gridInfoDO);
	        return new ResultBean().success();
	    }
	  /**
	     * 修改
	     * @param gridInfoDO
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grid/{id}", method = RequestMethod.PUT)
	    public ResultBean update(@RequestBody @Validated GridInfoDO gridInfoDO) throws Exception {
		  gridInfoService.updateByPrimaryKeySelective(gridInfoDO);
	        return new ResultBean().success();
	    }
	  
	  /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grid/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  gridInfoService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  /**
	     * 详情
	     * @param id
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grid/{id}", method = RequestMethod.GET)
	    public ResultBean get(@PathVariable Long id) throws Exception {
	        return new ResultBean().success(gridInfoService.selectByPrimaryKey(id));
	    }
	    /**
	     * 分页
	     * @param customerDO
	     * @param pageNum
	     * @param pageSize
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grid/list", method = RequestMethod.GET)
	    public ResultBean listPage(@RequestParam Map<String,Object> map) throws Exception {
	        PageInfo<GridInfoDO> pageInfo = new PageInfo<>(gridInfoService.list(map));
	        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
	    }/**
	     * 网格数据
	     * @param gridCode
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/gridInfo/data/{gridCode}", method = RequestMethod.GET)
	    public ResultBean get( @PathVariable String gridCode) throws Exception {
	        return new ResultBean().success(gridInfoService.getGridData(gridCode));
	    }
}
