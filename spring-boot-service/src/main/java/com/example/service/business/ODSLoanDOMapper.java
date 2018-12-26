package com.example.service.business;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ODSLoanDOMapper {

    List<ODSLoanDO> listLoan(ODSLoanDO record);

    int insertSelective(ODSLoanDO record);
}