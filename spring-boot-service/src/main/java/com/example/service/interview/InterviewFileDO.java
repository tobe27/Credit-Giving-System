package com.example.service.interview;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InterviewFileDO implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 647854671475760171L;

	private Long id;

    private String customerName;

    private String idNumber;

    private Long customerId;

    private Long interviewId;

    private String systemName;

    private String path;

    private String userName;

    private Long userId;

    private String type;

    private String status;

    private String deletePath;

    private Long deleteTime;

    private Long createdAt;

    private Long updatedAt;

   
}