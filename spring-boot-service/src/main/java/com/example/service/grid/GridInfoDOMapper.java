package com.example.service.grid;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.service.grid.GridInfoDO;
@Mapper
@Repository
public interface GridInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GridInfoDO record);

    int insertSelective(GridInfoDO record);

    GridInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GridInfoDO record);

    int updateByPrimaryKey(GridInfoDO record);

    int updateByUserIdSelective(GridInfoDO record);

    List<String> listGridReviewName(String gridCode);

    int getCalTypeByGridCode(String gridCode);

    BigDecimal getMaxCreatMoneyByGridCode(String gridCode);

    // 校验网格号是否存在
    String checkExistByGridCode(String gridCode);
    List<GridInfoDO> checkSameGridCode(GridInfoDO record);
    List<GridInfoDO> getList(Map<String,Object> map);
 
    //查询网格数据用
    String getResidentFamliyCount(Map<String,Object> map);
    String getResidentPeopleCount(Map<String,Object> map);
    String getBlackFamilyCount(Map<String,Object> map);
    String getPovertyFamilyCount(Map<String,Object> map);
    String getGreyFamilyCount(Map<String,Object> map);
    String getWhiteFamilyCount(Map<String,Object> map);
    String getCreditedFamilyCount(Map<String,Object> map);
    String getCreditLimit(Map<String,Object> map);
    String getUsedCreditLimit(Map<String,Object> map);
    
}