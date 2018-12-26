package com.example.service.org;

import org.apache.ibatis.annotations.Mapper;
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

    List<OrgDO> getByParentorgCode(String orgCode);

    List<OrgDO> listAllByStatus(OrgDO record);

    List<OrgDO> listByOrgCode(OrgDO record);

    List<OrgDO> listByOrgDO(OrgDO record);

    List<String> listStringOrgCodes(String orgCode);

    int updateByPrimaryKeySelective(OrgDO record);
    List<OrgDO> getAll();
}