package com.example.web.controller;

import com.example.service.conclusion.ConclusionDO;
import com.example.service.conclusion.ConclusionDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author CREATED BY L.C.Y on 2018/12/12
 */

@RestController
@RequestMapping("/customer")
public class ConclusionDOController {
    private final ConclusionDOService conclusionDOService;

    @Autowired
    public ConclusionDOController(ConclusionDOService conclusionDOService) {
        this.conclusionDOService = conclusionDOService;
    }

    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/conclusion/{id}", method = RequestMethod.DELETE)
    public ResultBean delete(@PathVariable Long id) throws Exception {
        conclusionDOService.deleteByPrimaryKey(id);
        return new ResultBean().success();
    }

    /**
     * 结论汇总信息
     * @param idNumber
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/conclusion/info", method = RequestMethod.GET)
    public ResultBean collect(String idNumber, String isValid) throws Exception {
        return new ResultBean().success(conclusionDOService.getConclusionInfo(idNumber, isValid));
    }

    /**
     * 添加结论
     * @param conclusionDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/conclusion", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated ConclusionDO conclusionDO) throws Exception {
        conclusionDOService.insertSelective(conclusionDO);
        return new ResultBean().success();
    }

    /**
     * 结论列表
     * @param householdId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/conclusion/list", method = RequestMethod.GET)
    public ResultBean list(String householdId) throws Exception {
        return new ResultBean().success(conclusionDOService.listByHouseholdId(householdId));
    }
}
