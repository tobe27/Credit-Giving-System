package com.example.service.business;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ODSAssureDO {
    private String creditId;

    private String name;

    private String idNumber;

    private String assureType;

    private String assureId;

    private String contId;

    private BigDecimal sumBalance;

}