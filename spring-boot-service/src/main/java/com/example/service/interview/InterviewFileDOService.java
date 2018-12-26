package com.example.service.interview;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Created by W.S.T on 2018-12-16
 */
public interface InterviewFileDOService {
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
	  boolean insertSelective(MultipartFile file,InterviewFileDO record)throws Exception;
	  /**
	     * 分页
	     * @param record
	     * @return
	     * @throws Exception
	     */
	    List<Map<String,Object>> getList(InterviewFileDO record)throws Exception;
}
