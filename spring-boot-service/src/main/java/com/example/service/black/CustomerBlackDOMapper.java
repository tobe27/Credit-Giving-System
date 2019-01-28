package com.example.service.black;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.service.black.CustomerBlackDO;
import com.example.service.customer.CustomerTagRelationDO;
@Mapper
@Repository
public interface CustomerBlackDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByIdNumber(String idNumber);

    int insert(CustomerBlackDO record);

    int insertSelective(CustomerBlackDO record);

    CustomerBlackDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerBlackDO record);

    int updateByPrimaryKey(CustomerBlackDO record);

    List<CustomerBlackDO> getList(Map<String,Object> map);

    List<CustomerBlackDO> getByIdNumber(Map<String,Object> map);

    List<CustomerBlackDO> getByIdNumbers(Map<String,Object> map);

    int deleteByOrdCode(CustomerBlackDO record);

    List<CustomerBlackDO> listByGridCode(String gridCode);

    List<CustomerBlackDO> getListByOrgCode(CustomerBlackDO record);

    int  batchSave(@Param("list")List<CustomerBlackDO> list);
    
}