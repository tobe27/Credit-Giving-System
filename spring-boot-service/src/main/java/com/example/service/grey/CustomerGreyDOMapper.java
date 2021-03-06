package com.example.service.grey;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.service.black.CustomerBlackDO;
@Mapper
@Repository
public interface CustomerGreyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerGreyDO record);

    int insertSelective(CustomerGreyDO record);

    int batchSave(@Param("list") List<CustomerGreyDO> list);

    CustomerGreyDO selectByPrimaryKey(Long id);

    CustomerGreyDO selectByIdNUmber(String idNumber);

    int deleteByIdNumber(String idNumber);

    int updateByPrimaryKeySelective(CustomerGreyDO record);

    int updateByPrimaryKey(CustomerGreyDO record);

    List<CustomerGreyDO> listByGridCode(String gridCode);

    List<CustomerGreyDO> getList(Map<String,Object> map);

    List<CustomerGreyDO> getByIdNumber(Map<String,Object> map);

    List<CustomerGreyDO> getByIdNumbers(Map<String,Object> map);
}