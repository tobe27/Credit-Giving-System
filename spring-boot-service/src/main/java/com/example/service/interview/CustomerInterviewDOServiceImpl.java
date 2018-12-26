package com.example.service.interview;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.white.CustomerWhiteDOMapper;
import com.github.pagehelper.PageHelper;

/**
 * @author Created by W.S.T on 2018-12-15
 */
@Service
public class CustomerInterviewDOServiceImpl implements CustomerInterviewDOService {
	private final CustomerGreyDOMapper customerGeryDOMapper;
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final GridInfoDOMapper  gridInfoDOMapper;
	private final OrgDOMapper orgDOMapper;
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
	private final CustomerInterviewDOMapper customerInterviewDOMapper;
	private final CustomerDOMapper customerDOMapper;
	@Autowired
	public CustomerInterviewDOServiceImpl(CustomerGreyDOMapper customerGeryDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper , OrgDOMapper orgDOMapper,GridInfoDOMapper  gridInfoDOMapper,CustomerWhiteDOMapper customerWhiteDOMapper,CustomerInterviewDOMapper customerInterviewDOMapper,CustomerDOMapper customerDOMapper) {
		this.customerGeryDOMapper = customerGeryDOMapper;
		this.customerTagRelationDOMapper=customerTagRelationDOMapper;
		this.orgDOMapper=orgDOMapper;
		this.gridInfoDOMapper=gridInfoDOMapper;
		this.customerWhiteDOMapper=customerWhiteDOMapper;
		this.customerInterviewDOMapper=customerInterviewDOMapper;
		this.customerDOMapper=customerDOMapper;

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
		customerInterviewDOMapper.deleteByPrimaryKey(id);
		return customerInterviewDOMapper.deleteByPrimaryKey(id)==1;
	}
	 /**
     * 新增
     * @param record
     * @return
     * @throws Exception
     */
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
     * @param id
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
     * 分页
     * @param id
     * @return
     * @throws Exception
     */
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
    		return customerInterviewDOMapper.getList(map);
    	} catch (Exception e) {
    	  logger.info("查询面谈面签异常:" + e.getMessage());
    	  throw new ServiceException("查询面谈面签异常！");
    		}	
		
		
	}

}
