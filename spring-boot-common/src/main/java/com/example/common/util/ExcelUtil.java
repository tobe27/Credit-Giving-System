package com.example.common.util;


import com.example.common.exception.CommonException;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 判断Excel的版本,获取Workbook 
     * @param path
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(String path) throws Exception{
        Workbook wb = null;
        File file = new File(path); // 创建文件对象
        FileInputStream in = new FileInputStream(file); // 文件流
        ExcelUtil.checkExcelValid(file);

        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003  
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010  
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 判断文件是否是excel 
     * @throws Exception
     */
    public static void checkExcelValid(File file) throws Exception{
        if(!file.exists()){
            throw new CommonException("文件不存在");
        }
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
            throw new CommonException("文件不是Excel");
        }
    }


    @SuppressWarnings("deprecation")
    public static Object getValue(Cell cell) {
        if(cell==null){
            return null;
        }
        Object obj = null;
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                obj = cell.getBooleanCellValue();
                break;
            case ERROR:
                obj = cell.getErrorCellValue();
                break;
            case NUMERIC:
                obj = cell.getNumericCellValue();
                break;
            case STRING:
                obj = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return obj;
    }


    /**
     * 获取各种格式的值转成String
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }

        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return cell.getCellFormula();
        }

        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    /**
     * 获取合并单元格的行
     * @param sheet
     * @return
     */
    public static List<CellRangeAddress> getMergeCellRow(Sheet sheet) {
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        // 获取sheet中合并单元格的数量
        int sheetMergeCellCount = sheet.getNumMergedRegions();
        // 遍历所有的合并单元格
        for (int i = 0; i < sheetMergeCellCount; i ++) {
            // 获得合并单元格保存到list
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            cellRangeAddressList.add(cellRangeAddress);
        }

        return cellRangeAddressList;
    }

    /**
     * 获取合并单元格占用几行
     * @param mergeCellList
     * @param cell
     * @param sheet
     * @return
     */
    public static int getMergeCellCount(Sheet sheet, List<CellRangeAddress> mergeCellList, Cell cell) {
        int count = 0;
        int firstCol = 0;
        int lastCol = 0;
        int firstRow = 0;
        int lastRow = 0;

        for (CellRangeAddress cellRangeAddress : mergeCellList) {
            firstCol = cellRangeAddress.getFirstColumn();
            lastCol = cellRangeAddress.getLastColumn();
            firstRow = cellRangeAddress.getFirstRow();
            lastRow = cellRangeAddress.getLastRow();

            if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow) {
                if (cell.getColumnIndex() >= firstCol && cell.getColumnIndex() <= lastCol) {
                    count = lastRow - firstRow + 1;
                }
            }
        }

        return count;
    }

    /**
     * 获取合并单元格的第一行
     * @param sheet
     * @param mergeCellList
     * @param cell
     * @return
     */
    public static int getMergeCellFirstRowNum(Sheet sheet, List<CellRangeAddress> mergeCellList, Cell cell) {
        int mergeCellFirstRowNum = 0;
        int firstCol = 0;
        int lastCol = 0;
        int firstRow = 0;
        int lastRow = 0;

        for (CellRangeAddress cellRangeAddress : mergeCellList) {
            firstCol = cellRangeAddress.getFirstColumn();
            lastCol = cellRangeAddress.getLastColumn();
            firstRow = cellRangeAddress.getFirstRow();
            lastRow = cellRangeAddress.getLastRow();

            if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow) {
                if (cell.getColumnIndex() >= firstCol && cell.getColumnIndex() <= lastCol) {
                    mergeCellFirstRowNum = firstRow;
                }
            }
        }

        return mergeCellFirstRowNum;
    }

    /**
     * 判断是否为合并单元格，是就返回该值
     * @param sheet
     * @param mergeCellList
     * @param cell
     * @return
     */
    public static String checkIsMergeCellAndGet(Sheet sheet, List<CellRangeAddress> mergeCellList, Cell cell) {
        int firstCol = 0;
        int lastCol = 0;
        int firstRow = 0;
        int lastRow = 0;
        String mergeCellValue = "";

        for (CellRangeAddress cellRangeAddress : mergeCellList) {
            firstCol = cellRangeAddress.getFirstColumn();
            lastCol = cellRangeAddress.getLastColumn();
            firstRow = cellRangeAddress.getFirstRow();
            lastRow = cellRangeAddress.getLastRow();

            if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow) {
                if (cell.getColumnIndex() >= firstCol && cell.getColumnIndex() <= lastCol) {
                    Row row = sheet.getRow(firstRow);
                    Cell cellL = row.getCell(firstCol);
                    mergeCellValue = getCellValue(cellL);
                    break;
                }
            }
        }
        return mergeCellValue;
    }


    /**
     * 获取合并单元格的值
     * @param sheet
     * @param rowNum
     * @param ColNum
     * @return
     */
    public static String getMergeCellValue(Sheet sheet, int rowNum, int ColNum) {
        int sheetMergeCellCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCellCount; i ++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            int firstCol = cellRangeAddress.getFirstColumn();
            int lastCol = cellRangeAddress.getLastColumn();
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();

            if (rowNum >= firstRow && rowNum <= lastRow) {
                if (ColNum >= firstCol && ColNum <= lastCol) {
                    Row row = sheet.getRow(firstRow);
                    Cell cell = row.getCell(firstCol);
                    return getCellValue(cell);
                }
            }
        }
        return "";
    }

    /**
     * 判断是否为合并单元格
     * @param sheet
     * @param rowNum
     * @param ColNum
     * @return
     */
    public static boolean isMergeCell(Sheet sheet, int rowNum, int ColNum) {
        int sheetMergeCellCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCellCount; i ++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            int firstCol = cellRangeAddress.getFirstColumn();
            int lastCol = cellRangeAddress.getLastColumn();
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();

            if (rowNum >= firstRow && rowNum <= lastRow) {
                if (ColNum >= firstCol && ColNum <= lastCol) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 调查信息生成Excel直接导出到IO流
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void output2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = output2Workbook(sheetName, dataList);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 创建Excel，把数据读入workbook
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataList
     * @return
     */
    public static Workbook output2Workbook(String sheetName, List<Map<String, Object>> dataList) {

        if (dataList == null || dataList.size() == 0) {
            throw new CommonException("数据为空！");
        }

        Integer totalDataRow = Integer.valueOf(String.valueOf(dataList.get(dataList.size() - 1).get("count"))) == 0
                ? 1 : Integer.valueOf(String.valueOf(dataList.get(dataList.size() - 1).get("count")));
        // 创建excel,HSSFWorkbook生成xls，XSSFWorkbook-xlsx
        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 4*256);
        sheet.setColumnWidth(1, 12*256);
        sheet.setColumnWidth(2, 6*256);
        sheet.setColumnWidth(3, 14*256);
        sheet.setColumnWidth(4, 8*256);
        sheet.setColumnWidth(5, 10*256);
        sheet.setColumnWidth(6, 6*256);
        sheet.setColumnWidth(7, 2*256);
        sheet.setColumnWidth(8, 2*256);
        sheet.setColumnWidth(9, 14*256);
        sheet.setColumnWidth(10, 2*256);
        sheet.setColumnWidth(11, 8*256);
        sheet.setColumnWidth(12, 12*256);
        sheet.setColumnWidth(13, 12*256);
        sheet.setColumnWidth(14, 10*256);
        sheet.setColumnWidth(15, 8*256);
        sheet.setColumnWidth(16, 8*256);
        sheet.setColumnWidth(17, 8*256);
        sheet.setColumnWidth(18, 8*256);
        sheet.setColumnWidth(19, 8*256);
        sheet.setColumnWidth(20, 8*256);
        sheet.setColumnWidth(21, 8*256);
        sheet.setColumnWidth(22, 10*256);

        // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 22); //起始行 结束行 起始列 结束列
        // 第二行地址、日期、单位
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 5);
        CellRangeAddress village = new CellRangeAddress(1, 1, 6, 9);
        CellRangeAddress date = new CellRangeAddress(1, 1, 10, 14);
        CellRangeAddress unit = new CellRangeAddress(1, 1, 15, 22);
        // 第三行评议员
        CellRangeAddress senator = new CellRangeAddress(2, 2, 0, 22);
        // 注意事项
        CellRangeAddress caution = new CellRangeAddress(3, 3, 0, 22);
        // 第四、五行是标题
        // 序号
        CellRangeAddress no = new CellRangeAddress(4, 5, 0, 0);
        // 户主
        CellRangeAddress houseOwner = new CellRangeAddress(4, 4, 1, 2);

        // 户号
        CellRangeAddress householdId = new CellRangeAddress(4, 5, 3, 3);

        // 成员
        CellRangeAddress member = new CellRangeAddress(4, 4, 4, 6);

        // 身份证及其他
        CellRangeAddress idNumber = new CellRangeAddress(4, 5, 7, 7);
        CellRangeAddress phoneNumber = new CellRangeAddress(4, 5, 8, 8);
        CellRangeAddress address = new CellRangeAddress(4, 5, 9, 9);
        CellRangeAddress ensureAmount = new CellRangeAddress(4, 5, 10, 10);

        CellRangeAddress isFamiliar = new CellRangeAddress(4, 5, 11, 11);
        CellRangeAddress negativeReason = new CellRangeAddress(4, 5, 12, 12);

        // 下拉框信息
        String[] isFamiliarArr = {"是", "否"};
        String[] negativeReasonArr = {"1游手好闲", "2欠债较多", "3服刑", "4长期外出", "5信用观念差",
                "6赌博、吸毒放高利贷", "7光棍", "8患病、残疾", "9有前科", "10婚变",
                "11家庭不和睦", "12常年不回家（2年以上）", "13长期外出务工", "14年龄不符合", "15其他负面情况"};
        String[] haveOrNot = {"有", "无"};
        // 下拉框格式设置
        CellRangeAddressList isFamiliarList = new CellRangeAddressList(6, totalDataRow + 6, 11, 11);
        CellRangeAddressList negativeReasonList = new CellRangeAddressList(6, totalDataRow + 6, 12, 12);
        CellRangeAddressList houseValueList = new CellRangeAddressList(6, totalDataRow + 6, 15, 15);
        CellRangeAddressList carValueList = new CellRangeAddressList(6, totalDataRow + 6, 16, 16);

        // 写入下拉数据
        DVConstraint isFamiliarConstraint = DVConstraint.createExplicitListConstraint(isFamiliarArr);
        DVConstraint negativeReasonConstraint = DVConstraint.createExplicitListConstraint(negativeReasonArr);
        DVConstraint haveOrNotConstraint = DVConstraint.createExplicitListConstraint(haveOrNot);

        // 绑定格式与数据
        HSSFDataValidation isFamiliarVal = new HSSFDataValidation(isFamiliarList, isFamiliarConstraint);
        HSSFDataValidation negativeReasonVal = new HSSFDataValidation(negativeReasonList, negativeReasonConstraint);
        HSSFDataValidation houseValueVal = new HSSFDataValidation(houseValueList, haveOrNotConstraint);
        HSSFDataValidation carValueVal = new HSSFDataValidation(carValueList, haveOrNotConstraint);

        sheet.addValidationData(isFamiliarVal);
        sheet.addValidationData(negativeReasonVal);
        sheet.addValidationData(houseValueVal);
        sheet.addValidationData(carValueVal);


        CellRangeAddress remark = new CellRangeAddress(4, 5, 13, 13);
        CellRangeAddress borrower = new CellRangeAddress(4, 5, 14, 14);
        CellRangeAddress houseValue = new CellRangeAddress(4, 5, 15, 15);
        CellRangeAddress carValue = new CellRangeAddress(4, 5, 16, 16);
        CellRangeAddress mainBusiness = new CellRangeAddress(4, 5, 17, 17);
        CellRangeAddress scale = new CellRangeAddress(4, 5, 18, 18);
        CellRangeAddress income = new CellRangeAddress(4, 5, 19, 19);
        CellRangeAddress payout = new CellRangeAddress(4, 5, 20, 20);
        CellRangeAddress creditAmount = new CellRangeAddress(4, 5, 21, 21);
        CellRangeAddress validTime = new CellRangeAddress(4, 5, 22, 22);

        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
        sheet.addMergedRegion(village);
        sheet.addMergedRegion(date);
        sheet.addMergedRegion(unit);
        sheet.addMergedRegion(senator);
        sheet.addMergedRegion(caution);
        sheet.addMergedRegion(no);
        sheet.addMergedRegion(houseOwner);
        sheet.addMergedRegion(householdId);
        sheet.addMergedRegion(member);
        sheet.addMergedRegion(idNumber);
        sheet.addMergedRegion(phoneNumber);
        sheet.addMergedRegion(address);
        sheet.addMergedRegion(ensureAmount);
        sheet.addMergedRegion(isFamiliar);
        sheet.addMergedRegion(negativeReason);
        sheet.addMergedRegion(remark);
        sheet.addMergedRegion(borrower);
        sheet.addMergedRegion(houseValue);
        sheet.addMergedRegion(carValue);
        sheet.addMergedRegion(mainBusiness);
        sheet.addMergedRegion(scale);
        sheet.addMergedRegion(income);
        sheet.addMergedRegion(payout);
        sheet.addMergedRegion(creditAmount);
        sheet.addMergedRegion(validTime);
        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信评议调查表");

        // 创建地址、日期、单位
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        Cell villageCol = unitRow.createCell(6);
        Cell dateCol = unitRow.createCell(10);
        Cell unitCol = unitRow.createCell(15);
        townshipCol.setCellValue("乡（镇，街道）:");
        villageCol.setCellValue("行政村:");
        dateCol.setCellValue("日期:     年   月   日");
        unitCol.setCellStyle(colTitleCellStyle);
        unitCol.setCellValue("单位: 万元");

        // 创建评议员姓名
        Row senatorRow = sheet.createRow(2);
        Cell senatorCol = senatorRow.createCell(0);
        senatorCol.setCellValue("评议员姓名:");

        // 注意事项
        Row cautionRow = sheet.createRow(3);
        Cell cautionCol = cautionRow.createCell(0);
        cautionCol.setCellValue("注意：当选择是否了解情况为否时，该户调查无效；当选择不符合授信原因时该户不予授信，选择其他负面情况时请注明理由。");

        // 列名
        Row colTitleRow = sheet.createRow(4);
        String[] colTitles = {"序号", "户主", "", "户号", "成员", "", "", "身份证号码", "手机号", "联系地址",
                "对外担保总额", "是否了解情况", "不符合授信原因", "备注", "借款主体", "有无商品房", "有无车辆",
                "主营项目", "规模", "收入", "支出", "授信额度（0-5万元）", "已完成有效调查次数"};
        for (int i = 0; i < colTitles.length; i ++) {
            Cell colTitleCol = colTitleRow.createCell(i);
            colTitleCol.setCellStyle(colTitleCellStyle);
            colTitleCol.setCellValue(colTitles[i]);
        }

        // 子列名
        Row sonTitleRow = sheet.createRow(5);
        Cell houseOwnerNameCol = sonTitleRow.createCell(1);
        Cell houseOwnerAgeCol = sonTitleRow.createCell(2);
        Cell relationshipCol = sonTitleRow.createCell(4);
        Cell memberNameCol = sonTitleRow.createCell(5);
        Cell memberAgeCol = sonTitleRow.createCell(6);
        houseOwnerNameCol.setCellStyle(colTitleCellStyle);
        houseOwnerNameCol.setCellValue("姓名");

        houseOwnerAgeCol.setCellStyle(colTitleCellStyle);
        houseOwnerAgeCol.setCellValue("年龄");

        relationshipCol.setCellStyle(colTitleCellStyle);
        relationshipCol.setCellValue("与户主的关系");

        memberNameCol.setCellStyle(colTitleCellStyle);
        memberNameCol.setCellValue("姓名");

        memberAgeCol.setCellStyle(colTitleCellStyle);
        memberAgeCol.setCellValue("年龄");


        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 300);

        int count = 6; // 起始行

        // 数据导入到EXCEL-数据最后一个是总行数，所有-1去除掉，循环导入户主
        for (int i = 0; i < dataList.size() - 1; i ++) {
            // 获取户主信息
            Map<String, Object> houseOwnerMap = dataList.get(i);
            // 成员列表
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) houseOwnerMap.get("memberList");

            // 户主表格合并单元格
            // 行数小于2时，无法合并单元格
            if (memberList.size() >= 2) {
                for (int colNum = 0; colNum <= 22; colNum++) {
                    if (colNum == 4 || colNum == 5 || colNum == 6) {
                        continue;
                    }
                    CellRangeAddress titleName = new CellRangeAddress(count, (count + memberList.size() - 1), colNum, colNum); // 起始行 结束行 起始列 结束列
                    sheet.addMergedRegion(titleName);
                }
            }

            // 户主信息填充到合并单元格
            String[] houseOwnerColName = {"no", "name", "age", "householdId", "", "", "", "idNumber", "phoneNumber", "address", "outEnsureAmount"};
            Row titleNameRow = sheet.createRow(count);
            Cell validTimeCol = titleNameRow.createCell(22);
            validTimeCol.setCellValue(houseOwnerMap.get("validTime") == null ? null : houseOwnerMap.get("validTime").toString());
            for (int houseOwnerColNum = 0; houseOwnerColNum <= 10; houseOwnerColNum ++) {
                if (houseOwnerColNum == 4 || houseOwnerColNum == 5 || houseOwnerColNum == 6) {
                    continue;
                }
                Cell titleNameCol = titleNameRow.createCell(houseOwnerColNum);
                if (houseOwnerColNum == 9) {
                    titleNameCol.setCellStyle(wrapCellStyle);
                } else {
                    titleNameCol.setCellStyle(contentCellStyle);
                }
                titleNameCol.setCellValue(houseOwnerMap.get(houseOwnerColName[houseOwnerColNum]) == null
                        ? null : houseOwnerMap.get(houseOwnerColName[houseOwnerColNum]).toString());

            }

            // 循环-成员信息填充到成员表格中
            if (memberList.size() > 0) {
                for (int k = 0; k < memberList.size(); k++) {
                    Map<String, Object> memberMap = memberList.get(k);
                    String[] memberColName = {"", "", "", "", "relationship", "name", "age"};
                    Row memberNameRow = null;
                    if (k > 0) {
                        memberNameRow = sheet.createRow(count + k);
                    }
                    for (int m = 4; m <= 6; m++) {
                        if (k == 0) {
                            Cell memberCol = titleNameRow.createCell(m);
                            memberCol.setCellStyle(contentCellStyle);
                            memberCol.setCellValue(memberMap.get(memberColName[m]) == null
                                    ? null : memberMap.get(memberColName[m]).toString());
                        } else {
                            Cell memberCol = memberNameRow.createCell(m);
                            memberCol.setCellStyle(contentCellStyle);
                            memberCol.setCellValue(memberMap.get(memberColName[m]) == null
                                    ? null : memberMap.get(memberColName[m]).toString());
                        }

                    }
                }
            }

            // 保证下次循环能继续往下走
            count += memberList.size() == 0 ? 1 : memberList.size();

        }

        return workbook;
    }


    /**
     * 不可以授信客户生成Excel直接导出到IO流
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void outputOutCredit2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputOutCredit2Workbook(sheetName, dataList);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 不能评议客户导出excel
     * @param sheetName
     * @param dataList
     * @return
     */
    public static Workbook outputOutCredit2Workbook(String sheetName, List<Map<String, Object>> dataList) {

        if (dataList == null || dataList.size() == 0) {
            throw new CommonException("数据为空！");
        }

        int totalDataRow = Integer.valueOf(String.valueOf(dataList.get(dataList.size() - 1).get("count"))) == 0
                ? 1 : Integer.valueOf(String.valueOf(dataList.get(dataList.size() - 1).get("count")));
        // 创建excel,HSSFWorkbook生成xls，XSSFWorkbook-xlsx

        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 6*256);
        sheet.setColumnWidth(1, 20*256);
        sheet.setColumnWidth(2, 8*256);
        sheet.setColumnWidth(3, 14*256);
        sheet.setColumnWidth(4, 12*256);
        sheet.setColumnWidth(5, 8*256);
        sheet.setColumnWidth(6, 22*256);
        sheet.setColumnWidth(7, 14*256);
        sheet.setColumnWidth(8, 50*256);
        sheet.setColumnWidth(9, 30*256);

        // 设置样式=============================================================================
        // 合并单元格，主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 9); //起始行 结束行 起始列 结束列
        // 第二行地址、日期、单位
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 5);
        CellRangeAddress village = new CellRangeAddress(1, 1, 6, 9);

        // 第三行评议员
        CellRangeAddress senator = new CellRangeAddress(2, 2, 0, 9);

        // 第四、五行是标题
        // 序号
        CellRangeAddress no = new CellRangeAddress(3, 4, 0, 0);

        // 户号
        CellRangeAddress householdId = new CellRangeAddress(3, 4, 1, 1);

        // 家庭人口数
        CellRangeAddress familySize = new CellRangeAddress(3, 4, 2, 2);

        // 家庭成员
        CellRangeAddress member = new CellRangeAddress(3, 3, 3, 9);


        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
        sheet.addMergedRegion(village);
        sheet.addMergedRegion(senator);
        sheet.addMergedRegion(no);
        sheet.addMergedRegion(householdId);
        sheet.addMergedRegion(familySize);
        sheet.addMergedRegion(member);

        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("不能评议客户");

        // 创建地址、日期、单位
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        Cell villageCol = unitRow.createCell(6);

        townshipCol.setCellValue("乡（镇，街道）:");
        villageCol.setCellValue("行政村:");


        // 创建评议员姓名
        Row senatorRow = sheet.createRow(2);
        Cell senatorCol = senatorRow.createCell(0);
        senatorCol.setCellValue("评议员姓名:");

        // 列名
        Row colTitleRow = sheet.createRow(3);
        String[] colTitles = {"序号", "户号", "家庭人口数", "家庭成员"};
        for (int i = 0; i < colTitles.length; i ++) {
            Cell colTitleCol = colTitleRow.createCell(i);
            colTitleCol.setCellStyle(colTitleCellStyle);
            colTitleCol.setCellValue(colTitles[i]);
        }

        // 子列名
        Row sonTitleRow = sheet.createRow(4);
        Cell relationshipCol = sonTitleRow.createCell(3);
        Cell memberNameCol = sonTitleRow.createCell(4);
        Cell memberAgeCol = sonTitleRow.createCell(5);
        Cell idNumberCol = sonTitleRow.createCell(6);
        Cell phoneNumberCol = sonTitleRow.createCell(7);
        Cell addressCol = sonTitleRow.createCell(8);
        Cell familyTagsCol = sonTitleRow.createCell(9);


        relationshipCol.setCellStyle(colTitleCellStyle);
        relationshipCol.setCellValue("与户主的关系");

        memberNameCol.setCellStyle(colTitleCellStyle);
        memberNameCol.setCellValue("姓名");

        memberAgeCol.setCellStyle(colTitleCellStyle);
        memberAgeCol.setCellValue("年龄");

        idNumberCol.setCellStyle(colTitleCellStyle);
        idNumberCol.setCellValue("身份证号");

        phoneNumberCol.setCellStyle(colTitleCellStyle);
        phoneNumberCol.setCellValue("手机号");

        addressCol.setCellStyle(colTitleCellStyle);
        addressCol.setCellValue("联系地址");

        familyTagsCol.setCellStyle(colTitleCellStyle);
        familyTagsCol.setCellValue("家庭成员标签");


        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 300);

        int count = 5; // 起始行

        // 数据导入到EXCEL-数据最后一个是总行数，所有-1去除掉，循环导入户主
        for (int i = 0; i < dataList.size() - 1; i ++) {
            // 获取户主信息
            Map<String, Object> houseOwnerMap = dataList.get(i);
            // 成员列表
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) houseOwnerMap.get("memberList");

            // 户主表格合并单元格
            // 行数小于2时，无法合并单元格
            if (memberList.size() >= 2) {
                for (int colNum = 0; colNum <= 2; colNum++) {
                    CellRangeAddress titleName = new CellRangeAddress(count, (count + memberList.size() - 1), colNum, colNum); // 起始行 结束行 起始列 结束列
                    sheet.addMergedRegion(titleName);
                }
            }

            // 户主信息填充到合并单元格
            String[] houseOwnerColName = {"no", "householdId", "familySize"};
            Row titleNameRow = sheet.createRow(count);

            for (int houseOwnerColNum = 0; houseOwnerColNum < houseOwnerColName.length; houseOwnerColNum ++) {

                Cell titleNameCol = titleNameRow.createCell(houseOwnerColNum);
                titleNameCol.setCellValue(houseOwnerMap.get(houseOwnerColName[houseOwnerColNum]) == null
                        ? null : houseOwnerMap.get(houseOwnerColName[houseOwnerColNum]).toString());

            }

            // 循环-成员信息填充到成员表格中
            if (memberList.size() > 0) {
                logger.info("成员数：" + memberList.size());
                for (int k = 0; k < memberList.size(); k++) {

                    Map<String, Object> memberMap = memberList.get(k);
                    String[] memberColName = {"", "", "", "relationship", "name", "age", "idNumber", "phoneNumber", "address", "familyTags"};
                    Row memberNameRow = null;
                    if (k > 0) {
                        memberNameRow = sheet.createRow(count + k);
                        logger.info("创建的行数：" +(count + k) );
                    }
                    for (int m = 3; m < memberColName.length; m++) {
                        if (k == 0) {
                            Cell memberCol = titleNameRow.createCell(m);
                            memberCol.setCellStyle(contentCellStyle);
                            memberCol.setCellValue(memberMap.get(memberColName[m]) == null
                                    ? null : memberMap.get(memberColName[m]).toString());
                        } else {
                            Cell memberCol = memberNameRow.createCell(m);
                            memberCol.setCellStyle(contentCellStyle);
                            memberCol.setCellValue(memberMap.get(memberColName[m]) == null
                                    ? null : memberMap.get(memberColName[m]).toString());
                        }

                    }
                }
            }

            // 保证下次循环能继续往下走
            count += memberList.size();

        }

        return workbook;
    }


    /**
     * 预授信生成Excel直接导出到IO流
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void outputCredit2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, List<String> senatorList, Integer calType, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputCredit2Workbook(sheetName, dataList, senatorList, calType);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 预评议客户导出，创建Excel，把数据读入workbook
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataList
     * @param senatorList 评议员列表
     * @param calType 计算授信额度方式 2-最小/1-平均
     * @return
     */
    public static Workbook outputCredit2Workbook(String sheetName, List<Map<String, Object>> dataList, List<String> senatorList, Integer calType) {

        int senatorCount = senatorList.size();
        int colCount = 7 + senatorCount; // 从0算起

        // 创建excel,HSSFWorkbook生成xls，XSSFWorkbook-xlsx
        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 8*256);
        sheet.setColumnWidth(1, 10*256);
        sheet.setColumnWidth(2, 20*256);
        sheet.setColumnWidth(3, 50*256);
        sheet.setColumnWidth(4, 15*256);

        for (int i = 1; i <= senatorCount; i ++) {
            sheet.setColumnWidth(4 + i, 12*256);
        }

        sheet.setColumnWidth(4 + senatorCount + 1, 14*256);
        sheet.setColumnWidth(4 + senatorCount + 2, 14*256);
        sheet.setColumnWidth(4 + senatorCount + 3, 20*256);


        // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, colCount); //起始行 结束行 起始列 结束列
        // 第二行地址、日期、单位
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 2);
        CellRangeAddress village = new CellRangeAddress(1, 1, 3, 5);
        CellRangeAddress date = new CellRangeAddress(1, 1, 6, 4 + senatorCount);
        CellRangeAddress unit = new CellRangeAddress(1, 1, 4 + senatorCount + 1, colCount);




        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
        sheet.addMergedRegion(village);
        sheet.addMergedRegion(date);
        sheet.addMergedRegion(unit);

        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信评议调查表");

        // 创建地址、日期、单位
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        Cell villageCol = unitRow.createCell(3);
        Cell dateCol = unitRow.createCell(6);
        Cell unitCol = unitRow.createCell(4 + senatorCount + 1);
        townshipCol.setCellValue("乡（镇，街道）:");
        villageCol.setCellValue("行政村:");
        dateCol.setCellValue("日期:     年   月   日");
        unitCol.setCellStyle(colTitleCellStyle);
        unitCol.setCellValue("单位: 万元");

        // 列名
        Row sonTitleRow = sheet.createRow(2);
        Cell noCol = sonTitleRow.createCell(1);
        noCol.setCellStyle(colTitleCellStyle);
        noCol.setCellValue("序号");

        Cell nameCol = sonTitleRow.createCell(1);
        nameCol.setCellStyle(colTitleCellStyle);
        nameCol.setCellValue("姓名");

        Cell idNumberCol = sonTitleRow.createCell(2);
        idNumberCol.setCellStyle(colTitleCellStyle);
        idNumberCol.setCellValue("身份证号码");

        Cell addressCol = sonTitleRow.createCell(3);
        addressCol.setCellStyle(colTitleCellStyle);
        addressCol.setCellValue("联系地址");

        Cell phoneNumberCol = sonTitleRow.createCell(4);
        phoneNumberCol.setCellStyle(colTitleCellStyle);
        phoneNumberCol.setCellValue("手机号码");

        Cell validTimeCol = sonTitleRow.createCell(colCount -2);
        validTimeCol.setCellStyle(colTitleCellStyle);
        validTimeCol.setCellValue("有效评议次数");

        Cell calTypeCol = sonTitleRow.createCell(colCount -1);
        calTypeCol.setCellStyle(colTitleCellStyle);
        calTypeCol.setCellValue("评议结果(" + (calType == 1 ? "平均" : "最小") + ")");

        Cell borrowerCol = sonTitleRow.createCell(colCount);
        borrowerCol.setCellStyle(colTitleCellStyle);
        borrowerCol.setCellValue("借款主体");

        // 评议员
        for (int i = 0; i < senatorCount; i ++) {
            Cell senatorCol = sonTitleRow.createCell(4 + i + 1);
            senatorCol.setCellStyle(colTitleCellStyle);
            senatorCol.setCellValue(senatorList.get(i));
        }


        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 300);

        int count = 3; // 起始行

        // 数据导入到EXCEL-循环导入户主
        for (int i = 0; i < dataList.size(); i ++) {
            // 获取户主信息
            Map<String, Object> houseOwnerMap = dataList.get(i);

            // 循环-户主信息填充到成员表格中
            String[] houseOwnerFrontKey = {"no", "name", "idNumber", "address", "phoneNumber"};
            String[] houseOwnerBackKey = {"validTime", "calType", "borrower"};
            Row houseOwnerRow = sheet.createRow(count + i);

            // 导入除评议员的信息
            for (int k = 0; k <= houseOwnerFrontKey.length - 1; k ++) {
                Cell fixedCol = houseOwnerRow.createCell(k);
                fixedCol.setCellStyle(contentCellStyle);
                fixedCol.setCellValue(houseOwnerMap.get(houseOwnerFrontKey[k]) == null
                        ? null : houseOwnerMap.get(houseOwnerFrontKey[k]).toString());

            }

            for (int k = 4 + senatorCount + 1; k <= colCount; k ++) {
                Cell fixedCol = houseOwnerRow.createCell(k);
                fixedCol.setCellStyle(contentCellStyle);
                fixedCol.setCellValue(houseOwnerMap.get(houseOwnerBackKey[k - 5- senatorCount]) == null
                        ? null : houseOwnerMap.get(houseOwnerBackKey[k - 5- senatorCount]).toString());
            }

            // 导入评议员信息
            for (int j = 0; j < senatorCount; j ++) {
                String senator = senatorList.get(j);
                Cell senatorCol = houseOwnerRow.createCell(4 + j + 1);
                senatorCol.setCellStyle(contentCellStyle);
                senatorCol.setCellValue(houseOwnerMap.get(senator) == null
                        ? null : houseOwnerMap.get(senator).toString());
            }
        }

        return workbook;
    }


    /**
     * 借款人调查信息生成Excel直接导出到IO流
     * @param excelName
     * @param sheetName
     * @param dataMap
     * @param response
     * @throws IOException
     */
    public static void outputSurvey2Excel(String excelName, String sheetName, Map<String, Object> dataMap, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputSurvey2Workbook(sheetName, dataMap);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }
    }

    /**
     * 导出借款人的调查表
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataMap
     * @return
     */
    public static Workbook outputSurvey2Workbook(String sheetName, Map<String, Object> dataMap) {

        // 创建excel,HSSFWorkbook生成xls，XSSFWorkbook-xlsx
        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 16*256);
        sheet.setColumnWidth(1, 10*256);
        sheet.setColumnWidth(2, 10*256);
        sheet.setColumnWidth(3, 12*256);
        sheet.setColumnWidth(4, 20*256);
        sheet.setColumnWidth(5, 20*256);
        sheet.setColumnWidth(6, 20*256);
        sheet.setColumnWidth(7, 20*256);
        sheet.setColumnWidth(8, 20*256);

        // 设置样式start=============================================================================

        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 8); //起始行 结束行 起始列 结束列
        // 第二行地址、日期、单位
        CellRangeAddress branchBank = new CellRangeAddress(1, 1, 0, 3);
        CellRangeAddress date = new CellRangeAddress(1, 1, 4, 6);
        CellRangeAddress unit = new CellRangeAddress(1, 1, 7, 8);

        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(branchBank);
        sheet.addMergedRegion(date);
        sheet.addMergedRegion(unit);


        // 第一部分-借款人基本情况
        CellRangeAddress borrowerInfo = new CellRangeAddress(2, 3, 0, 0);
        sheet.addMergedRegion(borrowerInfo);
        // 第二部分-家庭成员情况
        CellRangeAddress familyInfo = new CellRangeAddress(4, 10, 0, 0);
        sheet.addMergedRegion(familyInfo);
        for (int i = 4; i <= 10; i ++) {
            // 成员姓名
            CellRangeAddress memberName = new CellRangeAddress(i, i, 1, 2);
            // 成员与户主关系
            CellRangeAddress relationship = new CellRangeAddress(i, i, 3, 5);
            // 成员身份证号
            CellRangeAddress memberIdNumber = new CellRangeAddress(i, i, 6, 8);

            sheet.addMergedRegion(memberName);
            sheet.addMergedRegion(relationship);
            sheet.addMergedRegion(memberIdNumber);
        }

        // 第三部分-家庭资产情况
        CellRangeAddress familyProperty = new CellRangeAddress(11, 12, 0, 0);
        sheet.addMergedRegion(familyProperty);
        for (int i = 11; i <=12; i ++) {
            // 是否有商品房
            CellRangeAddress haveHouse = new CellRangeAddress(i, i, 1, 2);
            // 是否有车
            CellRangeAddress haveCar = new CellRangeAddress(i, i, 3, 4);
            // 是否有存款
            CellRangeAddress haveDeposit = new CellRangeAddress(i, i, 5, 6);
            // 其他资产情况
            CellRangeAddress otherProperty = new CellRangeAddress(i, i, 7, 8);

            sheet.addMergedRegion(haveHouse);
            sheet.addMergedRegion(haveCar);
            sheet.addMergedRegion(haveDeposit);
            sheet.addMergedRegion(otherProperty);
        }

        // 第四部分-负债情况、家庭收入预测、信用状况、资金需求、被调查人意见
        CellRangeAddress debt = new CellRangeAddress(13, 14, 0, 0);
        CellRangeAddress familyIncome = new CellRangeAddress(15, 16, 0, 0);
        CellRangeAddress credit = new CellRangeAddress(17, 20, 0, 0);
        CellRangeAddress needs = new CellRangeAddress(21, 22, 0, 0);
        CellRangeAddress opinion = new CellRangeAddress(23, 26, 0, 0);

        sheet.addMergedRegion(debt);
        sheet.addMergedRegion(familyIncome);
        sheet.addMergedRegion(credit);
        sheet.addMergedRegion(needs);
        sheet.addMergedRegion(opinion);

        // 第四部分内容第一列、第二列、第三列
        for (int i = 13; i <= 22; i ++) {
            CellRangeAddress forthDep1 = new CellRangeAddress(i, i, 1, 3);
            CellRangeAddress forthDep2 = new CellRangeAddress(i, i, 4, 5);
            CellRangeAddress forthDep3 = new CellRangeAddress(i, i, 6, 8);

            sheet.addMergedRegion(forthDep1);
            sheet.addMergedRegion(forthDep2);
            sheet.addMergedRegion(forthDep3);
        }

        // 最下面意见内容栏
        CellRangeAddress opinionTop = new CellRangeAddress(23, 24, 1, 8);
        CellRangeAddress opinionBottomLeft = new CellRangeAddress(25, 26, 1, 3);
        CellRangeAddress opinionBottomRight = new CellRangeAddress(25, 26, 8, 8);
        CellRangeAddress opinionBottomDate = new CellRangeAddress(26, 26, 6, 7);

        sheet.addMergedRegion(opinionTop);
        sheet.addMergedRegion(opinionBottomLeft);
        sheet.addMergedRegion(opinionBottomRight);
        sheet.addMergedRegion(opinionBottomDate);

        // 设置格式end=====================================================================================

        // 创建标题start===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("运城市农村信用社农户基本情况调查表");

        // 创建地址、日期、单位
        Row unitRow = sheet.createRow(1);
        Cell branchBankCol = unitRow.createCell(0);
        Cell dateCol = unitRow.createCell(4);
        Cell unitCol = unitRow.createCell(7);
        branchBankCol.setCellStyle(colTitleCellStyle);
        branchBankCol.setCellValue("_____农商银行（联社）___支行（信用社）");
        dateCol.setCellStyle(colTitleCellStyle);
        dateCol.setCellValue("    年   月   日");
        unitCol.setCellStyle(colTitleCellStyle);
        unitCol.setCellValue("单位: 万元");

        // 第一部分-借款人基本情况
        Row firstDeptRow1 = sheet.createRow(2);
        Cell borrowerInfoCol = firstDeptRow1.createCell(0);
        borrowerInfoCol.setCellStyle(colTitleCellStyle);
        borrowerInfoCol.setCellValue("借款人基本情况");

        String[] firstDeptTitleArr = {"姓名", "性别", "年龄", "身份证号", "联系电话", "主营项目", "规模", "住址"};
        for (int i = 1; i <= 8; i ++) {
            Cell firstDeptTitleCol = firstDeptRow1.createCell(i);
            firstDeptTitleCol.setCellStyle(colTitleCellStyle);
            firstDeptTitleCol.setCellValue(firstDeptTitleArr[i - 1]);
        }

        // 导入基本信息的数据
        String[] firstDeptContentArr = {"name", "gender", "age", "idNumber", "phoneNumber", "mainBusiness", "scale", "address"};
        Row firstDeptRow2 = sheet.createRow(3);
        for (int i = 0; i < firstDeptContentArr.length; i ++) {
            Cell firstDeptContentCol = firstDeptRow2.createCell(i+1);
            firstDeptContentCol.setCellStyle(contentCellStyle);
            firstDeptContentCol.setCellValue(dataMap.get(firstDeptContentArr[i]) == null ? "" : dataMap.get(firstDeptContentArr[i]).toString());
        }

        // 第二部分-家庭成员信息
        Row secondDeptRow1 = sheet.createRow(4);
        Cell familyInfoCol = secondDeptRow1.createCell(0);
        familyInfoCol.setCellStyle(colTitleCellStyle);
        familyInfoCol.setCellValue("家庭成员信息");

        Cell secondDeptTitleCol1 = secondDeptRow1.createCell(1);
        secondDeptTitleCol1.setCellStyle(colTitleCellStyle);
        secondDeptTitleCol1.setCellValue("姓名");

        Cell secondDeptTitleCol2 = secondDeptRow1.createCell(3);
        secondDeptTitleCol2.setCellStyle(colTitleCellStyle);
        secondDeptTitleCol2.setCellValue("关系");

        Cell secondDeptTitleCol3 = secondDeptRow1.createCell(6);
        secondDeptTitleCol3.setCellStyle(colTitleCellStyle);
        secondDeptTitleCol3.setCellValue("身份证号");

        // 导入家庭成员
        List<Map<String, Object>> memberList = (List<Map<String, Object>>) dataMap.get("memberList");
        if (memberList != null && memberList.size() != 0) {
            for (int i = 5; i < 5 + memberList.size(); i ++) {
                Row secondDeptRowX = sheet.createRow(i);
                for (Map<String, Object> memberMap : memberList) {
                    Cell secondDeptTitleColX1 = secondDeptRowX.createCell(1);
                    secondDeptTitleColX1.setCellStyle(contentCellStyle);
                    secondDeptTitleColX1.setCellValue(memberMap.get("name") == null ? "" : memberMap.get("name").toString());

                    Cell secondDeptTitleColX2 = secondDeptRowX.createCell(3);
                    secondDeptTitleColX2.setCellStyle(contentCellStyle);
                    secondDeptTitleColX2.setCellValue(memberMap.get("relationship") == null ? "" : memberMap.get("relationship").toString());

                    Cell secondDeptTitleColX3 = secondDeptRowX.createCell(6);
                    secondDeptTitleColX3.setCellStyle(contentCellStyle);
                    secondDeptTitleColX3.setCellValue(memberMap.get("idNumber") == null ? "" : memberMap.get("idNumber").toString());
                }
            }
        }

        // 第三部分-家庭资产情况
        Row thirdDeptRow1 = sheet.createRow(11);
        Cell familyPropertyCol = thirdDeptRow1.createCell(0);
        familyPropertyCol.setCellStyle(colTitleCellStyle);
        familyPropertyCol.setCellValue("家庭资产情况");

        Cell thirdDeptTitleCol1 = thirdDeptRow1.createCell(1);
        thirdDeptTitleCol1.setCellStyle(colTitleCellStyle);
        thirdDeptTitleCol1.setCellValue("是否有商品房");

        Cell thirdDeptTitleCol2 = thirdDeptRow1.createCell(3);
        thirdDeptTitleCol2.setCellStyle(colTitleCellStyle);
        thirdDeptTitleCol2.setCellValue("是否有车");

        Cell thirdDeptTitleCol3 = thirdDeptRow1.createCell(5);
        thirdDeptTitleCol3.setCellStyle(colTitleCellStyle);
        thirdDeptTitleCol3.setCellValue("是否有存款");

        Cell thirdDeptTitleCol4 = thirdDeptRow1.createCell(7);
        thirdDeptTitleCol4.setCellStyle(colTitleCellStyle);
        thirdDeptTitleCol4.setCellValue("其他资产情况");

        // 导入是否有车/是否有房/是否有存款
        Row thirdDeptRow2 = sheet.createRow(12);

        Cell thirdDeptContentCol1 = thirdDeptRow2.createCell(1);
        thirdDeptContentCol1.setCellStyle(contentCellStyle);
        thirdDeptContentCol1.setCellValue(dataMap.get("haveHouse") == null ? "" : dataMap.get("haveHouse").toString());

        Cell thirdDeptContentCol2 = thirdDeptRow2.createCell(3);
        thirdDeptContentCol2.setCellStyle(contentCellStyle);
        thirdDeptContentCol2.setCellValue(dataMap.get("haveCar") == null ? "" : dataMap.get("haveCar").toString());

        Cell thirdDeptContentCol3 = thirdDeptRow2.createCell(5);
        thirdDeptContentCol3.setCellStyle(contentCellStyle);
        thirdDeptContentCol3.setCellValue(dataMap.get("isHaveDeposit") == null ? "" : dataMap.get("isHaveDeposit").toString());


        // 第四部分-负债情况、家庭收入预测、信用状况、资金需求、被调查人意见
        // 负债情况
        Row forthDeptDebtRow1 = sheet.createRow(13);
        Cell debtCol = forthDeptDebtRow1.createCell(0);
        debtCol.setCellStyle(colTitleCellStyle);
        debtCol.setCellValue("负债情况");

        Cell debtTitleCol1 = forthDeptDebtRow1.createCell(1);
        debtTitleCol1.setCellStyle(colTitleCellStyle);
        debtTitleCol1.setCellValue("民间借贷");

        Cell debtTitleCol2 = forthDeptDebtRow1.createCell(4);
        debtTitleCol2.setCellStyle(colTitleCellStyle);
        debtTitleCol2.setCellValue("农商行（信用社）借款");

        Cell debtTitleCol3 = forthDeptDebtRow1.createCell(6);
        debtTitleCol3.setCellStyle(colTitleCellStyle);
        debtTitleCol3.setCellValue("其他金融机构借款");

        // 家庭收入预测
        Row forthDeptFamilyIncomeRow1 = sheet.createRow(15);
        Cell familyIncomeCol = forthDeptFamilyIncomeRow1.createCell(0);
        familyIncomeCol.setCellStyle(colTitleCellStyle);
        familyIncomeCol.setCellValue("家庭收入预测");

        Cell familyIncomeTitleCol1 = forthDeptFamilyIncomeRow1.createCell(1);
        familyIncomeTitleCol1.setCellStyle(colTitleCellStyle);
        familyIncomeTitleCol1.setCellValue("年收入");

        Cell familyIncomeTitleCol2 = forthDeptFamilyIncomeRow1.createCell(4);
        familyIncomeTitleCol2.setCellStyle(colTitleCellStyle);
        familyIncomeTitleCol2.setCellValue("年支出");

        Cell familyIncomeTitleCol3 = forthDeptFamilyIncomeRow1.createCell(6);
        familyIncomeTitleCol3.setCellStyle(colTitleCellStyle);
        familyIncomeTitleCol3.setCellValue("年纯收入");

        // 导入年收入/年支出
        Row forthDeptFamilyIncomeRow2 = sheet.createRow(16);
        Cell familyIncomeContentCol1 = forthDeptFamilyIncomeRow2.createCell(1);
        familyIncomeContentCol1.setCellStyle(contentCellStyle);
        familyIncomeContentCol1.setCellValue(dataMap.get("income") == null ? "" : dataMap.get("income").toString());

        Cell familyIncomeContentCol2 = forthDeptFamilyIncomeRow2.createCell(4);
        familyIncomeContentCol2.setCellStyle(contentCellStyle);
        familyIncomeContentCol2.setCellValue(dataMap.get("payout") == null ? "" : dataMap.get("payout").toString());

        // 信用状况、资金需求
        Row forthDeptCreditRow1 = sheet.createRow(17);
        Cell creditCol = forthDeptCreditRow1.createCell(0);
        creditCol.setCellStyle(colTitleCellStyle);
        creditCol.setCellValue("信用状况");

        Cell creditTitleCol1 = forthDeptCreditRow1.createCell(1);
        creditTitleCol1.setCellStyle(colTitleCellStyle);
        creditTitleCol1.setCellValue("有无逾期贷款");

        Cell creditTitleCol2 = forthDeptCreditRow1.createCell(4);
        creditTitleCol2.setCellStyle(colTitleCellStyle);
        creditTitleCol2.setCellValue("有无重大不良记录");

        Cell creditTitleCol3 = forthDeptCreditRow1.createCell(6);
        creditTitleCol3.setCellStyle(colTitleCellStyle);
        creditTitleCol3.setCellValue("有无反映不良信用状况");

        for (int i = 18; i <= 22; i ++) {
            Row forthDeptCreditRow2 = sheet.createRow(i);

            if (i == 21) {
                Cell needsCol = forthDeptCreditRow2.createCell(0);
                needsCol.setCellStyle(colTitleCellStyle);
                needsCol.setCellValue("资金需求情况");
            }

            String[] creditTitleRow2ColArr = {"无", "无", "无"};
            String[] creditTitleRow3ColArr = {"家庭及邻里关系", "有无涉黄、赌、毒等不良嗜好", "是否是失信人员"};
            String[] creditTitleRow4ColArr = {"和睦", "无", "否"};

            String[] needsTitleRow1ColArr = {"申请金额", "期限", "用途"};
            String[] needsTitleRow2ColArr = {"", "", "生产经营、生活消费"};


            String[] creditTitleRowColArr = i == 18 ? creditTitleRow2ColArr
                    : (i == 19 ? creditTitleRow3ColArr
                    : (i == 20 ? creditTitleRow4ColArr
                    : (i == 21 ? needsTitleRow1ColArr : needsTitleRow2ColArr)));

            Cell creditTitleRow2Col1 = forthDeptCreditRow2.createCell(1);
            creditTitleRow2Col1.setCellStyle(colTitleCellStyle);
            creditTitleRow2Col1.setCellValue(creditTitleRowColArr[0]);

            Cell creditTitleRow2Col2 = forthDeptCreditRow2.createCell(4);
            creditTitleRow2Col2.setCellStyle(colTitleCellStyle);
            creditTitleRow2Col2.setCellValue(creditTitleRowColArr[1]);

            Cell creditTitleRow2Col3 = forthDeptCreditRow2.createCell(6);
            creditTitleRow2Col3.setCellStyle(colTitleCellStyle);
            creditTitleRow2Col3.setCellValue(creditTitleRowColArr[2]);
        }

        // 最下面-被调查人意见
        Row opinionRow1 = sheet.createRow(23);
        Cell opinionCol = opinionRow1.createCell(0);
        opinionCol.setCellStyle(colTitleCellStyle);
        opinionCol.setCellValue("被调查人意见");

        Row opinionRow3 = sheet.createRow(25);
        Cell opinionMainCol = opinionRow3.createCell(4);
        opinionMainCol.setCellStyle(colTitleCellStyle);
        opinionMainCol.setCellValue("主调查人签字:");

        Cell opinionSubCol = opinionRow3.createCell(6);
        opinionSubCol.setCellStyle(colTitleCellStyle);
        opinionSubCol.setCellValue("次调查人签字:");

        Row opinionRow4 = sheet.createRow(26);
        Cell opinionDateCol = opinionRow4.createCell(6);
        opinionDateCol.setCellStyle(colTitleCellStyle);
        opinionDateCol.setCellValue("年    月    日");

        // 创建标题end========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 400);

        return workbook;
    }


    /**
     * 导出面签明细表
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void outputInterview2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputInterview2Workbook(sheetName, dataList);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 导出面签明细表
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataList
     * @return
     */
    public static Workbook outputInterview2Workbook(String sheetName, List<Map<String, Object>> dataList) {

        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 14*256);
        sheet.setColumnWidth(1, 16*256);
        sheet.setColumnWidth(2, 20*256);
        sheet.setColumnWidth(3, 20*256);
        sheet.setColumnWidth(4, 20*256);
        sheet.setColumnWidth(5, 50*256);
        sheet.setColumnWidth(6, 16*256);

        // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 6); //起始行 结束行 起始列 结束列
        // 第二行地址、日期
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 1);
        CellRangeAddress village = new CellRangeAddress(1, 1, 2, 3);
        CellRangeAddress date = new CellRangeAddress(1, 1, 4, 5);

        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
        sheet.addMergedRegion(village);
        sheet.addMergedRegion(date);

        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("面签客户明细表");

        // 创建地址、日期、单位
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        Cell villageCol = unitRow.createCell(2);
        Cell dateCol = unitRow.createCell(4);
        Cell unitCol = unitRow.createCell(6);
        townshipCol.setCellValue("乡（镇，街道）:");
        villageCol.setCellValue("行政村:");
        dateCol.setCellValue("日期:     年   月   日");
        unitCol.setCellStyle(colTitleCellStyle);
        unitCol.setCellValue("单位: 万元");

        // 列名
        Row colTitleRow = sheet.createRow(2);
        String[] colTitles = {"序号", "借款人姓名", "手机号码", "身份证号码", "年龄", "户籍地址", "评议授信金额"};
        for (int i = 0; i < colTitles.length; i ++) {
            Cell colTitleCol = colTitleRow.createCell(i);
            colTitleCol.setCellStyle(colTitleCellStyle);
            colTitleCol.setCellValue(colTitles[i]);
        }

        // 创建标题End===================================================================================

        // 导入数据Start=================================================================================
        // 列名
        for (int i = 0; i < dataList.size(); i ++) {
            Row colContentRow = sheet.createRow(i + 3);
            String[] colContentArr = {"no", "name", "phoneNumber", "idNumber", "age", "address", "creditAmount"};
            for (int j = 0; j < colContentArr.length; j ++) {
                Cell contentCol = colContentRow.createCell(j);
                contentCol.setCellStyle(contentCellStyle);
                contentCol.setCellValue(dataList.get(i).get(colContentArr[j]) == null ? "" : dataList.get(i).get(colContentArr[j]).toString());
            }

        }



        return workbook;
    }


    /**
     * 导出网格统计信息（一）
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void outputGridStatisticsOne2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputGridStatisticsOne2Workbook(sheetName, dataList);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 导出网格统计信息（一）
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataList
     * @return
     */
    public static Workbook outputGridStatisticsOne2Workbook(String sheetName, List<Map<String, Object>> dataList) {

        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 8*256);
        sheet.setColumnWidth(1, 8*256);
        sheet.setColumnWidth(2, 8*256);
        sheet.setColumnWidth(3, 8*256);
        sheet.setColumnWidth(4, 8*256);
        sheet.setColumnWidth(5, 8*256);
        sheet.setColumnWidth(6, 8*256);
        sheet.setColumnWidth(7, 8*256);
        sheet.setColumnWidth(8, 8*256);
        sheet.setColumnWidth(9, 8*256);
        sheet.setColumnWidth(10,8*256);
        sheet.setColumnWidth(11,8*256);
        sheet.setColumnWidth(12,8*256);
        sheet.setColumnWidth(13,8*256);
        sheet.setColumnWidth(14,8*256);
        sheet.setColumnWidth(15,8*256);
        sheet.setColumnWidth(16,8*256);
        sheet.setColumnWidth(17,8*256);
        sheet.setColumnWidth(18,8*256);
        sheet.setColumnWidth(19,8*256);


        // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行大标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 19); //起始行 结束行 起始列 结束列
        // sheet加载合并单元格
        sheet.addMergedRegion(title);

        // 第二行小标题
        // 第一块标题
        CellRangeAddress corpOrg = new CellRangeAddress(1, 3, 0, 0);
        CellRangeAddress branchBank = new CellRangeAddress(1, 3, 1, 1);
        CellRangeAddress manager = new CellRangeAddress(1, 3, 2, 2);
        CellRangeAddress gridName = new CellRangeAddress(1, 3, 3, 3);
        CellRangeAddress totalFamily = new CellRangeAddress(1, 3, 4, 4);
        CellRangeAddress totalPerson = new CellRangeAddress(1, 3, 5, 5);
        sheet.addMergedRegion(corpOrg);
        sheet.addMergedRegion(branchBank);
        sheet.addMergedRegion(manager);
        sheet.addMergedRegion(gridName);
        sheet.addMergedRegion(totalFamily);
        sheet.addMergedRegion(totalPerson);


        // 第二块标题-不需要评议
        CellRangeAddress outReview = new CellRangeAddress(1, 1, 6, 10);
        CellRangeAddress outReviewTotal = new CellRangeAddress(2, 3, 6, 6);
        CellRangeAddress outReviewIn = new CellRangeAddress(2, 2, 7, 10);
        sheet.addMergedRegion(outReview);
        sheet.addMergedRegion(outReviewTotal);
        sheet.addMergedRegion(outReviewIn);

        // 第三块标题-授信进度
        CellRangeAddress creditRate = new CellRangeAddress(1, 1, 11, 16);
        CellRangeAddress creditRateTotal = new CellRangeAddress(2, 3, 11, 11);
        CellRangeAddress creditRateYet = new CellRangeAddress(2, 3, 12, 12);
        CellRangeAddress creditRateNot = new CellRangeAddress(2, 3, 13, 13);
        CellRangeAddress creditRateIn = new CellRangeAddress(2, 2, 14, 16);
        sheet.addMergedRegion(creditRate);
        sheet.addMergedRegion(creditRateTotal);
        sheet.addMergedRegion(creditRateYet);
        sheet.addMergedRegion(creditRateNot);
        sheet.addMergedRegion(creditRateIn);

        // 第四块标题-用信情况
        CellRangeAddress creditUsing = new CellRangeAddress(1, 1, 17, 19);
        CellRangeAddress creditUsingYet = new CellRangeAddress(2, 3, 17, 17);
        CellRangeAddress creditUsingIn = new CellRangeAddress(2, 2, 18, 19);
        sheet.addMergedRegion(creditUsing);
        sheet.addMergedRegion(creditUsingYet);
        sheet.addMergedRegion(creditUsingIn);


        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信覆盖情况统计表(一)");

        // 小标题第一行
        Row titleRow1 = sheet.createRow(1);
        Cell corpOrgCol = titleRow1.createCell(0);
        Cell branchBankCol = titleRow1.createCell(1);
        Cell managerCol = titleRow1.createCell(2);
        Cell gridNameCol = titleRow1.createCell(3);
        Cell totalFamilyCol = titleRow1.createCell(4);
        Cell totalPersonCol = titleRow1.createCell(5);
        Cell outReviewCol = titleRow1.createCell(6);
        Cell creditRateCol = titleRow1.createCell(11);
        Cell creditUsingCol = titleRow1.createCell(17);

        corpOrgCol.setCellStyle(colTitleCellStyle);
        corpOrgCol.setCellValue("法人机构");

        branchBankCol.setCellStyle(colTitleCellStyle);
        branchBankCol.setCellValue("支行");

        managerCol.setCellStyle(colTitleCellStyle);
        managerCol.setCellValue("客户经理");

        gridNameCol.setCellStyle(colTitleCellStyle);
        gridNameCol.setCellValue("网格名称");

        totalFamilyCol.setCellStyle(colTitleCellStyle);
        totalFamilyCol.setCellValue("总户数");

        totalPersonCol.setCellStyle(colTitleCellStyle);
        totalPersonCol.setCellValue("总人数");

        outReviewCol.setCellStyle(colTitleCellStyle);
        outReviewCol.setCellValue("不需要评议");

        creditRateCol.setCellStyle(colTitleCellStyle);
        creditRateCol.setCellValue("授信进度");

        creditUsingCol.setCellStyle(colTitleCellStyle);
        creditUsingCol.setCellValue("用信情况");

        // 小标题-第二行
        Row titleRow2 = sheet.createRow(2);
        Cell outReviewTotalCol = titleRow2.createCell(6);
        Cell outReviewInCol = titleRow2.createCell(7);
        Cell creditRateTotalCol = titleRow2.createCell(11);
        Cell creditRateYetCol = titleRow2.createCell(12);
        Cell creditRateNotCol = titleRow2.createCell(13);
        Cell creditRateInCol = titleRow2.createCell(14);
        Cell creditUsingYetCol = titleRow2.createCell(17);
        Cell creditUsingInCol = titleRow2.createCell(18);

        outReviewTotalCol.setCellStyle(colTitleCellStyle);
        outReviewTotalCol.setCellValue("汇总");

        outReviewInCol.setCellStyle(colTitleCellStyle);
        outReviewInCol.setCellValue("其中");

        creditRateTotalCol.setCellStyle(colTitleCellStyle);
        creditRateTotalCol.setCellValue("汇总");

        creditRateYetCol.setCellStyle(colTitleCellStyle);
        creditRateYetCol.setCellValue("已有效授信");

        creditRateNotCol.setCellStyle(colTitleCellStyle);
        creditRateNotCol.setCellValue("未有效授信");

        creditRateInCol.setCellStyle(colTitleCellStyle);
        creditRateInCol.setCellValue("其中");

        creditUsingYetCol.setCellStyle(colTitleCellStyle);
        creditUsingYetCol.setCellValue("已有效授信");

        creditUsingInCol.setCellStyle(colTitleCellStyle);
        creditUsingInCol.setCellValue("其中");

        // 小标题-第三行
        Row titleRow3 = sheet.createRow(3);
        Cell blackCol = titleRow3.createCell(7);
        Cell povertyCol = titleRow3.createCell(8);
        Cell blueCol = titleRow3.createCell(9);
        Cell greyCol = titleRow3.createCell(10);
        Cell reviewInCol = titleRow3.createCell(14);
        Cell interviewInCol = titleRow3.createCell(15);
        Cell toCreditCol = titleRow3.createCell(16);
        Cell creditUsingPersonCol = titleRow3.createCell(18);
        Cell creditUsingAmountCol = titleRow3.createCell(19);

        blackCol.setCellStyle(colTitleCellStyle);
        blackCol.setCellValue("黑名单");

        povertyCol.setCellStyle(colTitleCellStyle);
        povertyCol.setCellValue("贫困户");

        blueCol.setCellStyle(colTitleCellStyle);
        blueCol.setCellValue("蓝名单");

        greyCol.setCellStyle(colTitleCellStyle);
        greyCol.setCellValue("灰名单");

        reviewInCol.setCellStyle(colTitleCellStyle);
        reviewInCol.setCellValue("评议中");

        interviewInCol.setCellStyle(colTitleCellStyle);
        interviewInCol.setCellValue("面签中");

        toCreditCol.setCellStyle(colTitleCellStyle);
        toCreditCol.setCellValue("待发放");

        creditUsingPersonCol.setCellStyle(colTitleCellStyle);
        creditUsingPersonCol.setCellValue("用信户数");

        creditUsingAmountCol.setCellStyle(colTitleCellStyle);
        creditUsingAmountCol.setCellValue("用信额");

        String[] contentArr = {"frOrgName", "orgName", "userName", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q"};
        for (int i = 0; i < dataList.size(); i ++) {
            Row rowX = sheet.createRow(i + 4);
            for (int j = 0; j < contentArr.length; j ++) {
                Cell xCol = rowX.createCell(j);
                xCol.setCellStyle(contentCellStyle);
                xCol.setCellValue(dataList.get(i).get(contentArr[j]) == null ? "" : dataList.get(i).get(contentArr[j]).toString());
            }
        }

        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 400);

        return workbook;
    }


    /**
     * 导出网格统计信息（二）
     * @param excelName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public static void outputGridStatisticsTwo2Excel(String excelName, String sheetName, List<Map<String, Object>> dataList, HttpServletResponse response) throws UnsupportedEncodingException {
        Workbook workbook = outputGridStatisticsTwo2Workbook(sheetName, dataList);
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"), "iso8859-1"));
        response.setCharacterEncoding("UTF-8");
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.info("导出失败");
        }

    }

    /**
     * 导出网格统计信息（二）
     * HSSFWorkbook-xls
     * XSSFWorkbook-xlsx
     * memberList:家庭成员列表
     * @param sheetName
     * @param dataList
     * @return
     */
    public static Workbook outputGridStatisticsTwo2Workbook(String sheetName, List<Map<String, Object>> dataList) {

        Workbook workbook = new HSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);

        // 设置宽度
        sheet.setColumnWidth(0, 12*256);
        sheet.setColumnWidth(1, 12*256);
        sheet.setColumnWidth(2, 12*256);
        sheet.setColumnWidth(3, 12*256);
        sheet.setColumnWidth(4, 12*256);
        sheet.setColumnWidth(5, 12*256);
        sheet.setColumnWidth(6, 12*256);
        sheet.setColumnWidth(7, 12*256);
        sheet.setColumnWidth(8, 12*256);
        sheet.setColumnWidth(9, 12*256);
        sheet.setColumnWidth(10,12*256);
        sheet.setColumnWidth(11,12*256);
        sheet.setColumnWidth(12,12*256);



        // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = createCellStyle(workbook,true, true, false);
        // 列标题
        CellStyle colTitleCellStyle = createCellStyle(workbook,true, true, true);
        // 内容
        CellStyle contentCellStyle = createCellStyle(workbook, true, false, false);
        // 设置换行
        CellStyle wrapCellStyle = createCellStyle(workbook, true, false, true);

        // 创建合并单元格
        // 第一行大标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 12); //起始行 结束行 起始列 结束列
        // sheet加载合并单元格
        sheet.addMergedRegion(title);

        // 第二行小标题
        // 第一块标题
        CellRangeAddress corpOrg = new CellRangeAddress(1, 2, 0, 0);
        CellRangeAddress branchBank = new CellRangeAddress(1, 2, 1, 1);
        CellRangeAddress manager = new CellRangeAddress(1, 2, 2, 2);
        CellRangeAddress gridName = new CellRangeAddress(1, 2, 3, 3);

        sheet.addMergedRegion(corpOrg);
        sheet.addMergedRegion(branchBank);
        sheet.addMergedRegion(manager);
        sheet.addMergedRegion(gridName);


        // 第二块标题-授信覆盖情况
        CellRangeAddress credit = new CellRangeAddress(1, 1, 4, 6);
        CellRangeAddress creditWhole = new CellRangeAddress(1, 1, 7, 9);
        CellRangeAddress rate = new CellRangeAddress(1, 1, 10, 12);
        sheet.addMergedRegion(credit);
        sheet.addMergedRegion(creditWhole);
        sheet.addMergedRegion(rate);


        // 设置格式=====================================================================================

        // 创建标题===================================================================================
        Row titleRow = sheet.createRow(0);
        Cell titleCol = titleRow.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信覆盖情况统计表(二)");

        // 小标题第一行
        Row titleRow1 = sheet.createRow(1);
        Cell corpOrgCol = titleRow1.createCell(0);
        Cell branchBankCol = titleRow1.createCell(1);
        Cell managerCol = titleRow1.createCell(2);
        Cell gridNameCol = titleRow1.createCell(3);
        Cell creditCol = titleRow1.createCell(4);
        Cell creditWholeCol = titleRow1.createCell(7);
        Cell rateCol = titleRow1.createCell(10);

        corpOrgCol.setCellStyle(colTitleCellStyle);
        corpOrgCol.setCellValue("法人机构");

        branchBankCol.setCellStyle(colTitleCellStyle);
        branchBankCol.setCellValue("支行");

        managerCol.setCellStyle(colTitleCellStyle);
        managerCol.setCellValue("客户经理");

        gridNameCol.setCellStyle(colTitleCellStyle);
        gridNameCol.setCellValue("网格名称");

        creditCol.setCellStyle(colTitleCellStyle);
        creditCol.setCellValue("授信覆盖情况");

        creditWholeCol.setCellStyle(colTitleCellStyle);
        creditWholeCol.setCellValue("整村授信覆盖情况");

        rateCol.setCellStyle(colTitleCellStyle);
        rateCol.setCellValue("各环节进度情况");

        // 小标题-第二行
        Row titleRow2 = sheet.createRow(2);
        Cell outReviewTotalCol = titleRow2.createCell(4);
        Cell outReviewInCol = titleRow2.createCell(5);
        Cell creditRateTotalCol = titleRow2.createCell(6);
        Cell creditRateYetCol = titleRow2.createCell(7);
        Cell creditRateNotCol = titleRow2.createCell(8);
        Cell creditRateInCol = titleRow2.createCell(9);
        Cell creditUsingYetCol = titleRow2.createCell(10);
        Cell creditUsingInCol = titleRow2.createCell(11);
        Cell creditUsingInCol1 = titleRow2.createCell(12);

        outReviewTotalCol.setCellStyle(colTitleCellStyle);
        outReviewTotalCol.setCellValue("授信覆盖率");

        outReviewInCol.setCellStyle(colTitleCellStyle);
        outReviewInCol.setCellValue("预授信覆盖率");

        creditRateTotalCol.setCellStyle(colTitleCellStyle);
        creditRateTotalCol.setCellValue("用信率");

        creditRateYetCol.setCellStyle(colTitleCellStyle);
        creditRateYetCol.setCellValue("增信覆盖率");

        creditRateNotCol.setCellStyle(colTitleCellStyle);
        creditRateNotCol.setCellValue("预授信覆盖率");

        creditRateInCol.setCellStyle(colTitleCellStyle);
        creditRateInCol.setCellValue("用信率");

        creditUsingYetCol.setCellStyle(colTitleCellStyle);
        creditUsingYetCol.setCellValue("评议率");

        creditUsingInCol.setCellStyle(colTitleCellStyle);
        creditUsingInCol.setCellValue("面签率");

        creditUsingInCol1.setCellStyle(colTitleCellStyle);
        creditUsingInCol1.setCellValue("发放率");

        // 数据导入到Excel
        String[] contentArr = {"frOrgName", "orgName", "userName", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        for (int i = 0; i < dataList.size(); i ++) {
            Row rowX = sheet.createRow(i + 3);
            for (int j = 0; j < contentArr.length; j ++) {
                Cell xCol = rowX.createCell(j);
                xCol.setCellStyle(contentCellStyle);
                xCol.setCellValue(dataList.get(i).get(contentArr[j]) == null ? "" : dataList.get(i).get(contentArr[j]).toString());
            }
        }

        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 400);

        return workbook;
    }


    /**
     * 设置字体大小，加粗， 是否水平居中
     * @param workbook excel
     * @param isAlignCenter 是否水平居中
     * @return 样式
     */
    public static CellStyle createCellStyle(Workbook workbook, boolean isAlignCenter, boolean isBold, boolean isWrap) {
        CellStyle cellStyle = workbook.createCellStyle();

        // 加粗
        Font font = workbook.createFont();
        font.setBold(isBold);
        cellStyle.setFont(font);

        // 是否水平居中
        if (isAlignCenter) {
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        // 垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 自动换行
        cellStyle.setWrapText(isWrap);

        return cellStyle;
    }


}
