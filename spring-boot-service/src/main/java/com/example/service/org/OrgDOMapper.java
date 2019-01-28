package com.example.service.org;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OrgDOMapper {
    int deleteByOrgCode(String orgCode);

    int insertSelective(OrgDO record);

    OrgDO getByPrimaryKey(Long id);

    OrgDO getByorgCode(String orgCode);

    OrgDO getParentOrgByOrgCode(String orgCode);

    List<OrgDO> getByParentorgCode(String orgCode);

    List<OrgDO> listAllByStatus(OrgDO record);

    List<OrgDO> listByOrgCode(OrgDO record);

    List<OrgDO> listByOrgDO(OrgDO record);

    List<String> listStringOrgCodes(String orgCode);

    int updateByPrimaryKeySelective(OrgDO record);
    List<OrgDO> getAll();

    /**
     * 更具身份证号查询灰名单或蓝名单所在的法人机构
     * @param idNUmber
     * @param tagId
     * @return
     */
    OrgDO selectByIdNumberInGreyAndBlue(@Param("idNUmber") String idNUmber, @Param("tagId")int tagId);
    /**
     * 更具身份证号查询黑名单或贫困户所在的法人机构
     * @param idNUmber
     * @param tagId
     * @return
     */
    OrgDO selectByIdNumberInBlackAndPoverty(@Param("idNUmber") String idNUmber, @Param("tagId")int tagId);
}