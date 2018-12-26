package com.example.service.interview;

import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-15
 */
public interface CustomerInterviewDOService {
	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
    */
	  boolean deleteByPrimaryKey(Long id)throws Exception;
	    /**
	     * 新增
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean insertSelective(CustomerInterviewDO record)throws Exception;
	  /**
	     * 查询
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  CustomerInterviewDO selectByPrimaryKey(Long id)throws Exception;
	  /**
	     * 修改
	     * @param record
	     * @return
	     * @throws Exception
	     */
	  boolean updateByPrimaryKeySelective(CustomerInterviewDO record)throws Exception;
	  /**
	     * 分页
	     * @param record
	     * @return
	     * @throws Exception
	     */
	    List<CustomerInterviewDO> getList(Map<String,Object> map)throws Exception;

}
