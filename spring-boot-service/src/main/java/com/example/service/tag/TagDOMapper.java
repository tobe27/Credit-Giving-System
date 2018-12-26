package com.example.service.tag;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TagDO record);

    TagDO getByPrimaryKey(Long id);

    TagDO getByName(String name);

    List<TagDO> listTags(TagDO record);

    int updateByPrimaryKeySelective(TagDO record);
}