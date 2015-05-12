package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CockpitUserMapper {

    void insert(CockpitUser cockpitUser);

    /**
     * Get user by specified ID or user name. One of them is required in order to determine a unique user.
     * @param id User ID.
     * @param userName User name.
     * @return Cockpit user.
     */
    CockpitUser get(@Param("id") long id, @Param("userName") String userName);


    List<CockpitUser> list(@Param("status")Status status);

    void grant(@Param("userId") long userId, @Param("roleId") long roleId);

    void revoke(@Param("userId") long userId, @Param("roleId") long roleId);

    List<CockpitRole> queryRoles(@Param("userId") long userId);

    void activate(long id);

    void suspend(long id);
}
