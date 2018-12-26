package com.example.service.business;

import com.example.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@Service
public class BusinessDOServiceImpl implements BusinessDOService {
    private final BusinessDOMapper businessDOMapper;

    @Autowired
    public BusinessDOServiceImpl(BusinessDOMapper businessDOMapper) {
        this.businessDOMapper = businessDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(BusinessDOServiceImpl.class);

    /**
     * 获取家庭业务信息
     *
     * @param idNumber
     * @return
     * @throws Exception
     */
    @Override
    public BusinessDO getBusinessByIdNumber(String idNumber) throws Exception {
        try {
            return businessDOMapper.getBusinessByIdNumber(idNumber);
        } catch (Exception e) {
            logger.info("获取业务信息异常:" + e.getMessage());
            throw new ServiceException("获取业务信息异常");
        }

    }
}
