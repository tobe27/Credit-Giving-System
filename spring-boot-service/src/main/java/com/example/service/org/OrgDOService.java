package com.example.service.org;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-4
 */
public interface OrgDOService {
    /**
     * 删除
     * @param orgCode
     * @return
     * @throws Exception
     */
    boolean deleteByOrgCode(String orgCode) throws Exception;

    /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertSelective(OrgDO record) throws Exception;

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    OrgDO getByPrimaryKey(Long id) throws Exception;

    /**
     * 根据状态查询机构
     * @param record
     * @return
     * @throws Exception
     */
    List<OrgDO> listAllByStatus(OrgDO record) throws Exception;

    /**
     * 列表，当前机构及所有子机构
     * @param record
     * @return
     * @throws Exception
     */
    List<OrgDO> listByOrgCode(OrgDO record) throws Exception;

    /**
     * 编辑
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(OrgDO record) throws Exception;

    /**
     * 树形结构查询
     * @param orgCode
     * @param parentOrgList
     * @return
     * @throws Exception
     */
    List<Object> treeOrgDOList(String orgCode, List<OrgDO> parentOrgList) throws Exception;
}
