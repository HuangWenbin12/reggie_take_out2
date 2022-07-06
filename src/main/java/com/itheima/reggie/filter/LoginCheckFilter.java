package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MARCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //1 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
        //2. 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动段发送短信
                "/user/login"//移动端登录
        };
        //3. 判断本次请求是否需要处理
        boolean check = check(urls,requestURI);
        //4. 如果不需要处理则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //5-1. 判断服务端登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("用户已登录：id为: {}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        //5. 判断客户端登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            Long empId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);
            log.info(empId.toString());
            log.info("用户已登录：id为: {}",request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
        //6. 如果未登录则返回登录结果，通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if(PATH_MARCHER.match(url,requestURI))
            {
                return true;
            }
        }
        return false;
    }
}
