package com.example.web.controller;

import com.example.service.ods.ODSLimitDO;
import com.example.service.ods.ODSLimitDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CREATED BY L.C.Y on 2018/12/26
 */
@RestController
@RequestMapping("/customer")
public class ODSLimitDOController {

    private final ODSLimitDOService odsLimitDOService;

    @Autowired
    public ODSLimitDOController(ODSLimitDOService odsLimitDOService) {
        this.odsLimitDOService = odsLimitDOService;
    }

    /**
     * 白蓝名单授信检测
     * @param odsLimitDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/limit/list", method = RequestMethod.GET)
    public ResultBean list(ODSLimitDO odsLimitDO, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<ODSLimitDO> pageInfo = new PageInfo<>(odsLimitDOService.listGivings(odsLimitDO, pageNum, pageSize));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }
}
