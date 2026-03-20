package com.example.demo.repository;

import com.example.demo.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
}
