package com.ndpmedia.rocketmq.authentication;

import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * the spring security configuration.
 * when the access is denied.
 */
public class RocketMQUserAccessDeniedHandler implements AccessDeniedHandler, LoginConstant {
    private String accessDeniedURL;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        if (accessDeniedURL.startsWith("/")) {
            response.sendRedirect(accessDeniedURL);
        } else {
            accessDeniedURL = request.getServletContext().getContextPath() + "/" + accessDeniedURL;
        }

        String deniedMessage = accessDeniedException.getMessage();

        request.getSession().setAttribute(ACCESS_DENIED_MSG, deniedMessage);
    }

    public String getAccessDeniedURL() {
        return accessDeniedURL;
    }

    public void setAccessDeniedURL(String accessDeniedURL) {
        this.accessDeniedURL = accessDeniedURL;
    }
}
