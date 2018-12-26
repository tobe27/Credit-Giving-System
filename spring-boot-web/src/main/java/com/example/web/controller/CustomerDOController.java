package com.example.web.controller;

import com.example.common.validation.DraftGroup;
import com.example.common.validation.InsertGroup;
import com.example.service.customer.CustomerDO;
import com.example.service.customer.CustomerDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Created by L.C.Y on 2018-12-5
 */
@RestController
@RequestMapping("/customer")
public class CustomerDOController {
    private final
    CustomerDOService customerDOService;

    @Autowired
    public CustomerDOController(CustomerDOService customerDOService) {
        this.customerDOService = customerDOService;
    }

    /**
     * 新建
     * @param customerDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated(InsertGroup.class) CustomerDO customerDO) throws Exception {
        customerDOService.insertSelective(customerDO);
        return new ResultBean().success(customerDO.getId());
    }

    /**
     * 新建-暂存
     * @param customerDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public ResultBean insertDraft(@RequestBody @Validated(DraftGroup.class) CustomerDO customerDO) throws Exception {
        customerDOService.insertDraftSelective(customerDO);
        return new ResultBean().success(customerDO.getId());
    }

    /**
     * 编辑
     * @param customerDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.PUT)
    public ResultBean update(@RequestBody @Validated(InsertGroup.class)  CustomerDO customerDO) throws Exception {
        customerDOService.updateByPrimaryKeySelective(customerDO);
        return new ResultBean().success();
    }

    /**
     * 编辑
     * @param customerDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/draft/{id}", method = RequestMethod.PUT)
    public ResultBean updateDraft(@RequestBody @Validated(DraftGroup.class)  CustomerDO customerDO) throws Exception {
        customerDOService.updateDraftByPrimaryKeySelective(customerDO);
        return new ResultBean().success();
    }

    /**
     * 编辑状态
     * @param customerDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info/{id}/part", method = RequestMethod.PUT)
    public ResultBean updatePart(@RequestBody CustomerDO customerDO) throws Exception {
        customerDOService.updatePartById(new CustomerDO().setId(customerDO.getId()).setStatus(customerDO.getStatus()));
        return new ResultBean().success();
    }

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable Long id) throws Exception {
        return new ResultBean().success(customerDOService.getByPrimaryKey(id));
    }

    /**
     * 通过身份证查看详情
     * @param idNumber
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{idNumber}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable String idNumber) throws Exception {
        return new ResultBean().success(customerDOService.getByIdNumber(idNumber));
    }

    /**
     * 分页
     * @param customerDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info/list", method = RequestMethod.GET)
    public ResultBean listPage(CustomerDO customerDO, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<CustomerDO> pageInfo = new PageInfo<>(customerDOService.listCustomers(customerDO, pageNum, pageSize));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }

    @RequestMapping(value = "/info/list/map", method = RequestMethod.GET)
    public ResultBean listMapPage(CustomerDO customerDO) throws Exception {
        return new ResultBean().success(customerDOService.listMapList(customerDO));
    }

}
