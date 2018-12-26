package com.example.service.business;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ODSDepositDO {
    private String creditId;

    private String name;

    private String idNumber;

    private String account;

    private String depositType;

    private String balance;

    private String balanceSum;

}