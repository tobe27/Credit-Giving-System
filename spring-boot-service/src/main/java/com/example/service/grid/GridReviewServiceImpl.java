package com.example.service.grid;

import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialException;
import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2019/1/20
 */

@Service
public class GridReviewServiceImpl implements GridReviewService {
    private final GridReviewDOMapper gridReviewDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final CustomerDOMapper customerDOMapper;

    @Autowired
    public GridReviewServiceImpl(GridReviewDOMapper gridReviewDOMapper, SurveyDOMapper surveyDOMapper, CustomerDOMapper customerDOMapper) {
        this.gridReviewDOMapper = gridReviewDOMapper;
        this.surveyDOMapper = surveyDOMapper;
        this.customerDOMapper = customerDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(GridReviewServiceImpl.class);

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) throws Exception {
        return false;
    }

    /**
     * 新建
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean insertSelective(GridReviewDO record) throws Exception {
        return false;
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateByPrimaryKeySelective(GridReviewDO record) throws Exception {
        return false;
    }

    /**
     * 停用评议员
     *
     * @param record
     * @throws Exception
     */
    @Override
    @Transactional
    public void banReview(GridReviewDO record) throws Exception {
        logger.info("停用评议员出入参数:" + record);
        // 停用评议员
        try {
            gridReviewDOMapper.updateStatusByReviewNameAndGridCode(
                    new GridReviewDO().setStatus("2").setGridReviewName(record.getGridReviewName()).setGridCode(record.getGridCode()));
        } catch (Exception e) {
            logger.info("停用评议员异常:" + e.getMessage());
            throw new SerialException("停用评议员异常");
        }


        try {
            // 停用时作废后更新客户的有效调查次数
            customerDOMapper.minusValidTimeByHouseholdIdAndGridCodeAndSenator(
                    new CustomerDO().setGridCode(record.getGridCode()).setSenator(record.getGridReviewName()));

            // 停用评议员同时，作废未下结论的调查信息
            surveyDOMapper.updateIsValidByBanSenatorAndGridCode(
                    new SurveyDO().setSenator(record.getGridReviewName()).setGridCode(record.getGridCode()));

        } catch (Exception e) {
            logger.info("停用评议员时作废评议异常:" + e.getMessage());
            throw new SerialException("停用评议员时作废评议异常");
        }





    }

    /**
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<GridReviewDO> list(GridReviewDO record) throws Exception {
        return null;
    }
}
