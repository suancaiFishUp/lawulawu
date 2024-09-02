package com.suancaiyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suancaiyu.entity.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {

    Person checkLogin(@Param("username") String username, @Param("password")String password);

    void insertPerson(@Param("username") String username, @Param("password") String password, @Param("address") String address);
}
