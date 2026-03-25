package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SAN_PHAM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {

    @Id
    @Column(name = "MaSanPham", length = 50)
    private String maSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCauHinh", referencedColumnName = "MaCauHinh")
    private CauHinh cauHinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoai", referencedColumnName = "MaLoai")
    private LoaiSanPham loaiSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHang", referencedColumnName = "MaHang")
    private Hang hang;

    @Column(name = "TenSanPham", length = 255)
    private String tenSanPham;

    @Column(name = "Gia", precision = 18, scale = 2)
    private BigDecimal gia;

    @Column(name = "HinhAnh", length = 500)
    private String hinhAnh;

    @Column(name = "MoTa", length = 2000)
    private String moTa;

    @Column(name = "PreOrder")
    private Boolean preOrder;

    @Column(name = "SoLuong")
    private Integer soLuong;

    // Một sản phẩm có nhiều chi tiết đơn hàng
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDonHang> chiTietDonHangs;

    // Một sản phẩm có nhiều chi tiết giỏ hàng
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietGioHang> chiTietGioHangs;
}
