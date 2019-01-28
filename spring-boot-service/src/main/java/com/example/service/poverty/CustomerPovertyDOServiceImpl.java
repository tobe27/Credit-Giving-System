package com.example.service.poverty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.example.service.conclusion.ConclusionDO;
import com.example.service.conclusion.ConclusionDOMapper;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.interview.CustomerInterviewDO;
import com.example.service.interview.CustomerInterviewDOMapper;
import com.example.service.org.OrgDO;
import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.util.StringUtil;
import com.example.service.black.CustomerBlackDO;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grey.CustomerGreyDO;
import com.example.service.grey.CustomerGreyDOMapper;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDOMapper;
import com.example.service.resident.ResidentDO;
import com.example.service.thread.AsyncTaskBlackSave;
import com.example.service.thread.AsyncTaskPovertySave;
import com.example.service.white.CustomerWhiteDO;
import com.example.service.white.CustomerWhiteDOMapper;
import com.example.service.white.CustomerWhiteDOServiceImpl;
import com.github.pagehelper.PageHelper;
@Service
public class CustomerPovertyDOServiceImpl implements CustomerPovertyDOService {
	 @Resource
	private AsyncTaskPovertySave asyncTaskPovertySave;
	private final CustomerPovertyDOMapper customerPovertyDOMapper;
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final GridInfoDOMapper  gridInfoDOMapper;
	private final OrgDOMapper orgDOMapper;
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
	private final SurveyDOMapper surveyDOMapper;
	private final CustomerInterviewDOMapper customerInterviewDOMapper;
	private final CustomerDOMapper customerDOMapper;
	private final ConclusionDOMapper conclusionDOMapper;
	private static Logger logger = LoggerFactory.getLogger(CustomerWhiteDOServiceImpl.class);
	@Autowired
	public CustomerPovertyDOServiceImpl(CustomerPovertyDOMapper customerPovertyDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper , OrgDOMapper orgDOMapper,GridInfoDOMapper  gridInfoDOMapper,CustomerWhiteDOMapper customerWhiteDOMapper,SurveyDOMapper surveyDOMapper,CustomerInterviewDOMapper customerInterviewDOMapper,CustomerDOMapper customerDOMapper,ConclusionDOMapper conclusionDOMapper) {
		this.customerPovertyDOMapper = customerPovertyDOMapper;
		this.customerTagRelationDOMapper=customerTagRelationDOMapper;
		this.orgDOMapper=orgDOMapper;
		this.gridInfoDOMapper=gridInfoDOMapper;
		this.customerWhiteDOMapper=customerWhiteDOMapper;
		this.surveyDOMapper = surveyDOMapper;
		this.customerInterviewDOMapper=customerInterviewDOMapper;
		this.customerDOMapper=customerDOMapper;
		this.conclusionDOMapper = conclusionDOMapper;
	}
	/**
     * 删除
     * @param map
     * @return
     * @throws Exception
    */
	@Transactional
	@Override
	public boolean delete(Map<String,String> map) throws Exception {
		if(!map.containsKey("idNumber")) {
			throw new ServiceException("请求参数异常！");
		} else {
			map.put("idNumber",map.get("idNumber").toUpperCase());
		}
		if(!map.containsKey("orgCode")) {
			throw new ServiceException("请求参数异常！");
		}
		OrgDO org=orgDOMapper.selectByIdNumberInBlackAndPoverty(map.get("idNumber"),3);
		if (org !=null && !map.get("orgCode").equals(org.getOrgCode())){
			throw new ServiceException("对不起，这个客户所在的法人机构为："+ org.getOrgName() +",您不能删除！");
		}
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(map.get("idNumber")).setTagId((long)3);
		customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
		try {
			return customerPovertyDOMapper.deleteByIdNumber(map.get("idNumber"))>=1;
		} catch (Exception e) {
			logger.info("删除贫困户标签异常:" + e.getMessage());
			throw new ServiceException("删除贫困户标签异常");
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
	public boolean insertSelective(CustomerPovertyDO record) throws Exception {
		if (StringUtil.isNotIdNumber(record.getIdNumber())) {
            throw new ServiceException("不是有效的中国居民身份证号！");
        }
		record.setIdNumber(record.getIdNumber().toUpperCase());
		//校验是否已存在相同身份证号的贫困户
		Map<String,Object> checkMap=new HashMap<>();
		checkMap.put("idNumber", record.getIdNumber());
		List<CustomerPovertyDO> cpoList=customerPovertyDOMapper.getByIdNumber(checkMap);
		if(cpoList.size()>0) {
			throw new ServiceException("系统中已存在贫困户信息");
		}
		long now=System.currentTimeMillis();
		record.setUpdatedAt(now);
		record.setCreatedAt(now);
		// 校验客户的家庭标签是否有白名单
		String householdId = customerDOMapper.getHouseholdIdByIdNumber(record.getIdNumber());
		List<CustomerTagRelationDO> list = customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(householdId);
		//作废评议信息，即 是否有效置为“0”
		try {
			surveyDOMapper.updateByHouseholdIdSelective(new SurveyDO().setHouseholdId(householdId).setIsValid("0"));
			// 同时客户信息-授信额度=0，有效调查次数=0，是否下结论=否
			customerDOMapper.updateByHouseholdIdSelective(
					new CustomerDO()
							.setHouseholdId(householdId)
							.setAmount(new BigDecimal("0"))
							.setIsConcluded("否")
							.setIsBorrower("否")
							.setValidTime(0));
		} catch (Exception e) {
			logger.info("评议信息作废异常:" + e.getMessage());
			throw new ServiceException("评议信息作废异常");
		}
		for(CustomerTagRelationDO tagDo:list) {
			 //如果存在白名单记录需要删除
			if((long)5==tagDo.getTagId()) {
				//删除客户标签中的白名单
				try {
					CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(tagDo.getIdNumber()).setTagId((long)5);
					customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
					customerWhiteDOMapper.deleteByIdNumber(tagDo.getIdNumber());
				} catch (Exception e) {
					logger.info("删除白名单标签异常:" + e.getMessage());
					throw new ServiceException("删除白名单标签异常");
				}
			 }
			//查询面签信息
			CustomerInterviewDO interview = customerInterviewDOMapper.selectByIdNumber(tagDo.getIdNumber());
			// 调查结论作废，即 是否有效置为“0”
			try {
				conclusionDOMapper.updateByHouseholdIdSelective(new ConclusionDO().setHouseholdId(interview.getHouseholdId()).setIsValid("0"));
			} catch (Exception e) {
				logger.info("调查结论信息异常:" + e.getMessage());
				throw new ServiceException("调查结论信息异常");
			}

			// 删除面签信息，同时将面签数据移植到作废表中
			try {
				customerInterviewDOMapper.deleteByPrimaryKey(interview.getId());
				customerInterviewDOMapper.insertBanSelective(interview.setUpdatedAt(interview.getId())); // 用updateAt字段存储面签的ID
			} catch (Exception e) {
				logger.info("作废面签信息异常:" + e.getMessage());
				throw new ServiceException("作废面签信息异常");
			}
		}
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)3);
		try {
			customerTagRelationDOMapper.insertSelective(tag);
		} catch (Exception e1) {
			logger.info("创建贫困户标签异常:" + e1.getMessage());
			throw new ServiceException("创建贫困户标签异常");
		}
		try {
			return customerPovertyDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			logger.info("创建贫困户异常:" + e.getMessage());
			throw new ServiceException("创建贫困户异常！");
		}	
		
	}

	/**
     * 查询
     * @param id
     * @return
     * @throws Exception
     */
	@Override
	public CustomerPovertyDO selectByPrimaryKey(Long id) throws Exception {
		try {
			return customerPovertyDOMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.info("查询贫困户异常:" + e.getMessage());
			throw new ServiceException("查询贫困户异常！");
		}
	}

	 /**
     * 修改
     * @param record
     * @return
     * @throws Exception
     */
	@Override
	public boolean updateByPrimaryKeySelective(CustomerPovertyDO record) throws Exception {
		record.setUpdatedAt(System.currentTimeMillis());
		try {
			return customerPovertyDOMapper.updateByPrimaryKeySelective(record)==1;
		} catch (Exception e) {
			logger.info("修改贫困户异常:" + e.getMessage());
			throw new ServiceException("修改贫困户异常！");
		
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
	public List<CustomerPovertyDO> getList(Map<String, Object> map) throws Exception {
		if(!map.containsKey("pageNum") || !map.containsKey("pageSize")) {
			throw new ServiceException("查询参数异常！");
		}
		if (map.containsKey("idNumber")){
			map.put("idNumber", map.get("idNumber").toString().toUpperCase());
		}
		PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
		try {
			return customerPovertyDOMapper.getList(map);
		} catch (Exception e) {
			logger.info("查询贫困户异常:" + e.getMessage());
			throw new ServiceException("查询贫困户异常！");
		}
	}
	
	/**
     * 从excel导入
     * @param  list
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public Map<String,Object> importFromExcel(List<Map<String, Object>> list,Map<String,Object> paramMap) throws Exception {
		if(list.size()>5000) {
			 throw new ServiceException("单次上传最多5000条数据");
		}
		//遍历数据，取出身份证号
		List<String> idNumberList=new ArrayList<>();
		List<CustomerPovertyDO> povrrtyDOList=new ArrayList<>();
		long now = System.currentTimeMillis();   
		for(Map<String, Object> map:list) {
			if(map.get("3")==null||"".equals(map.get("3").toString())) {
				 throw new ServiceException("身份证号不能为空");
			}
			String idNumber=map.get("3").toString().toUpperCase();
			if(idNumberList.contains(idNumber)) {
				throw new ServiceException("文件中存在重复身份证号:"+idNumber);
			}
			
			if (StringUtil.isNotIdNumber(idNumber)) {
	            throw new ServiceException("身份证号:"+idNumber+"不是有效的中国居民身份证号！");
	        }
			if(map.get("2")==null||"".equals(map.get("2").toString())) {
				 throw new ServiceException("身份证号:"+idNumber+"的姓名为空");
			}else if(map.get("2").toString().length()>10) {
				 throw new ServiceException("身份证号:"+idNumber+"的姓名长度超过了10位");
			}
			String country="";
            if(map.get("1")!=null) {
            	country=map.get("1").toString();
            if(country.length()>10) {
           		 throw new ServiceException("身份证号:"+idNumber+"的县的长度超过了10位");
           	}
            }
            String relation="";
            if(map.get("4")!=null) {
            	relation=map.get("4").toString();
            if(relation.length()>10) {
               		 throw new ServiceException("身份证号:"+idNumber+"与户主关系超过了10位");
               	}
            }
            String overcome="";
            if(map.get("5")!=null) {
            	overcome=map.get("5").toString();
            	 if(overcome.length()>20) {
               		 throw new ServiceException("身份证号:"+idNumber+"的脱贫属性超过了20位");
               	}
            }
            String poverty="";
            if(map.get("6")!=null) {
            	poverty=map.get("6").toString();
            	if(poverty.length()>20) {
              		 throw new ServiceException("身份证号:"+idNumber+"的贫困户属性超过了20位");
              	}
            }
			idNumberList.add(idNumber);
			CustomerPovertyDO customerPovertyDO=new CustomerPovertyDO();
			customerPovertyDO.setCustomerName(map.get("2").toString()).setIdNumber(idNumber).setOrgCode(paramMap.get("orgCode").toString()).setOrgName(paramMap.get("orgName").toString())
			.setUserName(paramMap.get("name").toString()).setUserId(Long.parseLong(paramMap.get("userId").toString())).
			setCreatedAt(now).setUpdatedAt(now).setCounty(country).setRelation(relation).setOvercome(overcome).setPoverty(poverty);
			povrrtyDOList.add(customerPovertyDO);
		}
		//检查系统中是否有重复的贫困户身份证号
		Map<String,Object> map=new HashMap<>();
		map.put("idNumberList", idNumberList);
		List<CustomerPovertyDO> doubleList=customerPovertyDOMapper.getByIdNumbers(map);
		if(doubleList !=null && !doubleList.isEmpty()) {
			/*String doubleIdNumber="";
			for(CustomerPovertyDO cd:doubleList) {
				doubleIdNumber=doubleIdNumber+","+cd.getIdNumber();
			}*/
			throw new ServiceException("系统中已存在"+doubleList.get(0).getIdNumber());
		}
		List<CustomerTagRelationDO> toList=new ArrayList<>();
	    List<CustomerPovertyDO> saveReList=new ArrayList<>();
		for(int i=0;i<povrrtyDOList.size();i++) {
			// 校验客户的家庭标签是否有白名单
			String householdId = customerDOMapper.getHouseholdIdByIdNumber(povrrtyDOList.get(0).getIdNumber());
			List<CustomerTagRelationDO> tagList = customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(householdId);
			//作废评议信息，即 是否有效置为“0”
			try {
				surveyDOMapper.updateByHouseholdIdSelective(new SurveyDO().setHouseholdId(householdId).setIsValid("0"));
				// 同时客户信息-授信额度=0，有效调查次数=0，是否下结论=否
				customerDOMapper.updateByHouseholdIdSelective(
						new CustomerDO()
								.setHouseholdId(householdId)
								.setAmount(new BigDecimal("0"))
								.setIsConcluded("否")
								.setIsBorrower("否")
								.setValidTime(0));
			} catch (Exception e) {
				logger.info("评议信息作废异常:" + e.getMessage());
				throw new ServiceException("评议信息作废异常");
			}
			for(CustomerTagRelationDO tagDo:tagList) {
				//如果存在白名单记录需要删除
				if((long)5==tagDo.getTagId()) {
					//删除客户标签中的白名单
					try {
						CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(tagDo.getIdNumber()).setTagId((long)5);
						customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
						customerWhiteDOMapper.deleteByIdNumber(tagDo.getIdNumber());
					} catch (Exception e) {
						logger.info("删除白名单标签异常:" + e.getMessage());
						throw new ServiceException("删除白名单标签异常");
					}
					//查询面签信息
					CustomerInterviewDO interview = customerInterviewDOMapper.selectByIdNumber(tagDo.getIdNumber());
					// 调查结论作废，即 是否有效置为“0”
					try {
						conclusionDOMapper.updateByHouseholdIdSelective(new ConclusionDO().setHouseholdId(interview.getHouseholdId()).setIsValid("0"));
					} catch (Exception e) {
						logger.info("作废调查结论异常:" + e.getMessage());
						throw new ServiceException("作废调查结论异常");
					}

					// 删除面签信息，同时将面签数据移植到作废表中
					try {
						customerInterviewDOMapper.deleteByPrimaryKey(interview.getId());
						customerInterviewDOMapper.insertBanSelective(interview.setUpdatedAt(interview.getId())); // 用updateAt字段存储面签的ID
					} catch (Exception e) {
						logger.info("作废面签信息异常:" + e.getMessage());
						throw new ServiceException("作废面签信息异常");
					}
				}
			}
		    toList.add(new CustomerTagRelationDO().setIdNumber(povrrtyDOList.get(i).getIdNumber()).setTagId((long)3));
		    saveReList.add(povrrtyDOList.get(i));
		    if(saveReList.size()>=200 || i==povrrtyDOList.size()-1) {
			    try {
					asyncTaskPovertySave.executeAsyncTask(saveReList,customerPovertyDOMapper);
			    } catch (Exception e) {
					logger.info("批量导入贫困户异常" + e.getMessage());
					throw new ServiceException("批量导入贫困户异常！");
				}
			    saveReList=new ArrayList<>();
			    toList=new ArrayList<>();
		    }
		}
		Map<String,Object> returnMap=new HashMap<>();
		returnMap.put("success", povrrtyDOList.size());
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
		CustomerPovertyDO cpo=new CustomerPovertyDO();
		cpo.setOrgCode(orgCode);
		List<CustomerPovertyDO> list=customerPovertyDOMapper.getListByOrgCode(cpo);
		if(list.isEmpty()) {
			throw new ServiceException("未检索到相关法人机构的贫困户数据");	
		}
		List<String> idNumberList=new ArrayList<>();
		for(CustomerPovertyDO poverty:list) {
			idNumberList.add(poverty.getIdNumber());
		}
		Map<String,Object> map=new HashMap<>();
		map.put("idNumberList", idNumberList);
		map.put("tagId", 3);
		try {
			customerTagRelationDOMapper.deleteByIdNumbersAndTagId(map);
			customerPovertyDOMapper.deleteByOrdCode(cpo);
			
			return customerPovertyDOMapper.deleteByOrdCode(cpo)==1;
		} catch (Exception e) {
			logger.info("批量删除贫困户异常:" + e.getMessage());
			throw new ServiceException("批量删除贫困户异常");
		}
	}
	


}
