package com.example.service.grid;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.service.grid.GridReviewDO;
@Mapper
@Repository
public interface GridReviewDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(GridReviewDO record);

    GridReviewDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GridReviewDO record);

    int updateStatusByReviewNameAndGridCode(GridReviewDO record);
    
    int  deleteByGridCode(GridReviewDO record);

    Long getReviewByNameAndGridCode(GridReviewDO record);

}