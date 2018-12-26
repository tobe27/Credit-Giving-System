package com.example.service.business;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@Mapper
@Repository
public interface BusinessDOMapper {

    BusinessDO getBusinessByIdNumber(String idNumber);
}
