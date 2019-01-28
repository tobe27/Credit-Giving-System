package com.example.service.grid;

import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOMapper;
import com.example.service.org.OrgDOServiceImpl;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 * @author Created by W.S.T on 2018-12-5
 */
@Service
public class GridInfoServiceImpl implements GridInfoService {
	private final GridInfoDOMapper  gridInfoDOMapper;
	private final CustomerDOMapper  customerDOMapper;
	private final GridReviewDOMapper gridReviewDOMapper;
	private final OrgDOMapper orgDOMapper;
	@Autowired
	public GridInfoServiceImpl(GridInfoDOMapper gridInfoDOMapper,CustomerDOMapper customerDOMapper,GridReviewDOMapper gridReviewDOMapper,OrgDOMapper orgDOMapper) {
		this.gridInfoDOMapper = gridInfoDOMapper;
		this.customerDOMapper=customerDOMapper;
		this.gridReviewDOMapper=gridReviewDOMapper;
		this.orgDOMapper=orgDOMapper;
	}

	private static Logger logger = LoggerFactory.getLogger(OrgDOServiceImpl.class);


	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean deleteByPrimaryKey(Long id) throws Exception {
		//网格中有关联的客户的话不能删除
		GridInfoDO gridInfoDO=gridInfoDOMapper.selectByPrimaryKey(id);
		if(gridInfoDO==null) {
			throw new ServiceException("未查询到相关网格！");
		}
		CustomerDO customerDO=new CustomerDO().setGridCode(gridInfoDO.getGridCode());
		if(customerDOMapper.listByHouseholdIdAndGridCode(customerDO).size()>0) {
			throw new ServiceException("该网格下有关联的客户信息！");
		}
		//删除网格关联的评议员
		GridReviewDO gro=new GridReviewDO().setGridCode(gridInfoDO.getGridCode());
		gridReviewDOMapper.deleteByGridCode(gro);
		try {
			return gridInfoDOMapper.deleteByPrimaryKey(id)==1;
		} catch (Exception e) {
			logger.info("删除网格异常:" + e.getMessage());
			throw new ServiceException("删除网格信息异常！");
		}
	}
	/**
	 * 新建
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean insertSelective(GridInfoDO record) throws Exception {
		//判断网格号是否重复
		if(gridInfoDOMapper.checkSameGridCode(record).size()>0) {
			throw new ServiceException("网格编号不能重复！");
		}
		long now=System.currentTimeMillis();
		//添加评议员信息
		if(record.getListReview()!=null&&record.getListReview().size()>0) {
			Set<String> reviewName=new HashSet<>();
			for(GridReviewDO gridReviewDO:record.getListReview() ) {
				reviewName.add(gridReviewDO.getGridReviewName());
			}
			if(reviewName.size()!=record.getListReview().size()) {
				throw new ServiceException("网格评议员姓名不能重复");
			}
			try {
				for(GridReviewDO gridReviewDO:record.getListReview() ) {
					gridReviewDO.setCreatedAt(now);
					gridReviewDO.setUpdatedAt(now);
					gridReviewDOMapper.insertSelective(gridReviewDO);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("添加评议员信息异常:" + e.getMessage());
				throw new ServiceException("添加评议员信息异常！");

			}
		}
		record.setCreatedAt(now);
		record.setUpdatedAt(now);
		try {
			return gridInfoDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			logger.info("添加网格信息异常:" + e.getMessage());
			throw new ServiceException("添加网格信息异常！");

		}

	}
	/**
	 * 详情
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	public GridInfoDO selectByPrimaryKey(Long id) throws Exception {
		try {
			return gridInfoDOMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.info("查询网格信息异常:" + e.getMessage());
			throw new ServiceException("查询网格信息异常！");
		}
	}
	/**
	 * 编辑
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean updateByPrimaryKeySelective(GridInfoDO record) throws Exception {
		if(record.getId()==null) {
			throw new ServiceException("网格id错误！");
		}
		//判断网格编号是否重复
		if(gridInfoDOMapper.checkSameGridCode(record).size()>0) {
			throw new ServiceException("网格编号不能重复！");
		}
		long now=System.currentTimeMillis();

		if(record.getListReview()!=null&&record.getListReview().size()>0) {
			Set<String> reviewName=new HashSet<>();
			for(GridReviewDO gridReviewDO:record.getListReview() ) {
				reviewName.add(gridReviewDO.getGridReviewName());
			}
			if(reviewName.size()!=record.getListReview().size()) {
				throw new ServiceException("网格评议员姓名不能重复");
			}
			//添加评议员信息
			try {
				//先删除原有的评议员信息
				GridReviewDO gridReview=new GridReviewDO().setGridCode(record.getGridCode());
				gridReviewDOMapper.deleteByGridCode(gridReview);
				for(GridReviewDO gridReviewDO:record.getListReview() ) {
					gridReviewDO.setCreatedAt(now);
					gridReviewDO.setUpdatedAt(now);
					gridReviewDOMapper.insertSelective(gridReviewDO);
				}
			} catch (Exception e) {
				logger.info("修改评议员信息异常:" + e.getMessage());
				throw new ServiceException("修改评议员信息异常！");

			}
		}

		// 是否变更calType计算方式isChange 0-不变，1-改变
		if (record.getIsChange() == 1) {
			if (customerDOMapper.countIsConcluded(record.getGridCode()) > 0) {
				throw new ServiceException("该网格已有下结论客户，不能更改额度计算规则");
			}
		} else {
			record.setCalType(null);
		}

		record.setCreatedAt(now);
		record.setUpdatedAt(now);
		try {
			return gridInfoDOMapper.updateByPrimaryKeySelective(record)==1;
		} catch (Exception e) {
			logger.info("修改网格信息异常:" + e.getMessage());
			throw new ServiceException("修改网格信息异常！");

		}
	}
	/**
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public List<GridInfoDO> list(Map<String,Object> map) throws Exception {
		if(!map.containsKey("roleId") || !map.containsKey("userId")|| !map.containsKey("orgCode")||!map.containsKey("pageNum")||!map.containsKey("pageSize")) {
			throw new ServiceException("查询参数异常！");
		}
		//登录人不是客户经理的话查询出所有能查看的机构号来查询出网格
		if(Long.parseLong(map.get("roleId").toString()) != 1) {
			Set<OrgDO> set=findChildOrg(new HashSet<OrgDO>(),map.get("orgCode").toString());
			List<String> orgCodeList=new ArrayList<>();
			for(OrgDO org:set) {
				orgCodeList.add(org.getOrgCode());
			}
			if(orgCodeList.isEmpty()) {
				throw new ServiceException("未检索到相关网格数据");
			}
			if (Long.parseLong(map.get("userId").toString()) != 0) {
				map.remove("userId");
			}
			map.put("orgCodeList", orgCodeList);
		}
		PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));

		return gridInfoDOMapper.getList(map);
	}

	/**
	 * 列出所有评议员姓名
	 *
	 * @param gridCode
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<String> listGridReviewName(String gridCode) throws Exception {
		List<String> stringList = null;
		try {
			stringList = gridInfoDOMapper.listGridReviewName(gridCode);
		} catch (Exception e) {
			logger.info("查询网格评议员异常:" + e.getMessage());
			throw new ServiceException("查询网格评议员异常");
		}

		if (stringList == null || stringList.size() < 3) {
			throw new ServiceException("请先添加至少3个评议员后再导出！");
		}

		return stringList;
	}

	/**
	 * 计算方式，1-平均，2-最小
	 *
	 * @param gridCode
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getCalTypeByGridCode(String gridCode) throws Exception {
		try {
			return gridInfoDOMapper.getCalTypeByGridCode(gridCode);
		} catch (Exception e) {
			logger.info("查询授信计算方式异常:" + e.getMessage());
			throw new ServiceException("查询授信计算方式异常");
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
	/**查询网格数据
	 * @param gridCode
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public Map<String, Object> getGridData(String gridCode) throws Exception {
		if("".equals(gridCode)||gridCode==null) {
			throw new ServiceException("查询参数错误");
		}
		Map<String,Object> map=new HashMap<>();
		map.put("gridCode", gridCode);
		//在籍户数
		String  residentFamliyCount=gridInfoDOMapper.getResidentFamliyCount(map);
		//在籍人数
		String 	residentPeopleCount=gridInfoDOMapper.getResidentPeopleCount(map);
		//黑名单户数（总户数-黑名单-灰名单-贫困户）
		String blackFamilyCoun=gridInfoDOMapper.getBlackFamilyCount(map);
		//贫困户户数
		String povertyFamilyCoun=gridInfoDOMapper.getPovertyFamilyCount(map);
		//灰名单户数
		String greyFamilyCoun=gridInfoDOMapper.getGreyFamilyCount(map);
		//白名单户数
		String whiteFamilyCoun=gridInfoDOMapper.getWhiteFamilyCount(map);
		//已授信户数
		String creditedFamilyCount=gridInfoDOMapper.getCreditedFamilyCount(map);
		//已授信额度
		String creditLimit=gridInfoDOMapper.getCreditLimit(map);
		//用信额度
		String usedCreditLimit=gridInfoDOMapper.getUsedCreditLimit(map);
		Map<String,Object> returnMap=new HashMap<>();
		//在籍户数
		returnMap.put("residentFamliyCount", residentFamliyCount);
		//在籍人数
		returnMap.put("residentPeopleCount", residentPeopleCount);
		//应授信户数
		returnMap.put("shouldCreditFamilyCount", Integer.parseInt(residentFamliyCount)-Integer.parseInt(blackFamilyCoun)-Integer.parseInt(povertyFamilyCoun)-Integer.parseInt(greyFamilyCoun));
		//预授信户数（ 白名单数） 
		returnMap.put("preCreditFamilyCount", whiteFamilyCoun);
		//已授信户数 （白名单已走过信贷系统通过的）
		returnMap.put("creditedFamilyCount", creditedFamilyCount);
		//未评议户数
		returnMap.put("yetCreditFamilyCount", Integer.parseInt(residentFamliyCount)-Integer.parseInt(blackFamilyCoun)-Integer.parseInt(povertyFamilyCoun)-Integer.parseInt(greyFamilyCoun)-Integer.parseInt(whiteFamilyCoun));
		//已授信额度
		returnMap.put("creditLimit", creditLimit);
		//用信额度 （白名单的用信检测的贷款余额
		returnMap.put("usedCreditLimit", usedCreditLimit);
		//灰名单户数
		returnMap.put("customerGreyFamilyCount", greyFamilyCoun);
		//贫困户户数
		returnMap.put("customerPovertyFamilyCount", povertyFamilyCoun);
		//黑名单户数
		returnMap.put("customerBlackFamilyCount", blackFamilyCoun);
		return returnMap;
	}


}
