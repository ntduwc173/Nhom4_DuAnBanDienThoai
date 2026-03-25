package com.example.demo.controller;

import com.example.demo.entity.Hang;
import com.example.demo.entity.SanPham;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
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

    // US01: Trang chủ - Danh sách sản phẩm + Tìm kiếm + Sắp xếp
    @GetMapping("/")
    public String trangChu(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") int size,
                           @RequestParam(required = false) String hang,
                           @RequestParam(required = false) String loai,
                           @RequestParam(required = false) String tuKhoa,
                           @RequestParam(required = false) String sapXep,
                           HttpSession session,
                           Model model) {

        Page<SanPham> sanPhamPage;

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            // Tìm kiếm
            sanPhamPage = sanPhamService.timKiem(tuKhoa, hang, page, size, sapXep);
            model.addAttribute("tuKhoa", tuKhoa);
        } else if (loai != null && !loai.isEmpty()) {
            // Lọc theo Loại sản phẩm
            sanPhamPage = sanPhamService.getDanhSachSanPhamTheoTenLoai(loai, page, size, sapXep);
            model.addAttribute("loaiDaChon", loai);
        } else if (hang != null && !hang.isEmpty()) {
            // Lọc theo hãng
            sanPhamPage = sanPhamService.locTheoHang(hang, page, size, sapXep);
        } else {
            // Tất cả sản phẩm
            sanPhamPage = sanPhamService.getDanhSachSanPham(page, size, sapXep);
        }

        if (hang != null && !hang.isEmpty()) {
            model.addAttribute("hangDaChon", hang);
        }

        List<Hang> danhSachHang = sanPhamService.getAllHang();

        model.addAttribute("sanPhams", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("totalItems", sanPhamPage.getTotalElements());
        model.addAttribute("danhSachHang", danhSachHang);
        model.addAttribute("sapXep", sapXep);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());

        return "trang-chu";
    }

    // Chi tiết sản phẩm
    @GetMapping("/san-pham/{maSanPham}")
    public String chiTietSanPham(@PathVariable String maSanPham,
                                  HttpSession session,
                                  Model model) {
        return sanPhamService.getSanPhamById(maSanPham)
                .map(sp -> {
                    model.addAttribute("sanPham", sp);
                    model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
                    return "chi-tiet-san-pham";
                })
                .orElse("redirect:/");
    }
}
