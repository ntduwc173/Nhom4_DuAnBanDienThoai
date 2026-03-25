package com.example.demo.controller;

import com.example.demo.entity.DonHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.KhachHangRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DonHangRepository donHangRepository;
    private final KhachHangRepository khachHangRepository;

    @GetMapping("/debug-db")
    public Map<String, Object> debug(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        // All customers
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        result.put("khachHangs", khachHangs);
        
        // All orders
        List<DonHang> donHangs = donHangRepository.findAll();
        result.put("donHangs", donHangs);
        
        // Current user session
        Object taiKhoan = session.getAttribute("taiKhoan");
        result.put("session_taiKhoan", taiKhoan);
        
        return result;
    }
}
