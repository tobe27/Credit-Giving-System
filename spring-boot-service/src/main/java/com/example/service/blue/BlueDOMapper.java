package com.example.service.blue;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BlueDOMapper {

    List<BlueDO> getList(BlueDO record);

    int insertSelective(BlueDO record);

    int deleteByIdNumber(String idNumber);
}