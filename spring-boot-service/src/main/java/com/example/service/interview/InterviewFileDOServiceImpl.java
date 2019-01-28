package com.example.service.interview;

import com.example.common.util.Base64Util;
import com.example.common.util.PoiUtil;
import com.example.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Created by W.S.T on 2018-12-16
 */
@Service
public class InterviewFileDOServiceImpl implements InterviewFileDOService {
	private final InterviewFileDOMapper interviewFileDOMapper;
	private final CustomerInterviewDOMapper customerInterviewDOMapper;
	@Autowired
	public InterviewFileDOServiceImpl(InterviewFileDOMapper interviewFileDOMapper,CustomerInterviewDOMapper customerInterviewDOMapper) {
		this.interviewFileDOMapper = interviewFileDOMapper;
		this.customerInterviewDOMapper=customerInterviewDOMapper;

	}

	private static Logger logger = LoggerFactory.getLogger(InterviewFileDOServiceImpl.class);
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean deleteByPrimaryKey(Long id) throws Exception {
		InterviewFileDO interviewFileDO= interviewFileDOMapper.selectByPrimaryKey(id);
		Map<String,Object> mapDelete=PoiUtil.moveFileToRecycle(interviewFileDO.getPath(), interviewFileDO.getType());
		CustomerInterviewDO customerInterviewDO=customerInterviewDOMapper.selectByPrimaryKey(interviewFileDO.getInterviewId());
		String[] attach=customerInterviewDO.getAttachFlag().split("\\.");
		//删除的是授信申请表
	    if("1".equals(interviewFileDO.getType())) {
	      attach[0]="0";
	      
	    }
	    //删除的是征信报告
	    if("3".equals(interviewFileDO.getType())) {
	      attach[1]="0";
	    }
	    //删除的是贷前调查影像资料
	    if("4".equals(interviewFileDO.getType())) {
	      attach[2]="0";
	    }    
	    customerInterviewDO.setAttachFlag(attach[0]+"."+attach[1]+"."+attach[2]);
		customerInterviewDO.setUpdatedAt(System.currentTimeMillis());
		customerInterviewDOMapper.updateByPrimaryKey(customerInterviewDO);
		return  interviewFileDOMapper.deleteByPrimaryKey(id)==1;

	}
	/**
	 * 新建/修改
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean insertSelective(MultipartFile file,InterviewFileDO record) throws Exception {
		long now =System.currentTimeMillis();
		if(file==null) {
			throw new ServiceException("上传的文件为空");
		}
		if("".equals(record.getIdNumber())||record.getInterviewId()==null||"".equals(record.getType())||"".equals(record.getUserName())||null==record.getUserId()) {
			throw new ServiceException("上传的参数有误");
		}
		//如果是修改直接把原来的图片删除
		if(record.getId()!=null) {
			deleteByPrimaryKey(record.getId());
		}
		String fileName="";
		if("3".equals(record.getType())) {
			fileName=record.getIdNumber()+now+"征信报告";
		}
		if("1".equals(record.getType())) {
			fileName=record.getIdNumber()+now+"授信申请表";
		}
		if("2".equals(record.getType())) {
			fileName=record.getIdNumber()+now+"面谈面签合影";
		}
		if("4".equals(record.getType())) {
			fileName=record.getIdNumber()+now+"贷前调查影像资料";
		}
		//保存图片
		Map<String,Object> map= PoiUtil.saveFile(file, "interview", fileName, record.getIdNumber());
		if(map==null) {
			throw new ServiceException("保存图片出现异常");
		}
		record.setId(null);
		record.setCreatedAt(now);
		record.setUpdatedAt(now);
		record.setSystemName(fileName+file.getOriginalFilename());
		record.setPath(map.get("path").toString());
		CustomerInterviewDO customerInterviewDO=customerInterviewDOMapper.selectByPrimaryKey(record.getInterviewId());
		String[] attach=customerInterviewDO.getAttachFlag().split("\\.");
		//上传的是授信申请表
	    if("1".equals(record.getType())) {
	      attach[0]="1";
	      
	    }
	    //上传的是征信报告
	    if("3".equals(record.getType())) {
	      attach[1]="1";
	    }
	    //上传的是贷前调查影像资料
	    if("4".equals(record.getType())) {
	      attach[2]="1";
	    }    
	    customerInterviewDO.setAttachFlag(attach[0]+"."+attach[1]+"."+attach[2]);
		customerInterviewDO.setUpdatedAt(now);
		customerInterviewDOMapper.updateByPrimaryKey(customerInterviewDO);
		return interviewFileDOMapper.insertSelective(record)==1;
	}
	/**
	 * 查询
	 * @param record
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public List<Map<String,Object>> getList(InterviewFileDO record) throws Exception {
		if( "".equals(record.getIdNumber()) || record.getType()==null ) {
			throw new ServiceException("查询参数异常异常");
		}
		List<Map<String,Object>> list=new ArrayList<>();
		//如果是查PDF 则查最新的一条
		if("3".equals(record.getType())) {
			InterviewFileDO io=interviewFileDOMapper.getOneByIdNumber(record);
			if(io != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("path", io.getPath());
				map.put("id", io.getId());
				list.add(map);
			}
			return list;
		}else {
			List<InterviewFileDO> listIn=interviewFileDOMapper.getList(record);
			for(InterviewFileDO io:listIn) {
				Map<String,Object> map=new HashMap<>();
				File file=new File(io.getPath());
				if(file.exists()==false) {
					continue;
				}
				String baseCode="data:image/jpg;base64,";
				baseCode=baseCode+ Base64Util.encodeBase64(file);
				map.put("id",io.getId() );
				map.put("baseCode", baseCode);
				list.add(map);
			}
			return list;
		}


	}

}
