package com.example.demo.controller;

import com.example.demo.entity.ChiTietGioHang;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.MaGiamGia;
import com.example.demo.service.DonHangService;
import com.example.demo.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dat-hang")
public class DatHangController {

    private final DonHangService donHangService;
    private final GioHangService gioHangService;

    // US04, US05: Trang đặt hàng (form thông tin + chọn thanh toán)
    @GetMapping("")
    public String trangDatHang(Model model) {
        List<ChiTietGioHang> items = gioHangService.getChiTietGioHang();
        if (items.isEmpty()) {
            return "redirect:/gio-hang";
        }

        BigDecimal tongTien = gioHangService.getTongTienGioHang();

        model.addAttribute("gioHangItems", items);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());

        return "dat-hang";
    }

    // US04, US05: Xử lý đặt hàng
    @PostMapping("/xac-nhan")
    public String xacNhanDatHang(@RequestParam String tenKhach,
                                  @RequestParam String soDienThoai,
                                  @RequestParam String diaChi,
                                  @RequestParam String phuongThucThanhToan,
                                  @RequestParam(required = false) String maGiamGia,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        // US04: Validate thông tin
        if (tenKhach == null || tenKhach.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tên!");
            return "redirect:/dat-hang";
        }
        if (soDienThoai == null || !soDienThoai.matches("^(0[3|5|7|8|9])[0-9]{8}$")) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại không hợp lệ! (VD: 0912345678)");
            return "redirect:/dat-hang";
        }
        if (diaChi == null || diaChi.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập địa chỉ!");
            return "redirect:/dat-hang";
        }

        try {
            DonHang donHang = donHangService.taoDonHang(tenKhach, soDienThoai, diaChi,
                    phuongThucThanhToan, maGiamGia);
            redirectAttributes.addFlashAttribute("donHang", donHang);
            return "redirect:/dat-hang/thanh-cong?maDon=" + donHang.getMaDonHang();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dat-hang";
        }
    }

    // Trang đặt hàng thành công
    @GetMapping("/thanh-cong")
    public String thanhCong(@RequestParam String maDon, Model model) {
        donHangService.getDonHangById(maDon).ifPresent(donHang -> {
            model.addAttribute("donHang", donHang);
        });
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
        return "dat-hang-thanh-cong";
    }

    // US06: API kiểm tra mã giảm giá (AJAX)
    @PostMapping("/kiem-tra-ma-giam-gia")
    @ResponseBody
    public Map<String, Object> kiemTraMaGiamGia(@RequestParam String maGiamGia) {
        Map<String, Object> result = new HashMap<>();
        MaGiamGia maGG = donHangService.kiemTraMaGiamGia(maGiamGia);

        if (maGG != null) {
            BigDecimal tongTien = gioHangService.getTongTienGioHang();
            BigDecimal soTienGiam = tongTien.multiply(BigDecimal.valueOf(maGG.getPhanTramGiam() / 100))
                    .setScale(0, RoundingMode.HALF_UP);

            result.put("success", true);
            result.put("phanTramGiam", maGG.getPhanTramGiam());
            result.put("soTienGiam", soTienGiam);
            result.put("tongTienSauGiam", tongTien.subtract(soTienGiam));
            result.put("tenMa", maGG.getTenMa());
        } else {
            result.put("success", false);
            result.put("message", "Mã giảm giá không hợp lệ hoặc đã hết hạn!");
        }
        return result;
    }
}
