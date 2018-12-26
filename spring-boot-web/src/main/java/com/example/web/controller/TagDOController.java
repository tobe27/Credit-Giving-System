package com.example.web.controller;

import com.example.service.tag.TagDO;
import com.example.service.tag.TagDOService;
import com.example.web.entity.ResultBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by L.C.Y on 2018-12-5
 */
@RestController
@RequestMapping("/admin")
public class TagDOController {
    private final
    TagDOService tagDOService;

    @Autowired
    public TagDOController(TagDOService tagDOService) {
        this.tagDOService = tagDOService;
    }

    /**
     * 删除标签
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag/{id}", method = RequestMethod.DELETE)
    public ResultBean delete(@PathVariable Long id) throws Exception {
        tagDOService.deleteByPrimaryKey(id);
        return new ResultBean().success();
    }

    /**
     * 新建标签
     * @param tagDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public ResultBean insert(@RequestBody @Validated TagDO tagDO) throws Exception {
        tagDOService.insertSelective(tagDO);
        return new ResultBean().success();
    }

    /**
     * 修改标签
     * @param tagDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag/{id}", method = RequestMethod.PUT)
    public ResultBean update(@RequestBody @Validated TagDO tagDO) throws Exception {
        tagDOService.updateByPrimaryKeySelective(tagDO);
        return new ResultBean().success();
    }

    /**
     * 查看标签
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag/{id}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable Long id) throws Exception {
        return new ResultBean().success(tagDOService.getByPrimaryKey(id));
    }

    /**
     * 分页查询标签
     * @param tagDO
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tag/list", method = RequestMethod.GET)
    public ResultBean listByPage(TagDO tagDO, Integer pageNum, Integer pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TagDO> pageInfo = new PageInfo<>(tagDOService.listTags(tagDO));
        return new ResultBean().success(pageInfo.getTotal(), pageInfo.getList());
    }

}
