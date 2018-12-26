package com.example.web.controller;

import com.example.common.util.ExcelUtil;
import com.example.common.util.PoiUtil;
import com.example.common.util.StringUtil;
import com.example.service.black.CustomerBlackDOService;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOService;
import com.example.service.poverty.CustomerPovertyDOService;
import com.example.service.resident.ResidentDOService;
import com.example.service.survey.SurveyDOService;
import com.example.service.white.CustomerWhiteDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/13
 */

@RestController
@RequestMapping("/file")
public class FileController {
    private final CustomerDOService customerDOService;
    private final ResidentDOService residentDOService;
    private final CustomerPovertyDOService customerPovertyDOService;
    private final CustomerBlackDOService customerBlackDOService;
    private final SurveyDOService surveyDOService;
    private final CustomerWhiteDOService customerWhiteDOService;
    @Autowired
    public FileController(CustomerDOService customerDOService, ResidentDOService residentDOService, CustomerPovertyDOService customerPovertyDOService, CustomerBlackDOService customerBlackDOService, SurveyDOService surveyDOService,CustomerWhiteDOService customerWhiteDOService) {
        this.customerDOService = customerDOService;
        this.residentDOService=residentDOService;
        this.customerPovertyDOService=customerPovertyDOService;
        this.customerBlackDOService=customerBlackDOService;
        this.surveyDOService = surveyDOService;
        this.customerWhiteDOService=customerWhiteDOService;
    }

    /**
     * 调查表导出
     * @param customerDO
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/survey", method = RequestMethod.GET)
    public void outputExcel(CustomerDO customerDO,HttpServletResponse response) throws Exception {
        String excelName = (StringUtil.isBlank(customerDO.getGridCode()) ? "全部" : customerDO.getGridCode()) + "网格" + "调查信息.xls";
        List<Map<String, Object>> mapList = customerDOService.listMapList(customerDO);
        ExcelUtil.output2Excel(excelName, "调查信息", mapList, response);
    }

    /**
     * 导入调查表信息
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/survey", method = RequestMethod.POST)
    public ResultBean inputExcel(MultipartFile file, @Validated ConclusionDO conclusionDO) throws Exception {
        return new ResultBean().success(surveyDOService.importExcelWithMessage(file, conclusionDO));

    }

    /**
     * 导入户籍信息
     * @throws Exception
     */
    @RequestMapping(value = "/resident", method = RequestMethod.POST)
    public  ResultBean importResident(MultipartFile file,@RequestParam Map<String,Object> paramMap) throws Exception {
        Map<String,Object> map=PoiUtil.uploadMultipartFile(file,"excel");
        if((boolean) map.get("flag")) {
            List<Map<String, Object>> residentList=PoiUtil.getExcelByColumnNumsAndRowNum(map.get("path").toString(),14,4,1);
            if(!paramMap.containsKey("orgCode")|| !paramMap.containsKey("orgName")|| !paramMap.containsKey("userId") ||!paramMap.containsKey("name")||!paramMap.containsKey("gridCode")||!paramMap.containsKey("gridName") ){
                return new ResultBean().fail("参数有误");
            }
            if(residentList==null||residentList.isEmpty()) {
                return new ResultBean().fail("Excel内容为空");
            }
            int success=residentDOService.importFromExcel(residentList,paramMap);
            /*List<Map<String, Object>> saveList=new ArrayList<>();
            for(int i=0;i<residentList.size();i++) {
            	
            	 saveList.add(residentList.get(i));
            	 if(saveList.size()>=300) {
            		 success=success+residentDOService.importFromExcel(saveList,paramMap);
            		 saveList.removeAll(saveList);
            	 }
            	 if(i==residentList.size()-1) {
            		 success=success+ residentDOService.importFromExcel(saveList,paramMap);
            	 }
            	 
            }*/
            Map<String,Object> returnMap=new HashMap<>();
            returnMap.put("success", success);
            return new ResultBean().success(returnMap);
        }

        return new ResultBean().fail("导入文件异常");
    }
    /**
     * 导入贫困户信息
     * @throws Exception
     */
    @RequestMapping(value = "/poverty", method = RequestMethod.POST)
    public  ResultBean importCustomerPoverty(MultipartFile file,@RequestParam Map<String,Object> paramMap) throws Exception {
        Map<String,Object> map=PoiUtil.uploadMultipartFile(file,"excel");
        if((boolean) map.get("flag")) {
            List<Map<String, Object>> residentList=PoiUtil.getExcelByColumnNumsAndRowNum(map.get("path").toString(),4,3,1);
            if(!paramMap.containsKey("orgCode")|| !paramMap.containsKey("orgName")|| !paramMap.containsKey("userId") ||!paramMap.containsKey("name")){
                return new ResultBean().fail("参数有误");
            }
            if(residentList==null||residentList.isEmpty()) {
                return new ResultBean().fail("Excel内容为空");
            }
            return new ResultBean().success(customerPovertyDOService.importFromExcel(residentList,paramMap));
        }

        return new ResultBean().fail("导入文件异常");
    }
    /**
     * 导入黑名单信息
     * @throws Exception
     */
    @RequestMapping(value = "/black", method = RequestMethod.POST)
    public  ResultBean importCustomerBlack(MultipartFile file,@RequestParam Map<String,Object> paramMap) throws Exception {
        Map<String,Object> map=PoiUtil.uploadMultipartFile(file,"excel");
        if((boolean) map.get("flag")) {
            List<Map<String, Object>> residentList=PoiUtil.getExcelByColumnNumsAndRowNum(map.get("path").toString(),4,4,1);
            if(!paramMap.containsKey("orgCode")|| !paramMap.containsKey("orgName")|| !paramMap.containsKey("userId") ||!paramMap.containsKey("name")){
                return new ResultBean().fail("参数有误");
            }
            if(residentList==null||residentList.isEmpty()) {
                return new ResultBean().fail("Excel内容为空");
            }
            return new ResultBean().success(customerBlackDOService.importFromExcel(residentList,paramMap));
        }

        return new ResultBean().fail("导入文件异常");
    }
    
    /**
     * 根据网格号下载整村授信批量授信审批表
     * @param gridCode
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/customer/white", method = RequestMethod.GET)
    public void downloadWhite( String gridCode, String gridName, HttpServletResponse response) throws Exception {
        if ("".equals(gridCode)||"".equals(gridName)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"code\":400,\"message\":\"缺少必要参数！\"}");
            return;
        }
        Map<String,Object> map=new HashMap<>();
        map.put("gridCode", gridCode);
        map.put("gridName", gridName);
        customerWhiteDOService.export2Excel(map, response);
    }

    /**
     * 根据网格号下载整村授信批量授信审批表
     * @param gridCode
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/customer/white/survey", method = RequestMethod.GET)
    public void downloadCustomInfoByGridCode( String gridCode, String gridName, HttpServletResponse response) throws Exception {
        if ("".equals(gridCode)||"".equals(gridName)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"code\":400,\"message\":\"缺少必要参数！\"}");
            return;
        }
        Map<String,Object> map=new HashMap<>();
        map.put("gridCode", gridCode);
        map.put("gridName", gridName);
        customerWhiteDOService.exportToExcel(map, response);
    }

    
    
}
