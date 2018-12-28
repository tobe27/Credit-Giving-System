package com.example.common.util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.exception.CommonException;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

	/**
	 * POI工具类
	 *
	 * **/
	public class PoiUtil {

	    private static Logger logger = LoggerFactory.getLogger(PoiUtil.class);

	    /**
	     *
	     * @param imagePath
	     *            图片文件地址
	     * @param pdfPath
	     *            PDF文件保存地址包包含文件名和后缀
	     *
	     */

	   /* public static boolean imageToPdf(String imagePath, String pdfPath) {
	        try {// 输出流
	            FileOutputStream fos;
	            fos = new FileOutputStream(pdfPath);
	            // 创建文档
	            Document doc = new Document(null, 0, 0, 0, 0);
	            // 写入PDF文档
	            PdfWriter.getInstance(doc, fos);
	            // 读取图片流
	            BufferedImage img = null;
	            // 实例化图片
	            Image image = null;
	            // 获取图片文件夹对象
	            File file = new File(imagePath);
	            img=ImageIO.read(file);
	            doc.setPageSize(new Rectangle(img.getWidth(), img
	                    .getHeight()));
	            // 实例化图片
	            image = Image.getInstance(imagePath);
	            // 添加图片到文档
	            doc.open();
	            doc.add(image);
	            doc.close();
	            return true;
	        } catch (Exception e) {
	            return false;
	        }

	    }*/


	    /**
	     * 检查文件是否是图片类型
	     * @param  file
	     * @return boolean
	     */
	   public static boolean isImage(MultipartFile file) {
	        String type=null;// 文件类型
	        String fileName=file.getOriginalFilename();// 文件原名称
	        // 判断文件类型
	        type= fileName.contains(".") ?fileName.substring(fileName.lastIndexOf(".")+1):null;
	        if (type!=null) {// 判断文件类型是否为空
	            return "JPEG".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase()) || "JPG".equals(type.toUpperCase());
	        }
	        return false;
	    }

	    /**
	     * 上传图片
	     * @param
	     * @return map
	     * @throws IOException
	     * @throws IllegalStateException
	     * @throws Exception
	     */
	   public static Map<String,Object> saveImage(MultipartFile file,String type,String fileName,String idNumber) throws Exception{
	        if(file==null) {
	            return null;
	        }
	        String pathType="";
	        switch (type) {
	            case "1":
	                pathType="basicInfo";
	                break;
	            case "1.1":
	                pathType="basicInfo";
	                break;
	            case "1.2":
	                pathType="basicInfo";
	                break;
	            case "2":
	                pathType="propertyInfo";
	                break;
	            case "3":
	                pathType="manageInfo";
	                break;
	            case "4":
	                pathType="loanInfo";
	                break;
	            /*case "4.1":
	                pathType="loanInfo";
	                break;
	            case "4.2":
	                pathType="loanInfo";
	                break;*/
	            case "5":
	                pathType="pledgeInfo";
	                break;
	            case "6":
	                pathType="otherInfo";
	                break;

	        }
	        String path="";
	        File tempFile = null;
	        String saveFile="";
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+"image/"+pathType+"/"+idNumber;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	            saveFile=path+"/"+fileName+file.getOriginalFilename();
	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+"image\\"+pathType+"\\"+idNumber;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	            saveFile=path+"\\"+fileName+file.getOriginalFilename();
	        }
	        File storeFile = new File(saveFile);
	        file.transferTo(storeFile);
	        Map<String,Object> map=new HashMap<>();
	        map.put("path", saveFile);
	        return map;
	    }

	   /**
	     * 上传图片
	     * @param
	     * @return map
	     * @throws IOException
	     * @throws IllegalStateException
	     * @throws Exception
	     */
	   public static Map<String,Object> saveFile(MultipartFile file,String type,String fileName,String idNumber) throws Exception{
	        if(file==null) {
	            return null;
	        }
	       
	        String path="";
	        File tempFile = null;
	        String saveFile="";
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+"image/"+type+"/"+idNumber;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	            saveFile=path+"/"+fileName+file.getOriginalFilename();
	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+"image\\"+type+"\\"+idNumber;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	            saveFile=path+"\\"+fileName+file.getOriginalFilename();
	        }
	        File storeFile = new File(saveFile);
	        file.transferTo(storeFile);
	        Map<String,Object> map=new HashMap<>();
	        map.put("path", saveFile);
	        return map;
	    }

	   /**
	     * 上传图片
	     * @param
	     * @return map
	     * @throws IOException
	     * @throws IllegalStateException
	     * @throws Exception
	     */
	   public static Map<String,Object> saveGridImage(MultipartFile file,String fileName,String gridCode) throws Exception{
	        if(file==null) {
	            return null;
	        }
	        
	      
	        String path="";
	        File tempFile = null;
	        String saveFile="";
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+"grid/image"+"/"+gridCode;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	            saveFile=path+"/"+fileName+file.getOriginalFilename();
	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+"grid\\image"+"\\"+gridCode;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	            saveFile=path+"\\"+fileName+file.getOriginalFilename();
	        }
	        File storeFile = new File(saveFile);
	        file.transferTo(storeFile);
	        Map<String,Object> map=new HashMap<>();
	        map.put("path", saveFile);
	        return map;
	    }
	    /**
	     * 将文件移至回收站
	     * @param
	     * @return map
	     * @throws Exception
	     */
	    public static Map<String,Object> moveFileToRecycle(String filePath,String fileType){

	        File file = new File(filePath);
	        String path="";
	        File tempFile;
	        String destinationFile ="";
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+"recycle/"+fileType;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	            destinationFile = tempFile+"/"+file.getName();
	            file.renameTo(new File(destinationFile));

	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+"recycle\\"+fileType;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	            destinationFile = tempFile+"\\"+file.getName();
	            file.renameTo(new File(destinationFile));
	        }

	        Map<String,Object> map=new HashMap<>();
	        map.put("path", destinationFile);

	        return map;

	    }

	    /**
	     * 上传文件
	     * @param request
	     * @return map
	     * @throws Exception
	     */
	    public static Map<String,Object> uploadFile(HttpServletRequest request,String pathType) throws Exception {
	        String path="";
	        Map<String,Object> map=new HashMap<>();
	        boolean flag=false;
	        if(request==null ) {
	            map.put("flag", flag);
	            map.put("msg", "参数有误");
	            return map;
	        }
	        Date date = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        String dateNowStr = sdf.format(date);
	        File tempFile = null;
	        //如果是linux系统
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+pathType+"/"+dateNowStr;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+pathType+"\\"+dateNowStr;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	        }
	        String fileName="";//上传后的文件名
	        try{
	            DiskFileItemFactory factory = new DiskFileItemFactory();
	            factory.setRepository(tempFile);
	            ServletFileUpload upload = new ServletFileUpload(factory);
	            upload.setHeaderEncoding("utf-8");
	            List<FileItem> list = upload.parseRequest(request);
	            for(int i=0;i<list.size();i++){
	                FileItem item=list.get(i);
	                if(!item.isFormField()){
	                    if(item.getName().length() > 0){
	                        fileName = item.getName();
	                        int num = (int) (Math.random() * 9000 + 1000);
	                        fileName=num+"-"+fileName;
	                        tempFile=new File(path+File.separator+fileName);
	                        item.write(tempFile);
	                        map.put("path", path);
	                        map.put("fileName", fileName);
	                        map.put("flag", true);
	                    }
	                }else {
	                    map.put(item.getFieldName(), item.getString());
	                }
	            }

	        }catch(Exception e){
	            logger.info("上传附件异常："+e.getMessage());
	            throw new CommonException("上传附件异常");
	        }
	        return map;
	    }

	    /**
	     * 上传文件
	     * @param request
	     * @return map
	     * @throws Exception
	     */
	    public static Map<String,Object> uploadMultipartFile(MultipartFile file,String pathType) throws Exception {
	        String path="";
	        Map<String,Object> map=new HashMap<>();
	        boolean flag=false;
	        if(file==null ) {
	            map.put("flag", flag);
	            map.put("msg", "文件有误");
	            return map;
	        }
	        Date date = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        String dateNowStr = sdf.format(date);
	        File tempFile = null;
	        String saveFile="";
	        //如果是linux系统
	        if(isOSLinux()) {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForLinux")+pathType+"/"+dateNowStr;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.setWritable(true, false);    //设置写权限，windows下不用此语句
	                tempFile.mkdirs();
	            }
	            saveFile=path+"/"+file.getOriginalFilename();
	        }else {
	            path=PropertiesUtil.getPropertyForString("dataSource.upLoadPathForWindows")+pathType+"\\"+dateNowStr;
	            tempFile =new File(path);
	            if(!tempFile.exists()) {
	                tempFile.mkdirs();
	            }
	            saveFile=path+"\\"+System.currentTimeMillis()+file.getOriginalFilename();
	        }
	        File storeFile = new File(saveFile);
	        String fileName=path+file.getOriginalFilename();//上传后的文件名
	        file.transferTo(storeFile);
	        map.put("path", saveFile);
	        map.put("flag", true);
	        return map;
	    }

	    /**
	     * 解析excel获取客户信息
	     * @param path
	     * @param status
	     * @param gridCode
	     * @param gridName
	     * @param registrant
	     * @param registerOrg
	     * @return
	     */
	    /*public static List<CustomerInfo> getCustomerInfoByExcel(String path, Integer status, String gridCode,
	                                                            String gridName, String registrant, String registerOrg) {
	        List<CustomerInfo> customerInfoList = new ArrayList<>();
	        try {
	            Workbook workbook = ExcelUtil.getWorkbook(path);
	            Sheet sheet = workbook.getSheetAt(0);

	            // 跳过第一行目录
	            int count = 0;
	            for (Row row : sheet) {
	                // 跳过第一行和第二行目录
	                if (count <= 1) {
	                    count++;
	                    continue;
	                }

	                // 解析sheet里面的每行数据
	                // 如果为空直接返回空结果
	                if(row.getCell(0)==null || "".equals(row.getCell(0).toString())){
	                    return customerInfoList;
	                }

	                CustomerInfo info = new CustomerInfo();


	                // 导入客户的状态
	                info.setStatus(status);

	                // 证件类型
	                info.setIdType("中国居民身份证");

	                // 导入所在网格
	                info.setGridCode(gridCode);   // 网格号
	                info.setGridName(gridName);     //网格名称
	                info.setRegistrant(registrant);  // 登记人
	                info.setRegisterOrg(registerOrg);  // 登记机构
	                info.setInvestigator1(registrant);  // 调查人A
	                // 导入时间
	                long now = System.currentTimeMillis();
	                info.setCreatedAt(now);     // 登记日期
	                info.setUpdatedAt(now);     // 变更日期

	                info.setCreditId(ExcelUtil.getValue(row.getCell(0)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(0)).toString());    // 信贷编号
	                info.setCustomerName(ExcelUtil.getValue(row.getCell(1)) == null
	                        ? null :  ExcelUtil.getValue(row.getCell(1)).toString());   // 姓名
	                info.setIdNumber(ExcelUtil.getValue(row.getCell(2)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(2)).toString());      // 身份证号
	                info.setSex(ExcelUtil.getValue(row.getCell(3)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(3)).toString());   // 性别
	                info.setBirthday(ExcelUtil.getValue(row.getCell(4)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(4)).toString());   // 出生日期
	                info.setPhoneNumber(ExcelUtil.getValue(row.getCell(5)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(5)).toString());   // 手机号码
	                info.setPhysicalCondition(ExcelUtil.getValue(row.getCell(6)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(6)).toString());    // 身体状况
	                info.setEducationLevel(ExcelUtil.getValue(row.getCell(7)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(7)).toString());   // 最高学历
	                info.setNationality(ExcelUtil.getValue(row.getCell(8)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(8)).toString());   // 国籍
	                info.setNation(ExcelUtil.getValue(row.getCell(9)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(9)).toString());   // 民族
	                info.setPoliticsStatus(ExcelUtil.getValue(row.getCell(10)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(10)).toString());   // 政治面貌
	                info.setMaritalStatus(ExcelUtil.getValue(row.getCell(11)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(11)).toString());   // 婚姻状况

	                info.setCareer(ExcelUtil.getValue(row.getCell(12)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(12)).toString());   // 职业
	                info.setWorkYear(ExcelUtil.getValue(row.getCell(13)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(13)).toString());   // 工作年限
	                info.setCompanyName(ExcelUtil.getValue(row.getCell(14)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(14)).toString());   // 单位名称
	                info.setCompanyAddress(ExcelUtil.getValue(row.getCell(15)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(15)).toString());   // 单位地址

	                info.setIsFarmer(ExcelUtil.getValue(row.getCell(16)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(16)).toString());   // 是否农户
	                info.setIsBuyHouse(ExcelUtil.getValue(row.getCell(17)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(17)).toString());   // 有无县城购房
	                info.setCellName(ExcelUtil.getValue(row.getCell(18)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(18)).toString());   // 小区名称
	                info.setCellAddress(ExcelUtil.getValue(row.getCell(19)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(19)).toString());   // 小区地址

	                info.setHouseholdId(ExcelUtil.getValue(row.getCell(20)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(20)).toString());   // 户号
	                info.setLivingSituation(ExcelUtil.getValue(row.getCell(21)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(21)).toString());   // 居住状况
	                info.setNativeAddress(ExcelUtil.getValue(row.getCell(22)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(22)).toString());   // 户籍地址
	                info.setResidenceAddress(ExcelUtil.getValue(row.getCell(23)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(23)).toString());   // 居住地址
	                info.setPostcode(ExcelUtil.getValue(row.getCell(24)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(24)).toString());   // 居住地邮编
	                info.setIsOwner(ExcelUtil.getValue(row.getCell(25)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(25)).toString());   // 是否企业主
	                info.setIsStaff(ExcelUtil.getValue(row.getCell(26)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(26)).toString());   // 是否本行员工
	                info.setIsStockholder(ExcelUtil.getValue(row.getCell(27)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(27)).toString());   // 是否本行股东
	                info.setInvestigator2(ExcelUtil.getValue(row.getCell(28)) == null
	                        ? null : ExcelUtil.getValue(row.getCell(28)).toString());     // 调查人(B角)


	                customerInfoList.add(info);
	            }

	        } catch (Exception e) {
	            logger.info("Excel解析客户异常："+e);
	            throw new MyException("解析客户信息Excel错误:"+e);
	        }
	        return customerInfoList;
	    }*/


	    /**
	     * 解析excel获取户籍信息
	     * @param path
	     * @return map
	     * @throws Exception
	     */
	   /* public static List<ResidentInfo> getExcel(String path, String gridCode) {
	        List<ResidentInfo> result=new ArrayList<>();
	        try {
	            Workbook workbook = ExcelUtil.getWorkbook(path);
	            Sheet sheet = workbook.getSheetAt(0);
	            // 为跳过第一行目录设置count  
	            int count = 0;
	            // DecimalFormat decimalFormat = new DecimalFormat("##0");
	            for (Row row : sheet) {
	                // 跳过第一和第二行的目录
	                if(count < 1 ) {
	                    count++;
	                    continue;
	                }
	                if(row.getCell(0)==null||"".equals(row.getCell(0).toString())){
	                    return result;
	                }
	                ResidentInfo residentInfo=new ResidentInfo();
	                residentInfo.setResidentName(ExcelUtil.getValue(row.getCell(0)).toString());
	                residentInfo.setSex(ExcelUtil.getValue(row.getCell(1)).toString());
	                residentInfo.setNation(ExcelUtil.getValue(row.getCell(2)).toString());
	                residentInfo.setBirthdate(ExcelUtil.getValue(row.getCell(3)).toString());
	                residentInfo.setIdNumber(ExcelUtil.getValue(row.getCell(4)).toString());
	                if(ExcelUtil.getValue(row.getCell(5)) !=null) {
	                    residentInfo.setAddress(ExcelUtil.getValue(row.getCell(5)).toString());
	                }
	                if(ExcelUtil.getValue(row.getCell(6))!=null) {
	                    residentInfo.setHouseholdType(ExcelUtil.getValue(row.getCell(6)).toString());
	                }
	                if(ExcelUtil.getValue(row.getCell(7))!=null) {
	                    residentInfo.setRelationship(ExcelUtil.getValue(row.getCell(7)).toString());
	                }
	                residentInfo.setHouseholdId(ExcelUtil.getValue(row.getCell(8)).toString());

	                if(ExcelUtil.getValue(row.getCell(9))!=null) {
	                    Cell cellCode = row.getCell(9);
	                    cellCode.setCellType(CellType.STRING);
	                    residentInfo.setContact(ExcelUtil.getValue(cellCode).toString());
	                }
	                if(ExcelUtil.getValue(row.getCell(10))!=null) {
	                	if("否".equals(ExcelUtil.getValue(row.getCell(10)).toString())){
	                		continue;
	                	}
	                    residentInfo.setIsInList(ExcelUtil.getValue(row.getCell(10)).toString());
	                }
	                if(ExcelUtil.getValue(row.getCell(11))!=null) {
	                    residentInfo.setRemark(ExcelUtil.getValue(row.getCell(11)).toString());
	                }

	                residentInfo.setGridCode(gridCode);   // 网格号

	                residentInfo.setCreatedAt(System.currentTimeMillis());
	                residentInfo.setUpdatedAt(System.currentTimeMillis());
	                result.add(residentInfo);

	            }
	        } catch (Exception e) {
	            logger.info("Excel解析户籍异常："+e.getMessage());
	            throw new MyException("解析户籍信息Excel错误:"+e.getMessage());
	        }
	        return result;
	    }*/

	    /**
	     * 解析excel的某些列的数据 
	     * @param path,column
	     * @return map
	     * @throws Exception
	     */
	    public static List<Map<String,Object>> getExcelByColumnNums(String path,int num) throws Exception{
	        List<Map<String,Object>> listMap=new ArrayList<>();
	        Workbook workbook = ExcelUtil.getWorkbook(path);
	        Sheet sheet = workbook.getSheetAt(0);
	        // 为跳过第一行目录设置count
	        int count = 0;
	        // DecimalFormat decimalFormat = new DecimalFormat("##0");
	        for (Row row : sheet) {
	            // 跳过第一和第二行的目录
	            if(count < 1 ) {
	                count++;
	                continue;
	            }
	            if(row.getCell(0)==null||"".equals(row.getCell(0).toString())){
	                return listMap;
	            }
	            Map<String,Object> map=new HashMap<>();
	            for(int i=0;i<num;i++) {
	                map.put(i+"", ExcelUtil.getValue(row.getCell(i)));
	            }
	            listMap.add(map);
	        }
	        return listMap;
	    }


	    /**
	     * 从第*行开始解析*列的数据
	     * @param path,列数：column，读取起始行:rowNum,读取的sheet页数: sheetNum
	     * @return map
	     * @throws Exception
	     */
	    public static List<Map<String,Object>> getExcelByColumnNumsAndRowNum(String path,int column,int rowNum,int sheetNum) throws Exception{
	        List<Map<String,Object>> listMap=new ArrayList<>();
	        Workbook workbook = ExcelUtil.getWorkbook(path);
	        Sheet sheet = workbook.getSheetAt(sheetNum-1);
	        // 为跳过第一行目录设置count
	        int count = 0;
	        // DecimalFormat decimalFormat = new DecimalFormat("##0");

	        for (Row row : sheet) {
	            // 跳过第一和第二行的目录
	            if(count < rowNum-1 ) {
	                count++;
	                continue;
	            }

	            Map<String,Object> map=new HashMap<>();
	            boolean flag=true;
	            for(int i=0;i<column;i++) {
	                Cell cellCode =row.getCell(i);
	                //cellCode.setCellType(CellType.STRING);
	                if( ExcelUtil.getValue(cellCode) !=null) {
	                	cellCode.setCellType(CellType.STRING);
	                }
	                if((i==0)&&(null== ExcelUtil.getValue(cellCode) ||"".equals( ExcelUtil.getValue(cellCode)))) {
	                	flag=false;
	                }
	                map.put(i+"", ExcelUtil.getValue(cellCode));
	            }
	            if(flag==false) {
	            	 continue;
	            }
	            listMap.add(map);
	        }

	        return listMap;
	    }


	    public static boolean isOSLinux() {
	        Properties prop = System.getProperties();
	        String os = prop.getProperty("os.name");
	        return os != null && os.toLowerCase().contains("linux");
	    }
	    public static boolean isValidLong(String str){
	        try{
	            long _v = Long.parseLong(str);
	            return true;
	        }catch(NumberFormatException e){
	            return false;
	        }
	    }

	    /**
	     * 将pdf文件转换成txt文件
	     * @param pdfPath pdf路径    txtPath 保存的txt路径
	     * **/
	    public static void  pdfToTxt(String pdfPath,String txtPath) {
	    	try {
	            // 是否排序
	            boolean sort = false;
	            // 开始提取页数
	            int startPage = 1;
	            // 结束提取页数
	            int endPage = Integer.MAX_VALUE;
	            String content = null;
	            PrintWriter writer = null;
	            //pdf文本路径
	          //  String path = "E:\\test3\\征信.pdf";
	            //输出txt文本路径
	           // String target="E:\\test3\\test.txt";
	            PDDocument document = PDDocument.load(new File(pdfPath));
	            PDFTextStripper pts = new PDFTextStripper();
	            endPage = document.getNumberOfPages();
	            System.out.println("Total Page: " + endPage);
	            pts.setStartPage(startPage);
	            pts.setEndPage(endPage);
	            try {
	                //content就是从pdf中解析出来的文本
	                content = pts.getText(document);
	                writer = new PrintWriter(new FileOutputStream(txtPath));
	                writer.write(content);// 写入文件内容
	                writer.flush();
	                writer.close();
	            } catch (Exception e) {
	                throw e;
	            }finally {
	                if (null != document)
	                    document.close();
	            }
	            //System.out.println("Get PDF Content ...");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	

	

}
