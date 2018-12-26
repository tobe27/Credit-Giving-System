package com.example.service.white;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.service.white.CustomerWhiteDO;
@Mapper
@Repository
public interface CustomerWhiteDOMapper {
    int deleteByPrimaryKey(long id);

    int insert(CustomerWhiteDO record);

    int insertSelective(CustomerWhiteDO record);

    CustomerWhiteDO selectByPrimaryKey(long id);

    int updateByPrimaryKeySelective(CustomerWhiteDO record);

    int updateByPrimaryKey(CustomerWhiteDO record);
    List<CustomerWhiteDO> getList(Map<String,Object> map);
    List<CustomerWhiteDO> getByIdNumber(Map<String,Object> map);
    List<CustomerWhiteDO> getListByIdNumbers(Map<String,Object> map);
    int deleteByIdNumbers(Map<String,Object> map);
    List<CustomerWhiteDO> getListByGrdiCode(Map<String,Object> map);
    
}