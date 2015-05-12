package com.ndpmedia.rocketmq.authentication;

import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * try to process login message when login failed.
 */
public class RocketMQUserLoginFailedHandler extends SimpleUrlAuthenticationFailureHandler implements LoginConstant {

    private final Logger logger = LoggerFactory.getLogger(RocketMQUserLoginFailedHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter(LOGIN_PARAMETER_USERNAME);
        int retryTime = ZERO;
        int retryTimeMAX = FIVE;
        logger.warn("[login] login failed , this user [" + username + "] already retry " + retryTime);
        request.getSession().setAttribute(username, retryTime + ONE);
        if (retryTime >= retryTimeMAX) {
            exception.addSuppressed(new Exception(" the user : [" + username + "] is locked !"));
        }
        this.setDefaultFailureUrl("/cockpit/login");
        super.onAuthenticationFailure(request, response, exception);
    }
}
