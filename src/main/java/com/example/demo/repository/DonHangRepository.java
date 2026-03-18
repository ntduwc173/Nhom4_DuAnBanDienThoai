package com.example.demo.repository;

import com.example.demo.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    // US09: Tìm đơn hàng theo mã và SĐT khách hàng
    Optional<DonHang> findByMaDonHangAndKhachHang_SoDienThoai(String maDonHang, String soDienThoai);

    // US10: Tìm đơn hàng hoàn thành mới nhất theo SĐT khách hàng (dùng cho bảo hành)
    Optional<DonHang> findTopByKhachHang_SoDienThoaiAndTrangThaiOrderByNgayDatDesc(String soDienThoai, String trangThai);

    // US13: Lấy tất cả đơn hàng sắp xếp theo ngày mới nhất
    List<DonHang> findAllByOrderByNgayDatDesc();
}
