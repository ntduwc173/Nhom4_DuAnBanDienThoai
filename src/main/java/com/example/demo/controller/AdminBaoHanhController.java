package com.example.demo.controller;

import com.example.demo.entity.BaoHanh;
import com.example.demo.repository.BaoHanhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/bao-hanh")
public class AdminBaoHanhController {

    private final BaoHanhRepository baoHanhRepository;

    @GetMapping("")
    public String danhSachBaoHanh(Model model) {
        List<BaoHanh> danhSach = baoHanhRepository.findAllByOrderByNgayYeuCauDesc();
        model.addAttribute("danhSachBaoHanh", danhSach);
        return "admin/admin-bao-hanh";
    }

    @PostMapping("/duyet")
    public String duyetBaoHanh(@RequestParam String maBaoHanh, 
                               @RequestParam String trangThai, 
                               RedirectAttributes redirectAttributes) {
        BaoHanh baoHanh = baoHanhRepository.findById(maBaoHanh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu bảo hành"));
        
        baoHanh.setTrangThai(trangThai);
        baoHanhRepository.save(baoHanh);
        
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái bảo hành thành công!");
        return "redirect:/admin/bao-hanh/chi-tiet/" + maBaoHanh;
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTietBaoHanh(@PathVariable("id") String maBaoHanh, Model model) {
        BaoHanh baoHanh = baoHanhRepository.findById(maBaoHanh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu bảo hành"));
        
        model.addAttribute("baoHanh", baoHanh);
        // Load chi tiết các sản phẩm trong đơn hàng
        model.addAttribute("chiTietList", baoHanh.getDonHang().getChiTietDonHangs());
        
        return "admin/admin-chi-tiet-bao-hanh";
    }
}
