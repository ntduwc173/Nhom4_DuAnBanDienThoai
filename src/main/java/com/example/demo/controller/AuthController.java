package com.example.demo.controller;

import com.example.demo.entity.TaiKhoan;
import com.example.demo.service.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final TaiKhoanService taiKhoanService;

    // Trang đăng nhập
    @GetMapping("/dang-nhap")
    public String trangDangNhap(HttpSession session) {
        // Nếu đã đăng nhập, chuyển về trang chủ
        if (session.getAttribute("taiKhoan") != null) {
            return "redirect:/";
        }
        return "dang-nhap";
    }

    // Xử lý đăng nhập
    @PostMapping("/dang-nhap")
    public String xuLyDangNhap(@RequestParam String tenDangNhap,
                                @RequestParam String matKhau,
                                HttpSession session,
                                Model model) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.dangNhap(tenDangNhap, matKhau);

        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            session.setAttribute("taiKhoan", taiKhoan);
            session.setAttribute("vaiTro", taiKhoan.getVaiTro());
            session.setAttribute("tenNguoiDung", taiKhoan.getKhachHang() != null ? taiKhoan.getKhachHang().getTen() : taiKhoan.getTenDangNhap());

            // Chuyển hướng theo vai trò
            if ("ADMIN".equals(taiKhoan.getVaiTro())) {
                return "redirect:/admin/san-pham";
            }
            return "redirect:/";
        }

        model.addAttribute("loi", "Tên đăng nhập hoặc mật khẩu không đúng!");
        return "dang-nhap";
    }

    // Trang đăng ký
    @GetMapping("/dang-ky")
    public String trangDangKy(HttpSession session) {
        if (session.getAttribute("taiKhoan") != null) {
            return "redirect:/";
        }
        return "dang-ky";
    }

    // Xử lý đăng ký
    @PostMapping("/dang-ky")
    public String xuLyDangKy(@RequestParam String tenDangNhap,
                              @RequestParam String matKhau,
                              @RequestParam String xacNhanMatKhau,
                              @RequestParam String hoTen,
                              @RequestParam String soDienThoai,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String diaChi,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        // Validate
        if (!matKhau.equals(xacNhanMatKhau)) {
            model.addAttribute("loi", "Mật khẩu xác nhận không khớp!");
            return "dang-ky";
        }

        if (matKhau.length() < 6) {
            model.addAttribute("loi", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "dang-ky";
        }

        try {
            taiKhoanService.dangKy(tenDangNhap, matKhau, hoTen, soDienThoai, email, diaChi);
            redirectAttributes.addFlashAttribute("thanhCong", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/dang-nhap";
        } catch (RuntimeException e) {
            model.addAttribute("loi", e.getMessage());
            return "dang-ky";
        }
    }

    // Đăng xuất
    @GetMapping("/dang-xuat")
    public String dangXuat(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
