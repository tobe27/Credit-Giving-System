package com.example.service.interview;

import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-15
 */
public interface CustomerInterviewDOService {
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	boolean deleteByPrimaryKey(Long id)throws Exception;
	/**
	 * 新增
	 * @param record
	 * @return
	 * @throws Exception
	 */
	boolean insertSelective(CustomerInterviewDO record)throws Exception;
	/**
	 * 查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	CustomerInterviewDO selectByPrimaryKey(Long id)throws Exception;
	/**
	 * 修改
	 * @param record
	 * @return
	 * @throws Exception
	 */
	boolean updateByPrimaryKeySelective(CustomerInterviewDO record)throws Exception;

	/**
	 * 修改借款主体
	 * @param record
	 * @return
	 * @throws Exception
	 */
	boolean modifyBorrower(CustomerInterviewDO record) throws Exception;

	/**
	 * 作废操作
	 * (1)客户从白名单中剔除，进入选择的名单库;
	 * (2)评议员评议数据及客户经理评议结论作废;
	 * (3)“面签”或“通过”的数据进入到“作废”。
	 * (4)作废后，退出对应名单，可以重新发起评议。
	 * @param
	 * @throws Exception
	 */
	void banInterview(CustomerInterviewDO record) throws Exception;


	/**
	 * 分页
	 * @param
	 * @return
	 * @throws Exception
	 */
	List<CustomerInterviewDO> getList(Map<String,Object> map) throws Exception;


	/**
	 * 列表
	 * @param
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getListNoPage(Map<String,Object> map) throws Exception;

	/**
	 * 作废分页
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<CustomerInterviewDO> listBanInterview(Map<String,Object> map) throws Exception;

}
