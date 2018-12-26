package com.example.service.user;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(UserDO record);

    String getTaskDate();

    UserDO getUserDO(Long id);

    UserDO getUserDOByUsername(String username);

    List<UserDO> listUserDOs(UserDO record);

    List<UserDO> listUserByOrgCode(String orgCode);

    List<Long> listUserIdByOrgCode(UserDO record);

    List<String> listGridCodeById(Long id);

    List<String> listStringRoles(Long id);

    List<String> listStringPerms(Long id);

    int updateByPrimaryKeySelective(UserDO record);
}