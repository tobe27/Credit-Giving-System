package com.example.service.white;

import com.example.common.exception.CommonException;
import com.example.common.util.ExcelUtil;
import com.example.service.approval.CustomerApprovalDO;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.interview.CustomerInterviewDO;
import com.example.service.interview.CustomerInterviewDOMapper;
import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOMapper;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import com.github.pagehelper.PageHelper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

/**
 * @author CREATED BY W.S.T on 2018/12/12
 */

@Service
public class CustomerWhiteDOServiceImpl implements CustomerWhiteDOService {
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final GridInfoDOMapper  gridInfoDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final CustomerInterviewDOMapper customerInterviewDOMapper;
    

	private final OrgDOMapper orgDOMapper;
	@Autowired
	public CustomerWhiteDOServiceImpl(CustomerWhiteDOMapper customerWhiteDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper , OrgDOMapper orgDOMapper,GridInfoDOMapper  gridInfoDOMapper,SurveyDOMapper surveyDOMapper,CustomerInterviewDOMapper customerInterviewDOMapper) {
		this.customerWhiteDOMapper = customerWhiteDOMapper;
		this.customerTagRelationDOMapper=customerTagRelationDOMapper;
		this.orgDOMapper=orgDOMapper;
		this.gridInfoDOMapper=gridInfoDOMapper;
		this.surveyDOMapper=surveyDOMapper;
		this.customerInterviewDOMapper=customerInterviewDOMapper;

	}
	private static Logger logger = LoggerFactory.getLogger(CustomerWhiteDOServiceImpl.class);
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean deleteByPrimaryKey(long id) throws Exception {
		//删除客户标签中的白名单
		CustomerWhiteDO record=customerWhiteDOMapper.selectByPrimaryKey(id);
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)5);
		customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
		try {
			return customerWhiteDOMapper.deleteByPrimaryKey(id)==1;
		} catch (Exception e) {
			logger.info("删除白名单标签异常:" + e.getMessage());
			throw new ServiceException("删除白名单标签异常");
		}
	}
	/**
	 * 新增
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean insertSelective(@Validated CustomerWhiteDO record) throws Exception {
		long now=System.currentTimeMillis();
		record.setCreatedAt(now);
		record.setUpdatedAt(now);
		// 校验客户的家庭标签是否有黑白灰蓝贫困户
		List<CustomerTagRelationDO> list = customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(record.getHouseholdId());
		String tag_id="";
		List<String> idNumberList=new ArrayList<>();
		for(CustomerTagRelationDO tagDo:list) {
			idNumberList.add(tagDo.getIdNumber());
			tag_id=tagDo.getTagId()+"";
			switch(tag_id){

				case "1":
					throw new ServiceException("该客户家庭存在黑名单标签");

				case "2":
					throw new ServiceException("该客户家庭存在灰名单标签");

				case "3":
					throw new ServiceException("该客户家庭存在贫困户标签");

				case "4":
					throw new ServiceException("该客户家庭存在蓝名单标签");
			}
		}
	
		//删除系统中原有的家庭成员的白名单
		Map<String,Object> map=new HashMap<>();
		map.put("idNumberList", idNumberList);
		map.put("tagId", (long)5);
		//检查家庭成员是否在面谈面签记录中（排除已归档的，排除蓝名单）
		map.put("approvalStatus", "3");
		map.put("type", "1");
		if(idNumberList.size()>0) {
			List<CustomerInterviewDO> listCido=customerInterviewDOMapper.getListByIdNumbersAndApprovalStatus(map);
			if(listCido.size()>0) {
				throw new ServiceException("该客户家庭成员已有未归档状态的面谈面签记录");
			}
		}
	
		if(idNumberList.size()>0) {
			customerTagRelationDOMapper.deleteByIdNumbersAndTagId(map);
			customerWhiteDOMapper.deleteByIdNumbers(map);
		}
		
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)5);
		try {
			customerTagRelationDOMapper.insertSelective(tag);
		} catch (Exception e1) {
			logger.info("创建白名单标签异常:" + e1.getMessage());
			throw new ServiceException("创建白名单标签异常");
		}
		try {
			return customerWhiteDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			logger.info("创建白名单异常:" + e.getMessage());
			throw new ServiceException("创建白名单异常！");
		}
	}

	/**
	 * 查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	public CustomerWhiteDO selectByPrimaryKey(long id) throws Exception {
		try {
			return customerWhiteDOMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.info("查询白名单异常:" + e.getMessage());
			throw new ServiceException("查询白名单异常！");
		}

	}
	/**
	 * 修改
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateByPrimaryKeySelective(CustomerWhiteDO record) throws Exception {

		return false;
	}
	/**
	 * 分页
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<CustomerWhiteDO> getList(Map<String, Object> map) throws Exception {
		if(!map.containsKey("roleId") || !map.containsKey("userId")|| !map.containsKey("orgCode")||!map.containsKey("pageNum")||!map.containsKey("pageSize")) {
			throw new ServiceException("查询参数异常！");
		}

		//如果不是客户经理登录的
		if(Long.parseLong(map.get("roleId").toString())!=1) {
			map.remove("userId");
			String orgCode=map.get("orgCode").toString();
			if(orgCode.length()==9) {
			orgCode=orgCode.substring(0, 6);
			map.put("orgCode", orgCode);
			}
		}

		PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
		try {
			return customerWhiteDOMapper.getList(map);
		} catch (Exception e) {
			logger.info("查询白名单异常:" + e.getMessage());
			throw new ServiceException("查询白名单异常！");
		}
	}
	/**
	 * 递归出所有下级机构信息
	 *
	 * */
	public Set<OrgDO> findChildOrg(Set<OrgDO> set, String orgCode) {
		OrgDO org=orgDOMapper.getByorgCode(orgCode);
		List<OrgDO> orgList = new ArrayList<>();
		orgList.add(org);
		set.addAll(new ArrayList<>(orgList));
		List<OrgDO> childList = orgDOMapper.getByParentorgCode(orgCode);
		for (OrgDO orgDo : childList) {
			findChildOrg(set, orgDo.getOrgCode());
		}
		return set;
	}
	  /**
     * 导出
     * @param
     * @return
     * @throws Exception
     */
	@Override
	public List<CustomerWhiteDO> export2Excel(Map<String, Object> map,HttpServletResponse response) throws Exception {
		if(!map.containsKey("gridCode")||"".equals(map.get("gridCode").toString()) ||!map.containsKey("gridName")||"".equals(map.get("gridName").toString())) {
			throw new ServiceException("请选择要下载的网格");
		}
		List<CustomerWhiteDO> list=customerWhiteDOMapper.getListByGrdiCode(map);
		 String fileName = map.get("gridName").toString() +map.get("gridCode").toString()+ "网格的整村授信批量审批表.xlsx";
	     String sheetName = "客户信息";
	       if (list == null || list.size() == 0) {
	            response.setContentType("application/json;charset=UTF-8");
	            response.getWriter().print("{\"code\":204,\"message\":\"未检索到信息\"}");
	            return null;
	        }
	       Workbook workbook = output2Workbook(sheetName, list);
	        response.setContentType("application/octet-stream;charset=utf-8");
	        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
	        response.setCharacterEncoding("UTF-8");
	        response.flushBuffer();
	        workbook.write(response.getOutputStream());
		return null;
	}

	

	/**
     * 输出workbook
     * @param sheetName
     * @param listInfo
     * @return
     */
    public  Workbook output2Workbook(String sheetName, List<CustomerWhiteDO> listInfo) {
        String[] columnName = {"序号", "姓名", "身份证号码", "授信金额", "授信期限",
                "备注"};
        return output2Workbook(sheetName, parseObject2ListMap(listInfo), columnName);
    }
    
    /**
     * 将客户信息列表转成map
     * @param listInfo
     * @return
     */
    public  List<Map<String, String>> parseObject2ListMap(List<CustomerWhiteDO> listInfo) {
        List<Map<String, String>> dataList = new ArrayList<>();
        int i=1;
        for (CustomerWhiteDO info : listInfo) {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("序号",i+"");
            dataMap.put("姓名", info.getCustomerName());
            dataMap.put("身份证号码",info.getIdNumber());
            dataMap.put("授信金额", info.getLimit()+"");
            dataMap.put("授信期限","3年");
            dataMap.put("备注","");
            i++;
           dataList.add(dataMap);
        }
        return dataList;
    }
    /**
     * 创建Excel，把数据读入workbook
     * @param sheetName
     * @param dataList
     * @param columnName
     * @return
     */
    public  Workbook output2Workbook(String sheetName, List<Map<String, String>> dataList, String[] columnName) {
        // 总列数
        int columnNum = columnName.length;
        // 创建excel
        Workbook workbook = new XSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);
     // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = ExcelUtil.createCellStyle(workbook,true, true, false);
       
      
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 5); //起始行 结束行 起始列 结束列
     // 第二行地址、日期、单位
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 5);
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
      
        CellStyle cellStyle1 = workbook.createCellStyle();
        // 创建第一行
        Row rowTitle = sheet.createRow(0);
        Cell titleCol = rowTitle.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信批量审批表");
        
        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setAlignment(HorizontalAlignment.RIGHT);
        // 创建第二行
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        townshipCol.setCellStyle(cellStyle2);
        townshipCol.setCellValue("单位：万元");
        
       
        Row row = sheet.createRow(2);
        // 创建两种单元格格式
       
       
        // 创建两种字体
        Font font1 = workbook.createFont();
        Font font2 = workbook.createFont();

        // 设置第一种字体样式(用于列名)
        font1.setBold(true);
        font1.setFontHeightInPoints((short) 10);
        font1.setColor(IndexedColors.BLACK.getIndex());

        // 设置第二种字体样式（用于值）
        font2.setFontHeightInPoints((short) 10);
        font2.setColor(IndexedColors.BLACK.getIndex());

        // 设置列名
        for (int i = 0; i < columnNum; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columnName[i]);
            cell.setCellStyle(cellStyle1);
        }
        // 设置每行每列的值
        // 总行数
        int rowNum = dataList.size()+2;
        for (int j = 3; j <= rowNum; j++) {
            // 创建第一行
            Row row1 = sheet.createRow(j);
            // 将一行数据每一列的值注入进去
            for (int k = 0; k < columnNum; k++) {
                Cell cell = row1.createCell(k);
                cell.setCellValue(dataList.get(j-3).get(columnName[k])); // map的值
                cell.setCellStyle(cellStyle2);
            }
        }
        return workbook;
    }
    /**
     * 输出workbook
     * @param sheetName
     * @param listInfo
     * @return
     */
    public  Workbook outputToWorkbook(String sheetName, List<CustomerWhiteDO> listInfo,List<SurveyDO> list) {
        String[] columnName = {"序号", "姓名", "身份证号", "联系地址", "手机号码", "评议人姓名","收入","支出","授信额（0-10万）",
                "软信息"};
        return outputToWorkbook(sheetName, listInfo, columnName,list);
    }	
    
    /**
     * 导出
     * @param record
     * @return
     * @throws Exception
     */
	@Override
	public List<CustomerWhiteDO> exportToExcel(Map<String, Object> map,HttpServletResponse response) throws Exception {
		long now =System.currentTimeMillis();
		if(null==map.get("gridCode")||"".equals(map.get("gridCode").toString()) ||null==map.get("gridName")||"".equals(map.get("gridName").toString())) {
			throw new ServiceException("请选择要下载的网格");
		}
		List<CustomerWhiteDO> list=customerWhiteDOMapper.getListByGrdiCode(map);
		 String fileName = map.get("gridName").toString() +map.get("gridCode").toString()+ "网格的整村授信评议预授信表.xlsx";
	     String sheetName = "预授信信息";
	       if (list == null || list.size() == 0) {
	            response.setContentType("application/json;charset=UTF-8");
	            response.getWriter().print("{\"code\":204,\"message\":\"未检索到信息\"}");
	            return null;
	        }
	       if(list.isEmpty()) {
	    	   throw new ServiceException("未检索到相应的网格数据");
	       }
	       //遍历出所有的身份证号
	       List<String> idNumberList=new ArrayList<>();
	       for(CustomerWhiteDO wo:list) {
	    	   idNumberList.add(wo.getIdNumber());
	       }
	       //查询出相对想的调查信息
	       Map<String,Object> surevyMap=new HashMap<>();
	       surevyMap.put("idNumberList", idNumberList);
	       surevyMap.put("isValid", "是");
	       List<SurveyDO>   surevyList=surveyDOMapper.listByIdNumbersAndIsValid(surevyMap);
	       
	       
	       
	       Workbook workbook = outputToWorkbook(sheetName, list,surevyList);
	        response.setContentType("application/octet-stream;charset=utf-8");
	        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
	        response.setCharacterEncoding("UTF-8");
	        response.flushBuffer();
	        long now1 =System.currentTimeMillis();
	        System.out.println("==============="+(now1-now));
	        workbook.write(response.getOutputStream());
		return null;
	}
	/**
     * 创建Excel，把数据读入workbook
     * @param sheetName
     * @param dataList
     * @param columnName
     * @return
     */
    public  Workbook outputToWorkbook(String sheetName, List<CustomerWhiteDO> whiteList, String[] columnName,List<SurveyDO> SurveyList) {
    	 if (whiteList == null || whiteList.isEmpty()||SurveyList.isEmpty()) {
             throw new CommonException("数据为空！");
         }
    	// 创建excel,HSSFWorkbook生成xls，XSSFWorkbook-xlsx
         Workbook workbook = new HSSFWorkbook();
         // 创建第一页并命名
         Sheet sheet = workbook.createSheet(sheetName);

         // 设置样式=============================================================================
         // 主标题
         CellStyle titleCellStyle = ExcelUtil.createCellStyle(workbook,true, true,false);
         // 列标题
         CellStyle colTitleCellStyle =  ExcelUtil.createCellStyle(workbook,true, true, false);
         // 内容
         CellStyle contentCellStyle = ExcelUtil. createCellStyle(workbook, true, false, false);
         // 创建合并单元格
         // 第一行标题
         CellRangeAddress title = new CellRangeAddress(0, 0, 0, 11); //起始行 结束行 起始列 结束列
         // 第二行地址、日期、单位
         CellRangeAddress township = new CellRangeAddress(1, 1, 0,2 );
         CellRangeAddress village = new CellRangeAddress(1, 1, 3, 4);
         CellRangeAddress date = new CellRangeAddress(1, 1, 5, 6);
         CellRangeAddress unit = new CellRangeAddress(1, 1, 7, 11);
         
         //序号
         CellRangeAddress xuhao = new CellRangeAddress(2, 3, 0, 0);
         //姓名
         CellRangeAddress xingming = new CellRangeAddress(2, 3, 1, 1);
       //身份证号
         CellRangeAddress idNumber = new CellRangeAddress(2, 3, 2, 2);
         //联系地址
         CellRangeAddress address = new CellRangeAddress(2, 3, 3, 3);
         
         //手机号码
         CellRangeAddress phone = new CellRangeAddress(2, 3, 4, 4);
         // 户主
         CellRangeAddress houseOwner = new CellRangeAddress(2, 2, 5, 9);
         // 评议结果
         CellRangeAddress result = new CellRangeAddress(2, 3, 10, 10);
           // 备注
         CellRangeAddress comment = new CellRangeAddress(2, 3, 11, 11);

         
         sheet.addMergedRegion(title);
         sheet.addMergedRegion(township);
         sheet.addMergedRegion(village);
         sheet.addMergedRegion(date);
         sheet.addMergedRegion(unit);
         sheet.addMergedRegion(xuhao);
         sheet.addMergedRegion(xingming);
         sheet.addMergedRegion(idNumber);
         sheet.addMergedRegion(address);
         sheet.addMergedRegion(phone);
         sheet.addMergedRegion(houseOwner);
         sheet.addMergedRegion(result);
         sheet.addMergedRegion(comment);
         
      // 创建标题===================================================================================
         Row titleRow = sheet.createRow(0);
         Cell titleCol = titleRow.createCell(0);
         titleCol.setCellStyle(titleCellStyle);
         titleCol.setCellValue("整村授信评议调查表");
         
         Row unitRow = sheet.createRow(1);
         Cell townshipCol = unitRow.createCell(0);
         Cell villageCol = unitRow.createCell(3);
         Cell dateCol = unitRow.createCell(5);
         Cell unitCol = unitRow.createCell(7);
         townshipCol.setCellValue("乡（镇，街道）:");
         villageCol.setCellValue("行政村:");
         dateCol.setCellValue("日期:     年   月   日");
         unitCol.setCellStyle(colTitleCellStyle);
         unitCol.setCellValue("单位: 万元");
         
         // 列名
         Row colTitleRow = sheet.createRow(2);
         String[] colTitles = {"序号", "姓名", "身份证号", "联系地址", "手机号码", "评议信息"};
         for (int i = 0; i < colTitles.length; i ++) {
             Cell colTitleCol = colTitleRow.createCell(i);
             colTitleCol.setCellStyle(colTitleCellStyle);
             colTitleCol.setCellValue(colTitles[i]);
         }
         Cell cell10 = colTitleRow.createCell(10);
         cell10.setCellValue("评议结果");
         cell10.setCellStyle(colTitleCellStyle);
         Cell cell11 = colTitleRow.createCell(11);
         cell11.setCellValue("备注");
         cell11.setCellStyle(colTitleCellStyle);
         
         // 子列名
         Row sonTitleRow = sheet.createRow(3);
         Cell houseOwnerNameCol = sonTitleRow.createCell(5);
         Cell houseOwnerAgeCol = sonTitleRow.createCell(6);
         Cell relationshipCol = sonTitleRow.createCell(7);
         Cell memberNameCol = sonTitleRow.createCell(8);
         Cell memberAgeCol = sonTitleRow.createCell(9);
         houseOwnerNameCol.setCellStyle(colTitleCellStyle);
         houseOwnerNameCol.setCellValue("评议人姓名");

         houseOwnerAgeCol.setCellStyle(colTitleCellStyle);
         houseOwnerAgeCol.setCellValue("收入");

         relationshipCol.setCellStyle(colTitleCellStyle);
         relationshipCol.setCellValue("支出");

         memberNameCol.setCellStyle(colTitleCellStyle);
         memberNameCol.setCellValue("授信额（0-10万）");

         memberAgeCol.setCellStyle(colTitleCellStyle);
         memberAgeCol.setCellValue("软信息");
         
        //将数据转换成以合并单元格为主的信息
         Map<String,Object> dataMap=new HashMap<>();
         for(CustomerWhiteDO cwo:whiteList) {
        	 List<SurveyDO> SList=new ArrayList<>();
        	 for(SurveyDO sdo:SurveyList) {
        		 if(cwo.getIdNumber().equals(sdo.getIdNumber())) {
        			 SList.add(sdo);
        		 }
        	 }
        	 dataMap.put(cwo.getIdNumber(), SList);
         }
         System.out.println("====="+dataMap.toString());
         int start=4;
         int rowNum=4;
         for(int i=0;i<whiteList.size();i++) {
        	 List<SurveyDO> SList=(List<SurveyDO>) dataMap.get(whiteList.get(i).getIdNumber());
        	 int size=SList.size();
        	 //序号
             CellRangeAddress xh = new CellRangeAddress(start,start+size-1, 0, 0);
             //姓名
             CellRangeAddress xm = new CellRangeAddress(start,start+size-1, 1, 1);
           //身份证号
             CellRangeAddress idnum = new CellRangeAddress(start,start+size-1, 2, 2);
             //联系地址
             CellRangeAddress dz = new CellRangeAddress(start,start+size-1, 3, 3);
             
             //手机号码
             CellRangeAddress num = new CellRangeAddress(start,start+size-1, 4, 4);
             // 评议结果
             CellRangeAddress jiguo = new CellRangeAddress(start,start+size-1, 10, 10);
               // 备注
             CellRangeAddress beizhu = new CellRangeAddress(start,start+size-1, 11, 11);        	 
             start=start+size;
             sheet.addMergedRegion(xh); 
             sheet.addMergedRegion(xm); 
             sheet.addMergedRegion(idnum);
             sheet.addMergedRegion(dz); 
             sheet.addMergedRegion(num); 
             sheet.addMergedRegion(jiguo);
             sheet.addMergedRegion(beizhu);
        	 //==========
             Row row = sheet.createRow(rowNum);
             String[] titles = {i+1+"", whiteList.get(i).getCustomerName(), whiteList.get(i).getIdNumber(), whiteList.get(i).getAddress(), whiteList.get(i).getPhone()};
             for (int k = 0; k< titles.length; k ++) {
                 Cell colTitleCol = row.createCell(k);
                 colTitleCol.setCellStyle(colTitleCellStyle);
                 colTitleCol.setCellValue(titles[k]);
             }
             Cell ce10 = row.createCell(10);
             ce10.setCellValue(whiteList.get(i).getLimit()+"");
             ce10.setCellStyle(colTitleCellStyle);
             Cell ce11 = row.createCell(11);
             ce11.setCellValue(SList.get(0).getRemark());
             ce11.setCellStyle(colTitleCellStyle);
             Cell c55 = row.createCell(5);
             c55.setCellStyle(colTitleCellStyle);
             c55.setCellValue(SList.get(0).getSenator());
             Cell c66 = row.createCell(6);
             c66.setCellStyle(colTitleCellStyle);
             c66.setCellValue(SList.get(0).getIncome());
             Cell c77 = row.createCell(7);
             c77.setCellStyle(colTitleCellStyle);
             c77.setCellValue(SList.get(0).getPayout());
             Cell c88 = row.createCell(8);
             c88.setCellStyle(colTitleCellStyle);
             c88.setCellValue(SList.get(0).getCreditAmount());
             Cell c99 = row.createCell(9);
             c99.setCellStyle(colTitleCellStyle);
             c99.setCellValue("房屋估值："+SList.get(0).getHouseValue()+";车辆估值："+SList.get(0).getCarValue()+";主营项目"+SList.get(0).getMainBusiness()+";规模；"+SList.get(0).getScale()+";备注："+SList.get(0).getRemark());
             rowNum++;
             
             for(int m=1;m<SList.size();m++) {
            	// 子列名
                 Row zirow = sheet.createRow(rowNum);
                 Cell c5 = zirow.createCell(5);
                 Cell c6 = zirow.createCell(6);
                 Cell c7 = zirow.createCell(7);
                 Cell c8 = zirow.createCell(8);
                 Cell c9 = zirow.createCell(9);
                 c5.setCellStyle(colTitleCellStyle);
                 c5.setCellValue(SList.get(m).getSenator());

                 c6.setCellStyle(colTitleCellStyle);
                 c6.setCellValue(SList.get(m).getIncome());

                 c7.setCellStyle(colTitleCellStyle);
                 c7.setCellValue(SList.get(m).getPayout());

                 c8.setCellStyle(colTitleCellStyle);
                 c8.setCellValue(SList.get(m).getCreditAmount());

                 c9.setCellStyle(colTitleCellStyle);
                 c9.setCellValue("房屋估值："+SList.get(m).getHouseValue()+";车辆估值："+SList.get(m).getCarValue()+";主营项目"+SList.get(m).getMainBusiness()+";规模；"+SList.get(m).getScale()+";备注："+SList.get(m).getRemark());
                 rowNum++; 
            	  
             }
            
        	 
         }
         
         
         
         
    	/*// 总列数
        int columnNum = columnName.length;
        // 创建excel
        Workbook workbook = new XSSFWorkbook();
        // 创建第一页并命名
        Sheet sheet = workbook.createSheet(sheetName);
     // 设置样式=============================================================================
        // 主标题
        CellStyle titleCellStyle = ExcelUtil.createCellStyle(workbook,true, true);
       
      
        // 第一行标题
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 5); //起始行 结束行 起始列 结束列
     // 第二行地址、日期、单位
        CellRangeAddress township = new CellRangeAddress(1, 1, 0, 5);
        sheet.addMergedRegion(title);
        sheet.addMergedRegion(township);
      
        CellStyle cellStyle1 = workbook.createCellStyle();
        // 创建第一行
        Row rowTitle = sheet.createRow(0);
        Cell titleCol = rowTitle.createCell(0);
        titleCol.setCellStyle(titleCellStyle);
        titleCol.setCellValue("整村授信批量审批表");
        
        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setAlignment(HorizontalAlignment.RIGHT);
        // 创建第二行
        Row unitRow = sheet.createRow(1);
        Cell townshipCol = unitRow.createCell(0);
        townshipCol.setCellStyle(cellStyle2);
        townshipCol.setCellValue("单位：万元");
        
       
        Row row = sheet.createRow(2);
        // 创建两种单元格格式
       
       
        // 创建两种字体
        Font font1 = workbook.createFont();
        Font font2 = workbook.createFont();

        // 设置第一种字体样式(用于列名)
        font1.setBold(true);
        font1.setFontHeightInPoints((short) 10);
        font1.setColor(IndexedColors.BLACK.getIndex());

        // 设置第二种字体样式（用于值）
        font2.setFontHeightInPoints((short) 10);
        font2.setColor(IndexedColors.BLACK.getIndex());

        // 设置列名
        for (int i = 0; i < columnNum; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columnName[i]);
            cell.setCellStyle(cellStyle1);
        }
        // 设置每行每列的值
        // 总行数
        int rowNum = dataList.size()+2;
        for (int j = 3; j <= rowNum; j++) {
            // 创建第一行
            Row row1 = sheet.createRow(j);
            // 将一行数据每一列的值注入进去
            for (int k = 0; k < columnNum; k++) {
                Cell cell = row1.createCell(k);
                cell.setCellValue(dataList.get(j-3).get(columnName[k])); // map的值
                cell.setCellStyle(cellStyle2);
            }
        }*/
        return workbook;
    }


}
