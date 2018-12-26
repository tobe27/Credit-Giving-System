package com.example.service.tag;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-4
 */
public interface TagDOService {
    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteByPrimaryKey(Long id) throws Exception;

    /**
     * 插入
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertSelective(TagDO record) throws Exception;

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    TagDO getByPrimaryKey(Long id) throws Exception;

    /**
     * 列表
     * @param record
     * @return
     * @throws Exception
     */
    List<TagDO> listTags(TagDO record) throws Exception;

    /**
     * 编辑
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(TagDO record) throws Exception;
}
