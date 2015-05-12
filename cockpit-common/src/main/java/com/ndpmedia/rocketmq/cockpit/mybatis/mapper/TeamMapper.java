package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Team;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper {

    Team get(long id);

    Team getTeamAndMembersByTeamId(long id);

    Team getByName(String name);

    Team getTeamAndMembersByTeamName(String name);

    List<Team> list();

    void insert(Team team);

    void delete(long id);

    void update(Team team);

    void addMember(@Param("teamId") long teamId, @Param("memberId") long memberId);
}
