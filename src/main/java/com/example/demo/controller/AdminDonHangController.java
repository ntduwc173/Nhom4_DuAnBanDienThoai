package com.example.demo.controller;

import com.example.demo.entity.ChiTietDonHang;
import com.example.demo.entity.DonHang;
import com.example.demo.repository.ChiTietDonHangRepository;
import com.example.demo.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/don-hang")
public class AdminDonHangController {

    private final DonHangService donHangService;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    // US13: Xem danh sách đơn hàng
    @GetMapping("")
    public String danhSachDonHang(Model model) {
        List<DonHang> donHangs = donHangService.getAllDonHang();
        model.addAttribute("donHangs", donHangs);
        return "admin/admin-don-hang";
    }

    // US14: Trang chi tiết đơn hàng (cập nhật trạng thái)
    @GetMapping("/chi-tiet/{maDonHang}")
    public String chiTietDonHang(@PathVariable String maDonHang, Model model) {
        Optional<DonHang> donHangOpt = donHangService.getDonHangById(maDonHang);

        if (donHangOpt.isEmpty()) {
            return "redirect:/admin/don-hang";
        }

        DonHang donHang = donHangOpt.get();
        List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);

        return "admin/admin-chi-tiet-don-hang";
    }

    // US14: Cập nhật trạng thái đơn hàng
    @PostMapping("/cap-nhat-trang-thai")
    public String capNhatTrangThai(@RequestParam String maDonHang,
                                    @RequestParam String trangThai,
                                    RedirectAttributes redirectAttributes) {
        try {
            donHangService.capNhatTrangThai(maDonHang, trangThai);
            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật trạng thái đơn hàng " + maDonHang + " thành: " + trangThai);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/don-hang/chi-tiet/" + maDonHang;
    }

    // US14: Đánh dấu đã thanh toán
    @PostMapping("/da-thanh-toan")
    public String daThanhToan(@RequestParam String maDonHang,
                               @RequestParam(required = false) Boolean daThanhToan,
                               RedirectAttributes redirectAttributes) {
        try {
            donHangService.capNhatDaThanhToan(maDonHang, daThanhToan != null && daThanhToan);
            String msg = (daThanhToan != null && daThanhToan)
                    ? "Đã đánh dấu đơn " + maDonHang + " là ĐÃ THANH TOÁN"
                    : "Đã bỏ đánh dấu thanh toán cho đơn " + maDonHang;
            redirectAttributes.addFlashAttribute("success", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/don-hang/chi-tiet/" + maDonHang;
    }
}
