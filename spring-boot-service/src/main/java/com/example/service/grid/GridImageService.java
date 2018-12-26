package com.example.service.grid;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Created by W.S.T on 2018-12-5
 */
public interface GridImageService {
	  /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	boolean deleteByPrimaryKey(Long id)throws Exception;
	/**
     * 新建/修改
     * @param record
     * @return
     * @throws Exception
     */
	boolean insertSelective(MultipartFile file,GridImageDO record)throws Exception;
    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    GridImageDO selectByPrimaryKey(Long id)throws Exception;
    /**
     * @param record
     * @return
     * @throws Exception
     */
    // List<GridImageDO> getList(String GridCode)throws Exception;
     /**
      * @param record
      * @return
      * @throws Exception
      */
      List<Map<String,Object>> getBaseCodeList(String GridCode,String type)throws Exception;
      
}
