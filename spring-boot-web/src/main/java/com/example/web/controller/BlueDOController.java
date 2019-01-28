package com.example.web.controller;

import com.example.service.blue.BlueDO;
import com.example.service.blue.BlueDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2018/12/21
 */
@RestController
@RequestMapping("/customer")
public class BlueDOController {
    private final BlueDOService blueDOService;

    @Autowired
    public BlueDOController(BlueDOService blueDOService) {
        this.blueDOService = blueDOService;
    }

    /**
     * 分页查询蓝名单
     * @param blueDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/blue/list", method = RequestMethod.GET)
    public ResultBean list(BlueDO blueDO, Integer pageNum, Integer pageSize) throws Exception {
        PageInfo<BlueDO> pageInfo = new PageInfo<>(blueDOService.listBlueByUserOrOrgCode(blueDO, pageNum, pageSize));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 删除
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/blue/delete", method = RequestMethod.DELETE)
    public ResultBean delete(@RequestParam Map<String,String> map) throws Exception {
        System.out.println(map);
        blueDOService.deleteBlue(map);
        return new ResultBean().success();
    }
}
