package com.example.demo.controller;

import com.example.demo.entity.ChiTietGioHang;
import com.example.demo.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gio-hang")
public class GioHangController {

    private final GioHangService gioHangService;

    // US03: Thêm sản phẩm vào giỏ hàng
    @PostMapping("/them")
    public String themVaoGioHang(@RequestParam String maSanPham,
                                  @RequestParam(defaultValue = "1") int soLuong,
                                  RedirectAttributes redirectAttributes,
                                  @RequestHeader(value = "referer", required = false) String referer) {
        try {
            gioHangService.themVaoGioHang(maSanPham, soLuong);
            redirectAttributes.addFlashAttribute("thongBaoGioHang", "Thêm vào giỏ hàng thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    // Xem giỏ hàng
    @GetMapping("")
    public String xemGioHang(Model model) {
        List<ChiTietGioHang> items = gioHangService.getChiTietGioHang();
        BigDecimal tongTien = gioHangService.getTongTienGioHang();

        model.addAttribute("gioHangItems", items);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("soLuongGioHang", gioHangService.getSoLuongTrongGio());

        return "gio-hang";
    }

    // Xóa sản phẩm khỏi giỏ
    @PostMapping("/xoa")
    public String xoaKhoiGioHang(@RequestParam String maCTGH) {
        gioHangService.xoaKhoiGioHang(maCTGH);
        return "redirect:/gio-hang";
    }

    // Cập nhật số lượng
    @PostMapping("/cap-nhat")
    public String capNhatSoLuong(@RequestParam String maCTGH,
                                  @RequestParam int soLuong,
                                  RedirectAttributes redirectAttributes) {
        try {
            gioHangService.capNhatSoLuong(maCTGH, soLuong);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
        }
        return "redirect:/gio-hang";
    }

    // API lấy số lượng giỏ hàng (cho AJAX)
    @GetMapping("/so-luong")
    @ResponseBody
    public Map<String, Integer> getSoLuong() {
        Map<String, Integer> result = new HashMap<>();
        result.put("soLuong", gioHangService.getSoLuongTrongGio());
        return result;
    }
}
