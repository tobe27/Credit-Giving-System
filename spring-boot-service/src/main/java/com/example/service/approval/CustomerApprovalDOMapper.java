package com.example.service.approval;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.service.approval.CustomerApprovalDO;
@Mapper
@Repository
public interface CustomerApprovalDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustomerApprovalDO record);

    int insertSelective(CustomerApprovalDO record);

    CustomerApprovalDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustomerApprovalDO record);

    int updateByPrimaryKey(CustomerApprovalDO record);
    List<CustomerApprovalDO> getListByInterviewId(CustomerApprovalDO record);
}