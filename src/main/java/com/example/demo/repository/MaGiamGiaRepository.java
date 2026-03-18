package com.example.demo.repository;

import com.example.demo.entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, String> {
    // US06: Tìm mã giảm giá theo mã code
    Optional<MaGiamGia> findByMaGiamGia(String maGiamGia);

    // US07: Tìm mã giảm giá tự động đang hoạt động
    List<MaGiamGia> findByTuDongTrue();
}
