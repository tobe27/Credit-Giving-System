package com.example.service.interview;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InterviewFileDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InterviewFileDO record);

    int insertSelective(InterviewFileDO record);

    InterviewFileDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InterviewFileDO record);

    int updateByPrimaryKey(InterviewFileDO record);
    InterviewFileDO getOneByIdNumber(InterviewFileDO record);
    List<InterviewFileDO> getList(InterviewFileDO record);
}