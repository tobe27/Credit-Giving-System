package com.example.service.black;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.util.StringUtil;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.poverty.CustomerPovertyDO;
import com.example.service.resident.ResidentDO;
import com.example.service.thread.AsyncTaskBlackSave;
import com.example.service.thread.AsyncTaskResidentSave;
import com.example.service.white.CustomerWhiteDO;
import com.example.service.white.CustomerWhiteDOMapper;
import com.example.service.white.CustomerWhiteDOServiceImpl;
import com.github.pagehelper.PageHelper;
/**
 * @author Created by W.S.T on 2018-12-14
 */
@Service
public class CustomerBlackDOServiceImpl implements CustomerBlackDOService {
	private static Logger logger = LoggerFactory.getLogger(CustomerWhiteDOServiceImpl.class);
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final GridInfoDOMapper  gridInfoDOMapper;
	private final OrgDOMapper orgDOMapper;
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
	private final CustomerBlackDOMapper customerBlackDOMapper;
	 @Resource
	private AsyncTaskBlackSave asyncTaskBlackSave;
	@Autowired
	public CustomerBlackDOServiceImpl(CustomerBlackDOMapper customerBlackDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper , OrgDOMapper orgDOMapper,GridInfoDOMapper  gridInfoDOMapper,CustomerWhiteDOMapper customerWhiteDOMapper) {
		this.customerTagRelationDOMapper=customerTagRelationDOMapper;
		this.orgDOMapper=orgDOMapper;
		this.gridInfoDOMapper=gridInfoDOMapper;
		this.customerWhiteDOMapper=customerWhiteDOMapper;
		this.customerBlackDOMapper=customerBlackDOMapper;

	}
	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
    */
	@Transactional
	@Override
	public boolean deleteByPrimaryKey(Long id) throws Exception {
		//删除客户标签中的黑名单
		CustomerBlackDO record=customerBlackDOMapper.selectByPrimaryKey(id);
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)1);
		customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
		try {
			return customerBlackDOMapper.deleteByPrimaryKey(id)==1;
		} catch (Exception e) {
			logger.info("删除黑名单标签异常:" + e.getMessage());
			throw new ServiceException("删除黑名单标签异常");
		}
		}
	 /**
     * 新增
     * @param record
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public boolean insertSelective(CustomerBlackDO record) throws Exception {
		if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号！");
        }
		//校验是否已存在相同身份证号的黑名单
		Map<String,Object> checkMap=new HashMap<>();
		checkMap.put("idNumber", record.getIdNumber());
		List<CustomerBlackDO> cpoList=customerBlackDOMapper.getByIdNumber(checkMap);
		if(cpoList.size()>0) {
			throw new ServiceException("系统中已存在黑名单信息");
		}		
		long now=System.currentTimeMillis();
		record.setUpdatedAt(now);
		record.setCreatedAt(now);
		// 校验客户的家庭标签是否有白名单
				List<CustomerTagRelationDO> list = customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(record.getHouseholdId());
				for(CustomerTagRelationDO tagDo:list) {
				 //如果存在白名单记录需要删除
				if((long)5==tagDo.getTagId()) {
					//删除客户标签中的白名单
					Map<String,Object> map=new HashMap<>();
					map.put("idNumber",tagDo.getIdNumber());
					List<CustomerWhiteDO> whiteList=customerWhiteDOMapper.getByIdNumber(map);
					CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(whiteList.get(0).getIdNumber()).setTagId((long)5);
					customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
					try {
						 customerWhiteDOMapper.deleteByPrimaryKey(whiteList.get(0).getId());
					} catch (Exception e) {
						logger.info("删除白名单标签异常:" + e.getMessage());
						throw new ServiceException("删除白名单标签异常");
					} 
				 }
				
				}
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)1);
		try {
			customerTagRelationDOMapper.insertSelective(tag);
		} catch (Exception e1) {
			logger.info("创建黑名单标签异常:" + e1.getMessage());
			throw new ServiceException("创建黑名单标签异常");
		}
		try {
			return customerBlackDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			logger.info("创建黑名单异常:" + e.getMessage());
			throw new ServiceException("创建黑名单异常！");
		}
	}
	/**
     * 查询
     * @param id
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public CustomerBlackDO selectByPrimaryKey(Long id) throws Exception {
		try {
			return customerBlackDOMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.info("查询黑名单异常:" + e.getMessage());
			throw new ServiceException("查询黑名单异常！");
		}
	}

	 /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
	@Override
	public boolean updateByPrimaryKeySelective(CustomerBlackDO record) throws Exception {
		record.setUpdatedAt(System.currentTimeMillis());
		try {
			return customerBlackDOMapper.updateByPrimaryKeySelective(record)==1;
		} catch (Exception e) {
			logger.info("修改黑名单异常:" + e.getMessage());
			throw new ServiceException("修改黑名单异常！");
		
		}
	}

	 /**
     * 分页
     * @param record
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public List<CustomerBlackDO> getList(Map<String, Object> map) throws Exception {
		if(!map.containsKey("roleId") || !map.containsKey("orgCode")||!map.containsKey("pageNum")||!map.containsKey("pageSize")) {
			throw new ServiceException("查询参数异常！");
		}
		//如果是客户经理登录的
		if(Long.parseLong(map.get("roleId").toString())==1) {
			if(!map.containsKey("idNumber")	||"".equals(map.get("idNumber").toString())) {
				throw new ServiceException("客户经理查询必须输入身份证号！");	
			}	
		}
		
		String orgCode=map.get("orgCode").toString();
		if(orgCode.length()>=9) {
		orgCode=orgCode.substring(0, 6);
		map.put("orgCode", orgCode);
		}
		
		PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
		try {
			return customerBlackDOMapper.getList(map);
		} catch (Exception e) {
			logger.info("查询黑名单异常:" + e.getMessage());
			throw new ServiceException("查询黑名单异常！");
		}
	}
	/**
     * 从excel导入
     * @param List<Map<String,Object>> list
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public Map<String,Object> importFromExcel(List<Map<String, Object>> list,Map<String,Object> paramMap) throws Exception {
    //遍历数据，取出身份证号
		List<String> idNumberList=new ArrayList<>();
		List<CustomerBlackDO> bovrrtyDOList=new ArrayList<>();
		if(list.size()>5000) {
			 throw new ServiceException("单次上传最多5000条数据");
		}
		long now = System.currentTimeMillis();   
		for(Map<String, Object> map:list) {
			if(map.get("2")==null||"".equals(map.get("2").toString())) {
				 throw new ServiceException("身份证号不能为空");
			}
			String idNumber=map.get("2").toString();
			if(idNumberList.contains(idNumber)) {
				throw new ServiceException("文件中存在重复身份证号:"+idNumber);
			}
			
			if (StringUtil.isNotIdNumber(idNumber)) {
	            throw new ServiceException("身份证号:"+idNumber+"不是有效的中国居民身份证号！");
	        }
			if(map.get("1")==null||"".equals(map.get("1").toString())) {
				 throw new ServiceException("身份证号:"+idNumber+"的姓名为空");
			}else if(map.get("1").toString().length()>10) {
				 throw new ServiceException("身份证号:"+idNumber+"的姓名长度超过了10位");
			}
			String reason="";
            if(map.get("3")!=null) {
            	reason=map.get("3").toString();
            	if(reason.length()>20) {
            		 throw new ServiceException("身份证号:"+idNumber+"的未授信原因长度超过了20位");
            	}
            }
			idNumberList.add(idNumber);
			CustomerBlackDO customerPovertyDO=new CustomerBlackDO();
			customerPovertyDO.setCustomerName(map.get("1").toString()).setIdNumber(idNumber).setOrgCode(paramMap.get("orgCode").toString()).setOrgName(paramMap.get("orgName").toString())
			.setUserName(paramMap.get("name").toString()).setUserId(Long.parseLong(paramMap.get("userId").toString())).
			setCreatedAt(now).setUpdatedAt(now).setReason(reason);
			bovrrtyDOList.add(customerPovertyDO);
		}
		//检查系统中是否有重复的黑名单身份证号
		Map<String,Object> map=new HashMap<>();
		map.put("idNumberList", idNumberList);
		List<CustomerBlackDO> doubleList=customerBlackDOMapper.getByIdNumbers(map);
		if(doubleList !=null && !doubleList.isEmpty()) {
			/*String doubleIdNumber="";
			for(CustomerBlackDO cd:doubleList) {
				doubleIdNumber=doubleIdNumber+","+cd.getIdNumber();
			}*/
			throw new ServiceException("系统中已存在"+doubleList.get(0).getIdNumber());
		}
		List<CustomerTagRelationDO> toList=new ArrayList<>();
		  List<CustomerBlackDO> saveReList=new ArrayList<>();
		for(int i=0;i<bovrrtyDOList.size();i++) {
			toList.add(new CustomerTagRelationDO().setIdNumber(bovrrtyDOList.get(i).getIdNumber()).setTagId((long)1));
			 saveReList.add(bovrrtyDOList.get(i));
			 if(saveReList.size()>=200) {
	               try {
					asyncTaskBlackSave.executeAsyncTask(saveReList,customerBlackDOMapper);
				} catch (Exception e) {
					logger.info("批量导入黑名单异常" + e.getMessage());
					throw new ServiceException("批量导入黑名单异常！");
				}
	               saveReList=new ArrayList<>();
	            }
	            if(i==bovrrtyDOList.size()-1) {
	              try {
					asyncTaskBlackSave.executeAsyncTask(saveReList, customerBlackDOMapper);
				} catch (Exception e) {
					logger.info("批量导入黑名单异常" + e.getMessage());
					throw new ServiceException("批量导入黑名单异常！");
				}
	            }
		}
		//删除相关的白名单和标签
	    customerWhiteDOMapper.deleteByIdNumbers(map);
	    map.put("tagId", 5);
		customerTagRelationDOMapper.deleteByIdNumbersAndTagId(map);
		long now1=System.currentTimeMillis();
		System.out.println("=====存完贫困户"+(now1-now));
		if(!toList.isEmpty()) {
      	  customerTagRelationDOMapper.batchSave(toList);
        }
		System.out.println("=====存完标签"+(now1-now));
		
		
		Map<String,Object> returnMap=new HashMap<>();
		returnMap.put("success", bovrrtyDOList.size());
		return returnMap;
	}
	 /**
     * 批量删除
     * @param roleId orgCode
     * @return
     * @throws Exception
    */
	@Transactional
	@Override
	public boolean deleteByOrgCode(Long roleId, String orgCode) throws Exception {
		if(roleId!=4) {
			throw new ServiceException("非法人机构管理员不允许删除黑名单");	
		}
		if(orgCode==null ||"".equals(orgCode)) {
			throw new ServiceException("机构编号不能为空");	
		}
		CustomerBlackDO cbo=new CustomerBlackDO();
		cbo.setOrgCode(orgCode);
		List<CustomerBlackDO> list=customerBlackDOMapper.getListByOrgCode(cbo);
		if(list.isEmpty()) {
			throw new ServiceException("未检索到相关法人机构的黑名单数据");	
		}
		List<String> idNumberList=new ArrayList<>();
		for(CustomerBlackDO black:list) {
			idNumberList.add(black.getIdNumber());
		}
		Map<String,Object> map=new HashMap<>();
		map.put("idNumberList", idNumberList);
		map.put("tagId", 1);
		try {
			customerTagRelationDOMapper.deleteByIdNumbersAndTagId(map);
			customerBlackDOMapper.deleteByOrdCode(cbo);
			
			return customerBlackDOMapper.deleteByOrdCode(cbo)==1;
		} catch (Exception e) {
			logger.info("批量删除黑名单异常:" + e.getMessage());
			throw new ServiceException("批量删除黑名单异常");
		}
	}
	

}
