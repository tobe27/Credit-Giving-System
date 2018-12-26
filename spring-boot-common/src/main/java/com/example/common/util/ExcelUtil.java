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
        // 第四、五行是标题
        // 序号
        CellRangeAddress no = new CellRangeAddress(3, 4, 0, 0);
        // 户主
        CellRangeAddress houseOwner = new CellRangeAddress(3, 3, 1, 2);

        // 户号
        CellRangeAddress householdId = new CellRangeAddress(3, 4, 3, 3);

        // 成员
        CellRangeAddress member = new CellRangeAddress(3, 3, 4, 6);

        // 身份证及其他
        CellRangeAddress idNumber = new CellRangeAddress(3, 4, 7, 7);
        CellRangeAddress phoneNumber = new CellRangeAddress(3, 4, 8, 8);
        CellRangeAddress address = new CellRangeAddress(3, 4, 9, 9);
        CellRangeAddress ensureAmount = new CellRangeAddress(3, 4, 10, 10);

        CellRangeAddress isFamiliar = new CellRangeAddress(3, 4, 11, 11);
        CellRangeAddress negativeReason = new CellRangeAddress(3, 4, 12, 12);

        // 下拉框信息
        String[] isFamiliarArr = {"是", "否"};
        String[] negativeReasonArr = {"1游手好闲", "2欠债较多", "3服刑", "4长期外出", "5信用观念差",
                "6赌博、吸毒放高利贷", "7光棍", "8患病、残疾", "9有前科", "10婚变",
                "11家庭不和睦", "12常年不回家（2年以上）", "13长期外出务工", "14年龄不符合", "15其他负面情况"};
        String[] haveOrNot = {"有", "无"};
        // 下拉框格式设置
        CellRangeAddressList isFamiliarList = new CellRangeAddressList(5, totalDataRow + 5, 11, 11);
        CellRangeAddressList negativeReasonList = new CellRangeAddressList(5, totalDataRow + 5, 12, 12);
        CellRangeAddressList houseValueList = new CellRangeAddressList(5, totalDataRow + 5, 15, 15);
        CellRangeAddressList carValueList = new CellRangeAddressList(5, totalDataRow + 5, 16, 16);

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


        CellRangeAddress remark = new CellRangeAddress(3, 4, 13, 13);
        CellRangeAddress borrower = new CellRangeAddress(3, 4, 14, 14);
        CellRangeAddress houseValue = new CellRangeAddress(3, 4, 15, 15);
        CellRangeAddress carValue = new CellRangeAddress(3, 4, 16, 16);
        CellRangeAddress mainBusiness = new CellRangeAddress(3, 4, 17, 17);
        CellRangeAddress scale = new CellRangeAddress(3, 4, 18, 18);
        CellRangeAddress income = new CellRangeAddress(3, 4, 19, 19);
        CellRangeAddress payout = new CellRangeAddress(3, 4, 20, 20);
        CellRangeAddress creditAmount = new CellRangeAddress(3, 4, 21, 21);
        CellRangeAddress validTime = new CellRangeAddress(3, 4, 22, 22);

        // 注意事项
        CellRangeAddress caution1 = new CellRangeAddress(totalDataRow + 5, totalDataRow + 5, 0, 22);
        CellRangeAddress caution2 = new CellRangeAddress(totalDataRow + 6, totalDataRow + 6, 0, 22);

        // sheet加载合并单元格
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
        sheet.addMergedRegion(village);
        sheet.addMergedRegion(date);
        sheet.addMergedRegion(unit);
        sheet.addMergedRegion(senator);
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
        sheet.addMergedRegion(caution1);
        sheet.addMergedRegion(caution2);
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

        // 列名
        Row colTitleRow = sheet.createRow(3);
        String[] colTitles = {"序号", "户主", "", "户号", "成员", "", "", "身份证号码", "手机号", "联系地址",
                "对外担保总额", "是否了解情况", "不符合授信原因", "备注", "借款主体", "有无商品房", "有无车辆",
                "主营项目", "规模", "收入", "支出", "授信额度（0-5万元）", "已完成有效调查次数"};
        for (int i = 0; i < colTitles.length; i ++) {
            Cell colTitleCol = colTitleRow.createCell(i);
            colTitleCol.setCellStyle(colTitleCellStyle);
            colTitleCol.setCellValue(colTitles[i]);
        }

        // 子列名
        Row sonTitleRow = sheet.createRow(4);
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

        // 注意事项
        Row cautionRow1 = sheet.createRow(totalDataRow + 5);
        Row cautionRow2 = sheet.createRow(totalDataRow + 6);
        Cell cautionCol1 = cautionRow1.createCell(0);
        Cell cautionCol2 = cautionRow2.createCell(0);
        cautionCol1.setCellValue("注(导入时删除此两行)：1.该表每位评议人填写一张，评议人之间互不见面、互不沟通和干扰。");
        cautionCol2.setCellValue("2.以下人员不予授信，其他请注明理由：游手好闲-1，欠债较多—2，服刑—3，长期外出—4，信用观念差—5，" +
                "赌博、吸毒、放高利贷—6，光棍—7，患病、残疾—8，有前科—9，婚变—10，" +
                "家庭不和睦—11；常年不回家（2年以上）—12；长期外出务工—13；年龄不符合—14；其他负面情况—15");

        // 创建标题========================================================================================

        // 设置默认行高列宽
        sheet.setDefaultRowHeight((short) 15);

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
