package com.example.service.poverty;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.service.black.CustomerBlackDO;
import com.example.service.grey.CustomerGreyDO;

@Mapper
@Repository
public interface CustomerPovertyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerPovertyDO record);

    int insertSelective(CustomerPovertyDO record);

    CustomerPovertyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerPovertyDO record);
    int deleteByOrdCode(CustomerPovertyDO record);
    int updateByPrimaryKey(CustomerPovertyDO record);
    List<CustomerPovertyDO> getList(Map<String,Object> map);
    List<CustomerPovertyDO> getByIdNumber(Map<String,Object> map);
    List<CustomerPovertyDO> getByIdNumbers(Map<String,Object> map);
    int  batchSave(@Param("list")List<CustomerPovertyDO> list);
    List<CustomerPovertyDO> getListByOrgCode(CustomerPovertyDO record);
}