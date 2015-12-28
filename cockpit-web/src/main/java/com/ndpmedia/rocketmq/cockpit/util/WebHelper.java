package com.ndpmedia.rocketmq.cockpit.util;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static boolean hasUser(HttpServletRequest request){
        CockpitUser cockpitUser = (CockpitUser) request.getSession().getAttribute(LoginConstant.COCKPIT_USER_KEY);
        if (null == cockpitUser) {
            return false;
        }
        return true;
    }

    public static void cookiesClear(HttpServletRequest request, HttpServletResponse response){
        List<Cookie> cookies = new ArrayList<>();
        List<Cookie> rm = new ArrayList<>();
        if (null != request.getCookies())
            cookies.addAll(Arrays.asList(request.getCookies()));
        HttpSession session = request.getSession();
        for (Cookie cookie:cookies){
            if (cookie.getName() == "JSESSIONID" && cookie.getValue() == session.getId())
                continue;
            else {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

}
