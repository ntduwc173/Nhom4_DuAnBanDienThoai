package com.example.demo.controller;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tai-khoan")
public class ProfileController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;

    // Trang thông tin cá nhân
    @GetMapping("")
    public String trangCaNhan(HttpSession session, Model model) {
        TaiKhoan taiKhoan = (TaiKhoan) session.getAttribute("taiKhoan");
        if (taiKhoan == null) return "redirect:/dang-nhap";

        // Reload from DB
        taiKhoanRepository.findById(taiKhoan.getMaTaiKhoan()).ifPresent(tk -> {
            model.addAttribute("taiKhoan", tk);
            model.addAttribute("khachHang", tk.getKhachHang());
        });

        return "tai-khoan";
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/cap-nhat")
    public String capNhatThongTin(@RequestParam String ten,
                                   @RequestParam String soDienThoai,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String diaChi,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        TaiKhoan taiKhoan = (TaiKhoan) session.getAttribute("taiKhoan");
        if (taiKhoan == null) return "redirect:/dang-nhap";

        try {
            KhachHang kh = khachHangRepository.findById(taiKhoan.getKhachHang().getMaKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin!"));
            kh.setTen(ten);
            kh.setSoDienThoai(soDienThoai);
            kh.setEmail(email);
            kh.setDiaChi(diaChi);
            khachHangRepository.save(kh);

            session.setAttribute("tenNguoiDung", ten);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/tai-khoan";
    }

    // Đổi mật khẩu
    @PostMapping("/doi-mat-khau")
    public String doiMatKhau(@RequestParam String matKhauCu,
                              @RequestParam String matKhauMoi,
                              @RequestParam String xacNhanMatKhauMoi,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        TaiKhoan taiKhoan = (TaiKhoan) session.getAttribute("taiKhoan");
        if (taiKhoan == null) return "redirect:/dang-nhap";

        TaiKhoan tk = taiKhoanRepository.findById(taiKhoan.getMaTaiKhoan()).orElse(null);
        if (tk == null) return "redirect:/dang-nhap";

        if (!tk.getMatKhau().equals(matKhauCu)) {
            redirectAttributes.addFlashAttribute("errorPw", "Mật khẩu cũ không đúng!");
            return "redirect:/tai-khoan";
        }

        if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            redirectAttributes.addFlashAttribute("errorPw", "Mật khẩu mới không khớp!");
            return "redirect:/tai-khoan";
        }

        if (matKhauMoi.length() < 6) {
            redirectAttributes.addFlashAttribute("errorPw", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "redirect:/tai-khoan";
        }

        tk.setMatKhau(matKhauMoi);
        taiKhoanRepository.save(tk);
        session.setAttribute("taiKhoan", tk);
        redirectAttributes.addFlashAttribute("successPw", "Đổi mật khẩu thành công!");
        return "redirect:/tai-khoan";
    }
}
