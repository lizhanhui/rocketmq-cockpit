package com.ndpmedia.rocketmq.cockpit.util;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by robert on 2015/11/9.
 */
public class UserHelper {

    public static boolean isAdmin(CockpitUser user) {
        List<CockpitRole> roles = user.getCockpitRoles();

        for (CockpitRole role : roles) {
            if (CockpitRole.ROLE_ADMIN.equals(role)) {
                return true;
            }
        }

        return false;
    }

    public static long getTeamId(HttpServletRequest request) {
        long teamId = 0;
        if (!WebHelper.hasRole(request, CockpitRole.ROLE_ADMIN)) {
            CockpitUser cockpitUser = (CockpitUser)request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
            teamId = cockpitUser.getTeam().getId();
        }
        return teamId;
    }
}
