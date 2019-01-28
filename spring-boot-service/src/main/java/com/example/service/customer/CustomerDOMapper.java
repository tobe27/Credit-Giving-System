package com.example.service.customer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.service.resident.ResidentDO;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CustomerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CustomerDO record);

    CustomerDO getByPrimaryKey(Long id);

    CustomerDO getByIdNumber(String idNumber);

    String getODSIsYes();

    String getIdNumberByNameAndHouseholdId(CustomerDO record);

    String getHouseholdIdByIdNumber(String idNumber);

    List<CustomerDO> listCustomers(CustomerDO record);

    List<CustomerDO> listByHouseholdIdAndGridCode(CustomerDO record);

    List<CustomerDO> listNoTagCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record);

    List<CustomerDO> listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(CustomerDO record);

    List<CustomerDO> listYetReviewCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record);

    List<CustomerDO> listSurveyCustomersByGridCodeAndRelationshipAndUserId(CustomerDO record);

    List<CustomerDO> listByHouseholdId(String householdId);

    int updateByPrimaryKeySelective(CustomerDO record);

    int updateByIdNumberSelective(CustomerDO record);

    int updateByHouseholdIdSelective(CustomerDO record);

    int minusValidTimeByHouseholdIdAndGridCodeAndSenator(CustomerDO record);

    int plusValidTimeByHouseholdId(CustomerDO record);

    int countFamilyInDeposit(String householdId);

    int countIsConcluded(String gridCode);

    int  batchSave(@Param("list")List<CustomerDO> list);

    List<String> getAllFamilyIdNumbersByIdNumbers(Map<String,Object> map);

    List<CustomerDO> getByIdNumbers (Map<String,Object> map);
}