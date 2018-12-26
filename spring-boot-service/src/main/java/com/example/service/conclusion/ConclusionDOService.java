package com.example.service.conclusion;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/12
 */
public interface ConclusionDOService {
    /**
     * 删除
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(Long id);

    /**
     * 新建
     * @param record
     * @return
     */
    boolean insertSelective(ConclusionDO record) throws Exception;

    /**
     * 查询结论汇总信息,调查信息必须大于三次
     * @param idNumber
     * @return
     * @throws Exception
     */
    ConclusionDO getConclusionInfo(String idNumber, String isValid) throws Exception;

    /**
     * 列表
     * @param idNumber
     * @return
     */
    List<ConclusionDO> listByIdNumber(String idNumber) throws Exception;

    /**
     * 户号列表
     * @param householdId
     * @return
     * @throws Exception
     */
    List<ConclusionDO> listByHouseholdId(String householdId) throws Exception;;

    /**
     * 编辑
     * @param record
     * @return
     */
    boolean updateByPrimaryKeySelective(ConclusionDO record);
}
