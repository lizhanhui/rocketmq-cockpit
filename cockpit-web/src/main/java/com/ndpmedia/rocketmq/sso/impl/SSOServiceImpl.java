package com.ndpmedia.rocketmq.sso.impl;

import com.ndpmedia.rocketmq.cockpit.model.Login;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.LoginMapper;
import com.ndpmedia.rocketmq.sso.SSOService;
import org.springframework.beans.factory.annotation.Autowired;

public class SSOServiceImpl implements SSOService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public Login authenticate(String token) {
        return loginMapper.getByToken(token);
    }
}
