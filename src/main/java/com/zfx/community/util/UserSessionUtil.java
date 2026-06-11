package com.zfx.community.util;

import com.zfx.community.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户会话工具类
 */
public final class UserSessionUtil {

    public static final String SESSION_KEY_USER = "user";

    private UserSessionUtil() {
        // utility class
    }

    /**
     * 从 Session 获取当前登录用户
     */
    public static User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(SESSION_KEY_USER);
    }

    /**
     * 检查是否已登录
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * 设置当前登录用户到 Session
     */
    public static void setCurrentUser(HttpServletRequest request, User user) {
        request.getSession().setAttribute(SESSION_KEY_USER, user);
    }

    /**
     * 清除 Session 中的用户信息
     */
    public static void removeCurrentUser(HttpServletRequest request) {
        request.getSession().removeAttribute(SESSION_KEY_USER);
    }
}
