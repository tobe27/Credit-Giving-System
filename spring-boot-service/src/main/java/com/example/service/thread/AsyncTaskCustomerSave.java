package com.example.service.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.example.common.enums.DatePatternEnum;
import com.example.common.util.DateConvertUtil;
import com.example.common.util.StringUtil;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.customer.ODSCustomerDO;
import com.example.service.customer.ODSCustomerDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.resident.ResidentDO;
import com.example.service.resident.ResidentDOMapper;

@Service
//线程执行任务类
public class AsyncTaskCustomerSave {
	Random random = new Random();// 默认构造方法
    @Async
    // 表明是异步方法
    // 无返回值
    public void executeAsyncTask(List<ResidentDO> list,  CustomerDOMapper customerDOMapper,ODSCustomerDOMapper odsCustomerDOMapper) {
    	 System.out.println(Thread.currentThread().getName()+"开启新线程执行CUSTOMER" + list.size());
    	 List<CustomerDO> customerList=new ArrayList<>();
    	 List<String> idNumberList=new ArrayList<>();
    	 for(ResidentDO record:list) {
    		  idNumberList.add(record.getIdNumber());
    	 }
    	 Map<String, Object> map=new HashMap<>();
         map.put("idNumberList", idNumberList);
    	  //查询到的本批数据
         List<ODSCustomerDO> odsCustomerDOList=odsCustomerDOMapper.listODSByIdNumbers(map);
         for(ResidentDO record:list) {
            long now = System.currentTimeMillis();
             // 计算年龄
             Integer birthYear = Integer.valueOf(record.getIdNumber().substring(6, 10));
             Integer nowYear = Integer.valueOf(DateConvertUtil.convert2String(now, DatePatternEnum.DATE).substring(0, 4));
             Integer age = nowYear - birthYear;

             // 户籍转换成客户信息
             int idNumber17th = Integer.valueOf(record.getIdNumber().substring(16, 17));
             String year = record.getIdNumber().substring(6, 10);
             String month = record.getIdNumber().substring(10, 12);
             String day = record.getIdNumber().substring(12, 14);
             String birthday = year + "-" + month + "-" + day;
             CustomerDO customerDO = new CustomerDO();
             customerDO.setName(record.getName()) // 姓名
                     .setType("其他自然人") // 类型
                     .setIdType("身份证") // 身份证类型
                     .setIdNumber(record.getIdNumber()) //身份证
                     .setBirthday(birthday) // 出生日期
                     .setGender(idNumber17th % 2 == 0 ? "2" : "1") // 性别，1-男，2-女
                     .setNativeAddress(record.getCounty() + record.getTownship() + record.getVillage() + record.getGroup()) //户籍地址
                     .setPhoneNumber(record.getContact())
                     .setHouseholdId(record.getHouseholdId())
                     .setIsBorrower("否") //借款主体
                     .setStatus("1")
                     .setRelationship(record.getRelationship()) // 与户主的关系
                     .setValidTime(0) // 有效次数
                     .setIsConcluded("否") // 是否有调查结论
                     .setAge(age) // 年龄
                     .setGridCode(record.getGridCode())
                     .setMainOrgName(record.getOrgName())
                     .setRegisterPerson(record.getUserName())
                     .setLastModifyPerson(record.getUserName())
                     .setCreatedAt(System.currentTimeMillis())
                     .setUpdatedAt(System.currentTimeMillis());

             customerList.add(customerDO);
         }
        customerDOMapper.batchSave(customerList);
    }
    /**
     * 从ODS获取数据补全信息
     * @param customerDO
     * @return
     */
    private CustomerDO getCustomerFromODS1(CustomerDO customerDO,ODSCustomerDO odsCustomerDOList) {
            ODSCustomerDO odsCustomerDO = odsCustomerDOList;
            customerDO.setCreditId(odsCustomerDO.getCreditId())
                    .setSignOrg(odsCustomerDO.getSignOrg())
                    .setIsLongTerm(odsCustomerDO.getIsLongTerm())
                    .setSignDate(odsCustomerDO.getSignDate())
                    .setDueDate(odsCustomerDO.getDueDate())
                    .setDetailAddress(odsCustomerDO.getDetailAddress())
                    .setPhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getPhoneNumber())
                            ? odsCustomerDO.getPhoneNumber() : null)
                    .setNationality(odsCustomerDO.getNationality())
                    .setNation(odsCustomerDO.getNation())
                    .setPoliticsStatus(odsCustomerDO.getPoliticsStatus())
                    .setEducationBackground(odsCustomerDO.getEducationBackground())
                    .setPhysicalCondition(odsCustomerDO.getPhysicalCondition())
                    .setMaritalStatus(odsCustomerDO.getMaritalStatus())
                    .setSpouseName(odsCustomerDO.getSpouseName())
                    .setSpouseIdNumber(StringUtil.isIdNumber(odsCustomerDO.getSpouseIdNumber())
                            ? odsCustomerDO.getSpouseIdNumber() : null)
                    .setSpousePhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getSpousePhoneNumber())
                            ? odsCustomerDO.getSpousePhoneNumber() : null)
                    .setSpouseCompanyName(odsCustomerDO.getSpouseCompanyName())
                    .setCareerType(odsCustomerDO.getCareerType())
                    .setMainBusiness(odsCustomerDO.getMainBusiness())
                    .setIsStaff(odsCustomerDO.getIsStaff())
                    .setIsStockholder(odsCustomerDO.getIsStockholder())
                    .setIsCivilServant(odsCustomerDO.getIsCivilServant())
                    .setIsHouseOwner(odsCustomerDO.getIsHouseOwner());
       
               return customerDO;
    }
    /**
     * 从ODS获取数据补全信息
     * @param customerDO
     * @return
     */
    private CustomerDO getCustomerFromODS(CustomerDO customerDO,ODSCustomerDOMapper odsCustomerDOMapper) {
        List<ODSCustomerDO> odsCustomerDOList;
        try {
            odsCustomerDOList = odsCustomerDOMapper.listODSByIdNumber(customerDO.getIdNumber());
        } catch (Exception e) {
            
            throw new ServiceException("获取存量客户异常");
        }
        if (odsCustomerDOList != null && odsCustomerDOList.size() !=0) {
            ODSCustomerDO odsCustomerDO = odsCustomerDOList.get(0);
            customerDO.setCreditId(odsCustomerDO.getCreditId())
                    .setSignOrg(odsCustomerDO.getSignOrg())
                    .setIsLongTerm(odsCustomerDO.getIsLongTerm())
                    .setSignDate(odsCustomerDO.getSignDate())
                    .setDueDate(odsCustomerDO.getDueDate())
                    .setDetailAddress(odsCustomerDO.getDetailAddress())
                    .setPhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getPhoneNumber())
                            ? odsCustomerDO.getPhoneNumber() : null)
                    .setNationality(odsCustomerDO.getNationality())
                    .setNation(odsCustomerDO.getNation())
                    .setPoliticsStatus(odsCustomerDO.getPoliticsStatus())
                    .setEducationBackground(odsCustomerDO.getEducationBackground())
                    .setPhysicalCondition(odsCustomerDO.getPhysicalCondition())
                    .setMaritalStatus(odsCustomerDO.getMaritalStatus())
                    .setSpouseName(odsCustomerDO.getSpouseName())
                    .setSpouseIdNumber(StringUtil.isIdNumber(odsCustomerDO.getSpouseIdNumber())
                            ? odsCustomerDO.getSpouseIdNumber() : null)
                    .setSpousePhoneNumber(StringUtil.isPhoneNumber(odsCustomerDO.getSpousePhoneNumber())
                            ? odsCustomerDO.getSpousePhoneNumber() : null)
                    .setSpouseCompanyName(odsCustomerDO.getSpouseCompanyName())
                    .setCareerType(odsCustomerDO.getCareerType())
                    .setMainBusiness(odsCustomerDO.getMainBusiness())
                    .setIsStaff(odsCustomerDO.getIsStaff())
                    .setIsStockholder(odsCustomerDO.getIsStockholder())
                    .setIsCivilServant(odsCustomerDO.getIsCivilServant())
                    .setIsHouseOwner(odsCustomerDO.getIsHouseOwner());
        }
        return customerDO;
    }
    /**
     * 异常调用返回Future
     *
     * @param i
     * @return
     * @throws InterruptedException
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) throws InterruptedException {
        System.out.println("input is " + i);
        Thread.sleep(1000 * random.nextInt(i));

        Future<String> future = new AsyncResult<String>("success:" + i);// Future接收返回值，这里是String类型，可以指明其他类型

        return future;
    }
}
