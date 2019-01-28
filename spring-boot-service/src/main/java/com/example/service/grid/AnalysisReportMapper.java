package com.example.service.grid;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AnalysisReportMapper {

    int insertSelective(AnalysisReport record);

    List<AnalysisReport> listCreditDetailAnalysisByGrid(AnalysisReport record);

    List<AnalysisReport> listCreditSumAnalysisByUserId(AnalysisReport record);

    List<AnalysisReport> listCreditSumAnalysisByBranch(AnalysisReport record);

    List<AnalysisReport> listCreditSumAnalysisByFrOrg(AnalysisReport record);

    List<AnalysisReport> listCreditSumAnalysisBySuper(AnalysisReport record);

    List<AnalysisReport> listCreditRateDetailAnalysisByGrid(AnalysisReport record);

}