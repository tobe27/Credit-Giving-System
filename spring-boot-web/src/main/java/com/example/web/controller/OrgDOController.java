package com.example.web.controller;

import com.example.service.org.OrgDO;
import com.example.service.org.OrgDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-4
 */
@RestController
@RequestMapping("/admin")
public class OrgDOController {
    private final
    OrgDOService orgDOService;

    @Autowired
    public OrgDOController(OrgDOService orgDOService) {
        this.orgDOService = orgDOService;
    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/org/{orgCode}", method = RequestMethod.DELETE)
    public ResultBean delete(@PathVariable String orgCode) throws Exception {
        orgDOService.deleteByOrgCode(orgCode);
        return new ResultBean().success();
    }

    /**
     * 新建机构
     * @param orgDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated OrgDO orgDO) throws Exception {
        orgDOService.insertSelective(orgDO);
        return new ResultBean().success();
    }

    /**
     * 编辑机构
     * @param orgDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/org/{id}", method = RequestMethod.PUT)
    public ResultBean update(@RequestBody @Validated OrgDO orgDO) throws Exception {
        orgDOService.updateByPrimaryKeySelective(orgDO);
        return new ResultBean().success();
    }

    /**
     * 查看详情
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/org/{id}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable Long id) throws Exception {
        return new ResultBean().success(orgDOService.getByPrimaryKey(id));
    }

    /**
     * 查询当前机构及子机构,树形机构展示
     * @param orgCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/org/list", method = RequestMethod.GET)
    public ResultBean get(String orgCode, String status) throws Exception {
        List<OrgDO> orgDOList = orgDOService.listAllByStatus(new OrgDO().setStatus(status));
        return new ResultBean().success(orgDOService.treeOrgDOList(orgCode, orgDOList));
    }
}
