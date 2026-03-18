package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DON_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHang {

    @Id
    @Column(name = "MaDonHang", length = 50)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang")
    private KhachHang khachHang;

    @Column(name = "NgayDat")
    private LocalDateTime ngayDat;

    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    @Column(name = "PhuongThucThanhToan", length = 100)
    private String phuongThucThanhToan;

    @Column(name = "DiaChiNhanHang", length = 500)
    private String diaChiNhanHang;

    // US14: Đã nhận tiền (áp dụng cho đơn chuyển khoản)
    @Column(name = "DaThanhToan")
    private Boolean daThanhToan;

    // Một đơn hàng có nhiều chi tiết đơn hàng
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDonHang> chiTietDonHangs;

    // Một đơn hàng có nhiều bảo hành
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaoHanh> baoHanhs;

    // Một đơn hàng có nhiều đổi trả
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoiTra> doiTras;

    // Một đơn hàng có nhiều đơn hàng giảm giá
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonHangGiamGia> donHangGiamGias;

    // Một đơn hàng có một hóa đơn
    @OneToOne(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HoaDon hoaDon;
}
