package com.example.service.grid;

import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2019/1/23
 */
public interface AnalysisReportService {

    /**
     * 1-1 授信明细-客户经理按网格，网格数据覆盖明细
     * @param record
     * @return
     */
    List<AnalysisReport> listCreditDetailAnalysisByGrid(AnalysisReport record) throws Exception;

    /**
     * 1-2 授信明细-支行的按客户经理，网格数据覆盖汇总明细
     * @param record
     * @return
     */
    List<AnalysisReport> listCreditSumAnalysisByUserId(AnalysisReport record) throws Exception;

    /**
     * 1-3 授信明细-法人的按支行，网格数据覆盖汇总明细
     * @param record
     * @return
     */
    List<AnalysisReport> listCreditSumAnalysisByBranch(AnalysisReport record) throws Exception;

    /**
     * 1-4 授信明细-管理员的按法人，网格数据覆盖汇总明细
     * @param record
     * @return
     * @throws Exception
     */
    List<AnalysisReport> listCreditSumAnalysisByFrOrg(AnalysisReport record) throws Exception;

    /**
     * 1-5 授信明细-管理员汇总全部，网格数据覆盖汇总明细
     * @param record
     * @return
     * @throws Exception
     */
    List<AnalysisReport> listCreditSumAnalysisBySuper(AnalysisReport record) throws Exception;

    /**
     *  1-6-1 各级授信明细
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listCreditDetailAnalysis(AnalysisReport record) throws Exception;

    /**
     *  1-6-2 各级授信汇总
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listCreditSumAnalysis(AnalysisReport record) throws Exception;

    /**
     *  2-6-1 各级授信率明细
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listCreditRateDetailAnalysis(AnalysisReport record) throws Exception;

    /**
     *  2-6-2 各级授信率汇总
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listCreditRateSumAnalysis(AnalysisReport record) throws Exception;


    /**
     * 2-1 授信率明细-客户经理按网格，网格数据覆盖率明细
     * @param record
     * @return
     */
    List<AnalysisReport> listCreditRateDetailAnalysisByGrid(AnalysisReport record) throws Exception;
}
