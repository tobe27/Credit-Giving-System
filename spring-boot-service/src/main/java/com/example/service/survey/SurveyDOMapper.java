package com.example.service.survey;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SurveyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(SurveyDO record);

    SurveyDO selectByPrimaryKey(Long id);

    int countValidTimeByHouseholdIdAndIsValid(SurveyDO record);

    List<SurveyDO> listByHouseholdIdAndIsValid(SurveyDO record);

    List<SurveyDO> listByHouseholdIdAndSenator(SurveyDO record);

    List<SurveyDO> listByIdNumbersAndIsValid(Map<String,Object> map);

    int updateByHouseholdIdSelective(SurveyDO record);

    int updateByPrimaryKeySelective(SurveyDO record);

    int updateIsValidByBanSenatorAndGridCode(SurveyDO record);
}