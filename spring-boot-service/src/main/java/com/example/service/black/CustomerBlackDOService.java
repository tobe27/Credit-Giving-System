package com.example.service.black;

import java.util.List;
import java.util.Map;

import com.example.service.grey.CustomerGreyDO;

/**
 * @author Created by W.S.T on 2018-12-14
 */
public interface CustomerBlackDOService {
	/**
     * 删除
     * @param map
     * @return
     * @throws Exception
    */
	  boolean delete(Map<String,String> map)throws Exception;
	  /**
	     * 批量删除
	     * @param roleId orgCode
	     * @return
	     * @throws Exception
	    */
		  boolean deleteByOrgCode(Long roleId,String orgCode)throws Exception;
	    /**
	     * 新增
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean insertSelective(CustomerBlackDO record)throws Exception;
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  CustomerBlackDO selectByPrimaryKey(Long id)throws Exception;
	  /**
	     * 修改
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean updateByPrimaryKeySelective(CustomerBlackDO record)throws Exception;
	  /**
	     * 分页
	     * @param record
	     * @return
	     * @throws Exception
	     */
	    List<CustomerBlackDO> getList(Map<String,Object> map)throws Exception;
	    /**
	     * 导入
	     * @param record
	     * @return
	     * @throws Exception
	     */
	    public Map<String,Object> importFromExcel(List<Map<String, Object>> list,Map<String,Object> paramMap)throws Exception;

}
