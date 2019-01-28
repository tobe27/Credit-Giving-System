package com.example.service.conclusion;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ConclusionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ConclusionDO record);

    List<ConclusionDO> listByIdNumber(String idNumber);

    List<ConclusionDO> listByHouseholdId(String householdId);

    int updateByHouseholdIdSelective(ConclusionDO record);

    int updateByPrimaryKeySelective(ConclusionDO record);
}