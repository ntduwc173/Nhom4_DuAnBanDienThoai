package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String vaiTro = (String) session.getAttribute("vaiTro");

        if (!"ADMIN".equals(vaiTro)) {
            response.sendRedirect("/dang-nhap");
            return false;
        }
        return true;
    }
}
