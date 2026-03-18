package com.example.demo.service;

import com.example.demo.entity.Hang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.HangRepository;
import com.example.demo.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final HangRepository hangRepository;

    // US01: Lấy danh sách sản phẩm phân trang
    public Page<SanPham> getDanhSachSanPham(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findAll(pageable);
    }

    // US02: Lọc theo hãng
    public Page<SanPham> locTheoHang(String maHang, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findByHang_MaHang(maHang, pageable);
    }

    // Lấy tất cả hãng
    public List<Hang> getAllHang() {
        return hangRepository.findAll();
    }

    // Lấy sản phẩm theo mã
    public Optional<SanPham> getSanPhamById(String maSanPham) {
        return sanPhamRepository.findById(maSanPham);
    }

    // US08: Lấy sản phẩm Pre-order
    public List<SanPham> getPreOrderProducts() {
        return sanPhamRepository.findByPreOrderTrue();
    }
}
