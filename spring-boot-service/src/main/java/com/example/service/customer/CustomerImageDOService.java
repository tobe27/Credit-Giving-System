package com.example.service.customer;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
/**
 * @author Created by W.S.T on 2018-12-7
 */
public interface CustomerImageDOService {
	/**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	boolean deleteByPrimaryKey(Long id) throws Exception;
	 /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
	boolean insertSelective(MultipartFile file,CustomerImageDO record)throws Exception;

    /**
     * 查看
     * @param id
     * @return
     * @throws Exception
     */
    CustomerImageDO selectByPrimaryKey(Long id)throws Exception;
    /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(CustomerImageDO record)throws Exception;
    /**
     * @param idNumber 客户身份证号 type 图片的类型
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> getListByIdNumberAndType(CustomerImageDO record) throws Exception;

}
