package com.example.service.grid;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class AnalysisReport {
    private String taskdate;

    private String gridCode;

    private String a; // 即gridName

    private Integer b;

    private Integer c;

    private Integer d;

    private Integer e;

    private Integer f;

    private Integer g;

    private Integer h;

    private Integer i;

    private Integer j;

    private Integer k;

    private Integer l;

    private Integer m;

    private Integer n;

    private Integer o;

    private Integer p;

    private BigDecimal q;

    // 关联使用
    private String frOrgCode;
    private String frOrgName;
    private String orgCode;
    private String orgName;
    private Long userId;
    private Long roleId;
    private String userName;
    private List<String> orgCodeList;
}