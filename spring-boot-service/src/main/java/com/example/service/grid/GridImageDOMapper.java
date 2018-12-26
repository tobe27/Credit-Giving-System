package com.example.service.grid;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GridImageDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GridImageDO record);

    int insertSelective(GridImageDO record);

    GridImageDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GridImageDO record);

    int updateByPrimaryKey(GridImageDO record);
    
    List<GridImageDO> getList(Map<String,Object> map);
}