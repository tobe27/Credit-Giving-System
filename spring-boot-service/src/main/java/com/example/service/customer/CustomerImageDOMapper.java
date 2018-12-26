package com.example.service.customer;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CustomerImageDOMapper {
	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    int deleteByPrimaryKey(Long id) ;
    /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
    int insertSelective(CustomerImageDO record);
    /**
     * 查看
     * @param id
     * @return
     * @throws Exception
     */
    CustomerImageDO selectByPrimaryKey(Long id);
    /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
    int updateByPrimaryKeySelective(CustomerImageDO record);
    
    List<CustomerImageDO> getImageListByIdNumberAndType(CustomerImageDO record);
}