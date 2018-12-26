package com.example.service.ods;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ODSLimitDOMapper {

    List<ODSLimitDO> listGivings(ODSLimitDO record);

    int insertSelective(ODSLimitDO record);
}