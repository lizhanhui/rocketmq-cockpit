package com.ndpmedia.rocketmq.cockpit.util;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public final class WebHelper {

    private WebHelper() {
    }

    public static boolean hasRole(HttpServletRequest request, CockpitRole cockpitRole) {
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        if (null == cockpitUser) {
            return false;
        }

        List<CockpitRole> roles = cockpitUser.getCockpitRoles();

        return null != roles && roles.contains(cockpitRole);

    }
}
