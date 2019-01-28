package com.example.service.blue;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class BlueDO {
    private String creditId;
    private String name;
    private String idNumber;
    private String reasonEn;
    private String orgCode;
    private String createdDate;

    private String reasonCh;//原因
}