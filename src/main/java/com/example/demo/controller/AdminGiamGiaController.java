package com.example.demo.controller;

import com.example.demo.entity.MaGiamGia;
import com.example.demo.repository.MaGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/giam-gia")
public class AdminGiamGiaController {

    private final MaGiamGiaRepository maGiamGiaRepository;

    // US12: Trang quản lý chương trình giảm giá
    @GetMapping("")
    public String trangQuanLy(Model model) {
        List<MaGiamGia> giamGias = maGiamGiaRepository.findAll();

        // Tự động cập nhật trạng thái dựa trên ngày
        LocalDateTime now = LocalDateTime.now();
        for (MaGiamGia gg : giamGias) {
            if (gg.getNgayBatDau() != null && gg.getNgayKetThuc() != null) {
                if (now.isBefore(gg.getNgayBatDau())) {
                    if (!"Chưa bắt đầu".equals(gg.getTrangThai())) {
                        gg.setTrangThai("Chưa bắt đầu");
                        maGiamGiaRepository.save(gg);
                    }
                } else if (now.isAfter(gg.getNgayKetThuc())) {
                    if (!"Hết hạn".equals(gg.getTrangThai())) {
                        gg.setTrangThai("Hết hạn");
                        maGiamGiaRepository.save(gg);
                    }
                } else {
                    if (!"Hoạt động".equals(gg.getTrangThai())) {
                        gg.setTrangThai("Hoạt động");
                        maGiamGiaRepository.save(gg);
                    }
                }
            }
        }

        model.addAttribute("giamGias", giamGias);
        return "admin/admin-giam-gia";
    }

    // US12: Tạo chương trình giảm giá mới
    @PostMapping("/them")
    public String themGiamGia(@RequestParam(required = false) String maGiamGia,
                               @RequestParam String tenMa,
                               @RequestParam Double phanTramGiam,
                               @RequestParam String ngayBatDau,
                               @RequestParam String ngayKetThuc,
                               @RequestParam(required = false) BigDecimal dieuKienToiThieu,
                               @RequestParam(required = false) Boolean tuDong,
                               RedirectAttributes redirectAttributes) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime batDau = LocalDateTime.parse(ngayBatDau, formatter);
            LocalDateTime ketThuc = LocalDateTime.parse(ngayKetThuc, formatter);

            MaGiamGia gg = new MaGiamGia();
            if (maGiamGia != null && !maGiamGia.trim().isEmpty()) {
                gg.setMaGiamGia(maGiamGia.toUpperCase().trim());
            } else {
                gg.setMaGiamGia("GG" + System.currentTimeMillis());
            }
            gg.setTenMa(tenMa);
            gg.setPhanTramGiam(phanTramGiam);
            gg.setNgayBatDau(batDau);
            gg.setNgayKetThuc(ketThuc);
            gg.setDieuKienToiThieu(dieuKienToiThieu);
            gg.setTuDong(tuDong != null && tuDong);

            // Tự xác định trạng thái
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(batDau)) {
                gg.setTrangThai("Chưa bắt đầu");
            } else if (now.isAfter(ketThuc)) {
                gg.setTrangThai("Hết hạn");
            } else {
                gg.setTrangThai("Hoạt động");
            }

            maGiamGiaRepository.save(gg);
            redirectAttributes.addFlashAttribute("success", "Tạo chương trình giảm giá thành công: " + tenMa);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/giam-gia";
    }

    // US12: Xóa chương trình giảm giá
    @PostMapping("/xoa")
    public String xoaGiamGia(@RequestParam String maGiamGia,
                              RedirectAttributes redirectAttributes) {
        try {
            maGiamGiaRepository.deleteById(maGiamGia);
            redirectAttributes.addFlashAttribute("success", "Xóa chương trình giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/admin/giam-gia";
    }
}
