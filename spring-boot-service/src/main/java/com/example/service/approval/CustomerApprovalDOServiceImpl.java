package com.example.service.approval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.service.customer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.util.StringUtil;
import com.example.service.black.CustomerBlackDO;
import com.example.service.black.CustomerBlackDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.interview.CustomerInterviewDO;
import com.example.service.interview.CustomerInterviewDOMapper;
import com.example.service.poverty.CustomerPovertyDO;
import com.example.service.resident.ResidentDO;
import com.example.service.white.CustomerWhiteDOMapper;

/**
 * @author Created by W.S.T on 2018-12-16
 */
@Service
public class CustomerApprovalDOServiceImpl implements CustomerApprovalDOService {
	private final CustomerApprovalDOMapper customerApprovalDOMapper;
	private final CustomerInterviewDOMapper customerInterviewDOMapper;
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final CustomerDOMapper customerDOMapper;
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
	private final CustomerBlackDOMapper  customerBlackDOMapper;
	@Autowired
	public CustomerApprovalDOServiceImpl(CustomerApprovalDOMapper customerApprovalDOMapper,CustomerInterviewDOMapper customerInterviewDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper,CustomerDOMapper customerDOMapper,CustomerWhiteDOMapper customerWhiteDOMapper,CustomerBlackDOMapper  customerBlackDOMapper) {
		this.customerApprovalDOMapper=customerApprovalDOMapper;
        this.customerInterviewDOMapper=customerInterviewDOMapper;
        this.customerTagRelationDOMapper=customerTagRelationDOMapper;
        this.customerDOMapper=customerDOMapper;
        this.customerWhiteDOMapper=customerWhiteDOMapper;
        this.customerBlackDOMapper=customerBlackDOMapper;
	}
	private static Logger logger = LoggerFactory.getLogger(CustomerApprovalDOServiceImpl.class);
	 /**
     * 新增
     * @param
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public int insertSelective(List<CustomerApprovalDO> recordList) throws Exception {
		if (recordList.isEmpty()) {
			throw new ServiceException("提交的记录为空");
		}
		long now=System.currentTimeMillis();
		//先查询出提交的审核中有没有未完善的或者未提交影像资料的数据
		List<Long> interviewIdList=new ArrayList<>();
		List<String> idNumberList=new ArrayList<>();
		for(CustomerApprovalDO ao:recordList) {
			interviewIdList.add(ao.getInterviewId());
			idNumberList.add(ao.getIdNumber());
			if("".equals(ao.getIdNumber())||"".equals(ao.getApprovalNode())||null==(ao.getApprovalRoleId())||"".equals(ao.getApprovalUserName())) {
				throw new ServiceException("参数有误");
			}
		}
		Map<String,Object> map=new HashMap<>();
		
		map.put("idList", interviewIdList);
		List<CustomerInterviewDO> list=customerInterviewDOMapper.getListByIds(map);
		if(list==null||list.isEmpty()) {
			throw new ServiceException("未检索到有效面签面谈信息");
		}
		for(CustomerInterviewDO cio:list) {
			List<CustomerTagRelationDO> listTags=customerTagRelationDOMapper.listCustomerTagRelationsByHouseholdId(cio.getHouseholdId());
		    for(CustomerTagRelationDO tag:listTags) {
		    	if(tag.getTagId()==1) {
					throw new ServiceException("客户："+cio.getCustomerName()+"家庭存在黑名单记录");
				}
				if(tag.getTagId()==2) {
					throw new ServiceException("客户："+cio.getCustomerName()+"家庭存在灰名单记录");
				}
				if(tag.getTagId()==3) {
					throw new ServiceException("客户："+cio.getCustomerName()+"家庭存在贫困户记录");
				}
		    }
		    //如果是蓝名单，必须上传完贷前调查影像资料
		    if("2".equals(cio.getType())&&(cio.getAttachFlag().endsWith("0"))) {
		    	throw new ServiceException("蓝名单客户："+cio.getCustomerName()+"提交前必须上传贷前调查影像资料");
		    }
		}
		
		String status="";
		//开始进行审核操作，如果是客户经理进行的提交
		if(1==recordList.get(0).getApprovalRoleId()) {
			for(CustomerInterviewDO io:list) {
//				if("1".equals(io.getStatus())) {
//					throw new ServiceException("客户："+io.getCustomerName()+"未完善面签面谈信息，请先编辑保存再提交");
//				}
//				if(io.getAttachFlag().startsWith("0")) {
//					throw new ServiceException("客户："+io.getCustomerName()+"未上传授信申请表，请先上传再提交");
//				}
				if(!"0".equals(io.getApprovalStatus())&&!"5".equals(io.getApprovalStatus())) {
					throw new ServiceException("客户："+io.getCustomerName()+"不是待办理的面谈面签，请确认后再操作，或咨询管理员");
				}
			}
			//更新面签面谈记录的审批状态
			status="1";
		}
		//如果是支行长进行的提交
		List<String> listString=new ArrayList<>();
		
		if(2==recordList.get(0).getApprovalRoleId()) {
			for(CustomerInterviewDO io:list) {
				listString.add(io.getIdNumber());
//				if("1".equals(io.getStatus())) {
//					throw new ServiceException("客户："+io.getCustomerName()+"未完善面签面谈信息，请先编辑保存再提交");
//				}
//				if(io.getAttachFlag().startsWith("0")) {
//					throw new ServiceException("客户："+io.getCustomerName()+"未上传授信申请表，请先上传再提交");
//				}
				if(!"1".equals(io.getApprovalStatus())) {
					throw new ServiceException("客户："+io.getCustomerName()+"不是审核中的面谈面签，请确认后再操作，或咨询管理员");
				}
			}
			//更新面签面谈记录的审批状态
			if("0".equals(recordList.get(0).getApprovalResult())) {
				status="2";
				// 如果是审批通过，则更新客户列表中的授信额度
				for(CustomerInterviewDO customerInterviewDO : list) {
					logger.info("面签信息：" + customerInterviewDO);
					CustomerDO customerDO = new CustomerDO();
					customerDO.setId(customerInterviewDO.getCustomerId())
							.setInterviewId(customerInterviewDO.getId())
							.setAmount(new BigDecimal(customerInterviewDO.getAppliedSum()));

					customerDOMapper.updateByPrimaryKeySelective(customerDO);
				}
			}else if("1".equals(recordList.get(0).getApprovalResult())) {
				status="5";
			}else {
				status="3";
				//否决的客户要删除白名单 加入黑名单
				Map<String,Object> whiteMap=new HashMap<>();
				whiteMap.put("idNumberList", listString);
				//删除白名单
				customerWhiteDOMapper.deleteByIdNumbers(whiteMap);
				//删除白名单标签
				whiteMap.put("tagId", 5);
				customerTagRelationDOMapper.deleteByIdNumbersAndTagId(whiteMap);
				//加入黑名单
				for(CustomerInterviewDO io:list) {
					CustomerBlackDO cbo=new CustomerBlackDO();
					cbo.setCustomerName(io.getCustomerName())
					.setIdNumber(io.getIdNumber())
					.setCustomerId(io.getCustomerId())
					.setGridCode(io.getGridCode())
					.setGridName(io.getGridName())
					.setOrgCode(io.getOrgCode())
					.setOrgName(io.getOrgName())
					.setPhone(io.getPhone())
					.setReason(recordList.get(0).getApprovalOpinion())
					.setCreatedAt(now)
					.setUpdatedAt(now);
					//加入黑名单标签
					customerBlackDOMapper.insertSelective(cbo);
					CustomerTagRelationDO ctrd=new CustomerTagRelationDO();
					ctrd.setIdNumber(io.getIdNumber())
					.setTagId((long)1);
					customerTagRelationDOMapper.insertSelective(ctrd);
				}
			}
		}

		if(!"".equals(status)) {
			for(int i=0;i<recordList.size();i++) {
				CustomerInterviewDO customerInterviewDO = list.get(i);

				recordList.get(i).setCreatedAt(now).setUpdatedAt(now);
				//插入审批记录表
				customerApprovalDOMapper.insertSelective(recordList.get(i));

				customerInterviewDO.setApprovalStatus(status).setUpdatedAt(now);
				//更新面谈面签
				customerInterviewDOMapper.updateByPrimaryKeySelective(customerInterviewDO);
			}
		}
		return recordList.size();
	}
	/**
     * 查询某条面谈面签数据的审批记录
     * @param
     * @return
     * @throws Exception
     */
	@Override
	public List<CustomerApprovalDO> getListByInterviewId(Long interviewId) throws Exception {
		CustomerApprovalDO customerApprovalDO=new CustomerApprovalDO().setInterviewId(interviewId);
		try {
			return customerApprovalDOMapper.getListByInterviewId(customerApprovalDO);
		} catch (Exception e) {
			logger.info("查询审批记录异常:" + e.getMessage());
			throw new ServiceException("查询审批记录异常");
		}
	}

}
