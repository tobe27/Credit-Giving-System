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

    int deleteByIdNumber(String idNumber);

    int insertSelective(ResidentDO record);

    ResidentDO getByPrimaryKey(Long id);

    ResidentDO getByIdNumber(String idNumber);

    List<ResidentDO> listByNameOrIdNumberOrHouseholdId(ResidentDO record);

    List<ResidentDO> listByHouseholdId(String householdId);

    List<ResidentDO> listByUserId(Long userId);

    int updateByIdNumberSelective(ResidentDO record);

    int updateByPrimaryKeySelective(ResidentDO record);

    List<ResidentDO> getByIdNumers(Map<String,Object> map);

    int  batchSave(@Param("list")List<ResidentDO> list);

    // 判断新姓名是否存在该户中
    int countInResidentByHouseholdIdAndName(ResidentDO record);
    // 修改户籍身份证、户号、网格
    int modifyResidentIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record);
    // 修改客户身份证、户号、网格
    int modifyCustomerIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record);

    // 修改调查表姓名
    int modifyNameByNameAndHouseholdId(ResidentDO record);
    // 通过身份证号修改结论姓名、身份证
    int modifyConclusionNameAndIdNumberByOldIdNumber(ResidentDO record);

    // 通过身份证号修改面签姓名、身份证
    int modifyInterviewNameAndIdNumberByOldIdNumber(ResidentDO record);
    // 通过身份证号修改面签作废姓名、身份证
    int modifyInterviewBanNameAndIdNumberByOldIdNumber(ResidentDO record);

    // 修改标签关联表身份证
    int modifyCustomerTagIdNumberByOldIdNumber(ResidentDO record);
    // 通过身份证修改白名单库姓名、身份证号
    int modifyWhiteNameAndIdNumberByOldIdNumber(ResidentDO record);
    // 通过身份证修改灰名单库姓名、身份证号
    int modifyGreyNameAndIdNumberByOldIdNumber(ResidentDO record);
    // 获取新旧身份证号客户是否在1-黑/3-贫/4-蓝
    int countInBlackAndPovertyAndBlueByIdNumberAndOldIdNumber(ResidentDO record);
}