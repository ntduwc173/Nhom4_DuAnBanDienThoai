package com.example.demo.service;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.TaiKhoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;

    // Đăng ký tài khoản mới
    @Transactional
    public TaiKhoan dangKy(String tenDangNhap, String matKhau, String hoTen, String soDienThoai, String email, String diaChi) {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (taiKhoanRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        // Tạo khách hàng
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang("KH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .ten(hoTen)
                .soDienThoai(soDienThoai)
                .email(email)
                .diaChi(diaChi)
                .build();
        khachHangRepository.save(khachHang);

        // Tạo tài khoản
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .maTaiKhoan("TK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .tenDangNhap(tenDangNhap)
                .matKhau(matKhau)
                .khachHang(khachHang)
                .vaiTro("KHACH_HANG")
                .build();
        return taiKhoanRepository.save(taiKhoan);
    }

    // Đăng nhập
    public Optional<TaiKhoan> dangNhap(String tenDangNhap, String matKhau) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
        if (taiKhoanOpt.isPresent() && taiKhoanOpt.get().getMatKhau().equals(matKhau)) {
            return taiKhoanOpt;
        }
        return Optional.empty();
    }

    // Kiểm tra role
    public boolean isAdmin(TaiKhoan taiKhoan) {
        return "ADMIN".equals(taiKhoan.getVaiTro());
    }
}
