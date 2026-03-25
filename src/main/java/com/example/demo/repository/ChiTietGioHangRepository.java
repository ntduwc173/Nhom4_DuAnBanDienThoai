package com.example.demo.repository;

import com.example.demo.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, String> {
    List<ChiTietGioHang> findByGioHang_MaGioHang(String maGioHang);
    Optional<ChiTietGioHang> findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(String maGioHang, String maSanPham);
    void deleteByGioHang_MaGioHang(String maGioHang);
}
