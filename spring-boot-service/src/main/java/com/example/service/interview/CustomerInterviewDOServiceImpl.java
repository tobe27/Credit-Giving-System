package com.example.service.interview;

import com.example.common.enums.DatePatternEnum;
import com.example.common.util.DateConvertUtil;
import com.example.service.black.CustomerBlackDO;
import com.example.service.black.CustomerBlackDOMapper;
import com.example.service.blue.BlueDO;
import com.example.service.blue.BlueDOMapper;
import com.example.service.conclusion.ConclusionDO;
import com.example.service.conclusion.ConclusionDOMapper;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOMapper;
import com.example.service.poverty.CustomerPovertyDO;
import com.example.service.poverty.CustomerPovertyDOMapper;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import com.example.service.white.CustomerWhiteDO;
import com.example.service.white.CustomerWhiteDOMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-15
 */
@Service
public class CustomerInterviewDOServiceImpl implements CustomerInterviewDOService {
    private final CustomerGreyDOMapper customerGeryDOMapper;
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
    private final OrgDOMapper orgDOMapper;
    private final CustomerWhiteDOMapper customerWhiteDOMapper;
    private final CustomerInterviewDOMapper customerInterviewDOMapper;
    private final CustomerDOMapper customerDOMapper;
    private final CustomerBlackDOMapper customerBlackDOMapper;
    private final CustomerPovertyDOMapper customerPovertyDOMapper;
    private final SurveyDOMapper surveyDOMapper;
    private final ConclusionDOMapper conclusionDOMapper;
    private final BlueDOMapper blueDOMapper;

    @Autowired
    public CustomerInterviewDOServiceImpl(CustomerGreyDOMapper customerGeryDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper, OrgDOMapper orgDOMapper, GridInfoDOMapper gridInfoDOMapper, CustomerWhiteDOMapper customerWhiteDOMapper, CustomerInterviewDOMapper customerInterviewDOMapper, CustomerDOMapper customerDOMapper, CustomerBlackDOMapper customerBlackDOMapper, CustomerPovertyDOMapper customerPovertyDOMapper, SurveyDOMapper surveyDOMapper, ConclusionDOMapper conclusionDOMapper, BlueDOMapper blueDOMapper) {
        this.customerGeryDOMapper = customerGeryDOMapper;
        this.customerTagRelationDOMapper=customerTagRelationDOMapper;
        this.orgDOMapper=orgDOMapper;
        this.customerWhiteDOMapper=customerWhiteDOMapper;
        this.customerInterviewDOMapper=customerInterviewDOMapper;
        this.customerDOMapper=customerDOMapper;
        this.customerBlackDOMapper = customerBlackDOMapper;
        this.customerPovertyDOMapper = customerPovertyDOMapper;
        this.surveyDOMapper = surveyDOMapper;
        this.conclusionDOMapper = conclusionDOMapper;
        this.blueDOMapper = blueDOMapper;
    }
    private static Logger logger = LoggerFactory.getLogger(CustomerInterviewDOServiceImpl.class);

    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) throws Exception {
        return customerInterviewDOMapper.deleteByPrimaryKey(id)==1;
    }

    /**
     * 新增
     * @param record
     * @return
     * @throws Exception
     */
    @Transactional
    @Override
    public boolean insertSelective(CustomerInterviewDO record) throws Exception {
        if(record.getIdNumber()==null||"".equals(record.getCustomerName())) {
            throw new ServiceException("客户姓名及客户身份证号不能为空");
        }
        if(record.getUserId()==null||"".equals(record.getUserName())) {
            throw new ServiceException("登记人姓名及账号不能为空");
        }
        if("".equals(record.getGridCode())||"".equals(record.getGridName())) {
            throw new ServiceException("客户网格编号及网格名称不能为空");
        }
        if("".equals(record.getHouseholdId())||null==record.getHouseholdId()) {
            throw new ServiceException("客户家庭户号不能为空");
        }
        if("".equals(record.getOrgCode())||"".equals(record.getOrgName())) {
            throw new ServiceException("机构编号及机构名称不能为空");
        }
        long now=System.currentTimeMillis();
        record.setCreatedAt(now).setUpdatedAt(now).setStatus("1");
        //查出家庭人数
        CustomerDO customer=new CustomerDO().setHouseholdId(record.getHouseholdId()).setGridCode(record.getGridCode());

        List<CustomerDO> list=customerDOMapper.listByHouseholdIdAndGridCode(customer);

        CustomerDO customerDO= customerDOMapper.getByIdNumber(record.getIdNumber());
        if(customerDO==null||customerDO.getIdNumber()==null) {
            logger.info("未查询到相关客户信息" );
            throw new ServiceException("未查询到相关客户信息");
        }
        record.setSex(customerDO.getGender()).setIsMarried(customerDO.getMaritalStatus()).setEducation(customerDO.getEducationBackground())
                .setPhone(customerDO.getPhoneNumber()).setFamilySize(list.size()+"").setNativeAddress(customerDO.getNativeAddress()).setResidenceAddress(customerDO.getDetailAddress()).setTimeLimit("3").setProvide("信用").setPurpose("生产经营、生活消费").setRepayment("不定期还款").setInterestSettlement("按月结息").setApprovalStatus("0")
                .setAttachFlag("0.0.0");
        List<CustomerInterviewDO> inlist=customerInterviewDOMapper.getListByIdNumber(new CustomerInterviewDO().setStatus("1").setIdNumber(record.getIdNumber()));
        if(inlist!=null&&inlist.size()>0) {
            throw new ServiceException("系统中已存在客户名："+record.getCustomerName()+",身份证号："+record.getIdNumber()+"的面谈面签信息");
        }
		/*for(CustomerInterviewDO cdo:inlist) {
			if(!"4".equals(cdo.getApprovalStatus())) {
				throw new ServiceException("系统中已存在客户名："+record.getCustomerName()+",身份证号："+record.getIdNumber()+"未归档信息");	
			}
		}*/
        if(null==record.getCustomerName()) {
            record.setCustomerName("");
        }
        return customerInterviewDOMapper.insertSelective(record)==1;
    }
    /**
     * 查询
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public CustomerInterviewDO selectByPrimaryKey(Long id) throws Exception {
        return customerInterviewDOMapper.selectByPrimaryKey(id);
    }
    /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateByPrimaryKeySelective(CustomerInterviewDO record) throws Exception {
        if("".equals(record.getAppliedSum())||"".equals(record.getAppraiseSum())) {
            throw new ServiceException("评议授信金额和申请授信金额不能为空");
        }
        //检查是否存在重复的完善且不是存档的数据
		/*List<CustomerInterviewDO> inlist=customerInterviewDOMapper.getListByIdNumber(new CustomerInterviewDO().setStatus("0").setIdNumber(record.getIdNumber()));
		for(CustomerInterviewDO co:inlist) {
			if((!"0".equals(co.getStatus()))&&(!"4".equals(co.getApprovalStatus()))) {
				throw new ServiceException("系统中已存在客户名："+record.getCustomerName()+",身份证号："+record.getIdNumber()+"的非归档状态的面谈面签信息");		
			}
		}*/
		/*if(inlist!=null&&inlist.size()>0) {
			throw new ServiceException("系统中已存在客户名："+record.getCustomerName()+",身份证号："+record.getIdNumber()+"的待完善面谈面签信息");	
		}*/
        record.setUpdatedAt(System.currentTimeMillis()).setStatus("0");
        return customerInterviewDOMapper.updateByPrimaryKeySelective(record)==1;
    }

    /**
     * 修改借款主体
     * ==面签
     * ==客户
     * ==白名单与标签库
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean modifyBorrower(CustomerInterviewDO record) throws Exception {
        // 修改面签的借款主体，姓名、身份证号、性别、手机号码、年龄
        CustomerInterviewDO customerInterviewDO = new CustomerInterviewDO();
        customerInterviewDO.setId(record.getId())
                .setCustomerId(record.getCustomerId())
                .setCustomerName(record.getCustomerName())
                .setIdNumber(record.getIdNumber())
                .setSex(record.getSex())
                .setPhone(record.getPhone())
                .setFamilySize(record.getFamilySize());

        try {
            customerInterviewDOMapper.updateByPrimaryKeySelective(customerInterviewDO);
        } catch (Exception e) {
            logger.info("变更借款主体异常:" + e.getMessage());
            throw new ServiceException("变更借款主体异常");
        }

        // 客户信息是否借款主体修改
        CustomerDO oldBorrower = new CustomerDO();
        oldBorrower.setId(record.getOldCustomerId()).setIsBorrower("否");
        CustomerDO newBorrower = new CustomerDO();
        newBorrower.setId(record.getCustomerId()).setIsBorrower("是");
        try {
            customerDOMapper.updateByPrimaryKeySelective(oldBorrower);
            customerDOMapper.updateByPrimaryKeySelective(newBorrower);
        } catch (Exception e) {
            logger.info("变更是否借款主体异常:" + e.getMessage());
            throw new ServiceException("变更是否借款主体异常");
        }

        // 变更白名单库
        CustomerWhiteDO customerWhiteDO = new CustomerWhiteDO();
        customerWhiteDO.setCustomerName(record.getCustomerName()).setIdNumber(record.getIdNumber()).setOldIdNumber(record.getOldIdNumber());
        // 变更标签库
        CustomerTagRelationDO customerTagRelationDO = new CustomerTagRelationDO();
        customerTagRelationDO.setIdNumber(record.getIdNumber()).setOldIdNumber(record.getOldIdNumber());

        try {
            customerWhiteDOMapper.updateByOldIdNumberSelective(customerWhiteDO);
            customerTagRelationDOMapper.updateByOldIdNumberSelective(customerTagRelationDO);
        } catch (Exception e) {
            logger.info("变更是否借款主体异常:" + e.getMessage());
            throw new ServiceException("变更是否借款主体异常");
        }

        return true;
    }

    /**
     * 作废操作
     * (1)客户从白名单中剔除，进入选择的名单库;
     * (2)评议员评议数据及客户经理评议结论作废;
     * (3)“面签”或“通过”的数据进入到“作废”。
     * (4)作废后，退出对应名单，可以重新发起评议。
     *
     * @param
     * @throws Exception
     */
    @Override
    @Transactional
    public void banInterview(CustomerInterviewDO record) throws Exception {
        String reason = record.getComment();
        int option = record.getOption();
        record = customerInterviewDOMapper.selectByPrimaryKey(record.getId());
        record.setComment(reason).setOption(option);

        // 作废时变更名单库
        changeTag(record);

        // 调查信息，调查结论作废，即 是否有效置为“0”
        try {
            surveyDOMapper.updateByHouseholdIdSelective(new SurveyDO().setHouseholdId(record.getHouseholdId()).setIsValid("0"));
            conclusionDOMapper.updateByHouseholdIdSelective(new ConclusionDO().setHouseholdId(record.getHouseholdId()).setIsValid("0"));
        } catch (Exception e) {
            logger.info("作废评议信息异常:" + e.getMessage());
            throw new ServiceException("作废评议信息异常");
        }

        // 删除面签信息，同时将面签数据移植到作废表中
        try {
            customerInterviewDOMapper.deleteByPrimaryKey(record.getId());
            customerInterviewDOMapper.insertBanSelective(record.setUpdatedAt(record.getId())); // 用updateAt字段存储面签的ID
            // 同时客户信息-授信额度=0，有效调查次数=0，是否下结论=否，是否借款主体=否
            customerDOMapper.updateByHouseholdIdSelective(
                    new CustomerDO()
                            .setHouseholdId(record.getHouseholdId())
                            .setInterviewId(record.getId())
                            .setAmount(new BigDecimal("0"))
                            .setIsConcluded("否")
                            .setIsBorrower("否")
                            .setValidTime(0));
        } catch (Exception e) {
            logger.info("作废面签信息异常:" + e.getMessage());
            throw new ServiceException("作废面签信息异常");
        }

    }

    /**
     * 作废时变更名单库
     * @param record
     */
    private void changeTag(CustomerInterviewDO record) {
        // 从白名单剔除 5
        try {
            // 删除白名单关联信息
            customerTagRelationDOMapper.deleteByIdNumberAndTagId(new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId(5L));
            // 删除白名单库信息
            customerWhiteDOMapper.deleteByIdNumber(record.getIdNumber());
        } catch (Exception e) {
            logger.info("作废时剔除白名单异常:" + e.getMessage());
            throw new ServiceException("作废时剔除白名单异常");
        }
        // 导入黑名单 =1
        if (record.getOption() == 1) {
            CustomerBlackDO customerBlackDO = new CustomerBlackDO();
            OrgDO frOrg = orgDOMapper.getParentOrgByOrgCode(record.getOrgCode());
            customerBlackDO.setCustomerId(record.getCustomerId())
                    .setCustomerName(record.getCustomerName())
                    .setIdNumber(record.getIdNumber())
                    .setHouseholdId(record.getHouseholdId())
                    .setReason(record.getComment())
                    .setUserId(record.getUserId())
                    .setUserName(record.getUserName())
                    .setGridCode(record.getGridCode())
                    .setGridName(record.getGridName())
                    .setOrgCode(frOrg.getOrgCode()) // 换成法人的机构
                    .setOrgName(frOrg.getOrgName())
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());

            try {
                customerTagRelationDOMapper.insertSelective(new CustomerTagRelationDO().setTagId(1L).setIdNumber(record.getIdNumber()));
                customerBlackDOMapper.insertSelective(customerBlackDO);
            } catch (Exception e) {
                logger.info("作废时添加黑名单异常:" + e.getMessage());
                throw new ServiceException("作废时添加黑名单异常");
            }

        }
        // 导入灰名单 =2
        if (record.getOption() == 2) {
            CustomerGreyDO customerGreyDO = new CustomerGreyDO();
            customerGreyDO.setCustomerId(record.getCustomerId())
                    .setCustomerName(record.getCustomerName())
                    .setIdNumber(record.getIdNumber())
                    .setHouseholdId(record.getHouseholdId())
                    .setReason(record.getComment())
                    .setUserId(record.getUserId())
                    .setUserName(record.getUserName())
                    .setGridCode(record.getGridCode())
                    .setGridName(record.getGridName())
                    .setOrgCode(record.getOrgCode())
                    .setOrgName(record.getOrgName())
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());

            try {
                customerTagRelationDOMapper.insertSelective(new CustomerTagRelationDO().setTagId(2L).setIdNumber(record.getIdNumber()));
                customerGeryDOMapper.insertSelective(customerGreyDO);
            } catch (Exception e) {
                logger.info("作废时添加灰名单异常:" + e.getMessage());
                throw new ServiceException("作废时添加灰名单异常");
            }
        }
        // 导入贫困户 =3
        if (record.getOption() == 3) {
            OrgDO frOrg = orgDOMapper.getParentOrgByOrgCode(record.getOrgCode());
            CustomerPovertyDO customerPovertyDO = new CustomerPovertyDO();
            customerPovertyDO.setCustomerId(record.getCustomerId())
                    .setCustomerName(record.getCustomerName())
                    .setIdNumber(record.getIdNumber())
                    .setHouseholdId(record.getHouseholdId())
                    .setReason(record.getComment())
                    .setUserId(record.getUserId())
                    .setUserName(record.getUserName())
                    .setGridCode(record.getGridCode())
                    .setGridName(record.getGridName())
                    .setOrgCode(frOrg.getOrgCode()) // 换成法人的机构
                    .setOrgName(frOrg.getOrgName())
                    .setCreatedAt(System.currentTimeMillis())
                    .setUpdatedAt(System.currentTimeMillis());

            try {
                customerTagRelationDOMapper.insertSelective(new CustomerTagRelationDO().setTagId(3L).setIdNumber(record.getIdNumber()));
                customerPovertyDOMapper.insertSelective(customerPovertyDO);
            } catch (Exception e) {
                logger.info("作废时添加贫困户异常:" + e.getMessage());
                throw new ServiceException("作废时添加贫困户异常");
            }
        }

        // 导入蓝名单标签 =4
        if (record.getOption() == 4) {
            try {
                customerTagRelationDOMapper.insertSelective(new CustomerTagRelationDO().setTagId(4L).setIdNumber(record.getIdNumber()));
                BlueDO blueDO = new BlueDO();
                blueDO.setName(record.getCustomerName())
                        .setIdNumber(record.getIdNumber())
                        .setReasonEn("09")
                        .setCreatedDate(DateConvertUtil.convert2String(System.currentTimeMillis(),DatePatternEnum.DATE));
                blueDOMapper.insertSelective(blueDO);
            } catch (Exception e) {
                logger.info("作废时添加蓝名单异常:" + e.getMessage());
                throw new ServiceException("作废时添加蓝名单异常");
            }
        }
    }

    /**
     * 分页
     * @param map
     * @return
     * @throws Exception
     */
    @Transactional
    @Override
    public List<CustomerInterviewDO> getList(Map<String, Object> map) throws Exception {
        if(!map.containsKey("roleId") || map.get("roleId")==null) {
            throw new ServiceException("查询参数异常");
        }

        if(!map.containsKey("userId") || map.get("userId")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("approvalStatus") || map.get("approvalStatus")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("orgCode")|| map.get("orgCode")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("attachFlag")|| map.get("attachFlag")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("pageNum")) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("pageSize")) {
            throw new ServiceException("查询参数异常");
        }
       /* if("0".equals(map.get("attachFlag").toString())) {
        	map.remove("attachFlag");
        	
        }else if("1".equals(map.get("attachFlag").toString())) {
        	map.put("attachFlag","");
        	
        }*/
        if("0".equals(map.get("attachFlag").toString())) {
            map.remove("attachFlag");

        }
        //如果不是客户经理登录的
        if (Long.parseLong(map.get("roleId").toString()) != 1) {
            try {
                map.put("orgCodeList", orgDOMapper.listStringOrgCodes(map.get("orgCode").toString()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        try {
            return customerInterviewDOMapper.getList(map);
        } catch (Exception e) {
            logger.info("查询面谈面签异常:" + e.getMessage());
            throw new ServiceException("查询面谈面签异常！");
        }


    }

    /**
     * 列表,用于导出
     *
     * @param map
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getListNoPage(Map<String, Object> map) throws Exception {

        if("0".equals(map.get("attachFlag").toString())) {
            map.remove("attachFlag");
        }
        List<CustomerInterviewDO> customerInterviewDOList;
        try {
            customerInterviewDOList = customerInterviewDOMapper.getList(map);
        } catch (Exception e) {
            logger.info("查询面谈面签异常:" + e.getMessage());
            throw new ServiceException("查询面谈面签异常！");
        }
        if (customerInterviewDOList == null || customerInterviewDOList.size() == 0) {
            throw new ServiceException("数据为空");
        }
        List<Map<String, Object>> interviewList = new ArrayList<>();

        int no = 1; // 序号
        for (CustomerInterviewDO customerInterviewDO : customerInterviewDOList) {
            Map<String, Object> interviewMap = new HashMap<>();
            interviewMap.put("no", no ++);
            interviewMap.put("name", customerInterviewDO.getCustomerName());
            interviewMap.put("phoneNumber", customerInterviewDO.getPhone());
            interviewMap.put("idNumber", customerInterviewDO.getIdNumber());
            interviewMap.put("age", customerInterviewDO.getFamilySize());
            interviewMap.put("address", customerInterviewDO.getNativeAddress());
            interviewMap.put("creditAmount", customerInterviewDO.getAppliedSum());
            interviewList.add(interviewMap);
        }

        return interviewList;
    }

    /**
     * 作废分页
     *
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<CustomerInterviewDO> listBanInterview(Map<String, Object> map) throws Exception {
        if(!map.containsKey("roleId") || map.get("roleId")==null) {
            throw new ServiceException("查询参数异常");
        }

        if(!map.containsKey("userId") || map.get("userId")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("orgCode")|| map.get("orgCode")==null) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("pageNum")) {
            throw new ServiceException("查询参数异常");
        }
        if(!map.containsKey("pageSize")) {
            throw new ServiceException("查询参数异常");
        }
       /* if("0".equals(map.get("attachFlag").toString())) {
        	map.remove("attachFlag");

        }else if("1".equals(map.get("attachFlag").toString())) {
        	map.put("attachFlag","");

        }*/

        //如果不是客户经理登录的
        if (Long.parseLong(map.get("roleId").toString()) != 1) {
            try {
                map.put("orgCodeList", orgDOMapper.listStringOrgCodes(map.get("orgCode").toString()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }

        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        try {
            return customerInterviewDOMapper.listBanInterview(map);
        } catch (Exception e) {
            logger.info("查询作废面谈面签异常:" + e.getMessage());
            throw new ServiceException("查询作废面谈面签异常！");
        }

    }

}
