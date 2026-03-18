package com.example.demo.service;

import com.example.demo.entity.ChiTietDonHang;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.HoaDon;
import com.example.demo.repository.ChiTietDonHangRepository;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    // US15: Lấy hoặc tạo hóa đơn cho đơn hàng
    public HoaDon layHoaDon(String maDonHang) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDonHang);
        if (donHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng!");
        }

        DonHang donHang = donHangOpt.get();

        // Kiểm tra nếu đã có hóa đơn
        if (donHang.getHoaDon() != null) {
            return donHang.getHoaDon();
        }

        // Tạo hóa đơn mới
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD" + System.currentTimeMillis());
        hoaDon.setDonHang(donHang);
        hoaDon.setNgayTao(LocalDateTime.now());

        return hoaDonRepository.save(hoaDon);
    }

    // Lấy chi tiết đơn hàng
    public List<ChiTietDonHang> getChiTietDonHang(String maDonHang) {
        return chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
    }

    // Lấy đơn hàng theo mã
    public Optional<DonHang> getDonHangById(String maDonHang) {
        return donHangRepository.findById(maDonHang);
    }

    // Lấy tất cả đơn hàng
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAll();
    }
}
