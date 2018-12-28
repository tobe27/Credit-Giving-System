package com.example.service.grid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.util.Base64Util;
import com.example.common.util.PoiUtil;
import com.example.service.exception.ServiceException;
import com.example.service.org.OrgDOServiceImpl;
/**
 * @author Created by W.S.T on 2018-12-5
 */
@Service
public class GridImageServiceImpl implements GridImageService {
	private final GridImageDOMapper gridImageDOMapper;
	@Autowired
    public GridImageServiceImpl(GridImageDOMapper gridImageDOMapper) {
        this.gridImageDOMapper = gridImageDOMapper;
       
    }
    private static Logger logger = LoggerFactory.getLogger(GridImageServiceImpl.class);

	 /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
	@Override
	@Transactional
	public boolean deleteByPrimaryKey(Long id) throws Exception {
		GridImageDO image= gridImageDOMapper.selectByPrimaryKey(id);
		PoiUtil.moveFileToRecycle(image.getPath(), "image");
		gridImageDOMapper.deleteByPrimaryKey(image.getId());	
		try {
			return gridImageDOMapper.deleteByPrimaryKey(image.getId())==1;
		} catch (Exception e) {
			 logger.info("删除图片异常:" + e.getMessage());
			  throw new ServiceException("删除图片异常！");
		}
	}

	/**
     * 新建/修改
     * @param record
     * @return
     * @throws Exception
     */
	@Override
	@Transactional
	public boolean insertSelective(MultipartFile file,GridImageDO record) throws Exception {
		if(file==null) {
			throw new ServiceException("请选择您要上传的图片");
		}
	    if(null==record.getType()||"".equals(record.getType())){
	    	throw new ServiceException("请选择上传文件的种类");
	    }
	    long now =System.currentTimeMillis();	
	    Map<String,Object> map=new HashMap<>();
	    //保存图片
	    if("1".equals(record.getType()) || "2".equals(record.getType())){
    		if(PoiUtil.isImage(file)==false) {
    			throw new ServiceException("您上传的不是图片");
    		}
    		 map= PoiUtil.saveGridImage(file,now+record.getGridCode(), record.getGridCode());
    	}
	    //保存附件
	    if("3".equals(record.getType()) ){
	    	 map=PoiUtil.uploadMultipartFile(file,"file");
    	}
		if(map==null) {
			   throw new ServiceException("保存文件出现异常"); 
		   }
		 record.setOriginalName(file.getOriginalFilename());
		 record.setCreatedAt(now);
		 record.setUpdatedAt(now);
		 record.setSystemName(now+record.getGridCode()+file.getOriginalFilename());
		 record.setPath(map.get("path").toString());
		 //如果id不为空,将原来的文件移除，数据库删除记录
		 if(record.getId()!=null) {
			GridImageDO image= gridImageDOMapper.selectByPrimaryKey(record.getId());
			PoiUtil.moveFileToRecycle(image.getPath(), "image");
			gridImageDOMapper.deleteByPrimaryKey(image.getId());
		 }
		 record.setId(null);
		try {
			return  gridImageDOMapper.insertSelective(record)==1;
		} catch (Exception e) {
			 logger.info("保存网格文件异常:" + e.getMessage());
			  throw new ServiceException("保存网格文件异常！");
			
		}
	}
	 /**
     * 详情
     * @param id
     * @return
     * @throws Exception
     */
	@Override
	public GridImageDO selectByPrimaryKey(Long id) throws Exception {
		return gridImageDOMapper.selectByPrimaryKey(id);
	}

	 /**
     * 列表
     * @param 
     * @return
     * @throws Exception
     */
	/*@Override
	public List<GridImageDO> getList(String GridCode) throws Exception {
	   try {
		return gridImageDOMapper.getList(GridCode);
	} catch (Exception e) {
		logger.info("获取图片列表异常:" + e.getMessage());
		throw new ServiceException("获取图片列表异常！");	
	}
	}*/
	/**
     * base64列表
     * @param 
     * @return
     * @throws Exception
     */
	@Transactional
	@Override
	public List<Map<String,Object>> getBaseCodeList(String GridCode,String type) throws Exception {
		if("".equals(type)||"".equals(GridCode)) {
			throw new ServiceException("查询参数异常！");	
		}
		Map<String,Object> mapParam=new HashMap<>();
		mapParam.put("gridCode", GridCode);
		mapParam.put("type", type);
		List<Map<String,Object>> returnList=new ArrayList<>();
		 try {
		List<GridImageDO> list=gridImageDOMapper.getList(mapParam);
	    if(list !=null &&list.size()>0) {
		 for(GridImageDO image:list) {
			Map<String,Object> map=new HashMap<>();
			if("1".equals(type) || "2".equals(type)) {
			File file=new File(image.getPath());
			if(file.exists()==true) {	
				String baseCode="data:image/jpg;base64,";
				baseCode=baseCode+ Base64Util.encodeBase64(file);
				 map.put("baseCode", baseCode);
			}
	     }else {
	    	 map.put("baseCode", "");
	     }
			 map.put("id",image.getId() );
			 map.put("comment",image.getComment() );
			 map.put("type",image.getType() );
			 map.put("originalName",image.getOriginalName());
			 map.put("createdAt",image.getCreatedAt());
			 returnList.add(map);
					}
				}
			return returnList;
			} catch (Exception e) {
				logger.info("获取文件列表异常:" + e.getMessage());
				throw new ServiceException("获取文件列表异常！");	
			}
	}

}
