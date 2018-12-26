package com.example.service.grid;
import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GridInfoDO implements Serializable{
    
	private static final long serialVersionUID = -4500250302987876286L;
	private Long id;
    @NotNull(message = "网格编号不能为空！")
    @Size(max = 20, message = "网格编号最长20位！")
    private String gridCode;
    @NotNull(message = "网格名称不能为空！")
    @Size(max = 20, message = "网格名称最长20位！")
    private String gridName;
    @NotNull(message = "机构代码不能为空！")
    @Size(max = 20, message = "机构代码最长20位！")
    private String orgCode;
    private String orgName;
    @NotNull(message = "网格类型不能为空！")
    private String gridType;

    private String description;
    @NotNull(message = "请选择网格管理员！")
    private Long userId;
    private String userName;

    private String assistManager;

    private String superviseManager;

    private String qrCode;

    private String gridMap;

    private Long createdAt;

    private Long updatedAt;

    private String status;
    @NotNull(message = "乡镇名称不能为空！")
    @Size(max = 20, message = "乡镇名称最长20位！")
    private String township;
    @NotNull(message = "行政村名称不能为空！")
    @Size(max = 20, message = "行政村名称最长20位！")
    private String village;
    @NotNull(message = "自然村名称不能为空！")
    @Size(max = 20, message = "自然村名称最长20位！")
    private String group;
    
    private List<GridReviewDO> listReview;


}