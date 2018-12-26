package com.example.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.grid.GridImageDO;
import com.example.service.grid.GridImageService;
import com.example.web.entity.ResultBean;

/**
 * @author Created by W.S.T on 2018-12-6
 */
@RestController
@RequestMapping("/image")
public class GridImageDOController {
	 private final GridImageService gridImageService;
	 @Autowired
	public GridImageDOController(GridImageService gridImageService) {
	        this.gridImageService = gridImageService;
	    }
	 
	 /**
	     * 新建/修改图片
	     * @param GridImageDO
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grid", method = RequestMethod.POST)
	    public ResultBean insert(@RequestBody MultipartFile file,@Validated GridImageDO gridImageDO) throws Exception {
	    	gridImageService.insertSelective(file,gridImageDO);
	        return new ResultBean().success();
	    }
	    /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/grid/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  gridImageService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  
	  /**
	     *
	     * @param gridCode
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grid/{id}", method = RequestMethod.GET)
	    public void getList( HttpServletResponse response,@PathVariable Long  id) throws Exception {
	    	GridImageDO gio=gridImageService.selectByPrimaryKey(id);
	    	response.setCharacterEncoding("utf-8");
	    	response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(gio.getOriginalName(), "UTF-8"));
			InputStream inputStream = null;
			OutputStream os = null;
			String path=gio.getPath().toString();
            try {
				inputStream = new FileInputStream(new File(path));
				os = response.getOutputStream();
				byte[] b = new byte[2048];
				int length;
				while ((length = inputStream.read(b)) > 0) {
					os.write(b, 0, length);
				}
			} catch (Exception e) {
			}finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
				}
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
				}
			}
	        
	    } 
	    /**
	     *
	     * @param gridCode
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/grid/base64/list", method = RequestMethod.GET)
	    public ResultBean listPage(String  gridCode,String type) throws Exception {
	        return new ResultBean().success(gridImageService.getBaseCodeList(gridCode,type));
	    }  
}
