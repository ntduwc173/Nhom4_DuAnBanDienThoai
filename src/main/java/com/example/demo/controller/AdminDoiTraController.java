package com.example.demo.controller;

import com.example.demo.entity.DoiTra;
import com.example.demo.repository.DoiTraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/doi-tra")
public class AdminDoiTraController {

    private final DoiTraRepository doiTraRepository;

    @GetMapping("")
    public String danhSachDoiTra(Model model) {
        List<DoiTra> danhSach = doiTraRepository.findAllByOrderByNgayYeuCauDesc();
        model.addAttribute("danhSachDoiTra", danhSach);
        return "admin/admin-doi-tra";
    }

    @PostMapping("/duyet")
    public String duyetDoiTra(@RequestParam String maDoiTra, 
                              @RequestParam String trangThai, 
                              RedirectAttributes redirectAttributes) {
        DoiTra doiTra = doiTraRepository.findById(maDoiTra)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu đổi trả"));
        
        doiTra.setTrangThai(trangThai);
        doiTraRepository.save(doiTra);
        
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái đổi trả thành công!");
        return "redirect:/admin/doi-tra/chi-tiet/" + maDoiTra;
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTietDoiTra(@PathVariable("id") String maDoiTra, Model model) {
        DoiTra doiTra = doiTraRepository.findById(maDoiTra)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu đổi trả"));
        
        model.addAttribute("doiTra", doiTra);
        // Load chi tiết các sản phẩm trong đơn hàng
        model.addAttribute("chiTietList", doiTra.getDonHang().getChiTietDonHangs());
        
        return "admin/admin-chi-tiet-doi-tra";
    }
}
