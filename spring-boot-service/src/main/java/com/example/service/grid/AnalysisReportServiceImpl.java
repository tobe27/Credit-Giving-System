package com.example.service.grid;

import com.example.common.util.JSONUtil;
import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CREATED BY L.C.Y on 2019/1/23
 */
@Service
public class AnalysisReportServiceImpl implements AnalysisReportService {
    private final AnalysisReportMapper analysisReportMapper;
    private final OrgDOMapper orgDOMapper;

    @Autowired
    public AnalysisReportServiceImpl(AnalysisReportMapper analysisReportMapper, OrgDOMapper orgDOMapper) {
        this.analysisReportMapper = analysisReportMapper;
        this.orgDOMapper = orgDOMapper;
    }

    private static Logger logger = LoggerFactory.getLogger(AnalysisReportServiceImpl.class);

    /**
     * 1-1 授信明细-客户经理按网格，网格数据覆盖明细
     *
     * @param record taskdate 格式20190101
     *               看一个网格 gridCode != null
     *               客户经理在自己的所有网格明细 roleId ==1, userId != null
     *               支行长看支行所有网格明细 roleId != 1, orgCode != null
     *
     * @return
     */
    @Override
    public List<AnalysisReport> listCreditDetailAnalysisByGrid(AnalysisReport record) throws Exception {
        if (record.getRoleId() != 1) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                throw new ServiceException("查询下级机构异常");
            }
        }
        return analysisReportMapper.listCreditDetailAnalysisByGrid(record);
    }

    /**
     * 1-2 授信明细-支行的按客户经理，网格数据覆盖汇总明细
     *
     * @param record taskdate 格式20190101
     *               客户经理看自己的汇总 roleId == 1, userId != null
     *               支行长看所有客户经理明细 orgCode != null
     * @return
     */
    @Override
    public List<AnalysisReport> listCreditSumAnalysisByUserId(AnalysisReport record) throws Exception {
        return analysisReportMapper.listCreditSumAnalysisByUserId(record);
    }

    /**
     * 1-3 授信明细-法人的按支行，网格数据覆盖汇总明细
     *
     * @param record taskdate 格式20190101
     *               支行查看自己的汇总 roleId == 2 or 3, orgCode != null
     *               法人查看所有支行的明细 roleId == 4 or 5 or 6, orgCode != null
     *
     * @return
     */
    @Override
    public List<AnalysisReport> listCreditSumAnalysisByBranch(AnalysisReport record) throws Exception {
        if (record.getRoleId() == 4 || record.getRoleId() == 5 || record.getRoleId() == 6) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                throw new ServiceException("查询下级机构异常");
            }
        }
        return analysisReportMapper.listCreditSumAnalysisByBranch(record);
    }

    /**
     * 1-4 授信明细-管理员的按法人（总行），网格数据覆盖汇总明细
     *
     * @param record taskdate 格式20190101
     *               法人查看自己的汇总 roleId == 4 or 5 or 6, orgCode != null
     *               超级管理员查看所有法人的明细 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<AnalysisReport> listCreditSumAnalysisByFrOrg(AnalysisReport record) throws Exception {
        return analysisReportMapper.listCreditSumAnalysisByFrOrg(record);
    }

    /**
     * 1-5 授信明细-管理员汇总全部，网格数据覆盖汇总明细
     *
     * @param record taskdate 格式20190101
     *               管理员汇总一条数据 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<AnalysisReport> listCreditSumAnalysisBySuper(AnalysisReport record) throws Exception {
        return analysisReportMapper.listCreditSumAnalysisBySuper(record);
    }

    /**
     * 1-6-1 各级授信明细
     *
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listCreditDetailAnalysis(AnalysisReport record) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<AnalysisReport> analysisReportList = null;
        // 客户经理看网格明细
        if (record.getRoleId() == 1) {
            analysisReportList = analysisReportMapper.listCreditDetailAnalysisByGrid(record);
        }

        // 支行看客户经理明细
        if (record.getRoleId() == 2 || record.getRoleId() == 3) {
            analysisReportList = analysisReportMapper.listCreditSumAnalysisByUserId(record);
        }

        // 法人看支行明细
        if (record.getRoleId() == 4 || record.getRoleId() == 5 || record.getRoleId() == 6) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                throw new ServiceException("查询下级机构异常");
            }
            analysisReportList = analysisReportMapper.listCreditSumAnalysisByBranch(record);
        }

        // 超级看法人明细
        if (record.getRoleId() == 7) {
            analysisReportList = analysisReportMapper.listCreditSumAnalysisByFrOrg(record);
        }

        if (analysisReportList == null || analysisReportList.size() == 0) {
            throw new ServiceException("暂无数据");
        }

        for (AnalysisReport analysisReport : analysisReportList) {
            mapList.add(JSONUtil.convertObject2Map(analysisReport));
        }

        return mapList;
    }

    /**
     * 1-6-2 各级授信汇总
     *
     * @param record 必有参数 taskdate userId roleId orgCode
     *
     *
     *               客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listCreditSumAnalysis(AnalysisReport record) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<AnalysisReport> analysisReportList = null;

        // 客户经理看网格汇总
        if (record.getRoleId() == 1) {
            analysisReportList = analysisReportMapper.listCreditSumAnalysisByUserId(record);
        }

        // 支行看客户经理汇总
        if (record.getRoleId() == 2 || record.getRoleId() == 3) {
            analysisReportList =  analysisReportMapper.listCreditSumAnalysisByBranch(record);
        }

        // 法人看支行汇总
        if (record.getRoleId() == 4 || record.getRoleId() == 5 || record.getRoleId() == 6) {
            analysisReportList =  analysisReportMapper.listCreditSumAnalysisByFrOrg(record);
        }

        // 超级看法人汇总
        if (record.getRoleId() == 7) {
            analysisReportList =  analysisReportMapper.listCreditSumAnalysisBySuper(record);
        }


        if (analysisReportList == null || analysisReportList.size() == 0) {
            throw new ServiceException("暂无数据");
        }

        for (AnalysisReport analysisReport : analysisReportList) {
            mapList.add(JSONUtil.convertObject2Map(analysisReport));
        }

        return mapList;
    }

    /**
     * 2-6-1 各级授信率明细
     *
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listCreditRateDetailAnalysis(AnalysisReport record) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();

        // 客户经理看网格明细
        if (record.getRoleId() == 1) {
            List<AnalysisReport> originList = analysisReportMapper.listCreditDetailAnalysisByGrid(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }

        }

        // 支行看客户经理明细
        if (record.getRoleId() == 2 || record.getRoleId() == 3) {
            List<AnalysisReport> originList = analysisReportMapper.listCreditSumAnalysisByUserId(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        // 法人看支行明细
        if (record.getRoleId() == 4 || record.getRoleId() == 5 || record.getRoleId() == 6) {
            try {
                record.setOrgCodeList(orgDOMapper.listStringOrgCodes(record.getOrgCode()));
            } catch (Exception e) {
                throw new ServiceException("查询下级机构异常");
            }
            List<AnalysisReport> originList = analysisReportMapper.listCreditSumAnalysisByBranch(record);

            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        // 超级看法人明细
        if (record.getRoleId() == 7) {
            List<AnalysisReport> originList = analysisReportMapper.listCreditSumAnalysisByFrOrg(record);

            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        if (mapList.size() == 0) {
            throw new ServiceException("暂无数据");
        }

        return mapList;
    }

    /**
     * 2-6-2 各级授信率汇总
     *
     * @param record 客户经理 roleId == 1
     *               支行 roleId == 2 or 3
     *               法人 roleId == 4 or 5 or 6
     *               超级 roleId == 7
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listCreditRateSumAnalysis(AnalysisReport record) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();

        // 客户经理看网格汇总
        if (record.getRoleId() == 1) {
            List<AnalysisReport> originList = analysisReportMapper.listCreditSumAnalysisByUserId(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        // 支行看客户经理汇总
        if (record.getRoleId() == 2 || record.getRoleId() == 3) {
            List<AnalysisReport> originList =  analysisReportMapper.listCreditSumAnalysisByBranch(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        // 法人看支行汇总
        if (record.getRoleId() == 4 || record.getRoleId() == 5 || record.getRoleId() == 6) {
            List<AnalysisReport> originList =  analysisReportMapper.listCreditSumAnalysisByFrOrg(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }

        // 超级看法人汇总
        if (record.getRoleId() == 7) {
            List<AnalysisReport> originList =  analysisReportMapper.listCreditSumAnalysisBySuper(record);
            for (AnalysisReport analysisReport : originList) {
                mapList.add(calculateCreditRate(analysisReport));
            }
        }


        if (mapList.size() == 0) {
            throw new ServiceException("暂无数据");
        }

        return mapList;

    }

    private Map<String, Object> calculateCreditRate(AnalysisReport analysisReport) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Map<String, Object> analysisMap = new HashMap<>();
        analysisMap.put("frOrgName", analysisReport.getFrOrgName());
        analysisMap.put("orgName", analysisReport.getOrgName());
        analysisMap.put("userName", analysisReport.getUserName());

        analysisMap.put("a", analysisReport.getA());

        analysisMap.put("b", (analysisReport.getO() + analysisReport.getG()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getO() + analysisReport.getG()) * 100 / (analysisReport.getI() + analysisReport.getG())) + "%");

        analysisMap.put("c", (analysisReport.getO() + analysisReport.getN() + analysisReport.getG()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getO() + analysisReport.getN() + analysisReport.getG()) * 100 / (analysisReport.getI() + analysisReport.getG())) + "%");

        analysisMap.put("d", (analysisReport.getP() + analysisReport.getG()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getP() + analysisReport.getG()) * 100 / (analysisReport.getO() + analysisReport.getG())) + "%");

        analysisMap.put("e", analysisReport.getO() == 0
                ? 0 : decimalFormat.format((double) analysisReport.getO() * 100 / analysisReport.getI()) + "%");

        analysisMap.put("f", (analysisReport.getO() + analysisReport.getN()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getO() + analysisReport.getN()) * 100 / analysisReport.getI()) + "%");

        analysisMap.put("g", analysisReport.getP() == 0
                ? 0 : decimalFormat.format((double) analysisReport.getP() * 100 / analysisReport.getO()) + "%");

        analysisMap.put("h", (analysisReport.getO() + analysisReport.getM() + analysisReport.getN() + analysisReport.getH()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getO() + analysisReport.getM() + analysisReport.getN() + analysisReport.getH()) * 100 / (analysisReport.getI() + analysisReport.getH())) + "%");

        analysisMap.put("i", (analysisReport.getO() + analysisReport.getN()) == 0
                ? 0 : decimalFormat.format((double) (analysisReport.getO() + analysisReport.getN()) * 100 / (analysisReport.getO() + analysisReport.getM() + analysisReport.getN())) + "%");

        analysisMap.put("j", analysisReport.getO() == 0
                ? 0 : decimalFormat.format((double) analysisReport.getO() * 100 / (analysisReport.getO() + analysisReport.getN())) + "%");

        return analysisMap;

    }

    /**
     * 2-1 授信率明细-网格数据覆盖明细
     *
     * @param record taskdate 格式20190101
     * @return
     */
    @Override
    public List<AnalysisReport> listCreditRateDetailAnalysisByGrid(AnalysisReport record) throws Exception {
        return analysisReportMapper.listCreditRateDetailAnalysisByGrid(record);
    }
}
