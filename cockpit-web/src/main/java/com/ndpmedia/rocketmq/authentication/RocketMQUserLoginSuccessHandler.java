package com.ndpmedia.rocketmq.authentication;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Login;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.LoginMapper;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;

/**
 * try to save user login session.
 */
public class RocketMQUserLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
        implements LoginConstant {

    private final Logger logger = LoggerFactory.getLogger(RocketMQUserLoginSuccessHandler.class);


    @Autowired
    private LoginMapper loginMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(COCKPIT_USER_KEY, authentication.getPrincipal());
        Object principal = authentication.getPrincipal();

        if (principal instanceof CockpitUser) {
            CockpitUser cockpitUser = (CockpitUser)principal;
            Login login = new Login();
            login.setCockpitUser(cockpitUser);
            login.setLoginTime(new Date());
            login.setToken(UUID.randomUUID().toString().replace("-", ""));
            httpSession.setAttribute(LoginConstant.TOKEN_IN_SESSION, login.getToken());
            httpSession.setAttribute(LoginConstant.HANDLE_IN_SESSION, cockpitUser.getUsername());


            if (null != cockpitUser.getCockpitRoles() && cockpitUser.getCockpitRoles().contains(CockpitRole.ROLE_ADMIN)) {
                httpSession.setAttribute(LoginConstant.IS_ADMIN_IN_SESSION, true);
                logger.info("Admin [" + cockpitUser.getUsername() + "] logs in");
            } else {
                httpSession.setAttribute(LoginConstant.IS_ADMIN_IN_SESSION, false);
                logger.info("User: [" + cockpitUser.getUsername() + "] logs in");
            }

            loginMapper.insert(login);
            logger.info("Account {Team: " + cockpitUser.getTeam().getName() +
                    ", Member: " + cockpitUser.getUsername() + "} logs in");

            httpSession.removeAttribute(LoginConstant.LOGIN_SESSION_ERROR_KEY);

            Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
            cookie.setPath("/");
            cookie.setSecure(false);
            response.addCookie(cookie);

            Object redirectURL = httpSession.getAttribute(LoginConstant.REDIRECT_URL_IN_SESSION);
            if (null != redirectURL && redirectURL.toString().trim().length() > 0) {
                String redirect = URLDecoder.decode(redirectURL.toString(), "UTF-8");
                StringBuilder stringBuilder = new StringBuilder(redirect);
                if (!redirect.contains("?")) {
                    stringBuilder.append("?");
                } else {
                    stringBuilder.append("&");
                }
                stringBuilder.append("token=").append(login.getToken());
                //remove redirect url in session.
                httpSession.removeAttribute(LoginConstant.REDIRECT_URL_IN_SESSION);

                response.sendRedirect(stringBuilder.toString());
                return;
            }

        } else {
            logger.error("Fatal error, principal should be a CockpitUser or sub-class instance");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
