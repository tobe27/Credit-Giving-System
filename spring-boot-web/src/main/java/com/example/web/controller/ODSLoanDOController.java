package com.example.web.controller;

import com.example.service.business.ODSLoanDO;
import com.example.service.business.ODSLoanDOService;
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
public class ODSLoanDOController {

    private final ODSLoanDOService odsLoanDOService;

    @Autowired
    public ODSLoanDOController(ODSLoanDOService odsLoanDOService) {
        this.odsLoanDOService = odsLoanDOService;
    }

    /**
     * 白蓝用信检测
     * @param odsLoanDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/loan/list", method = RequestMethod.GET)
    public ResultBean list(ODSLoanDO odsLoanDO, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<ODSLoanDO> pageInfo = new PageInfo<>(odsLoanDOService.listLoan(odsLoanDO, pageNum, pageSize));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }
}
