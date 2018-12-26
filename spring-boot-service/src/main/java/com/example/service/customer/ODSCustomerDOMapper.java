package com.example.service.customer;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ODSCustomerDOMapper {
    List<ODSCustomerDO> listODSByIdNumber(String idNumber);
    List<ODSCustomerDO> listODSByIdNumbers(Map<String,Object> map);
}