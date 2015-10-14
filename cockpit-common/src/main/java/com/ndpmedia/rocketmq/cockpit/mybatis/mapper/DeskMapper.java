package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Desk;
import org.apache.ibatis.annotations.Param;

/**
 * Created by macbookpro on 15/10/13.
 */
public interface DeskMapper {
    Desk getDesk(@Param("deskId")long id);
}
