package com.example.service.resident;

import com.example.service.resident.ResidentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ResidentDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ResidentDO record);

    ResidentDO getByPrimaryKey(Long id);

    ResidentDO getByIdNumber(String idNumber);

    List<ResidentDO> listByNameOrIdNumberOrHouseholdId(ResidentDO record);

    List<ResidentDO> listByHouseholdId(String householdId);

    List<ResidentDO> listByUserId(Long userId);

    int updateByPrimaryKeySelective(ResidentDO record);
    List<ResidentDO> getByIdNumers(Map<String,Object> map);
    int  batchSave(@Param("list")List<ResidentDO> list);

    // 修改户籍身份证、户号、网格
    int modifyResidentIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record);
    // 修改客户身份证、户号、网格
    int modifyCustomerIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record);
    // 修改调查表身份证、户号
    int modifySurveyIdNumberAndHouseholdIdByOldIdNumber(ResidentDO record);
    // 修改标签关联表身份证
    int modifyCustomerTagIdNumberByOldIdNumber(ResidentDO record);
    // 修改调查表姓名
    int modifyNameByNameAndHouseholdId(ResidentDO record);
}