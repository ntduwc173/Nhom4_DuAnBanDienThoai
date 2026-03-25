package com.example.demo.controller;

import com.example.demo.entity.Hang;
import com.example.demo.entity.LoaiSanPham;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.HangRepository;
import com.example.demo.repository.LoaiSanPhamRepository;
import com.example.demo.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/san-pham")
public class AdminSanPhamController {

    private final SanPhamRepository sanPhamRepository;
    private final HangRepository hangRepository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;

    // US11: Trang quản lý sản phẩm
    @GetMapping("")
    public String trangQuanLy(Model model) {
        List<SanPham> sanPhams = sanPhamRepository.findAll();
        List<Hang> hangs = hangRepository.findAll();
        List<LoaiSanPham> loais = loaiSanPhamRepository.findAll();

        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("hangs", hangs);
        model.addAttribute("loais", loais);
        return "admin/admin-san-pham";
    }

    // US11: Thêm sản phẩm mới
    @PostMapping("/them")
    public String themSanPham(@RequestParam String tenSanPham,
                               @RequestParam String maHang,
                               @RequestParam BigDecimal gia,
                               @RequestParam String maLoai,
                               @RequestParam(required = false) String hinhAnh,
                               @RequestParam(required = false) String moTa,
                               @RequestParam(required = false) Integer soLuong,
                               @RequestParam(required = false) Boolean preOrder,
                               RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = new SanPham();
            sp.setMaSanPham("SP" + System.currentTimeMillis());
            sp.setTenSanPham(tenSanPham);
            sp.setGia(gia);
            sp.setHinhAnh(hinhAnh);
            sp.setMoTa(moTa);
            sp.setSoLuong(soLuong != null ? soLuong : 0);
            sp.setPreOrder(preOrder != null && preOrder);

            hangRepository.findById(maHang).ifPresent(sp::setHang);
            loaiSanPhamRepository.findById(maLoai).ifPresent(sp::setLoaiSanPham);

            sanPhamRepository.save(sp);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công: " + tenSanPham);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/san-pham";
    }

    // US11: Cập nhật sản phẩm
    @PostMapping("/cap-nhat")
    public String capNhatSanPham(@RequestParam String maSanPham,
                                  @RequestParam String tenSanPham,
                                  @RequestParam String maHang,
                                  @RequestParam BigDecimal gia,
                                  @RequestParam String maLoai,
                                  @RequestParam(required = false) String hinhAnh,
                                  @RequestParam(required = false) String moTa,
                                  @RequestParam(required = false) Integer soLuong,
                                  @RequestParam(required = false) Boolean preOrder,
                                  RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamRepository.findById(maSanPham)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

            sp.setTenSanPham(tenSanPham);
            sp.setGia(gia);
            sp.setMoTa(moTa);
            sp.setSoLuong(soLuong != null ? soLuong : 0);
            sp.setPreOrder(preOrder != null && preOrder);

            if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
                sp.setHinhAnh(hinhAnh);
            }

            hangRepository.findById(maHang).ifPresent(sp::setHang);
            loaiSanPhamRepository.findById(maLoai).ifPresent(sp::setLoaiSanPham);

            sanPhamRepository.save(sp);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công: " + tenSanPham);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/san-pham";
    }

    // US11: Xóa sản phẩm
    @PostMapping("/xoa")
    public String xoaSanPham(@RequestParam String maSanPham,
                              RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamRepository.findById(maSanPham)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
            sanPhamRepository.delete(sp);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công: " + sp.getTenSanPham());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/admin/san-pham";
    }
}
