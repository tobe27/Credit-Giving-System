package com.example.web.controller;

import com.example.service.business.BusinessDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CREATED BY L.C.Y on 2018/12/20
 */
@RestController
@RequestMapping("/customer")
public class BusinessDOController {
    private final BusinessDOService businessDOService;

    @Autowired
    public BusinessDOController(BusinessDOService businessDOService) {
        this.businessDOService = businessDOService;
    }

    /**
     * 获取业务信息
     * @param idNumber
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/business/{idNumber}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable String idNumber) throws Exception {
        return new ResultBean().success(businessDOService.getBusinessByIdNumber(idNumber));
    }
}
