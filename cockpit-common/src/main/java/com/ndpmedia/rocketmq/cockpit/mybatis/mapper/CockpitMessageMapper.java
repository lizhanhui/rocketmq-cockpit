package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CockpitMessageMapper {

    List<CockpitMessageFlow> list(@Param("msgId") String msgId, @Param("tracerId") String tracerId);

    String tracerId(@Param("id") String id);

    /**
     * Delete data that are older than the specified date.
     *
     * @param date specified date
     * @return number of rows deleted.
     */
    int bulkDelete(Date date);
}
