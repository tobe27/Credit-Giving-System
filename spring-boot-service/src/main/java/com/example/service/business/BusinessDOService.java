package com.example.service.business;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
public interface BusinessDOService {
    /**
     * 获取家庭业务信息
     * @param idNumber
     * @return
     * @throws Exception
     */
    BusinessDO getBusinessByIdNumber(String idNumber) throws Exception;
}
