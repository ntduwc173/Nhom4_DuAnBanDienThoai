package com.example.demo.service;

import com.example.demo.entity.BaoHanh;
import com.example.demo.entity.DonHang;
import com.example.demo.repository.BaoHanhRepository;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaoHanhService {

    private final BaoHanhRepository baoHanhRepository;
    private final DonHangRepository donHangRepository;

    // US10: Kiểm tra thời hạn bảo hành (12 tháng kể từ ngày nhận hàng - ngày đặt)
    public DonHang kiemTraBaoHanh(String maDonHangOrSdt) {
        Optional<DonHang> donHangOpt;

        // Thử tìm theo mã đơn hàng trước
        donHangOpt = donHangRepository.findById(maDonHangOrSdt);

        // Nếu không tìm thấy theo mã đơn, tìm theo SĐT (lấy đơn hàng hoàn thành mới nhất)
        if (donHangOpt.isEmpty()) {
            donHangOpt = donHangRepository
                    .findTopByKhachHang_SoDienThoaiAndTrangThaiOrderByNgayDatDesc(
                            maDonHangOrSdt, "Hoàn thành");
        }

        if (donHangOpt.isEmpty()) {
            return null;
        }

        DonHang donHang = donHangOpt.get();

        // Kiểm tra trạng thái đơn hàng phải là "Hoàn thành"
        if (!"Hoàn thành".equals(donHang.getTrangThai())) {
            return null;
        }

        // Kiểm tra thời hạn bảo hành: tính từ ngày đặt <= 12 tháng
        if (donHang.getNgayDat() != null) {
            long thangDaQua = ChronoUnit.MONTHS.between(donHang.getNgayDat(), LocalDateTime.now());
            if (thangDaQua > 12) {
                return null; // Hết hạn bảo hành
            }
        }

        return donHang;
    }

    // US10: Tạo yêu cầu bảo hành
    public BaoHanh taoYeuCauBaoHanh(String maDonHang, String moTaLoi) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDonHang);
        if (donHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng!");
        }

        DonHang donHang = donHangOpt.get();

        // Kiểm tra lại thời hạn
        if (donHang.getNgayDat() != null) {
            long thangDaQua = ChronoUnit.MONTHS.between(donHang.getNgayDat(), LocalDateTime.now());
            if (thangDaQua > 12) {
                throw new RuntimeException("Đơn hàng đã hết hạn bảo hành (quá 12 tháng)!");
            }
        }

        BaoHanh baoHanh = new BaoHanh();
        baoHanh.setMaBaoHanh("BH" + System.currentTimeMillis());
        baoHanh.setDonHang(donHang);
        baoHanh.setNgayYeuCau(LocalDateTime.now());
        baoHanh.setMoTaLoi(moTaLoi);
        baoHanh.setTrangThai("Chờ xử lý");

        return baoHanhRepository.save(baoHanh);
    }

    // Tính số tháng bảo hành còn lại
    public long tinhSoThangConLai(DonHang donHang) {
        if (donHang.getNgayDat() == null) return 0;
        long thangDaQua = ChronoUnit.MONTHS.between(donHang.getNgayDat(), LocalDateTime.now());
        return Math.max(0, 12 - thangDaQua);
    }
}
