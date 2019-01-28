package com.example.web.controller;

import com.example.service.grid.AnalysisReport;
import com.example.service.grid.AnalysisReportService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CREATED BY L.C.Y on 2019/1/23
 */
@RestController
@RequestMapping("/grid")
public class AnalysisReportController {
    private final AnalysisReportService analysisReportService;

    @Autowired
    public AnalysisReportController(AnalysisReportService analysisReportService) {
        this.analysisReportService = analysisReportService;
    }

    /**
     * 覆盖明细
     * @param analysisReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistics/one/detail", method = RequestMethod.GET)
    public ResultBean listDetailByGrid(AnalysisReport analysisReport) throws Exception {
        return new ResultBean().success(analysisReportService.listCreditDetailAnalysis(analysisReport));
    }

    /**
     * 覆盖汇总
     * @param analysisReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistics/one/sum", method = RequestMethod.GET)
    public ResultBean listSumByGrid(AnalysisReport analysisReport) throws Exception {
        return new ResultBean().success(analysisReportService.listCreditSumAnalysis(analysisReport));
    }

    /**
     * 覆盖率明细
     * @param analysisReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistics/two/detail", method = RequestMethod.GET)
    public ResultBean listTwoDetailByGrid(AnalysisReport analysisReport) throws Exception {
        return new ResultBean().success(analysisReportService.listCreditRateDetailAnalysis(analysisReport));
    }

    /**
     * 覆盖率汇总
     * @param analysisReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistics/two/sum", method = RequestMethod.GET)
    public ResultBean listTwoSumByGrid(AnalysisReport analysisReport) throws Exception {
        return new ResultBean().success(analysisReportService.listCreditRateSumAnalysis(analysisReport));
    }
}
