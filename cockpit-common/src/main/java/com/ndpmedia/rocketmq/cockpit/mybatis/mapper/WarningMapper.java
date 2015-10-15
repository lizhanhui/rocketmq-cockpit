package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Warning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningMapper {

    void create(Warning warning);

    Warning get(long id);

    List<Warning> list(@Param("statuses") Status... statuses);

    void mark(@Param("id") long id,
              @Param("status")Status status);

}
