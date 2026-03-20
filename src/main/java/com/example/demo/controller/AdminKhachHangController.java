package com.example.demo.controller;

import com.example.demo.entity.KhachHang;
import com.example.demo.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/khach-hang")
public class AdminKhachHangController {

    private final KhachHangRepository khachHangRepository;

    @GetMapping("")
    public String danhSachKhachHang(Model model) {
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        model.addAttribute("khachHangs", khachHangs);
        return "admin/admin-khach-hang";
    }
}
