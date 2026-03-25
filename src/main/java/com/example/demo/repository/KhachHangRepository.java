package com.example.demo.repository;

import com.example.demo.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findFirstBySoDienThoai(String soDienThoai);

    // Tìm kiếm khách hàng theo tên hoặc số điện thoại
    List<KhachHang> findByTenContainingIgnoreCaseOrSoDienThoaiContaining(String ten, String soDienThoai);
}
