package com.example.service.customer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.util.Base64Util;
import com.example.common.util.PoiUtil;
import com.example.service.exception.ServiceException;

/**
 * @author Created by W.S.T on 2018-12-7
 */
@Service
public class CustomerImageDOServiceImpl implements CustomerImageDOService {
	private final CustomerImageDOMapper customerImageDOMapper;
	 @Autowired
	public CustomerImageDOServiceImpl(CustomerImageDOMapper customerImageDOMapper) {
	        this.customerImageDOMapper = customerImageDOMapper;
	       
	    }
	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	@Override
	@Transactional
	public boolean deleteByPrimaryKey(Long id) {
		  CustomerImageDO image= customerImageDOMapper.selectByPrimaryKey(id);
		  Map<String,Object> mapDelete=PoiUtil.moveFileToRecycle(image.getPath(), "image");
		return  customerImageDOMapper.deleteByPrimaryKey(image.getId())==1;
	}
	  /**
     * 新建
     * @param record
     * @return
     * @throws Exception
     */
	@Override
	@Transactional
	public boolean insertSelective(MultipartFile file,CustomerImageDO record)throws Exception {
		 long now =System.currentTimeMillis();
		    String fileName="";
		    if("1.1".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"身份证正面";
		    }
		    if("1.2".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"身份证背面";
		    }
		    if("2".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"资产信息";
		    }
		    if("3".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"经营信息";
		    }
		    if("5".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"押品信息";
		    }
		    if("4".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"贷款流程信息";
		    }
		    if("6".equals(record.getType())) {
		    	fileName=record.getIdNumber()+now+"其他信息";
		    }
		    //保存图片
			Map<String,Object> map= PoiUtil.saveImage(file, record.getType(), fileName, record.getIdNumber());
			 if(map==null) {
				   throw new ServiceException("保存图片出现异常"); 
			   }
			//如果传入的id不为空，则把传入的id的图片删除
			 if(record.getId() !=null) {
				 CustomerImageDO image= customerImageDOMapper.selectByPrimaryKey(record.getId());
				 if(image==null ||image.getId()==null) {
					 throw new ServiceException("要修改的图片未找到"); 
				 }
				  Map<String,Object> mapDelete=PoiUtil.moveFileToRecycle(image.getPath(), "image");
				  customerImageDOMapper.deleteByPrimaryKey(image.getId());
			 }
			 record.setId(null);
			 record.setCreatedAt(now);
			 record.setUpdatedAt(now);
			 record.setSystemName(fileName+file.getOriginalFilename());
			 record.setPath(map.get("path").toString());
		return customerImageDOMapper.insertSelective(record)==1;
	}
	/**
     * 查看
     * @param id
     * @return
     * @throws Exception
     */
	@Override
	public CustomerImageDO selectByPrimaryKey(Long id)throws Exception {
		return customerImageDOMapper.selectByPrimaryKey(id);
	}

	/**
	 * 修改
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateByPrimaryKeySelective(CustomerImageDO record)throws Exception {
		return customerImageDOMapper.updateByPrimaryKeySelective(record)==1;
	}
	/**
	 * 查询某个身份证号下的某类型的所有图片
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public List<Map<String,Object>> getListByIdNumberAndType(CustomerImageDO record) throws Exception {
		if(record.getIdNumber()==null || "".equals(record.getIdNumber()) || record.getType()==null ||"".equals(record.getType())) {
			throw new ServiceException("查询参数异常异常"); 
		}
		List<CustomerImageDO> list = null;
		try {
			list = customerImageDOMapper.getImageListByIdNumberAndType(record);
		} catch (Exception e) {
			throw new ServiceException("查询数据异常"); 
			
		}
		List<Map<String,Object>> returnList=new ArrayList<>();
		//后期加入判断权限
		if(list !=null &&list.size()>0) {
			for(CustomerImageDO customerImage:list) {
				Map<String,Object> map=new HashMap<>();
				File file=new File(customerImage.getPath());
				if(file.exists()==false) {
					continue;
				}
				String baseCode="data:image/jpg;base64,";
				baseCode=baseCode+ Base64Util.encodeBase64(file);
				map.put("id",customerImage.getId() );
				map.put("baseCode", baseCode);
				returnList.add(map);
			}
		}
		return returnList;
	}

}
