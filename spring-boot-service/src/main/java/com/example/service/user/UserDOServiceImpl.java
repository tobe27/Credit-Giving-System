package com.example.service.user;

import com.example.common.util.MD5Util;
import com.example.common.util.StringUtil;
import com.example.service.exception.ServiceException;
import com.example.service.grid.GridInfoDO;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.role.RoleDO;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-11-22
 */
@Service
public class UserDOServiceImpl implements UserDOService {
    private final UserDOMapper userDOMapper;
    private final UserRoleRelationDOMapper userRoleRelationDOMapper;
    private final OrgDOMapper orgDOMapper;
    private final GridInfoDOMapper gridInfoDOMapper;

    @Autowired
    public UserDOServiceImpl(UserDOMapper userDOMapper, UserRoleRelationDOMapper userRoleRelationDOMapper, OrgDOMapper orgDOMapper, GridInfoDOMapper gridInfoDOMapper) {
        this.userDOMapper = userDOMapper;
        this.userRoleRelationDOMapper = userRoleRelationDOMapper;
        this.orgDOMapper = orgDOMapper;
        this.gridInfoDOMapper = gridInfoDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(UserDOServiceImpl.class);

    /**
     * 删除用户
     *
     * @param id 用户编号
     * @return boolean
     * @throws Exception 数据库异常
     */
    @Override
    @Transactional
    public boolean deleteByPrimaryKey(Long id) throws Exception {

        // 如果有管辖的网格则不能删除
        List<String> gridCodeList;
        try {
            gridCodeList = userDOMapper.listGridCodeById(id);
        }  catch (Exception e) {
            logger.info("查询网格异常：" + e.getMessage());
            throw new ServiceException("查询网格异常");
        }
        if (gridCodeList != null && gridCodeList.size() != 0) {
            throw new ServiceException("该用户有管辖的网格，不能删除");
        }

        int count;
        try {
            count = userDOMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("删除用户异常：" + e.getMessage());
            throw new ServiceException("删除用户异常");
        }

        // 删除关联表
        try {
            userRoleRelationDOMapper.deleteByUser(id);
        } catch (Exception e) {
            logger.info("删除用户角色关联表异常：" + e.getMessage());
            throw new ServiceException("删除用户角色关联表异常");
        }

        return count == 1;
    }

    /**
     * 新建用户
     * 同时导入用户角色关联表
     * @param record 用户
     * @return boolean
     * @throws Exception 数据库异常
     */
    @Override
    @Transactional
    public boolean insertSelective(UserDO record) throws Exception {
        // 校验身份证
        if (StringUtil.isNotBlank(record.getIdNumber()) && StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号码！");
        }

        if (userDOMapper.getUserDOByUsername(record.getUsername()) != null) {
            throw new ServiceException("登录代码已存在！");
        }
        // 密码加密
        record.setPassword(MD5Util.create(record.getPassword()));
        // 创建时间
        long now = System.currentTimeMillis();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        int count;
        try {
            // 插入数据库
            count = userDOMapper.insertSelective(record);
        } catch (Exception e) {
            logger.info("新建用户异常：" + e.getMessage());
            throw new ServiceException("新建用户异常");
        }

        // 关联用户角色
        insertUserRoleRelation(record);

        return count == 1;
    }

    /**
     * 获取ODS更新时间
     *
     * @return
     * @throws Exception
     */
    @Override
    public String getTaskDate() throws Exception {
        try {
            return userDOMapper.getTaskDate();
        } catch (Exception e) {
            logger.info("获取数据更新时间异常：" + e.getMessage());
            throw new ServiceException("获取数据更新时间异常");
        }
    }

    /**
     * 查询用户
     *
     * @param id 用户编号
     * @return 用户
     * @throws Exception 数据库异常
     */
    @Override
    public UserDO getUserDO(Long id) throws Exception {
        try {
            return userDOMapper.getUserDO(id);
        } catch (Exception e) {
            logger.info("查询用户异常：" + e.getMessage());
            throw new ServiceException("查询用户异常");
        }
    }

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 用户
     * @throws Exception 数据库异常
     */
    @Override
    public UserDO getUserDOByUsername(String username) throws Exception {
        try {
            return userDOMapper.getUserDOByUsername(username);
        } catch (Exception e) {
            logger.info("查询用户异常：" + e.getMessage());
            throw new ServiceException("查询用户异常");
        }
    }

    /**
     * 查询列表
     * @param record 用户
     * @param pageNum 页码
     * @param pageSize 页行数
     * @return 用户列表
     * @throws Exception 数据库异常
     */
    @Override
    @Transactional
    public List<UserDO> listUserDOs(UserDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (pageNum == null || pageSize == null) {
            throw new ServiceException("页码和页行数不能为空");
        }
        if (StringUtil.isBlank(record.getOrgCode())) {
            throw new ServiceException("机构代码不能为空");
        }

        // 查询下辖机构, 用于查询
        List<String> orgCodeList;
        try {
            orgCodeList = orgDOMapper.listStringOrgCodes(record.getOrgCode());
        } catch (Exception e) {
            logger.info("查询下级机构异常:" + e.getMessage());
            throw new ServiceException("查询下级机构异常");
        }
        record.setOrgCodeList(orgCodeList);

        try {
            PageHelper.startPage(pageNum, pageSize);
            return userDOMapper.listUserDOs(record);
        } catch (Exception e) {
            logger.info("查询用户列表异常：" + e.getMessage());
            throw new ServiceException("查询用户列表异常");
        }
    }

    /**
     * 通过机构获取所有的客户经理ID列表
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<Long> listUserIdByOrgCode(UserDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (pageNum == null || pageSize == null) {
            throw new ServiceException("页码和页行数不能为空");
        }

        if (StringUtil.isBlank(record.getOrgCode())) {
            throw new ServiceException("机构代码不能为空");
        }
        // 查询下辖机构, 用于查询
        List<String> orgCodeList;
        try {
            orgCodeList = orgDOMapper.listStringOrgCodes(record.getOrgCode());
        } catch (Exception e) {
            logger.info("查询下级机构异常:" + e.getMessage());
            throw new ServiceException("查询下级机构异常");
        }
        record.setOrgCodeList(orgCodeList);

        try {
            return userDOMapper.listUserIdByOrgCode(record);
        } catch (Exception e) {
            logger.info("查询用户编号列表异常：" + e.getMessage());
            throw new ServiceException("查询用户编号列表异常");
        }
    }

    /**
     * 查询用户的所有角色
     *
     * @param id 用户编号
     * @return 角色列表
     * @throws Exception 数据库异常
     */
    @Override
    public List<String> listStringRoles(Long id) throws Exception {
        try {
            return userDOMapper.listStringRoles(id);
        } catch (Exception e) {
            logger.info("查询用户的所有角色异常：" + e.getMessage());
            throw new ServiceException("查询用户的所有角色异常");
        }
    }

    /**
     * 查询用户的所有权限
     *
     * @param id 用户编号
     * @return 权限列表
     * @throws Exception 数据库异常
     */
    @Override
    public List<String> listStringPerms(Long id) throws Exception {
        try {
            return userDOMapper.listStringPerms(id);
        } catch (Exception e) {
            logger.info("查询用户的所有权限异常：" + e.getMessage());
            throw new ServiceException("查询用户的所有权限异常");
        }
    }

    /**
     * 编辑用户
     *
     * @param record 用户
     * @return boolean
     * @throws Exception 数据库异常
     */
    @Override
    @Transactional
    public boolean updateByPrimaryKeySelective(UserDO record) throws Exception {
        // 校验身份证
        if (StringUtil.isNotBlank(record.getIdNumber()) && StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号码！");
        }
        // 如果密码不为空，则进行加密
        if (StringUtil.isNotBlank(record.getPassword())) {
            // 密码加密
            record.setPassword(MD5Util.create(record.getPassword()));
        }


        // 角色为1时,角色或机构变更及停用
        UserDO userDO = userDOMapper.getUserDO(record.getId());
        Long roleId = userDO.getRoles().get(0).getId();
        Long newRoleId = record.getRoles().get(0).getId();
        if (roleId == 1 && (newRoleId != 1 || !record.getOrgCode().equals(userDO.getOrgCode()) || "0".equals(record.getStatus()))) {
            // 将该用户的管理网格的userId置为0
            gridInfoDOMapper.updateByUserIdSelective(new GridInfoDO().setUserId(record.getId()));
        }

        // 修改时间
        long now = System.currentTimeMillis();
        record.setUpdatedAt(now);
        int count;
        try {
            count = userDOMapper.updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            logger.info("编辑用户异常：" + e.getMessage());
            if (userDOMapper.getUserDOByUsername(record.getUsername()) != null) {
                throw new ServiceException("登录代码已存在！");
            }
            throw new ServiceException("编辑用户异常");
        }

        // 关联用户角色
        insertUserRoleRelation(record);

        return count == 1;
    }

    private void disableUser(UserDO record) {

    }

    /**
     * 编辑用户部分字段
     * 如果修改密码则加密
     * @param record 用户
     * @return boolean
     * @throws Exception 数据库异常
     */
    @Override
    public boolean updateFieldById(UserDO record) throws Exception {
        // 如果密码不为空，则加密
        if (StringUtil.isNotBlank(record.getPassword())) {
            record.setPassword(MD5Util.create(record.getPassword()));
        }

        // 角色为1时,角色或机构变更及停用
        if ("0".equals(record.getStatus())) {
            // 将该用户的管理网格的userId置为0
            gridInfoDOMapper.updateByUserIdSelective(new GridInfoDO().setUserId(record.getId()));
        }

        try {
            return userDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑用户异常:" + e.getMessage());
            throw new ServiceException("编辑用户异常");
        }
    }


    /**
     * 插入用户角色关联表
     * 插入之前删除之前的角色
     * @param record 用户
     * @return 插入的条数
     */
    private int insertUserRoleRelation(UserDO record) {
        // 插入之前-删除之前的角色
        userRoleRelationDOMapper.deleteByUser(record.getId());

        int count = 0;
        // 关联用户角色
        List<RoleDO> roleDOList = record.getRoles();
        if (roleDOList == null || roleDOList.size() == 0) {
            throw new ServiceException("角色不能为空！");
        }
        for (RoleDO roleDO : roleDOList) {
            UserRoleRelationDO userRoleRelationDO = new UserRoleRelationDO()
                    .setRoleId(roleDO.getId())
                    .setUserId(record.getId())
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());
            try {
                // 插入关联表
                userRoleRelationDOMapper.insertSelective(userRoleRelationDO);
                count ++;
            } catch (Exception e) {
                logger.info("插入用户角色关联表异常：" + e.getMessage());
                throw new ServiceException("插入用户角色关联表异常");
            }
        }
        return count;
    }
}
