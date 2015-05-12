package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Login;

import java.util.Date;

public interface LoginMapper {

    void insert(Login login);

    void delete(Date date);

    Login getByToken(String token);
}
