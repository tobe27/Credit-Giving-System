package com.example.service.interview;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.service.interview.CustomerInterviewDO;
@Mapper
@Repository
public interface CustomerInterviewDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerInterviewDO record);

    int insertSelective(CustomerInterviewDO record);

    CustomerInterviewDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerInterviewDO record);

    int updateByPrimaryKey(CustomerInterviewDO record);
    List<CustomerInterviewDO> getListByIdNumber(CustomerInterviewDO record);
    List<CustomerInterviewDO> getList(Map<String,Object> map);
    List<CustomerInterviewDO> getListByIds(Map<String,Object> map);
    List<CustomerInterviewDO> getListByIdNumbersAndApprovalStatus(Map<String,Object> map);
}