package com.example.web.controller;

import com.example.service.grid.GridReviewDO;
import com.example.service.grid.GridReviewService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CREATED BY L.C.Y on 2019/1/20
 */
@RestController
@RequestMapping
public class GridReviewController {

    private final GridReviewService gridReviewService;

    @Autowired
    public GridReviewController(GridReviewService gridReviewService) {
        this.gridReviewService = gridReviewService;
    }

    @RequestMapping(value = "/grid/review", method = RequestMethod.PUT)
    public ResultBean banReview(@RequestBody GridReviewDO gridReviewDO) throws Exception {
        gridReviewService.banReview(gridReviewDO);
        return new ResultBean().success();
    }
}
