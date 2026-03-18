package com.example.demo.repository;

import com.example.demo.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, String> {

    // US01: Lấy danh sách sản phẩm có phân trang
    Page<SanPham> findAll(Pageable pageable);

    // US02: Lọc theo hãng
    Page<SanPham> findByHang_MaHang(String maHang, Pageable pageable);

    // US08: Lấy sản phẩm Pre-order
    List<SanPham> findByPreOrderTrue();

    // Lọc theo loại sản phẩm
    Page<SanPham> findByLoaiSanPham_MaLoai(String maLoai, Pageable pageable);
}
