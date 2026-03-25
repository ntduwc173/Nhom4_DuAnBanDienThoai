package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final GioHangService gioHangService;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final DonHangGiamGiaRepository donHangGiamGiaRepository;

    // US04, US05: Tạo đơn hàng từ giỏ hàng
    @Transactional
    public DonHang taoDonHang(String tenKhach, String soDienThoai, String diaChi,
                              String phuongThucThanhToan, String maGiamGiaCode, KhachHang loggedInKhachHang) {

        // Tìm hoặc tạo khách hàng (nếu đang đăng nhập thì dùng trực tiếp)
        KhachHang khachHang;
        if (loggedInKhachHang != null) {
            khachHang = loggedInKhachHang;
        } else {
            khachHang = khachHangRepository.findFirstBySoDienThoai(soDienThoai)
                    .orElseGet(() -> {
                        KhachHang kh = new KhachHang();
                        kh.setMaKhachHang(UUID.randomUUID().toString().substring(0, 20));
                        kh.setTen(tenKhach);
                        kh.setSoDienThoai(soDienThoai);
                        kh.setDiaChi(diaChi);
                        return khachHangRepository.save(kh);
                    });
        }

        // Lấy giỏ hàng
        List<ChiTietGioHang> gioHangItems = gioHangService.getChiTietGioHang();
        if (gioHangItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // Kiểm tra tồn kho trước khi đặt hàng
        for (ChiTietGioHang cartItem : gioHangItems) {
            SanPham sp = cartItem.getSanPham();
            if (!Boolean.TRUE.equals(sp.getPreOrder())) {
                int tonKho = sp.getSoLuong() != null ? sp.getSoLuong() : 0;
                if (tonKho <= 0) {
                    throw new RuntimeException("Sản phẩm '" + sp.getTenSanPham() + "' đã hết hàng!");
                }
                if (cartItem.getSoLuong() > tonKho) {
                    throw new RuntimeException("Sản phẩm '" + sp.getTenSanPham() + "' chỉ còn " + tonKho + " sản phẩm trong kho!");
                }
            }
        }

        // Tính tổng tiền
        BigDecimal tongTien = gioHangService.getTongTienGioHang();

        // Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setMaDonHang("DH" + System.currentTimeMillis());
        donHang.setKhachHang(khachHang);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTongTien(tongTien);
        donHang.setTrangThai("Chờ xử lý");
        donHang.setPhuongThucThanhToan(phuongThucThanhToan);
        donHang.setDiaChiNhanHang(diaChi);

        donHang = donHangRepository.save(donHang);

        // Tạo chi tiết đơn hàng + trừ tồn kho
        for (ChiTietGioHang cartItem : gioHangItems) {
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setMaCTDonHang(UUID.randomUUID().toString().substring(0, 20));
            chiTiet.setDonHang(donHang);
            chiTiet.setSanPham(cartItem.getSanPham());
            chiTiet.setSoLuong(cartItem.getSoLuong());
            chiTiet.setGia(cartItem.getSanPham().getGia());
            chiTietDonHangRepository.save(chiTiet);

            // Trừ tồn kho
            SanPham sp = cartItem.getSanPham();
            if (!Boolean.TRUE.equals(sp.getPreOrder()) && sp.getSoLuong() != null) {
                sp.setSoLuong(sp.getSoLuong() - cartItem.getSoLuong());
                sanPhamRepository.save(sp);
            }
        }

        // US06: Áp dụng mã giảm giá (nếu có)
        if (maGiamGiaCode != null && !maGiamGiaCode.trim().isEmpty()) {
            apDungMaGiamGia(donHang, maGiamGiaCode, tongTien);
        }

        // US07: Kiểm tra và áp dụng giảm giá tự động 20%
        apDungGiamGiaTuDong(donHang, tongTien);

        // Xóa giỏ hàng sau khi đặt hàng
        gioHangService.xoaTatCaGioHang();

        return donHang;
    }

    // US06: Kiểm tra mã giảm giá
    public MaGiamGia kiemTraMaGiamGia(String maGiamGiaCode, BigDecimal tongTienTruocGiam) {
        if (maGiamGiaCode == null || maGiamGiaCode.trim().isEmpty()) {
            return null;
        }
        
        maGiamGiaCode = maGiamGiaCode.trim().toUpperCase();
        Optional<MaGiamGia> maGGOpt = maGiamGiaRepository.findByMaGiamGia(maGiamGiaCode);
        
        if (maGGOpt.isEmpty()) {
            return null;
        }
        MaGiamGia maGG = maGGOpt.get();

        // Bỏ check "Hoạt động" bằng String để tránh lỗi mất dấu tiếng Việt trên SQL Server
        // Chỉ dùng logic kiểm tra giới hạn thời gian thực tế:

        // Kiểm tra điều kiện tối thiểu
        if (maGG.getDieuKienToiThieu() != null && tongTienTruocGiam != null && tongTienTruocGiam.compareTo(maGG.getDieuKienToiThieu()) < 0) {
            return null;
        }

        // Kiểm tra hạn sử dụng
        LocalDateTime now = LocalDateTime.now();
        if (maGG.getNgayKetThuc() != null && now.isAfter(maGG.getNgayKetThuc())) {
            return null;
        }
        if (maGG.getNgayBatDau() != null && now.isBefore(maGG.getNgayBatDau())) {
            return null;
        }

        return maGG;
    }

    // Áp dụng mã giảm giá cho đơn hàng
    private void apDungMaGiamGia(DonHang donHang, String maGiamGiaCode, BigDecimal tongTien) {
        MaGiamGia maGG = kiemTraMaGiamGia(maGiamGiaCode, tongTien);
        if (maGG != null) {
            BigDecimal soTienGiam = tongTien.multiply(BigDecimal.valueOf(maGG.getPhanTramGiam() / 100))
                    .setScale(2, RoundingMode.HALF_UP);

            DonHangGiamGia dhgg = new DonHangGiamGia();
            dhgg.setMa(UUID.randomUUID().toString().substring(0, 20));
            dhgg.setDonHang(donHang);
            dhgg.setMaGiamGia(maGG);
            dhgg.setSoTienGiam(soTienGiam);
            donHangGiamGiaRepository.save(dhgg);

            // Cập nhật tổng tiền
            donHang.setTongTien(tongTien.subtract(soTienGiam));
            donHangRepository.save(donHang);
        }
    }

    // US07: Áp dụng giảm giá tự động
    private void apDungGiamGiaTuDong(DonHang donHang, BigDecimal tongTienGoc) {
        List<MaGiamGia> autoDiscounts = maGiamGiaRepository.findByTuDongTrue();
        for (MaGiamGia maGG : autoDiscounts) {
            // Kiểm tra điều kiện tối thiểu
            if (maGG.getDieuKienToiThieu() != null && tongTienGoc.compareTo(maGG.getDieuKienToiThieu()) < 0) {
                continue;
            }

            // Kiểm tra hạn sử dụng
            LocalDateTime now = LocalDateTime.now();
            if (maGG.getNgayKetThuc() != null && now.isAfter(maGG.getNgayKetThuc())) {
                continue;
            }
            if (maGG.getNgayBatDau() != null && now.isBefore(maGG.getNgayBatDau())) {
                continue;
            }

            BigDecimal soTienGiam = donHang.getTongTien()
                    .multiply(BigDecimal.valueOf(maGG.getPhanTramGiam() / 100))
                    .setScale(2, RoundingMode.HALF_UP);

            DonHangGiamGia dhgg = new DonHangGiamGia();
            dhgg.setMa(UUID.randomUUID().toString().substring(0, 20));
            dhgg.setDonHang(donHang);
            dhgg.setMaGiamGia(maGG);
            dhgg.setSoTienGiam(soTienGiam);
            donHangGiamGiaRepository.save(dhgg);

            donHang.setTongTien(donHang.getTongTien().subtract(soTienGiam));
            donHangRepository.save(donHang);
        }
    }

    // US08: Đặt hàng Pre-order
    @Transactional
    public DonHang taoPreOrder(String maSanPham, String tenKhach, String soDienThoai,
                                String diaChi) {
        // Pre-order bắt buộc chuyển khoản
        KhachHang khachHang = khachHangRepository.findFirstBySoDienThoai(soDienThoai)
                .orElseGet(() -> {
                    KhachHang kh = new KhachHang();
                    kh.setMaKhachHang(UUID.randomUUID().toString().substring(0, 20));
                    kh.setTen(tenKhach);
                    kh.setSoDienThoai(soDienThoai);
                    kh.setDiaChi(diaChi);
                    return khachHangRepository.save(kh);
                });

        DonHang donHang = new DonHang();
        donHang.setMaDonHang("PO" + System.currentTimeMillis());
        donHang.setKhachHang(khachHang);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTrangThai("Pre-order");
        donHang.setPhuongThucThanhToan("Chuyển khoản");
        donHang.setDiaChiNhanHang(diaChi);

        donHang = donHangRepository.save(donHang);
        return donHang;
    }

    // US09: Tìm đơn hàng để đổi trả
    public Optional<DonHang> timDonHangDeDoiTra(String maDonHang, String soDienThoai) {
        Optional<DonHang> donHangOpt = donHangRepository
                .findByMaDonHangAndKhachHang_SoDienThoai(maDonHang, soDienThoai);

        if (donHangOpt.isPresent() && "Hoàn thành".equals(donHangOpt.get().getTrangThai())) {
            return donHangOpt;
        }
        return Optional.empty();
    }

    // Lấy đơn hàng theo mã
    public Optional<DonHang> getDonHangById(String maDonHang) {
        return donHangRepository.findById(maDonHang);
    }

    // US13: Lấy tất cả đơn hàng (admin)
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAllByOrderByNgayDatDesc();
    }

    // US14: Cập nhật trạng thái đơn hàng
    @Transactional
    public DonHang capNhatTrangThai(String maDonHang, String trangThaiMoi) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        donHang.setTrangThai(trangThaiMoi);
        return donHangRepository.save(donHang);
    }

    // US14: Đánh dấu đã thanh toán (cho đơn chuyển khoản)
    @Transactional
    public DonHang capNhatDaThanhToan(String maDonHang, boolean daThanhToan) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        donHang.setDaThanhToan(daThanhToan);
        return donHangRepository.save(donHang);
    }

    // T49: Hủy đơn hàng (chỉ cho phép khi trạng thái "Chờ xử lý") + hoàn trả tồn kho
    @Transactional
    public DonHang huyDonHang(String maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (donHang.getTrangThai() == null || !donHang.getTrangThai().toLowerCase().startsWith("ch")) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ xử lý'!");
        }

        // Hoàn trả tồn kho
        List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
        for (ChiTietDonHang ct : chiTietList) {
            SanPham sp = ct.getSanPham();
            if (sp != null && !Boolean.TRUE.equals(sp.getPreOrder())) {
                int currentStock = sp.getSoLuong() != null ? sp.getSoLuong() : 0;
                sp.setSoLuong(currentStock + ct.getSoLuong());
                sanPhamRepository.save(sp);
            }
        }

        donHang.setTrangThai("Đã hủy");
        return donHangRepository.save(donHang);
    }

    // Đơn hàng của tôi
    public List<DonHang> getDonHangByKhachHang(String maKhachHang) {
        return donHangRepository.findByKhachHang_MaKhachHangOrderByNgayDatDesc(maKhachHang);
    }
}
