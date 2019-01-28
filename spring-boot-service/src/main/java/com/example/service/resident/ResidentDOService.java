package com.example.service.resident;

import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/11
 */
public interface ResidentDOService {
    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteByPrimaryKey(Long id) throws Exception;

    /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
    long insertSelective(ResidentDO record) throws Exception;

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    ResidentDO getByPrimaryKey(Long id) throws Exception;

    /**
     * 分页
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<ResidentDO> listByNameOrIdNumberOrHouseholdId(ResidentDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 编辑
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(ResidentDO record) throws Exception;
    /**
     * 从excel导入
     * @param List<Map<String,Object>> list
     * @return
     * @throws Exception
     */
   int importFromExcel(List<Map<String,Object>> list,Map<String,Object> paramMap) throws Exception;

    /**
     * 更新户籍、客户、调查表、客户标签关联表
     * 身份证号、户号、网格
     * @param record
     * @return
     * @throws Exception
     */
   boolean updateIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(ResidentDO record) throws Exception;
    
}
