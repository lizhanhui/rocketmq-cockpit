package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitUserMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TeamMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cockpitUserService")
public class CockpitUserServiceImpl implements CockpitUserService {

    @Autowired
    private CockpitUserMapper cockpitUserMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Override
    @Transactional
    public void registerUser(CockpitUser cockpitUser) {
        cockpitUserMapper.insert(cockpitUser);

        if (null != cockpitUser.getTeam() && cockpitUser.getTeam().getId() > 0) {
            teamMapper.addMember(cockpitUser.getTeam().getId(), cockpitUser.getId());

        }

        //For now, grant user and watcher role.
        cockpitUserMapper.grant(cockpitUser.getId(), 3);
        cockpitUserMapper.grant(cockpitUser.getId(), 4);
    }
}
