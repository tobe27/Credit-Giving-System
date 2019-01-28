package com.example.service.grey;

import java.util.List;
import java.util.Map;

import com.example.service.white.CustomerWhiteDO;

/**
 * @author Created by W.S.T on 2018-12-12
 */
public interface CustomerGreyDOService {
	 /**
      * 删除
      * @param map
      * @return
      * @throws Exception
     */
	  boolean deleteGrey(Map<String,String> map)throws Exception;
	    /**
	     * 新增
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean insertSelective(CustomerGreyDO record)throws Exception;
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  CustomerGreyDO selectByPrimaryKey(Long id)throws Exception;
	  /**
	     * 修改
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean updateByPrimaryKeySelective(CustomerGreyDO record)throws Exception;
	  /**
	     * 分页
	     * @param record
	     * @return
	     * @throws Exception
	     */
	    List<CustomerGreyDO> getList(Map<String,Object> map)throws Exception;

}
