package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final SanPhamRepository sanPhamRepository;

    private static final String SESSION_CART_ID = "CART_SESSION_01";

    // Lấy hoặc tạo giỏ hàng mặc định (dùng session-based, không cần đăng nhập)
    public GioHang getOrCreateGioHang() {
        Optional<GioHang> existing = gioHangRepository.findById(SESSION_CART_ID);
        if (existing.isPresent()) {
            return existing.get();
        }
        GioHang gioHang = new GioHang();
        gioHang.setMaGioHang(SESSION_CART_ID);
        return gioHangRepository.save(gioHang);
    }

    // US03: Thêm sản phẩm vào giỏ hàng
    @Transactional
    public void themVaoGioHang(String maSanPham, int soLuong) {
        GioHang gioHang = getOrCreateGioHang();
        Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(maSanPham);

        if (sanPhamOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm!");
        }

        SanPham sanPham = sanPhamOpt.get();

        // Kiểm tra tồn kho - không cho thêm sản phẩm hết hàng
        if (sanPham.getSoLuong() != null && sanPham.getSoLuong() <= 0 && !Boolean.TRUE.equals(sanPham.getPreOrder())) {
            throw new RuntimeException("Sản phẩm đã hết hàng!");
        }

        // Kiểm tra xem SP đã có trong giỏ chưa
        Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository
                .findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(gioHang.getMaGioHang(), maSanPham);

        if (existingItem.isPresent()) {
            // Cập nhật số lượng - kiểm tra không vượt quá tồn kho
            ChiTietGioHang item = existingItem.get();
            int soLuongMoi = item.getSoLuong() + soLuong;
            if (sanPham.getSoLuong() != null && soLuongMoi > sanPham.getSoLuong() && !Boolean.TRUE.equals(sanPham.getPreOrder())) {
                throw new RuntimeException("Số lượng trong giỏ hàng sẽ vượt quá tồn kho (Tối đa: " + sanPham.getSoLuong() + ")!");
            }
            item.setSoLuong(soLuongMoi);
            chiTietGioHangRepository.save(item);
        } else {
            // Thêm mới - kiểm tra không vượt quá tồn kho
            if (sanPham.getSoLuong() != null && soLuong > sanPham.getSoLuong() && !Boolean.TRUE.equals(sanPham.getPreOrder())) {
                throw new RuntimeException("Không thể thêm! Số lượng yêu cầu vượt quá tồn kho (Còn lại: " + sanPham.getSoLuong() + ")!");
            }
            ChiTietGioHang chiTiet = new ChiTietGioHang();
            chiTiet.setMaCTGH(UUID.randomUUID().toString().substring(0, 20));
            chiTiet.setGioHang(gioHang);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(soLuong);
            chiTietGioHangRepository.save(chiTiet);
        }
    }

    // Lấy danh sách chi tiết giỏ hàng
    public List<ChiTietGioHang> getChiTietGioHang() {
        GioHang gioHang = getOrCreateGioHang();
        return chiTietGioHangRepository.findByGioHang_MaGioHang(gioHang.getMaGioHang());
    }

    // Đếm số lượng sản phẩm trong giỏ
    public int getSoLuongTrongGio() {
        List<ChiTietGioHang> items = getChiTietGioHang();
        return items.stream().mapToInt(ChiTietGioHang::getSoLuong).sum();
    }

    // Tính tổng tiền giỏ hàng
    public BigDecimal getTongTienGioHang() {
        List<ChiTietGioHang> items = getChiTietGioHang();
        return items.stream()
                .map(item -> item.getSanPham().getGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Xóa sản phẩm khỏi giỏ
    @Transactional
    public void xoaKhoiGioHang(String maCTGH) {
        chiTietGioHangRepository.deleteById(maCTGH);
    }

    // Cập nhật số lượng
    @Transactional
    public void capNhatSoLuong(String maCTGH, int soLuong) {
        Optional<ChiTietGioHang> itemOpt = chiTietGioHangRepository.findById(maCTGH);
        if (itemOpt.isPresent()) {
            ChiTietGioHang item = itemOpt.get();
            if (soLuong <= 0) {
                chiTietGioHangRepository.deleteById(maCTGH);
            } else {
                SanPham sp = item.getSanPham();
                if (sp.getSoLuong() != null && soLuong > sp.getSoLuong() && !Boolean.TRUE.equals(sp.getPreOrder())) {
                    throw new RuntimeException("Số lượng cập nhật vượt quá tồn kho (Tối đa: " + sp.getSoLuong() + ")!");
                }
                item.setSoLuong(soLuong);
                chiTietGioHangRepository.save(item);
            }
        }
    }

    // Xóa tất cả giỏ hàng
    @Transactional
    public void xoaTatCaGioHang() {
        chiTietGioHangRepository.deleteByGioHang_MaGioHang(SESSION_CART_ID);
    }
}
