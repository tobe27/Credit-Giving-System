package com.example.service.ods;

import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDOMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/26
 */
@Service
public class ODSLimitDOServiceImpl implements ODSLimitDOService {
    private final ODSLimitDOMapper odsLimitDOMapper;
    private final OrgDOMapper orgDOMapper;

    @Autowired
    public ODSLimitDOServiceImpl(ODSLimitDOMapper odsLimitDOMapper, OrgDOMapper orgDOMapper) {
        this.odsLimitDOMapper = odsLimitDOMapper;
        this.orgDOMapper = orgDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(ODSLimitDOServiceImpl.class);

    /**
     * 白蓝名单授信检测
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<ODSLimitDO> listGivings(ODSLimitDO record, Integer pageNum, Integer pageSize) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                logger.info("查询下级机构异常:" + e.getMessage());
                throw new ServiceException("查询下级机构异常");
            }
        }
        try {
            PageHelper.startPage(pageNum, pageSize);
            return odsLimitDOMapper.listGivings(record);
        } catch (Exception e) {
            logger.info("授信检测查询异常:" + e.getMessage());
            throw new ServiceException("授信检测查询异常");
        }
    }
}
