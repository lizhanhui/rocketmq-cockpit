package com.ndpmedia.rocketmq.cockpit.util;

/**
 * constant for login action.
 */
public interface LoginConstant extends Constant {

    String LOGIN_TOO_MANY_TIMES_MSG = " you try too many times !";

    String LOGIN_VERIFICATION_CODE_WRONG = " please check your verification code !";

    String LOGIN_SESSION_ERROR_KEY = "errorMSG";

    String LOGIN_PARAMETER_USERNAME = "j_username";

    String LOGIN_PARAMETER_PASSWORD = "j_password";

    String LOGIN_PARAMETER_AUTHORITY = "j_authority";

    String LOGIN_PARAMETER_KAPTCHA = "kaptcha";

    String LOGIN_PAGE_PATH = "/cockpit/login.jsp";

    String LOGIN_SSO_KEY_USERNAME = "SSO_USER_DETAIL_USERNAME";

    String LOGIN_SSO_KEY_PASSWORD = "SSO_USER_DETAIL_PASSWORD";

    String LOGIN_SSO_KEY_AUTHORITY = "SSO_USER_DETAIL_AUTHORITY";

    String PROPERTIES_KEY_JDBC_DRIVER = "jdbc.driverClassName";

    String PROPERTIES_KEY_JDBC_URL = "";

    String COOKIE_ENCRYPTION_KEY = "C0ckp1t";

    String ACCESS_DENIED_MSG = "ACCESS_DENIED_MSG";

    String COCKPIT_USER_KEY = "cockpit_user_in_session";

    String REDIRECT_KEY = "redirect";
    String REDIRECT_URL_IN_SESSION = "redirect_url_in_session";
    String TOKEN_IN_SESSION = "token_in_session";
    String HANDLE_IN_SESSION = "handle_in_session";

    String IS_ADMIN_IN_SESSION = "is_admin_in_session";
}
