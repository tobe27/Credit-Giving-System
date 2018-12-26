package com.example.web.controller;

import com.example.service.interview.InterviewFileDO;
import com.example.service.interview.InterviewFileDOService;
import com.example.web.entity.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author Created by W.S.T on 2018-12-16
 */
@RestController
@RequestMapping("/customer")
public class InterviewFileDOController {
	private final
	InterviewFileDOService interviewFileDOService;
	@Autowired
	public InterviewFileDOController(InterviewFileDOService interviewFileDOService) {
		this.interviewFileDOService = interviewFileDOService;
	}
	/**
	 * 新建/修改
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interviewfile", method = RequestMethod.POST)
	public ResultBean insert(@RequestParam MultipartFile file ,@Validated InterviewFileDO interviewFileDO) throws Exception {
		interviewFileDOService.insertSelective(file,interviewFileDO);
		return new ResultBean().success();
	}
	/**
	 * 调用此接口获取某类型所有图片
	 * @param idNumber,type,interviewId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interviewfile/list",method = RequestMethod.GET)
	public ResultBean getImageBaseCodeList(HttpServletResponse response, String  idNumber,String type,long interviewId) throws Exception {
		InterviewFileDO interviewFileDO=new InterviewFileDO();
		interviewFileDO.setIdNumber(idNumber);
		interviewFileDO.setType(type);
		if("3".equals(type)) {
			List<Map<String,Object>> list=interviewFileDOService.getList(interviewFileDO);
			if(list == null || list.size() == 0) {
				return new ResultBean().success(null);
			}
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/octet-stream;charset=utf-8");
//			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode("zhengxin.pdf", "UTF-8"));
			InputStream inputStream = null;
			OutputStream os = null;
			String path=list.get(0).get("path").toString();


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
			return new ResultBean().fail("文件丢失");
		}else {
			interviewFileDO.setInterviewId(interviewId);
			return new ResultBean().success( interviewFileDOService.getList(interviewFileDO));
		}

	}

	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interviewfile/{id}", method = RequestMethod.DELETE)
	public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		interviewFileDOService.deleteByPrimaryKey(id);
		return new ResultBean().success();
	}


}
