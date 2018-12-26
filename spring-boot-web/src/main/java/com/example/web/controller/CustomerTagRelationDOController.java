package com.example.web.controller;

import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.customer.CustomerTagRelationDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Created by L.C.Y on 2018-12-6
 */
@RestController
@RequestMapping("/customer")
public class CustomerTagRelationDOController {
    private final CustomerTagRelationDOService customerTagRelationDOService;

    @Autowired
    public CustomerTagRelationDOController(CustomerTagRelationDOService customerTagRelationDOService) {
        this.customerTagRelationDOService = customerTagRelationDOService;
    }

    /**
     * 添加客户标签
     * @param customerTagRelationDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated CustomerTagRelationDO customerTagRelationDO) throws Exception {
        customerTagRelationDOService.insertSelective(customerTagRelationDO);
        return new ResultBean().success();
    }

    /**
     * 查看客户标签
     * @param idNumber
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag/{idNumber}", method = RequestMethod.GET)
    public ResultBean list(@PathVariable String idNumber) throws Exception {
        return new ResultBean().success(customerTagRelationDOService.listByIdNumber(idNumber));
    }
}
