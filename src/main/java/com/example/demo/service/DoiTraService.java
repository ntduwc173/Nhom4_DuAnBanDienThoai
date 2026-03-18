package com.example.demo.service;

import com.example.demo.entity.DoiTra;
import com.example.demo.entity.DonHang;
import com.example.demo.repository.DoiTraRepository;
import com.example.demo.repository.DonHangRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoiTraService {

    private final DoiTraRepository doiTraRepository;
    private final DonHangRepository donHangRepository;

    // US09: Gửi yêu cầu đổi trả
    @Transactional
    public DoiTra taoYeuCauDoiTra(String maDonHang, String lyDo) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDonHang);
        if (donHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng!");
        }

        DonHang donHang = donHangOpt.get();
        if (!"Hoàn thành".equals(donHang.getTrangThai())) {
            throw new RuntimeException("Chỉ đơn hàng đã hoàn thành mới được đổi trả!");
        }

        DoiTra doiTra = new DoiTra();
        doiTra.setMaDoiTra("DT" + UUID.randomUUID().toString().substring(0, 18));
        doiTra.setDonHang(donHang);
        doiTra.setLyDo(lyDo);
        doiTra.setNgayYeuCau(LocalDateTime.now());
        doiTra.setTrangThai("Chờ xử lý");

        return doiTraRepository.save(doiTra);
    }
}
