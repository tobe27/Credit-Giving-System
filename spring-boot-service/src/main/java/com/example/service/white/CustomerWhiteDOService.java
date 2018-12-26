package com.example.service.white;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Created by W.S.T on 2018-12-12
 */
public interface CustomerWhiteDOService {
	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	boolean deleteByPrimaryKey(long id) throws Exception;
	 /**
     * 新增
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertSelective(CustomerWhiteDO record)throws Exception;
    /**
     * 查询
     * @param id
     * @return
     * @throws Exception
     */
    CustomerWhiteDO selectByPrimaryKey(long id)throws Exception;
    /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(CustomerWhiteDO record)throws Exception;
    /**
     * 分页
     * @param record
     * @return
     * @throws Exception
     */
    List<CustomerWhiteDO> getList(Map<String,Object> map)throws Exception;
    /**
     * 导出整村授信批量授信审批表
     * @param record 
     * @return
     * @throws Exception
     */
    List<CustomerWhiteDO> export2Excel(Map<String,Object> map,HttpServletResponse response)throws Exception;
    /**
     * 导出
     * @param record
     * @return
     * @throws Exception
     */
    List<CustomerWhiteDO> exportToExcel(Map<String,Object> map,HttpServletResponse response)throws Exception;
}
