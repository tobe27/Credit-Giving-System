package com.example.service.customer;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-6
 */
public interface CustomerTagRelationDOService {

    int deleteByTagId(Long tagId);

    int deleteByIdNumber(String idNumber);

    boolean insertSelective(CustomerTagRelationDO record);

    List<CustomerTagRelationDO> listByIdNumber(String idNumber);
}
