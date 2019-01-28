package com.example.service.blue;

import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOMapper;
import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/21
 */
@Service
public class BlueDOServiceImpl implements BlueDOService {
    private final BlueDOMapper blueDOMapper;
    private final OrgDOMapper orgDOMapper;
    private final CustomerTagRelationDOMapper customerTagRelationDOMapper;

    @Autowired
    public BlueDOServiceImpl(BlueDOMapper blueDOMapper, OrgDOMapper orgDOMapper,CustomerTagRelationDOMapper customerTagRelationDOMapper) {
        this.blueDOMapper = blueDOMapper;
        this.orgDOMapper = orgDOMapper;
        this.customerTagRelationDOMapper = customerTagRelationDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(BlueDOServiceImpl.class);

    /**
     * 查询列表
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public List<BlueDO> listBlueByUserOrOrgCode(BlueDO record, Integer pageNum, Integer pageSize) throws Exception {
        try {
            if(record.getIdNumber()!=null){
                record.setIdNumber(record.getIdNumber().toUpperCase());
            }
            PageHelper.startPage(pageNum, pageSize);
            return blueDOMapper.getList(record);
        } catch (Exception e) {
            logger.info("分页查询蓝名单异常:" + e.getMessage());
            throw new ServiceException("分页查询蓝名单异常");
        }

    }
    /**
     * 删除
     * @param map
     * @return
     * @throws Exception
     */
    @Transactional
    @Override
    public boolean deleteBlue(Map<String,String> map) throws Exception {
        if(!map.containsKey("idNumber")) {
            throw new ServiceException("请求参数异常！");
        } else {
            map.put("idNumber",map.get("idNumber").toUpperCase());
        }
        if(!map.containsKey("orgCode")) {
            throw new ServiceException("请求参数异常！");
        }
        OrgDO org=orgDOMapper.selectByIdNumberInGreyAndBlue(map.get("idNumber"),4);
        if (org !=null && !map.get("orgCode").equals(org.getOrgCode())){
            throw new ServiceException("对不起，这个客户所在的法人机构为："+ org.getOrgName() +",您不能删除！");
        }
        CustomerTagRelationDO tag=new CustomerTagRelationDO().setIdNumber(map.get("idNumber")).setTagId((long)4);
        customerTagRelationDOMapper.deleteByIdNumberAndTagId(tag);
        try {
            return blueDOMapper.deleteByIdNumber(map.get("idNumber"))>=1;
        } catch (Exception e) {
            logger.info("删除灰名单标签异常:" + e.getMessage());
            throw new ServiceException("删除灰名单标签异常");
        }
    }

    /**
     * 新建
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    public boolean insertSelective(BlueDO record) throws Exception {
        return false;
    }
}
