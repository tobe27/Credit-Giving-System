package com.example.service.survey;

import com.example.common.util.ExcelUtil;
import com.example.common.util.PoiUtil;
import com.example.common.util.StringUtil;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOService;
import com.example.service.grid.GridReviewDO;
import com.example.service.grid.GridReviewDOMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/17
 */
@Service
public class SurveyDOServiceImpl implements SurveyDOService {
    private final SurveyDOMapper surveyDOMapper;
    private final CustomerDOMapper customerDOMapper;
    private final CustomerGreyDOService customerGreyDOService;
    private final GridReviewDOMapper gridReviewDOMapper;

    @Autowired
    public SurveyDOServiceImpl(SurveyDOMapper surveyDOMapper, CustomerDOMapper customerDOMapper, CustomerGreyDOService customerGreyDOService, GridReviewDOMapper gridReviewDOMapper) {
        this.surveyDOMapper = surveyDOMapper;
        this.customerDOMapper = customerDOMapper;
        this.customerGreyDOService = customerGreyDOService;
        this.gridReviewDOMapper = gridReviewDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(SurveyDOServiceImpl.class);

    /**
     * 删除
     *
     * @param id
     * @return
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
     */
    @Override
    public boolean insertSelective(SurveyDO record) throws Exception {
        return false;
    }

    /**
     * 导入Excel，并返回错误信息
     *
     * @param file
     * @return
     */
    @Override
    @Transactional
    public String importExcelWithMessage(MultipartFile file, ConclusionDO conclusionDO) throws Exception {
        Map<String,Object> map= PoiUtil.uploadMultipartFile(file,"excel");
        if (!(boolean) map.get("flag")) {
            throw new ServiceException("文件为空！");
        }

        Workbook workbook = ExcelUtil.getWorkbook(map.get("path").toString());
        Sheet sheet = workbook.getSheetAt(0);
        List<CellRangeAddress> cellRangeAddressList = ExcelUtil.getMergeCellRow(sheet);

        // 总行数
        int totalRowCount = sheet.getLastRowNum();
        logger.info("总行数：" + totalRowCount);

        // 获取评议员的值
        Row row = sheet.getRow(2);
        String senatorRowValue = ExcelUtil.getCellValue(row.getCell(0));
        String[] senatorArr = senatorRowValue.split(":");
        if (senatorArr.length != 2) {
            throw new ServiceException("评议员不能为空！");
        }
        String senator = senatorArr[1];
        Long senatorId;
        try {
            senatorId = gridReviewDOMapper.getReviewByNameAndGridCode(
                    new GridReviewDO().setGridReviewName(senator).setGridCode(conclusionDO.getGridCode()));
        } catch (Exception e) {
            logger.info("获取评议员异常:" + e.getMessage());
            throw new ServiceException("获取评议员异常");
        }
        if (StringUtil.isBlank(senatorId)) {
            throw new ServiceException("该评议员不在网格中！");
        }

        // 读取Excel数据，并校验
        List<SurveyDO> surveyDOList = new ArrayList<>(1000);
        int i = 5;
        for (; i <= totalRowCount; ) {
            SurveyDO surveyDO = new SurveyDO().setSenator(senator);
            Row iRow = sheet.getRow(i);

            if (ExcelUtil.isMergeCell(sheet, i, 0)) {
                int firstMergeCellNum = ExcelUtil.getMergeCellFirstRowNum(sheet, cellRangeAddressList, iRow.getCell(0));
                int mergeRowCount = ExcelUtil.getMergeCellCount(sheet, cellRangeAddressList, iRow.getCell(0));
                logger.info("合并单元格第一行：" + firstMergeCellNum);
                surveyDO = getSurveyDOFromExcel(surveyDO, iRow);

                i += mergeRowCount;
                logger.info("累加行数：" + i);
            } else {
                surveyDO = getSurveyDOFromExcel(surveyDO, iRow);
                logger.info("实体类：" + surveyDO);
                i += 1;
                logger.info("累加行数：" + i);

            }
            if (surveyDO == null) {
                continue;
            }
            surveyDOList.add(surveyDO);
        }

        // 通过户号查询客户数据，并导入到调查信息数据库表
        insertSurveyDOFromExcelList(surveyDOList, conclusionDO);

        return "success";
    }

    /**
     * 通过户号查询客户数据，并导入到调查信息数据库表
     * @param surveyDOList
     */
    private void insertSurveyDOFromExcelList(List<SurveyDO> surveyDOList, ConclusionDO conclusionDO) throws Exception {
        for (SurveyDO surveyDO : surveyDOList) {
            List<CustomerDO> customerDOList;
            try {
                customerDOList = customerDOMapper.listByHouseholdIdAndGridCode(new CustomerDO().setHouseholdId(surveyDO.getHouseholdId()));
            } catch (Exception e) {
                logger.info("获取客户信息异常:" + e.getMessage());
                throw new ServiceException("获取客户信息异常");
            }
            if (customerDOList == null || customerDOList.size() == 0) {
                continue;
            }
            for (CustomerDO customerDO : customerDOList) {
                surveyDO.setName(customerDO.getName())
                        .setIdNumber(customerDO.getIdNumber())
                        .setDate(String.valueOf(System.currentTimeMillis()))
                        .setCreatedAt(System.currentTimeMillis())
                        .setUpdatedAt(System.currentTimeMillis());

                // 是否是同一个评议员，是就不导入
                List<SurveyDO> surveyDOList1;
                try {
                    surveyDOList1 = surveyDOMapper.listByIdNumberAndSenator(surveyDO);
                } catch (Exception e) {
                    logger.info("调查信息查询异常:" + e.getMessage());
                    throw new ServiceException("调查信息查询异常");
                }
                if (surveyDOList1 != null && surveyDOList1.size() != 0) {
                    continue;
                }

                // 如果了解为是且不授信原因不为空，则设为灰名单
                if ("是".equals(surveyDO.getIsFamiliar()) && StringUtil.isNotBlank(surveyDO.getNegativeReason())) {
                    CustomerGreyDO customerGreyDO = new CustomerGreyDO();
                    customerGreyDO.setCustomerName(surveyDO.getName())
                            .setIdNumber(surveyDO.getIdNumber())
                            .setCustomerId(customerDO.getId())
                            .setHouseholdId(surveyDO.getHouseholdId())
                            .setGridCode(conclusionDO.getGridCode())
                            .setGridName(conclusionDO.getGridName())
                            .setOrgCode(conclusionDO.getOrgCode())
                            .setOrgName(conclusionDO.getOrgName())
                            .setUserId(conclusionDO.getUserId())
                            .setReason(surveyDO.getNegativeReason())
                            .setUserName(conclusionDO.getUserName());

                    customerGreyDOService.insertSelective(customerGreyDO);
                }

                // 导入调查信息
                try {
                    surveyDOMapper.insertSelective(surveyDO);
                } catch (Exception e) {
                    logger.info("调查信息导入异常:" + e.getMessage());
                    throw new ServiceException("调查信息导入异常");
                }

                // 如果有效，客户调查有效次数＋1
                // 有效次数,根据身份证和是有效来获取有效调查次数
                int validTime = surveyDOMapper.countValidTimeByIdNumberAndIsValid(surveyDO);

                try {
                    customerDOMapper.updateByPrimaryKeySelective(new CustomerDO()
                            .setId(customerDO.getId())
                            .setValidTime(validTime));

                } catch (Exception e) {
                    logger.info("客户有效调查次数更新异常:" + e.getMessage());
                    throw new ServiceException("客户有效调查次数更新异常");
                }

            }

        }
    }

    /**
     * 获取调查信息并校验
     * @param surveyDO
     * @param iRow
     * @return
     */
    private SurveyDO getSurveyDOFromExcel(SurveyDO surveyDO, Row iRow) {
        String no = ExcelUtil.getCellValue(iRow.getCell(0));
        if (StringUtil.isBlank(no)) {
            return null;
        }
        String householdId = ExcelUtil.getCellValue(iRow.getCell(3));
        if (StringUtil.isNotLength(householdId, 1, 20)) {
            throw new ServiceException("序号:" + no + ",户号错误：最长20位");
        }
        String isFamiliar = ExcelUtil.getCellValue(iRow.getCell(11));
        if (!"是".equals(isFamiliar) && !"否".equals(isFamiliar)) {
            throw new ServiceException("序号:" + no + ",是否了解情况错误：只能为是或否");
        }
        String negativeReason = ExcelUtil.getCellValue(iRow.getCell(12));

        String remark = ExcelUtil.getCellValue(iRow.getCell(13));

        String borrower = ExcelUtil.getCellValue(iRow.getCell(14));


        String houseValue = ExcelUtil.getCellValue(iRow.getCell(15));

        String carValue = ExcelUtil.getCellValue(iRow.getCell(16));

        String mainBusiness = ExcelUtil.getCellValue(iRow.getCell(17));

        String scale = ExcelUtil.getCellValue(iRow.getCell(18));

        String income = ExcelUtil.getCellValue(iRow.getCell(19));

        String payout = ExcelUtil.getCellValue(iRow.getCell(20));

        String creditAmount = ExcelUtil.getCellValue(iRow.getCell(21));

        // 如果不熟悉，则后面不校验
        // 或，如果熟悉但是有不授信原因，则后面不校验
        boolean isNotFamiliar = "否".equals(isFamiliar);
        boolean isFamiliarButHaveNegativeReason = "是".equals(isFamiliar) && StringUtil.isNotBlank(negativeReason);
        boolean valid = !(isNotFamiliar || isFamiliarButHaveNegativeReason);

        if (StringUtil.isNotBlank(negativeReason) && StringUtil.isNotLength(negativeReason, 0, 20)) {
            throw new ServiceException("序号:" + no + ",不符合授信原因错误：最长20位");
        }

        if (StringUtil.isNotBlank(remark) && StringUtil.isNotLength(remark, 0, 50)) {
            throw new ServiceException("序号:" + no + ",备注错误：最长50位");
        }

        if ((valid && StringUtil.isNotLength(borrower, 0, 20)) || (!valid && StringUtil.isNotBlank(borrower) && StringUtil.isNotLength(borrower, 0, 20))) {
            throw new ServiceException("序号:" + no + ",借款主体错误：不能为空，且最长10位");
        }
        // 判断借款主体是否存在
        if (valid) {
            String borrowerIdNumber;
            try {
                borrowerIdNumber = customerDOMapper.getIdNumberByNameAndHouseholdId(
                        new CustomerDO().setName(borrower).setHouseholdId(householdId));
            } catch (Exception e) {
                logger.info("查询借款主体客户信息异常:" + e.getMessage());
                throw new ServiceException("查询借款主体客户信息异常");
            }
            if (StringUtil.isBlank(borrowerIdNumber)) {
                throw new ServiceException("序号:" + no + ",借款主体不在该户中！");
            }
        }

        if (StringUtil.isNotBlank(houseValue) &&!"有".equals(houseValue) && !"无".equals(houseValue)) {
            throw new ServiceException("序号:" + no + ",有无商品房错误：只能是有或无");
        }

        if (StringUtil.isNotBlank(carValue) &&!"有".equals(carValue) && !"无".equals(carValue)) {
            throw new ServiceException("序号:" + no + ",有无车辆错误：只能是有或无");
        }

        if (StringUtil.isNotBlank(mainBusiness) && StringUtil.isNotLength(mainBusiness, 0, 20)) {
            throw new ServiceException("序号:" + no + ",主营项目错误：最长20位");
        }

        if (StringUtil.isNotBlank(scale) && StringUtil.isNotLength(scale, 0, 20)) {
            throw new ServiceException("序号:" + no + ",规模错误：最长20位");
        }

        if (StringUtil.isNotBlank(income) && StringUtil.isNotNumeric(income, 18)) {
            throw new ServiceException("序号:" + no + ",收入错误：只能是数字");
        }

        if (StringUtil.isNotBlank(payout) && StringUtil.isNotNumeric(payout, 18)) {
            throw new ServiceException("序号:" + no + ",支出错误：只能是数字");
        }

        if ((valid && StringUtil.isNotNumeric(creditAmount, 18)) || (!valid && StringUtil.isNotBlank(borrower) && StringUtil.isNotLength(borrower, 0, 20))) {
            throw new ServiceException("序号:" + no + ",授信额度错误：不能为空，且只能是数字");
        }

        // 是否有效
        String isValid = "是";
        if (isNotFamiliar || isFamiliarButHaveNegativeReason) {
            isValid = "否";
        }

        // 控制为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        surveyDO.setHouseholdId(householdId)
                .setIsFamiliar(isFamiliar)
                .setNegativeReason(negativeReason)
                .setRemark(remark)
                .setHouseValue(houseValue)
                .setCarValue(carValue)
                .setMainBusiness(mainBusiness)
                .setScale(scale)
                .setIncome(income)
                .setPayout(payout)
                .setCreditAmount(creditAmount)
                .setBorrower(borrower)
                .setSurveyType("评议员意见")
                .setIsValid(isValid);

        return surveyDO;
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @Override
    public SurveyDO selectByPrimaryKey(Long id) throws Exception {
        try {
            return surveyDOMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            logger.info("查看调查信息异常:" + e.getMessage());
            throw new ServiceException("查看调查信息异常");
        }
    }

    /**
     * 查询列表
     *
     * @param record
     * @return
     */
    @Override
    public List<SurveyDO> listByIdNumberAndIsValid(SurveyDO record) throws Exception {
        try {
            return surveyDOMapper.listByIdNumberAndIsValid(record);
        } catch (Exception e) {
            logger.info("查询调查信息异常:" + e.getMessage());
            throw new ServiceException("查询调查信息异常");
        }
    }

    /**
     * 编辑
     *
     * @param record
     * @return
     */
    @Override
    public boolean updateByPrimaryKeySelective(SurveyDO record) throws Exception {
        return false;
    }
}
