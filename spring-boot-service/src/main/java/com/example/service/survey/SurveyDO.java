package com.example.service.survey;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SurveyDO {
    private Long id;

    private String name;

    private String idNumber;

    private String householdId;

    private String isFamiliar;

    private String negativeReason;

    private String remark;

    private String outWork;

    private String houseValue;

    private String carValue;

    private String mainBusiness;

    private String scale;

    private String income;

    private String payout;

    private String creditAmount;

    private String borrower;

    private String surveyType;

    private String senator;

    private String isValid;

    private String date;

    private Long createdAt;

    private Long updatedAt;
}