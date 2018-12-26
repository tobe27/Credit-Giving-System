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

    List<SurveyDO> listByIdNumberAndIsValid(SurveyDO record);

    List<SurveyDO> listByIdNumberAndSenator(SurveyDO record);
    List<SurveyDO> listByIdNumbersAndIsValid(Map<String,Object> map);

    int updateByPrimaryKeySelective(SurveyDO record);
}