package com.example.demo.controller;

import com.example.demo.entity.KhachHang;
import com.example.demo.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/khach-hang")
public class AdminKhachHangController {

    private final KhachHangRepository khachHangRepository;

    // Danh sách khách hàng (có tìm kiếm)
    @GetMapping("")
    public String danhSachKhachHang(@RequestParam(required = false) String tuKhoa, Model model) {
        List<KhachHang> khachHangs;
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            khachHangs = khachHangRepository.findByTenContainingIgnoreCaseOrSoDienThoaiContaining(tuKhoa, tuKhoa);
            model.addAttribute("tuKhoa", tuKhoa);
        } else {
            khachHangs = khachHangRepository.findAll();
        }
        model.addAttribute("khachHangs", khachHangs);
        return "admin/admin-khach-hang";
    }

    // Thêm khách hàng
    @PostMapping("/them")
    public String themKhachHang(@RequestParam String ten,
                                 @RequestParam String soDienThoai,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String diaChi,
                                 RedirectAttributes redirectAttributes) {
        try {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang(UUID.randomUUID().toString().substring(0, 20));
            kh.setTen(ten);
            kh.setSoDienThoai(soDienThoai);
            kh.setEmail(email);
            kh.setDiaChi(diaChi);
            khachHangRepository.save(kh);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công: " + ten);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/khach-hang";
    }

    // Cập nhật khách hàng
    @PostMapping("/cap-nhat")
    public String capNhatKhachHang(@RequestParam String maKhachHang,
                                    @RequestParam String ten,
                                    @RequestParam String soDienThoai,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) String diaChi,
                                    RedirectAttributes redirectAttributes) {
        try {
            KhachHang kh = khachHangRepository.findById(maKhachHang)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
            kh.setTen(ten);
            kh.setSoDienThoai(soDienThoai);
            kh.setEmail(email);
            kh.setDiaChi(diaChi);
            khachHangRepository.save(kh);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/khach-hang";
    }

    // Xóa khách hàng
    @PostMapping("/xoa")
    public String xoaKhachHang(@RequestParam String maKhachHang,
                                RedirectAttributes redirectAttributes) {
        try {
            khachHangRepository.deleteById(maKhachHang);
            redirectAttributes.addFlashAttribute("success", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa khách hàng (có đơn hàng liên quan): " + e.getMessage());
        }
        return "redirect:/admin/khach-hang";
    }
}
