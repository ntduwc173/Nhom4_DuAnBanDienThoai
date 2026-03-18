package com.example.demo.controller;

import com.example.demo.entity.ChiTietDonHang;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.HoaDon;
import com.example.demo.service.GioHangService;
import com.example.demo.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/don-hang")
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final GioHangService gioHangService;

    // US15: Trang chi tiết đơn hàng (có nút "In hóa đơn")
    @GetMapping("/chi-tiet/{maDonHang}")
    public String chiTietDonHang(@PathVariable String maDonHang, Model model) {
        Optional<DonHang> donHangOpt = hoaDonService.getDonHangById(maDonHang);

        if (donHangOpt.isEmpty()) {
            return "redirect:/";
        }

        DonHang donHang = donHangOpt.get();
        List<ChiTietDonHang> chiTietList = hoaDonService.getChiTietDonHang(maDonHang);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());

        return "chi-tiet-don-hang";
    }

    // US15: In hóa đơn (mở tab mới - UI đen trắng)
    @GetMapping("/in-hoa-don/{maDonHang}")
    public String inHoaDon(@PathVariable String maDonHang, Model model) {
        Optional<DonHang> donHangOpt = hoaDonService.getDonHangById(maDonHang);

        if (donHangOpt.isEmpty()) {
            return "redirect:/";
        }

        DonHang donHang = donHangOpt.get();
        List<ChiTietDonHang> chiTietList = hoaDonService.getChiTietDonHang(maDonHang);

        // Tạo hoặc lấy hóa đơn
        HoaDon hoaDon = hoaDonService.layHoaDon(maDonHang);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);
        model.addAttribute("hoaDon", hoaDon);

        return "in-hoa-don";
    }
}
