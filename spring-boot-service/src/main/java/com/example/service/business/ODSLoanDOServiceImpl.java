package com.example.service.business;

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
public class ODSLoanDOServiceImpl implements ODSLoanDOService {

    private final ODSLoanDOMapper odsLoanDOMapper;
    private final OrgDOMapper orgDOMapper;

    @Autowired
    public ODSLoanDOServiceImpl(ODSLoanDOMapper odsLoanDOMapper, OrgDOMapper orgDOMapper) {
        this.odsLoanDOMapper = odsLoanDOMapper;
        this.orgDOMapper = orgDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(ODSLoanDOServiceImpl.class);

    /**
     * 分页-白蓝用信检测
     *
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<ODSLoanDO> listLoan(ODSLoanDO record, Integer pageNum, Integer pageSize) throws Exception {
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
            return odsLoanDOMapper.listLoan(record);
        } catch (Exception e) {
            logger.info("用信检测查询异常:" + e.getMessage());
            throw new ServiceException("用信检测查询异常");
        }
    }
}
