package com.example.demo.service;

import com.example.demo.entity.Hang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.HangRepository;
import com.example.demo.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final HangRepository hangRepository;

    // US01: Lấy danh sách sản phẩm phân trang + sắp xếp
    public Page<SanPham> getDanhSachSanPham(int page, int size, String sapXep) {
        Pageable pageable = PageRequest.of(page, size, getSort(sapXep));
        return sanPhamRepository.findAll(pageable);
    }

    // Backward compatible
    public Page<SanPham> getDanhSachSanPham(int page, int size) {
        return getDanhSachSanPham(page, size, null);
    }

    // US02: Lọc theo hãng
    public Page<SanPham> locTheoHang(String maHang, int page, int size, String sapXep) {
        Pageable pageable = PageRequest.of(page, size, getSort(sapXep));
        return sanPhamRepository.findByHang_MaHang(maHang, pageable);
    }

    public Page<SanPham> locTheoHang(String maHang, int page, int size) {
        return locTheoHang(maHang, page, size, null);
    }

    // Tìm kiếm sản phẩm
    public Page<SanPham> timKiem(String tuKhoa, String maHang, int page, int size, String sapXep) {
        Pageable pageable = PageRequest.of(page, size, getSort(sapXep));
        if (maHang != null && !maHang.isEmpty()) {
            return sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndHang_MaHang(tuKhoa, maHang, pageable);
        }
        return sanPhamRepository.findByTenSanPhamContainingIgnoreCase(tuKhoa, pageable);
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

    // Đếm tổng sản phẩm
    public long demTongSanPham() {
        return sanPhamRepository.count();
    }

    // Helper: tạo Sort từ string
    private Sort getSort(String sapXep) {
        if (sapXep == null || sapXep.isEmpty()) return Sort.unsorted();
        return switch (sapXep) {
            case "gia-tang" -> Sort.by(Sort.Direction.ASC, "gia");
            case "gia-giam" -> Sort.by(Sort.Direction.DESC, "gia");
            case "ten-az" -> Sort.by(Sort.Direction.ASC, "tenSanPham");
            case "ten-za" -> Sort.by(Sort.Direction.DESC, "tenSanPham");
            case "moi-nhat" -> Sort.by(Sort.Direction.DESC, "maSanPham");
            default -> Sort.unsorted();
        };
    }
}
