package com.example.service.ods;

import java.util.List;

/**
 * @author CREATED BY L.C.Y on 2018/12/26
 */
public interface ODSLimitDOService {
    /**
     * 白蓝名单授信检测
     * @param record
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<ODSLimitDO> listGivings(ODSLimitDO record, Integer pageNum, Integer pageSize) throws Exception;
}
