package com.example.demo.repository;

import com.example.demo.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    // US09: Tìm đơn hàng theo mã và SĐT khách hàng
    Optional<DonHang> findByMaDonHangAndKhachHang_SoDienThoai(String maDonHang, String soDienThoai);
}
