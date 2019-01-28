package com.example.service.survey;

import com.example.service.conclusion.ConclusionDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/17
 */
public interface SurveyDOService {
    /**
     * 删除
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(Long id) throws Exception;

    /**
     * 新建
     * @param record
     * @return
     */
    boolean insertSelective(SurveyDO record) throws Exception;

    /**
     * 导入Excel，并返回信息
     * @param
     * @return
     */
    String importExcelWithMessage(MultipartFile file, ConclusionDO conclusionDO) throws Exception;

    /**
     * 查询详情
     * @param id
     * @return
     */
    SurveyDO selectByPrimaryKey(Long id) throws Exception;

    /**
     * 查询列表
     * @param record
     * @return
     */
    List<SurveyDO> listByHouseholdIdAndIsValid(SurveyDO record) throws Exception;

    /**
     * 编辑
     * @param record
     * @return
     */
    boolean updateByPrimaryKeySelective(SurveyDO record) throws Exception;
}
