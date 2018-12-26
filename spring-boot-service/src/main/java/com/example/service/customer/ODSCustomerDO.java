package com.example.service.customer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ODSCustomerDO {
    private String creditId;

    private String name;

    private String type;

    private String idNumber;

    private String signOrg;

    private String isLongTerm;

    private String signDate;

    private String dueDate;

    private String districtAddress;

    private String birthday;

    private String gender;

    private String detailAddress;

    private String postcode;

    private String nativeAddress;

    private String phoneNumber;

    private String nationality;

    private String nation;

    private String politicsStatus;

    private String educationBackground;

    private String physicalCondition;

    private String maritalStatus;

    private String spouseName;

    private String spouseIdNumber;

    private String spousePhoneNumber;

    private String spouseCompanyName;

    private String careerType;

    private String taxType;

    private String mainBusiness;

    private String industryType;

    private String isStaff;

    private String isStockholder;

    private String isCivilServant;

    private String isHouseOwner;

    private String custManagerId;

    private String orgId;
}