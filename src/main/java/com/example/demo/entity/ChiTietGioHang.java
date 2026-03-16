package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietGioHang {

    @Id
    @Column(name = "MaCTGH", length = 50)
    private String maCTGH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham", referencedColumnName = "MaSanPham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGioHang", referencedColumnName = "MaGioHang")
    private GioHang gioHang;

    @Column(name = "SoLuong")
    private Integer soLuong;
}
