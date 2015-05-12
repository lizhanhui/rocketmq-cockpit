package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;

import java.util.List;

public interface CockpitRoleMapper {

    void insert(CockpitRole cockpitRole);

    void delete(long id);

    void update(CockpitRole cockpitRole);

    List<CockpitRole> list();

}
