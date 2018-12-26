package com.example.service.customer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.service.resident.ResidentDO;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CustomerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CustomerDO record);

    CustomerDO getByPrimaryKey(Long id);

    CustomerDO getByIdNumber(String idNumber);

    String getIdNumberByNameAndHouseholdId(CustomerDO record);

    List<CustomerDO> listCustomers(CustomerDO record);

    List<CustomerDO> listByHouseholdIdAndGridCode(CustomerDO record);

    int updateByPrimaryKeySelective(CustomerDO record);

    int updateByIdNumberSelective(CustomerDO record);
    int  batchSave(@Param("list")List<CustomerDO> list);
    List<String> getAllFamilyIdNumbersByIdNumbers(Map<String,Object> map);
    List<CustomerDO> getByIdNumbers (Map<String,Object> map);
}