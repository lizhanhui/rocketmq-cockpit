package com.ndpmedia.rocketmq.authentication;

import com.google.code.kaptcha.Constants;
import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * personal filter.just for check retry times.
 */
public class RocketMQLoginFilter implements Filter, LoginConstant {
    private final Logger logger = LoggerFactory.getLogger(RocketMQLoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        logger.debug("[personal filter]check verification code and retry times. ");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();

        //Check SSO.
        String redirectURL = request.getParameter(LoginConstant.REDIRECT_KEY);
        Object tokenObj = session.getAttribute(LoginConstant.TOKEN_IN_SESSION);
        if (null != tokenObj && null != redirectURL) {
            String decodedRedirectURL = URLDecoder.decode(redirectURL, "UTF-8");
            StringBuilder stringBuilder = new StringBuilder(decodedRedirectURL);
            if (decodedRedirectURL.contains("?")) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append("token=" + tokenObj);
            logger.info("Redirect for SSO.");
            response.sendRedirect(stringBuilder.toString());
            return;
        }

        session.removeAttribute(LOGIN_SESSION_ERROR_KEY);

        if (checkRetryTimes(request)) {
            logger.warn("[personal filter] retry too many times !");
            session.setAttribute(LOGIN_SESSION_ERROR_KEY, LOGIN_TOO_MANY_TIMES_MSG);
            request.getRequestDispatcher(LOGIN_PAGE_PATH).forward(request, response);
            return;
        }

        if (checkVerificationCode(request)) {
            logger.warn("[personal filter] verification code is not right !");
            session.setAttribute(LOGIN_SESSION_ERROR_KEY, LOGIN_VERIFICATION_CODE_WRONG);
            request.getRequestDispatcher(LOGIN_PAGE_PATH).forward(request, response);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * check how many times try to login.
     *
     * @param request
     * @return if login too many times,return true;
     */
    private boolean checkRetryTimes(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = request.getParameter(LOGIN_PARAMETER_USERNAME);
        if (null != session && null != session.getAttribute(username)) {
            int retryTime = Integer.parseInt("" + session.getAttribute(username));
            int retryTimeMAX = FIVE;
            if (retryTime >= retryTimeMAX) {
                return true;
            }
        }
        return false;
    }

    /**
     * check the input verification code
     *
     * @param request
     * @return verify ok return false;
     */
    private boolean checkVerificationCode(HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            String kaptcha = request.getParameter(LOGIN_PARAMETER_KAPTCHA);
            if (null == kaptcha || kaptcha.isEmpty())
                return false;
            logger.debug(LOGIN_PARAMETER_KAPTCHA + " [=] " + kaptcha);
            String code = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            logger.debug(Constants.KAPTCHA_SESSION_KEY + " [=] " + code);
            if (kaptcha.equals(code))
                return false;
        } catch (Exception e) {
            logger.warn("[personal filter] try to check verification code failed !");
        }
        return true;
    }
}
