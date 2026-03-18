package com.example.demo.repository;

import com.example.demo.entity.BaoHanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoHanhRepository extends JpaRepository<BaoHanh, String> {
    List<BaoHanh> findByDonHang_MaDonHang(String maDonHang);
}
