package com.example.service.customer;

import com.example.service.customer.CustomerTagRelationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
@Repository
public interface CustomerTagRelationDOMapper {
    int deleteByTagId(Long tagId);

    int deleteByIdNumber(String idNumber);
    
    int deleteByIdNumberAndTagId(CustomerTagRelationDO record);

    int insertSelective(CustomerTagRelationDO record);

    Set<String> setIdNumberByInIdNumber(CustomerTagRelationDO record);

    List<CustomerTagRelationDO> listByIdNumber(String idNumber);

    Set<Long> listTagIdByHouseholdId(String householdId);
    List<CustomerTagRelationDO> listCustomerTagRelationsByHouseholdId(String householdId);
    int  batchSave(@Param("list")List<CustomerTagRelationDO> list);
    List<CustomerTagRelationDO> getListByIdNumbers(Map<String,Object> map);
    int deleteByIdNumbersAndTagId(Map<String,Object> map);
}