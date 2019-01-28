package com.example.service.survey;

import com.alibaba.fastjson.JSON;
import com.example.common.util.ExcelUtil;
import com.example.common.util.PoiUtil;
import com.example.common.util.StringUtil;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOService;
import com.example.service.grid.GridInfoDO;
import com.example.service.grid.GridInfoDOMapper;
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

import java.math.BigDecimal;
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
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
    private final GridInfoDOMapper gridInfoDOMapper;

    @Autowired
    public SurveyDOServiceImpl(SurveyDOMapper surveyDOMapper, CustomerDOMapper customerDOMapper, CustomerGreyDOService customerGreyDOService, GridReviewDOMapper gridReviewDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper, GridInfoDOMapper gridInfoDOMapper) {
        this.surveyDOMapper = surveyDOMapper;
        this.customerDOMapper = customerDOMapper;
        this.customerGreyDOService = customerGreyDOService;
        this.gridReviewDOMapper = gridReviewDOMapper;
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
        this.gridInfoDOMapper = gridInfoDOMapper;
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
        String senator = null;
        if (senatorArr.length == 2) {
            senator = senatorArr[1];
            Long senatorId;
            try {
                senatorId = gridReviewDOMapper.getReviewByNameAndGridCode(
                        new GridReviewDO().setGridReviewName(senator).setGridCode(conclusionDO.getGridCode()).setStatus("1"));
            } catch (Exception e) {
                logger.info("获取评议员异常:" + e.getMessage());
                throw new ServiceException("获取评议员异常");
            }
            if (StringUtil.isBlank(senatorId)) {
                throw new ServiceException("评议员不在此网格中或已停用！");
            }
        } else {
            throw new ServiceException("评议员不能为空！");
        }

        // 获取网格最大授信金额
        BigDecimal maxCreditMoney = gridInfoDOMapper.getMaxCreatMoneyByGridCode(conclusionDO.getGridCode());
        double maxCreditAmount = Double.valueOf(maxCreditMoney.toString());

        // 读取Excel数据，并校验
        List<SurveyDO> surveyDOList = new ArrayList<>(1000);
        List<String> errorMessageList = new ArrayList<>();

        // 读取excel起始行
        int i = 6;
        for (; i <= totalRowCount; ) {
            SurveyDO surveyDO = new SurveyDO().setSenator(senator.trim()).setMaxCreditAmount(maxCreditAmount);
            Row iRow = sheet.getRow(i);

            if (iRow == null) {
                break;
            }
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

            if (surveyDO.getErrorMessage() != null) {
                errorMessageList.add(surveyDO.getErrorMessage());
            }
            surveyDOList.add(surveyDO);
        }
        logger.info(JSON.toJSONString(errorMessageList, true));

        if (errorMessageList.size() != 0) {
            throw new ServiceException("" + errorMessageList);
        }

        insertSurveyDOFromExcelList(surveyDOList, conclusionDO);

        return "success";
    }

    /**
     * 通过户号查询客户数据，并导入到调查信息数据库表
     * @param surveyDOList
     */
    private void insertSurveyDOFromExcelList(List<SurveyDO> surveyDOList, ConclusionDO conclusionDO) throws Exception {

        for (SurveyDO surveyDO : surveyDOList) {
            // 是否是同一个评议员，是就不导入
            List<SurveyDO> surveyDOList1 = surveyDOMapper.listByHouseholdIdAndSenator(surveyDO);
            if (surveyDOList1 != null && surveyDOList1.size() != 0) {
                continue;
            }

            // 以户号导入调查信息
            surveyDO.setName(surveyDO.getBorrower())
                    .setIdNumber("123456789987654321")
                    .setDate(String.valueOf(System.currentTimeMillis()))
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());
            try {
                surveyDOMapper.insertSelective(surveyDO);
                if ("是".equals(surveyDO.getIsFamiliar())) {
                    customerDOMapper.plusValidTimeByHouseholdId(new CustomerDO().setHouseholdId(surveyDO.getHouseholdId()));
                }
            } catch (Exception e) {
                logger.info("调查信息导入异常:" + e.getMessage());
                throw new ServiceException("调查信息导入异常");
            }

            // 如果了解为是且不授信原因不为空，则设为灰名单
            if ("是".equals(surveyDO.getIsFamiliar()) && StringUtil.isNotBlank(surveyDO.getNegativeReason())) {

                // 如果在灰名单库里面，则不再重复导入
                List<CustomerTagRelationDO> customerTagRelationDOList = customerTagRelationDOMapper
                        .listCustomerTagRelationsByHouseholdIdAndIfNNTagId(
                                new CustomerTagRelationDO().setTagId(2L).setHouseholdId(surveyDO.getHouseholdId()));

                if (customerTagRelationDOList == null || customerTagRelationDOList.size() == 0) {
                    logger.info("调查表导入灰名单开始");
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
                        CustomerGreyDO customerGreyDO = new CustomerGreyDO();
                        customerGreyDO.setCustomerName(customerDO.getName())
                                .setIdNumber(customerDO.getIdNumber())
                                .setCustomerId(customerDO.getId())
                                .setHouseholdId(surveyDO.getHouseholdId())
                                .setGridCode(conclusionDO.getGridCode())
                                .setGridName(conclusionDO.getGridName())
                                .setOrgCode(conclusionDO.getOrgCode())
                                .setOrgName(conclusionDO.getOrgName())
                                .setUserId(conclusionDO.getUserId())
                                .setReason(surveyDO.getNegativeReason())
                                .setUserName(conclusionDO.getUserName());

                        try {
                            customerGreyDOService.insertSelective(customerGreyDO);
                            logger.info("调查表导入灰名单结束");
                        } catch (Exception e) {
                            logger.info("导入灰名单异常:" + e.getMessage());
                            throw new ServiceException("导入灰名单异常");
                        }
                    }
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
        double maxCreditAmount = surveyDO.getMaxCreditAmount();
        String no = ExcelUtil.getCellValue(iRow.getCell(0));
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtil.isBlank(no)) {
            throw new ServiceException("序号不能为空，请先补全序号后重新导入!");
        }
        String householdId = ExcelUtil.getCellValue(iRow.getCell(3));
        if (StringUtil.isNotLength(householdId, 1, 20)) {
            stringBuilder.append("户号错误：最长20位；");
        }
        String isFamiliar = ExcelUtil.getCellValue(iRow.getCell(11));
        if (!"是".equals(isFamiliar) && !"否".equals(isFamiliar)) {
            stringBuilder.append("是否了解情况错误：只能为是或否；");
        }

        String negativeReason = null;
        String remark =null;
        String borrower = null;
        String houseValue = null;
        String carValue = null;
        String mainBusiness = null;
        String scale = null;
        String income = null;
        String payout = null;
        String creditAmount = null;

        // 如果不熟悉，则后面不校验
        // 或，如果熟悉但是有不授信原因，则后面不校验
        boolean isNotFamiliar = "否".equals(isFamiliar);
        boolean valid = !isNotFamiliar;

        if (valid) {
            negativeReason = ExcelUtil.getCellValue(iRow.getCell(12));
            if (StringUtil.isNotBlank(negativeReason) && StringUtil.isNotLength(negativeReason, 0, 20)) {
                stringBuilder.append("不符合授信原因错误：最长20位；");
            }
        }

        if (valid && StringUtil.isBlank(negativeReason)) {

            remark = ExcelUtil.getCellValue(iRow.getCell(13));

            borrower = ExcelUtil.getCellValue(iRow.getCell(14));

            houseValue = ExcelUtil.getCellValue(iRow.getCell(15));

            carValue = ExcelUtil.getCellValue(iRow.getCell(16));

            mainBusiness = ExcelUtil.getCellValue(iRow.getCell(17));

            scale = ExcelUtil.getCellValue(iRow.getCell(18));

            income = ExcelUtil.getCellValue(iRow.getCell(19));

            payout = ExcelUtil.getCellValue(iRow.getCell(20));

            creditAmount = ExcelUtil.getCellValue(iRow.getCell(21));


            if (StringUtil.isNotBlank(remark) && StringUtil.isNotLength(remark, 0, 50)) {
                stringBuilder.append("备注错误：最长50位；");
            }

            if (StringUtil.isNotLength(borrower, 0, 10)) {
                stringBuilder.append("借款主体错误：不能为空且最长10位；");
            }

            // 判断借款主体是否存在
            if (StringUtil.isLength(borrower, 0, 10)) {
                String borrowerIdNumber;
                try {
                    borrowerIdNumber = customerDOMapper.getIdNumberByNameAndHouseholdId(
                            new CustomerDO().setName(borrower).setHouseholdId(householdId));
                } catch (Exception e) {
                    logger.info("序号：" + no + "查询借款主体客户信息异常:" + e.getMessage());
                    throw new ServiceException("序号：" + no + "，查询借款主体客户信息异常");
                }

                if (StringUtil.isBlank(borrowerIdNumber)) {
                    stringBuilder.append("借款主体错误：借款主体不在此户中；");
                }
            }

            if (StringUtil.isNotBlank(houseValue) && !"有".equals(houseValue) && !"无".equals(houseValue)) {
                stringBuilder.append("有无商品房错误：只能是有或无；");
            }

            if (StringUtil.isNotBlank(carValue) && !"有".equals(carValue) && !"无".equals(carValue)) {
                stringBuilder.append("有无车辆错误：只能是有或无；");
            }

            if (StringUtil.isNotBlank(mainBusiness) && StringUtil.isNotLength(mainBusiness, 0, 20)) {
                stringBuilder.append("主营项目错误：最长20位；");
            }

            if (StringUtil.isNotBlank(scale) && StringUtil.isNotLength(scale, 0, 20)) {
                stringBuilder.append("规模错误：最长20位；");
            }

            if (StringUtil.isNotBlank(income) && StringUtil.isNotNumeric(income, 18)) {
                stringBuilder.append("收入错误：只能是数字；");
            }

            if (StringUtil.isNotBlank(payout) && StringUtil.isNotNumeric(payout, 18)) {
                stringBuilder.append("支出错误：只能是数字；");
            }

            if (StringUtil.isNotNumeric(creditAmount, 18) || (StringUtil.isNumeric(creditAmount, 18) && (Double.valueOf(creditAmount) <= 0 || Double.valueOf(creditAmount) > maxCreditAmount))) {
                stringBuilder.append("授信额度错误：授信额度必须大于0且不大于").append(maxCreditAmount).append("万；");
            }
        }

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
                .setErrorMessage(stringBuilder.toString().isEmpty() ? null : "序号" + no + "错误信息===>" + stringBuilder.toString()) // 错误信息
                .setIsValid(isNotFamiliar ? "否" : "是"); // 不熟悉则为无效

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
    public List<SurveyDO> listByHouseholdIdAndIsValid(SurveyDO record) throws Exception {
        try {
            return surveyDOMapper.listByHouseholdIdAndIsValid(record);
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
