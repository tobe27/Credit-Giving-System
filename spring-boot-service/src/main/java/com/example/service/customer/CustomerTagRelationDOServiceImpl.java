package com.example.service.customer;

import com.example.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-6
 */
@Service
public class CustomerTagRelationDOServiceImpl implements CustomerTagRelationDOService {
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;

    @Autowired
    public CustomerTagRelationDOServiceImpl(CustomerTagRelationDOMapper customerTagRelationDOMapper) {
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(CustomerTagRelationDOServiceImpl.class);

    /**
     * 删除标签
     * @param tagId
     * @return
     */
    @Override
    public int deleteByTagId(Long tagId) {
        try {
            return customerTagRelationDOMapper.deleteByTagId(tagId);
        } catch (Exception e) {
            logger.info("删除标签关联表异常:" + e.getMessage());
            throw new ServiceException("删除标签关联表异常");
        }
    }

    /**
     * 删除客户标签
     * @param idNumber
     * @return
     */
    @Override
    public int deleteByIdNumber(String idNumber) {
        try {
            return customerTagRelationDOMapper.deleteByIdNumber(idNumber);
        } catch (Exception e) {
            logger.info("删除客户标签异常:" + e.getMessage());
            throw new ServiceException("删除客户标签异常");
        }
    }

    /**
     * 添加
     * @param record
     * @return
     */
    @Override
    @Transactional
    public boolean insertSelective(CustomerTagRelationDO record) {
        // 插入之前先删除客户非系统标签
        try {
            customerTagRelationDOMapper.deleteByIdNumber(record.getIdNumber());
        } catch (Exception e) {
            logger.info("删除客户标签异常:" + e.getMessage());
            throw new ServiceException("删除客户标签异常");
        }

        // 标签名导入到实体类
        List<CustomerTagRelationDO> customerTagRelationDOList = new ArrayList<>();
        for (Long tagId : record.getTagIds()) {
            // 系统标签不可添加
            if(tagId <= 5) {
                throw new ServiceException("不可为客户添加系统级标签！");
            }
            CustomerTagRelationDO customerTagRelationDO = new CustomerTagRelationDO();
            customerTagRelationDO.setIdNumber(record.getIdNumber()).setTagId(tagId);
            customerTagRelationDOList.add(customerTagRelationDO);
        }

        // 循环插入到客户标签表
        int count = 0;
        try {
            for (CustomerTagRelationDO customerTagRelationDO : customerTagRelationDOList) {
                customerTagRelationDOMapper.insertSelective(customerTagRelationDO);
                count ++;
            }
        } catch (Exception e) {
            logger.info("添加客户标签异常:" + e.getMessage());
            throw new ServiceException("添加客户标签异常");
        }
        return count == customerTagRelationDOList.size();
    }

    /**
     * 查看
     * @param idNumber
     * @return
     */
    @Override
    public List<CustomerTagRelationDO> listByIdNumber(String idNumber) {
        try {
            return customerTagRelationDOMapper.listByIdNumber(idNumber);
        } catch (Exception e) {
            logger.info("查看客户标签异常:" + e.getMessage());
            throw new ServiceException("查看客户标签异常");
        }
    }
}
