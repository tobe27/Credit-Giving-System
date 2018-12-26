package com.example.service.blue;

import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDOMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/21
 */
@Service
public class BlueDOServiceImpl implements BlueDOService {
    private final BlueDOMapper blueDOMapper;
    private final OrgDOMapper orgDOMapper;

    @Autowired
    public BlueDOServiceImpl(BlueDOMapper blueDOMapper, OrgDOMapper orgDOMapper) {
        this.blueDOMapper = blueDOMapper;
        this.orgDOMapper = orgDOMapper;
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
            return blueDOMapper.listBlueByUserOrOrgCode(record);
        } catch (Exception e) {
            logger.info("分页查询蓝名单异常:" + e.getMessage());
            throw new ServiceException("分页查询蓝名单异常");
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
