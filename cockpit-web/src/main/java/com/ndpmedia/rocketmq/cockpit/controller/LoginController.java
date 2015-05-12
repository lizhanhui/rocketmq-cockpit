package com.ndpmedia.rocketmq.cockpit.controller;

import com.ndpmedia.rocketmq.cockpit.util.LoginConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;

/**
 * the login controller.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String showLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println(" first , we need open the login page .");
        HttpSession session = request.getSession();


        String redirectURL = request.getParameter(LoginConstant.REDIRECT_KEY);

        Object tokenObj = session.getAttribute(LoginConstant.TOKEN_IN_SESSION);

        // tokenObj != null means the current user has already logged in from cockpit.
        // redirectURL != null means this is a SSO login.
        // Thus we simply redirect back and append token to the redirect back URL.
        if (null != tokenObj && null != redirectURL) {
            String decodedRedirectURL = URLDecoder.decode(redirectURL, "UTF-8");
            StringBuilder stringBuilder = new StringBuilder(decodedRedirectURL);
            if (decodedRedirectURL.contains("?")) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append("token=" + tokenObj);
            response.sendRedirect(stringBuilder.toString());
            return null;
        }

        // If redirectURL != null, this is a SSO login. We need to set redirect url in session and start login
        // process.
        if (null != redirectURL) {
            request.getSession().setAttribute(LoginConstant.REDIRECT_URL_IN_SESSION, redirectURL);
            System.out.println("Redirect URL in session set");
        } else {
            System.out.println("Redirect URL not found: " + request.getRequestURL());
        }

        return "login";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String showHome() {
        return "home";
    }
}
