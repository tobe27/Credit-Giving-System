package com.example.service.business;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/26
 */
public interface ODSLoanDOService {

    /**
     * 分页-白蓝用信检测
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<ODSLoanDO> listLoan(ODSLoanDO record, Integer pageNum, Integer pageSize) throws Exception;
}
