package com.example.service.customer;

import java.util.List;
import java.util.Map;

/**
 * @author Created by L.C.Y on 2018-12-5
 */
public interface CustomerDOService {
    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteByPrimaryKey(Long id) throws Exception;

    /**
     * 删除不是借款主体客户
     * 同时删除户籍信息
     * @param record
     * @return
     */
    boolean deleteNotBorrowerByIdAndIdNumber(CustomerDO record) throws Exception;

    /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertSelective(CustomerDO record) throws Exception;

    /**
     * 新建暂存
     * @param record
     * @return
     * @throws Exception
     */
    boolean insertDraftSelective(CustomerDO record) throws Exception;

    /**
     * 查看
     * @param id
     * @return
     * @throws Exception
     */
    CustomerDO getByPrimaryKey(Long id) throws Exception;

    /**
     * 通过身份证查看客户
     * @param idNumber
     * @return
     * @throws Exception
     */
    CustomerDO getByIdNumber(String idNumber) throws Exception;

    /**
     * 分页
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     * @param record 客户
     * @return
     * @throws Exception
     */
    List<CustomerDO> listCustomers(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 分页查询无标签客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     * @param record 客户
     * @return
     * @throws Exception
     */
    List<CustomerDO> listNoTagCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 分页查询有标签客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<CustomerDO> listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 导出不能评议客户到Excel
     * @param record
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listHaveTagCustomersByGridCodeAndRelationshipAndTagIdAndNameAndIdNumber(CustomerDO record) throws Exception;

    /**
     * 分页查询已评议客户，且有白名单客户
     * 角色 == 1， 客户经理， 只能查看自己网格的客户
     * 角色 ！= 1， 非客户经理， 可以查看本机构及子机构所有客户
     * 查询条件：
     * 角色ID，用户ID， 机构代码
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<CustomerDO> listYetReviewCustomersByGridCodeAndRelationshipAndValidTimeAndNameAndIdNumber(CustomerDO record, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 已评议客户导出-整村授信评议预授信表
     * @param record
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listSurveyCustomersByGridCodeAndRelationshipAndUserId(CustomerDO record, List<String> senatorList, int calType) throws Exception;


    /**
     * 已下结论借款主体信息，返回导出Excel数据
     * @param record
     * @return
     * @throws Exception
     */
    Map<String, Object> borrowerSurveyList(CustomerDO record) throws Exception;


    /**
     * 返回导出Excel数据
     * @param record
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listMapList(CustomerDO record) throws Exception;


    /**
     * 变更客户和户籍的与户主关系
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateRelationshipByIdNumberSelective(CustomerDO record) throws Exception;

    /**
     * 编辑
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateByPrimaryKeySelective(CustomerDO record) throws Exception;

    /**
     * 编辑暂存
     * @param record
     * @return
     * @throws Exception
     */
    boolean updateDraftByPrimaryKeySelective(CustomerDO record) throws Exception;

    /**
     * 编辑部分字段
     * @param record
     * @return
     * @throws Exception
     */
    boolean updatePartById(CustomerDO record) throws Exception;
}
