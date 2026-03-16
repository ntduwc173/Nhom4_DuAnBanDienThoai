package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CHI_TIET_DON_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonHang {

    @Id
    @Column(name = "MaCTDonHang", length = 50)
    private String maCTDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham", referencedColumnName = "MaSanPham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    private DonHang donHang;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "Gia", precision = 18, scale = 2)
    private BigDecimal gia;
}
