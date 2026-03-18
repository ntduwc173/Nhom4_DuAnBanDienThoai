package com.example.demo.controller;

import com.example.demo.entity.Hang;
import com.example.demo.entity.SanPham;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final GioHangService gioHangService;

    // US01: Trang chủ - Danh sách sản phẩm dạng Grid
    @GetMapping("/")
    public String trangChu(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") int size,
                           @RequestParam(required = false) String hang,
                           Model model) {

        Page<SanPham> sanPhamPage;

        // US02: Lọc theo hãng
        if (hang != null && !hang.isEmpty()) {
            sanPhamPage = sanPhamService.locTheoHang(hang, page, size);
            model.addAttribute("hangDaChon", hang);
        } else {
            sanPhamPage = sanPhamService.getDanhSachSanPham(page, size);
        }

        List<Hang> danhSachHang = sanPhamService.getAllHang();

        model.addAttribute("sanPhams", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("totalItems", sanPhamPage.getTotalElements());
        model.addAttribute("danhSachHang", danhSachHang);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());

        return "trang-chu";
    }
}
