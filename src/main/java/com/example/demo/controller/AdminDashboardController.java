package com.example.demo.controller;

import com.example.demo.entity.DonHang;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final DonHangRepository donHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @GetMapping("")
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long tongSanPham = sanPhamRepository.count();
        long tongKhachHang = khachHangRepository.count();
        long tongDonHang = donHangRepository.count();
        long tongTaiKhoan = taiKhoanRepository.count();

        List<DonHang> allDonHang = donHangRepository.findAllByOrderByNgayDatDesc();

        // Tính tổng doanh thu
        BigDecimal tongDoanhThu = allDonHang.stream()
                .filter(dh -> dh.getTrangThai() != null && dh.getTrangThai().startsWith("Ho"))
                .map(DonHang::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đếm theo trạng thái (Kết hợp xử lý lỗi font chuỗi ký tự cũ trong DB)
        long donChoXuLy = allDonHang.stream().filter(dh -> 
                dh.getTrangThai() != null && dh.getTrangThai().toLowerCase().startsWith("ch")).count();
        long donDangShip = allDonHang.stream().filter(dh -> 
                dh.getTrangThai() != null && (dh.getTrangThai().startsWith("Đang") || dh.getTrangThai().startsWith("Dang"))).count();
        long donHoanThanh = allDonHang.stream().filter(dh -> 
                dh.getTrangThai() != null && dh.getTrangThai().startsWith("Ho")).count();
        long donHuy = allDonHang.stream().filter(dh -> 
                dh.getTrangThai() != null && dh.getTrangThai().startsWith("H") && !dh.getTrangThai().startsWith("Ho")).count();

        // Đơn hàng gần đây (5 đơn)
        List<DonHang> donHangGanDay = allDonHang.stream().limit(5).toList();

        model.addAttribute("tongSanPham", tongSanPham);
        model.addAttribute("tongKhachHang", tongKhachHang);
        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("tongTaiKhoan", tongTaiKhoan);
        model.addAttribute("tongDoanhThu", tongDoanhThu);
        model.addAttribute("donChoXuLy", donChoXuLy);
        model.addAttribute("donDangShip", donDangShip);
        model.addAttribute("donHoanThanh", donHoanThanh);
        model.addAttribute("donHuy", donHuy);
        model.addAttribute("donHangGanDay", donHangGanDay);

        return "admin/admin-dashboard";
    }
}
