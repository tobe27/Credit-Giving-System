package com.example.web.controller;

import com.example.service.survey.SurveyDO;
import com.example.service.survey.SurveyDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CREATED BY L.C.Y on 2018/12/17
 */
@RestController
@RequestMapping("/customer")
public class SurveyDOController {
    private final SurveyDOService surveyDOService;

    @Autowired
    public SurveyDOController(SurveyDOService surveyDOService) {
        this.surveyDOService = surveyDOService;
    }

    /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/survey/{id}", method = RequestMethod.GET)
    public ResultBean get(@PathVariable Long id) throws Exception {
        return new ResultBean().success(surveyDOService.selectByPrimaryKey(id));
    }

    /**
     * 列表
     * @param surveyDO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/survey/list", method = RequestMethod.GET)
    public ResultBean list(SurveyDO surveyDO) throws Exception {
        return new ResultBean().success(surveyDOService.listByIdNumberAndIsValid(surveyDO));
    }
}
