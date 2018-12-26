package com.example.service.org;

import com.example.service.exception.ServiceException;
import com.example.service.user.UserDO;
import com.example.service.user.UserDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by L.C.Y on 2018-12-4
 */
@Service
public class OrgDOServiceImpl implements OrgDOService {
    private final
    OrgDOMapper orgDOMapper;
    private final
    UserDOMapper userDOMapper;

    @Autowired
    public OrgDOServiceImpl(OrgDOMapper orgDOMapper, UserDOMapper userDOMapper) {
        this.orgDOMapper = orgDOMapper;
        this.userDOMapper = userDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(OrgDOServiceImpl.class);

    /**
     * 删除
     *
     * @param orgCode
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean deleteByOrgCode(String orgCode) throws Exception {
        // 如果存在机构则不可删除
        OrgDO orgDO = new OrgDO();
        orgDO.setParentOrgCode(orgCode);
        List<String> orgCodeList;

        try {
            orgCodeList = orgDOMapper.listStringOrgCodes(orgCode);
        } catch (Exception e) {
            logger.info("查询机构及下级机构异常:" + e.getMessage());
            throw new ServiceException("查询机构及下级机构异常！");
        }

        if (orgCodeList != null && orgCodeList.size() != 0 && orgCodeList.size() != 1) {
            throw new ServiceException("该机构存在子机构，不可删除！");
        }

        // 如果该机构下有用户则不可删除
        List<UserDO> userDOList;
        try {
            userDOList = userDOMapper.listUserByOrgCode(orgCode);
        } catch (Exception e) {
            logger.info("查询机构下用户异常:" + e.getMessage());
            throw new ServiceException("查询机构下用户异常！");
        }

        if (userDOList != null && userDOList.size() != 0) {
            throw new ServiceException("该机构下存在用户，不可删除！");
        }

        try {
            return orgDOMapper.deleteByOrgCode(orgCode) == 1;
        } catch (Exception e) {
            logger.info("删除机构异常:" + e.getMessage());
            throw new ServiceException("删除机构异常");
        }
    }

    /**
     * 新建
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean insertSelective(OrgDO record) throws Exception {
        List<OrgDO> orgDOList = orgDOMapper.listByOrgDO(new OrgDO().setOrgCode(record.getOrgCode()));
        if (orgDOList != null && orgDOList.size() != 0) {
            throw new ServiceException("该机构代码已存在！");
        }
        List<OrgDO> orgDOList1 = orgDOMapper.listByOrgDO(new OrgDO().setCoreOrgCode(record.getCoreOrgCode()));
        if (orgDOList1 != null && orgDOList1.size() != 0) {
            throw new ServiceException("该核心机构代码已存在！");
        }
        List<OrgDO> orgDOList2 = orgDOMapper.listByOrgDO(new OrgDO().setOrgName(record.getOrgName()));
        if (orgDOList2 != null && orgDOList2.size() != 0) {
            throw new ServiceException("该机构名称已存在！");
        }
        long now = System.currentTimeMillis();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        try {
            return orgDOMapper.insertSelective(record) == 1;
        } catch (Exception e) {
            logger.info("新建机构异常:" + e.getMessage());
            throw new ServiceException("新建机构异常");
        }
    }

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public OrgDO getByPrimaryKey(Long id) throws Exception {
        try {
            return orgDOMapper.getByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("查看机构异常:" + e.getMessage());
            throw new ServiceException("查看机构异常");
        }
    }

    /**
     * 根据状态查询机构
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<OrgDO> listAllByStatus(OrgDO record) throws Exception {
        try {
            return orgDOMapper.listAllByStatus(record);
        } catch (Exception e) {
            logger.info("根据状态查询机构异常:" + e.getMessage());
            throw new ServiceException("根据状态查询机构异常");
        }
    }

    /**
     * 列表，当前机构及所有子机构
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public List<OrgDO> listByOrgCode(OrgDO record) throws Exception {
        List<String> orgCodeList;
        try {
            orgCodeList = orgDOMapper.listStringOrgCodes(record.getOrgCode());
        } catch (Exception e) {
            logger.info("查询机构及下级机构异常:" + e.getMessage());
            throw new ServiceException("查询机构及下级机构异常！");
        }

        if (orgCodeList == null || orgCodeList.size() == 0) {
            return null;
        }
        record.setOrgCodeList(orgCodeList);
        try {
            return orgDOMapper.listByOrgCode(record);
        } catch (Exception e) {
            logger.info("查询机构异常:" + e.getMessage());
            throw new ServiceException("查询机构异常");
        }
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean updateByPrimaryKeySelective(OrgDO record) throws Exception {
        record.setUpdatedAt(System.currentTimeMillis());

        List<OrgDO> orgDOList;
        List<OrgDO> orgDOList1;
        List<OrgDO> orgDOList2;

        try {
            orgDOList = orgDOMapper.listByOrgDO(new OrgDO().setOrgCode(record.getOrgCode()));
            orgDOList1 = orgDOMapper.listByOrgDO(new OrgDO().setCoreOrgCode(record.getCoreOrgCode()));
            orgDOList2 = orgDOMapper.listByOrgDO(new OrgDO().setOrgName(record.getOrgName()));
        } catch (Exception e) {
            logger.info("编辑机构异常:" + e.getMessage());
            throw new ServiceException("编辑机构异常");
        }

        try {
            return orgDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑机构异常:" + e.getMessage());

            if (orgDOList != null && orgDOList.size() != 0) {
                throw new ServiceException("该机构代码已存在！");
            }

            if (orgDOList1 != null && orgDOList1.size() != 0) {
                throw new ServiceException("该核心机构代码已存在！");
            }

            if (orgDOList2 != null && orgDOList2.size() != 0) {
                throw new ServiceException("该机构名称已存在！");
            }

            throw new ServiceException("编辑机构异常");
        }
    }


    // 获取树形结构
    private List<OrgDO> parentOrgList;
    /**
     * 树形结构查询
     * @param orgCode
     * @param parentOrgList
     * @return
     * @throws Exception
     */
    @Override
    public List<Object> treeOrgDOList(String orgCode, List<OrgDO> parentOrgList) throws Exception {
        if (parentOrgList == null || parentOrgList.size() == 0) {
            return null;
        }
        this.parentOrgList = parentOrgList;
        List<Object> childList = new ArrayList<>(12);
        for (OrgDO orgDO : parentOrgList) {
            Map<String, Object> childMap = new LinkedHashMap<>();
            if (orgCode.equals(orgDO.getOrgCode())) {
                childMap.put("id", orgDO.getId());
                childMap.put("orgCode", orgDO.getOrgCode());
                childMap.put("coreOrgCode", orgDO.getCoreOrgCode());
                childMap.put("orgName", orgDO.getOrgName());
                childMap.put("parentOrgCode", orgDO.getParentOrgCode());
                childMap.put("corpOrgCode", orgDO.getCorpOrgCode());
                childMap.put("level", orgDO.getLevel());
                childMap.put("remark", orgDO.getRemark());
                childMap.put("status",orgDO.getStatus());
                childMap.put("createdAt", orgDO.getCreatedAt());
                childMap.put("updatedAt",orgDO.getUpdatedAt());
                childMap.put("childList", getChildOrgList(orgDO.getOrgCode()));
                childList.add(childMap);
            }
        }
        return childList;
    }

    private List<Object> getChildOrgList(String orgCode) {
        List<Object> childList = new ArrayList<>(12);
        for (OrgDO orgDO : parentOrgList) {
            Map<String, Object> childMap = new LinkedHashMap<>();
            if (orgCode.equals(orgDO.getParentOrgCode())) {
                childMap.put("id", orgDO.getId());
                childMap.put("orgCode", orgDO.getOrgCode());
                childMap.put("coreOrgCode", orgDO.getCoreOrgCode());
                childMap.put("orgName", orgDO.getOrgName());
                childMap.put("parentOrgCode", orgDO.getParentOrgCode());
                childMap.put("corpOrgCode", orgDO.getCorpOrgCode());
                childMap.put("level", orgDO.getLevel());
                childMap.put("remark", orgDO.getRemark());
                childMap.put("status",orgDO.getStatus());
                childMap.put("createdAt", orgDO.getCreatedAt());
                childMap.put("updatedAt",orgDO.getUpdatedAt());
                childMap.put("childList", getChildOrgList(orgDO.getOrgCode()));
                childList.add(childMap);
            }
        }
        return childList;
    }
}
