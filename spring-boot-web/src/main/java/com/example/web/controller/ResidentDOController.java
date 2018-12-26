package com.example.web.controller;

import com.example.service.resident.ResidentDO;
import com.example.service.resident.ResidentDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author CREATED BY L.C.Y on 2018/12/11
 */

@RestController
@RequestMapping("/customer")
public class ResidentDOController {
    private final ResidentDOService residentDOService;

    @Autowired
    public ResidentDOController(ResidentDOService residentDOService) {
        this.residentDOService = residentDOService;
    }

    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident/{id}", method = RequestMethod.DELETE)
    public ResultBean delete(@PathVariable Long id) throws Exception {
        residentDOService.deleteByPrimaryKey(id);
        return new ResultBean().success();
    }

    /**
     * 新建
     * @param residentDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated ResidentDO residentDO) throws Exception {
        residentDOService.insertSelective(residentDO);
        return new ResultBean().success();
    }

    /**
     * 编辑
     * @param residentDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident/{id}", method = RequestMethod.PUT)
    public ResultBean update(@RequestBody @Validated ResidentDO residentDO) throws Exception {
        residentDOService.updateByPrimaryKeySelective(residentDO);
        return new ResultBean().success();
    }

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident/{id}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable Long id) throws Exception {
        return new ResultBean().success(residentDOService.getByPrimaryKey(id));
    }

    /**
     * 分页
     * @param residentDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident/list", method = RequestMethod.GET)
    public ResultBean listPage(ResidentDO residentDO, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<ResidentDO> pageInfo = new PageInfo<>(residentDOService.listByNameOrIdNumberOrHouseholdId(residentDO, pageNum, pageSize));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 编辑户籍、客户、调查表、标签关联表的身份证和户号
     * @param residentDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resident/{oldIdNumber}/part", method = RequestMethod.PUT)
    public ResultBean updateIdNumberAndHouseholdId(@RequestBody ResidentDO residentDO) throws Exception {
        residentDOService.updateIdNumberAndHouseholdIdAndGridCodeByOldIdNumber(residentDO);
        return new ResultBean().success();
    }



}
