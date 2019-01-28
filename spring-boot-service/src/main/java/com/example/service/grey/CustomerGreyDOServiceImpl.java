package com.example.service.grey;

import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.grid.GridInfoDOMapper;
import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOMapper;
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
@Service
public class CustomerGreyDOServiceImpl implements CustomerGreyDOService {
	private final CustomerDOMapper customerDOMapper;
	private final CustomerGreyDOMapper customerGeryDOMapper;
	private final CustomerTagRelationDOMapper customerTagRelationDOMapper;
	private final GridInfoDOMapper  gridInfoDOMapper;
	private final OrgDOMapper orgDOMapper;
	private final CustomerWhiteDOMapper customerWhiteDOMapper;
    private final SurveyDOMapper surveyDOMapper;
	@Autowired
	public CustomerGreyDOServiceImpl(CustomerGreyDOMapper customerGeryDOMapper, CustomerTagRelationDOMapper customerTagRelationDOMapper , OrgDOMapper orgDOMapper, GridInfoDOMapper  gridInfoDOMapper, CustomerWhiteDOMapper customerWhiteDOMapper,CustomerDOMapper customerDOMapper,SurveyDOMapper surveyDOMapper) {
		this.customerGeryDOMapper = customerGeryDOMapper;
		this.customerTagRelationDOMapper=customerTagRelationDOMapper;
		this.orgDOMapper=orgDOMapper;
		this.gridInfoDOMapper=gridInfoDOMapper;
		this.customerWhiteDOMapper=customerWhiteDOMapper;
		this.customerDOMapper = customerDOMapper;
        this.surveyDOMapper = surveyDOMapper;
	}
	private static Logger logger = LoggerFactory.getLogger(CustomerGreyDOServiceImpl.class);
	/**
	 * 删除
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean deleteGrey(Map<String,String> map) throws Exception {
		if(!map.containsKey("idNumber")) {
			throw new ServiceException("请求参数异常！");
		} else {
			map.put("idNumber",map.get("idNumber").toUpperCase());
		}
		if(!map.containsKey("orgCode")) {
			throw new ServiceException("请求参数异常！");
		}
		OrgDO org=orgDOMapper.selectByIdNumberInGreyAndBlue(map.get("idNumber"),2);
		if (org !=null && !map.get("orgCode").equals(org.getOrgCode())){
			throw new ServiceException("对不起，这个客户所在的法人机构为："+ org.getOrgName() +",您不能删除！");
		}
		List<String> list = new ArrayList<>();
		list.add(map.get("idNumber"));
		Map<String,Object> param = new HashMap();
		param.put("idNumberList", list);
		List<String> idNumbers = customerDOMapper.getAllFamilyIdNumbersByIdNumbers(param);
		try {
			for (String idNumber: idNumbers) {
				CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(idNumber).setTagId((long)2);
				customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
				tag=new CustomerTagRelationDO().setIdNumber(idNumber).setTagId((long)6);
				customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
				customerGeryDOMapper.deleteByIdNumber(idNumber);
			}
            String householdId = customerDOMapper.getHouseholdIdByIdNumber(map.get("idNumber"));
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
			return true;
		} catch (Exception e) {
			logger.info("删除灰名单标签异常:" + e.getMessage());
			throw new ServiceException("删除灰名单标签异常");
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
	public boolean insertSelective(CustomerGreyDO record) throws Exception {
		long now=System.currentTimeMillis();
		record.setCreatedAt(now);
		record.setUpdatedAt(now);
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
		//删除原有的灰名单数据
		Map<String,Object> map=new HashMap<>();
		map.put("idNumber", record.getIdNumber());
		List<CustomerGreyDO> listGrey=customerGeryDOMapper.getByIdNumber(map);
		for(CustomerGreyDO go:listGrey) {
			customerGeryDOMapper.deleteByPrimaryKey(go.getId());
		}
		
		
		CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(record.getIdNumber()).setTagId((long)2);
		
		try {
			customerTagRelationDOMapper.insertSelective(tag);
		} catch (Exception e1) {
			logger.info("创建灰名单标签异常:" + e1.getMessage());
			throw new ServiceException("创建灰名单标签异常");
		}
		try {
			return customerGeryDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			logger.info("创建灰名单异常:" + e.getMessage());
			throw new ServiceException("创建灰名单异常！");
		}
	}
	/**
	 * 查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	public CustomerGreyDO selectByPrimaryKey(Long id) throws Exception {
		try {
			return customerGeryDOMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.info("查询灰名单异常:" + e.getMessage());
			throw new ServiceException("查询灰名单异常！");
		}
	}
	/**
	 * 修改
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateByPrimaryKeySelective(CustomerGreyDO record) throws Exception {
		record.setUpdatedAt(System.currentTimeMillis());
		try {
			return customerGeryDOMapper.updateByPrimaryKeySelective(record)==1;
		} catch (Exception e) {
			logger.info("修改灰名单异常:" + e.getMessage());
			throw new ServiceException("修改灰名单异常！");

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
	public List<CustomerGreyDO> getList(Map<String, Object> map) throws Exception {
		if(!map.containsKey("pageNum")||!map.containsKey("pageSize")) {
			throw new ServiceException("查询参数异常！");
		}
		if (map.containsKey("idNumber")){
			map.put("idNumber", map.get("idNumber").toString().toUpperCase());
		}
		PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
		try {
			return customerGeryDOMapper.getList(map);
		} catch (Exception e) {
			logger.info("查询灰名单异常:" + e.getMessage());
			throw new ServiceException("查询灰名单异常！");
		}
	}

}
