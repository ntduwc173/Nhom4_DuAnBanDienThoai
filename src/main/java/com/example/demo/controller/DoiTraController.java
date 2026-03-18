package com.example.demo.controller;

import com.example.demo.entity.DoiTra;
import com.example.demo.entity.DonHang;
import com.example.demo.service.DoiTraService;
import com.example.demo.service.DonHangService;
import com.example.demo.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doi-tra")
public class DoiTraController {

    private final DoiTraService doiTraService;
    private final DonHangService donHangService;
    private final GioHangService gioHangService;

    // US09: Trang đổi trả
    @GetMapping("")
    public String trangDoiTra(Model model) {
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
        return "doi-tra";
    }

    // US09: Tìm đơn hàng để đổi trả
    @PostMapping("/tim-don")
    public String timDonHang(@RequestParam String maDonHang,
                              @RequestParam String soDienThoai,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<DonHang> donHangOpt = donHangService.timDonHangDeDoiTra(maDonHang, soDienThoai);

        if (donHangOpt.isPresent()) {
            model.addAttribute("donHang", donHangOpt.get());
            model.addAttribute("maDonHang", maDonHang);
            model.addAttribute("soDienThoai", soDienThoai);
            model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());
            return "doi-tra";
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Không tìm thấy đơn hàng hoặc đơn hàng chưa hoàn thành. Chỉ đơn hàng có trạng thái \"Hoàn thành\" mới được đổi trả.");
            return "redirect:/doi-tra";
        }
    }

    // US09: Gửi yêu cầu đổi trả
    @PostMapping("/gui-yeu-cau")
    public String guiYeuCauDoiTra(@RequestParam String maDonHang,
                                   @RequestParam String lyDo,
                                   RedirectAttributes redirectAttributes) {
        try {
            DoiTra doiTra = doiTraService.taoYeuCauDoiTra(maDonHang, lyDo);
            redirectAttributes.addFlashAttribute("success",
                    "Gửi yêu cầu đổi trả thành công! Mã đổi trả: " + doiTra.getMaDoiTra());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/doi-tra";
    }
}
