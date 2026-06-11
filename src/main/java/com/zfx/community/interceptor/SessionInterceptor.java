package com.zfx.community.interceptor;

import com.zfx.community.mapper.UserMapper;
import com.zfx.community.model.Ad;
import com.zfx.community.model.Nav;
import com.zfx.community.model.User;
import com.zfx.community.model.UserExample;
import com.zfx.community.service.AdService;
import com.zfx.community.service.NavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会话拦截器 - Cookie 鉴权 + 全局数据注入
 */
@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdService adService;

    @Autowired
    private NavService navService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 加载全局数据（广告、导航）
        List<Ad> ads = adService.listByPos("side");
        List<Nav> navs = navService.list();
        request.getServletContext().setAttribute("ads", ads);
        request.getServletContext().setAttribute("navs", navs);

        // Cookie 鉴权
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (!users.isEmpty()) {
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // no-op
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // no-op
    }
}
