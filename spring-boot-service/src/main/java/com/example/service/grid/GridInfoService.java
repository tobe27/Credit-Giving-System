package com.example.service.grid;

import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-5
 */
public interface GridInfoService {
	  /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	boolean deleteByPrimaryKey(Long id)throws Exception;
	/**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
	boolean insertSelective(GridInfoDO record)throws Exception;
	 /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    GridInfoDO selectByPrimaryKey(Long id)throws Exception;
    /**
     * 编辑
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(GridInfoDO record)throws Exception;
    /**
     * @param record
     * @return
     * @throws Exception
     */
    List<GridInfoDO> list(Map<String,Object> map)throws Exception;
    /**查询网格数据
     * @param gridCode
     * @return
     * @throws Exception
     */
    Map<String,Object> getGridData(String gridCode)throws Exception;
    
}
