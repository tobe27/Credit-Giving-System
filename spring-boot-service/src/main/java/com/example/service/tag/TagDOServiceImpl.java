package com.example.service.tag;

import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-4
 */
@Service
public class TagDOServiceImpl implements TagDOService {
    private final
    TagDOMapper tagDOMapper;
    private final
    CustomerTagRelationDOMapper customerTagRelationDOMapper;

    @Autowired
    public TagDOServiceImpl(TagDOMapper tagDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper) {
        this.tagDOMapper = tagDOMapper;
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(TagDOServiceImpl.class);

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean deleteByPrimaryKey(Long id) throws Exception {
        // 如果是系统标签则不能删除
        // 系统标签 1- 黑名单， 2- 贫困户， 3- 灰名单， 4- 蓝名单， 5- 白名单
        if ( id == 1 || id == 2 || id == 3 || id == 4 || id == 5) {
            throw new ServiceException("系统级标签不能删除！");
        }

        // 删除非系统标签
        int count;
        try {
            count = tagDOMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("删除标签异常:" + e.getMessage());
            throw new ServiceException("删除标签异常");
        }

        // 删除客户标签关联表
        try {
            customerTagRelationDOMapper.deleteByTagId(id);
        } catch (Exception e) {
            logger.info("删除客户标签关联异常:" + e.getMessage());
            throw new ServiceException("删除客户标签关联异常");
        }

        return count == 1;
    }

    /**
     * 插入
     * 唯一性判定
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean insertSelective(TagDO record) throws Exception {
        if (tagDOMapper.getByName(record.getName()) != null) {
            throw new ServiceException("标签名称已存在！");
        }

        // 默认类型为非系统级标签
        record.setType("非系统级标签");

        long now = System.currentTimeMillis();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        try {
            return tagDOMapper.insertSelective(record) == 1;
        } catch (Exception e) {
            logger.info("新建标签异常:" + e.getMessage());
            throw new ServiceException("新建标签异常");
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
    public TagDO getByPrimaryKey(Long id) throws Exception {
        try {
            return tagDOMapper.getByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("查看标签异常:" + e.getMessage());
            throw new ServiceException("查看标签异常");
        }
    }

    /**
     * 列表
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<TagDO> listTags(TagDO record) throws Exception {
        try {
            return tagDOMapper.listTags(record);
        } catch (Exception e) {
            logger.info("查询标签异常:" + e.getMessage());
            throw new ServiceException("查询标签异常");
        }
    }

    /**
     * 编辑
     * 唯一性判定，系统标签判定
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateByPrimaryKeySelective(TagDO record) throws Exception {
        // 系统标签不可编辑
        if ( record.getId() == 1 || record.getId() == 2 || record.getId() == 3 || record.getId() == 4 || record.getId() == 5) {
            throw new ServiceException("系统级标签不可编辑！");
        }

        // 默认类型为非系统级标签
        record.setType("非系统级标签");

        long now = System.currentTimeMillis();
        record.setUpdatedAt(now);
        try {
            return tagDOMapper.updateByPrimaryKeySelective(record) == 1;
        } catch (Exception e) {
            logger.info("编辑标签异常:" + e.getMessage());
            if (tagDOMapper.getByName(record.getName()) != null) {
                throw new ServiceException("标签名称已存在！");
            }
            throw new ServiceException("编辑标签异常");
        }
    }
}
