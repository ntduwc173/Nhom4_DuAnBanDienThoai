package com.example.demo.controller;

import com.example.demo.entity.SanPham;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pre-order")
public class PreOrderController {

    private final SanPhamService sanPhamService;
    private final GioHangService gioHangService;

    // US08: Trang Pre-order
    @GetMapping("")
    public String trangPreOrder(Model model) {
        List<SanPham> preOrderProducts = sanPhamService.getPreOrderProducts();
        model.addAttribute("preOrderProducts", preOrderProducts);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
        return "pre-order";
    }
}
