package com.suancaiyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suancaiyu.entity.Picture;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PictureMapper extends BaseMapper<Picture> {
    List<Picture> selectByNote(@Param("note") String note, @Param("start") int start,@Param("end") int end,@Param("personId")int personId);

    List<Picture> select(@Param("start")int start,@Param("end")int end,@Param("personId")int personId);
}
