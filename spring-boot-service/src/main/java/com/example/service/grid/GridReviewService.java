package com.example.service.grid;

import java.util.List;

/**
 * @author Created by W.S.T on 2018-12-6
 */
public interface GridReviewService {
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	boolean deleteByPrimaryKey(Long id)throws Exception;
	/**
	 * 新建
	 * @param record
	 * @return
	 * @throws Exception
	 */
	boolean insertSelective(GridReviewDO record)throws Exception;
	/**
	 * 编辑
	 * @param record
	 * @return
	 * @throws Exception
	 */
	boolean updateByPrimaryKeySelective(GridReviewDO record) throws Exception;


	/**
	 * 停用评议员
	 * @param record
	 * @throws Exception
	 */
	void banReview(GridReviewDO record) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 */
	List<GridReviewDO> list(GridReviewDO record) throws Exception;


}
