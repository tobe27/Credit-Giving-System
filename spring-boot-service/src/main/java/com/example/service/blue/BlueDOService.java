package com.example.service.blue;

import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/21
 */
public interface BlueDOService {
    /**
     * 查询列表
     * @param record
     * @return
     * @throws Exception
     */
    List<BlueDO> listBlueByUserOrOrgCode(BlueDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 删除
     * @param map
     * @return
     * @throws Exception
     */
    boolean deleteBlue(Map<String,String> map)throws Exception;

    /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertSelective(BlueDO record) throws Exception;
}
