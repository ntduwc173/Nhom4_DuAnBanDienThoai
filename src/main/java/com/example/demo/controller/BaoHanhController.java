package com.example.demo.controller;

import com.example.demo.entity.BaoHanh;
import com.example.demo.entity.DonHang;
import com.example.demo.service.BaoHanhService;
import com.example.demo.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bao-hanh")
public class BaoHanhController {

    private final BaoHanhService baoHanhService;
    private final GioHangService gioHangService;

    // US10: Trang bảo hành
    @GetMapping("")
    public String trangBaoHanh(Model model) {
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
        return "bao-hanh";
    }

    // US10: Kiểm tra thời hạn bảo hành bằng mã đơn/SĐT
    @PostMapping("/kiem-tra")
    public String kiemTraBaoHanh(@RequestParam String maDonHangOrSdt,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        DonHang donHang = baoHanhService.kiemTraBaoHanh(maDonHangOrSdt);

        if (donHang != null) {
            long soThangConLai = baoHanhService.tinhSoThangConLai(donHang);
            model.addAttribute("donHang", donHang);
            model.addAttribute("soThangConLai", soThangConLai);
            model.addAttribute("maDonHangOrSdt", maDonHangOrSdt);
            model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
            return "bao-hanh";
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Không tìm thấy đơn hàng hoặc đơn hàng đã hết hạn bảo hành (quá 12 tháng). " +
                    "Chỉ đơn hàng có trạng thái \"Hoàn thành\" và trong thời hạn 12 tháng mới được bảo hành.");
            return "redirect:/bao-hanh";
        }
    }

    // US10: Gửi yêu cầu bảo hành
    @PostMapping("/gui-yeu-cau")
    public String guiYeuCauBaoHanh(@RequestParam String maDonHang,
                                    @RequestParam String moTaLoi,
                                    RedirectAttributes redirectAttributes) {
        if (moTaLoi == null || moTaLoi.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng mô tả lỗi máy!");
            return "redirect:/bao-hanh";
        }

        try {
            BaoHanh baoHanh = baoHanhService.taoYeuCauBaoHanh(maDonHang, moTaLoi);
            redirectAttributes.addFlashAttribute("success",
                    "Gửi yêu cầu bảo hành thành công! Mã bảo hành: " + baoHanh.getMaBaoHanh() +
                    ". Chúng tôi sẽ liên hệ bạn trong thời gian sớm nhất.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bao-hanh";
    }
}
