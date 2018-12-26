package com.example.web.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.service.customer.CustomerImageDO;
import com.example.service.customer.CustomerImageDOService;
import com.example.web.entity.ResultBean;

/**
 * @author Created by W.S.T on 2018-12-7
 */
@RestController
@RequestMapping("/customer")
public class CustomerImageDOController {
	private final
    CustomerImageDOService CustomerImageDOService;
	 @Autowired
	 public CustomerImageDOController( CustomerImageDOService CustomerImageDOService) {
	      this.CustomerImageDOService = CustomerImageDOService;
	 }
	    /**
	     * 新建/修改
	     * @param CustomerImageDO
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/image", method = RequestMethod.POST)
	    public ResultBean insert(@RequestParam MultipartFile file ,@Validated CustomerImageDO customerImageDO) throws Exception {
	    	CustomerImageDOService.insertSelective(file,customerImageDO);
	        return new ResultBean().success();
	    }
	    /**
	     * 删除
	     * @param id
	     * @return
	     * @throws Exception
	     */
	  @RequestMapping(value = "/image/{id}", method = RequestMethod.DELETE)
	    public ResultBean delete(@RequestBody @PathVariable Long id) throws Exception {
		  CustomerImageDOService.deleteByPrimaryKey(id);
	        return new ResultBean().success();
	    }
	  /**
	     * 调用此接口获取某客户的某类型所有图片
	     * @param idNumber,type
	     * @return
	     * @throws Exception
	     */
		@RequestMapping(value = "/image/{idNumber}/list",method = RequestMethod.GET)
		 public ResultBean getImageBaseCodeList(@PathVariable String  idNumber,String type) throws Exception {
			CustomerImageDO customerImage=new CustomerImageDO();
			customerImage.setIdNumber(idNumber);
			customerImage.setType(type);
		 return new ResultBean().success( CustomerImageDOService.getListByIdNumberAndType(customerImage));
		}
}
