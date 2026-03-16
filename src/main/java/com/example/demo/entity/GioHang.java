package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "GIO_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHang {

    @Id
    @Column(name = "MaGioHang", length = 50)
    private String maGioHang;

    @OneToOne
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang")
    private KhachHang khachHang;

    // Một giỏ hàng có nhiều chi tiết giỏ hàng
    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietGioHang> chiTietGioHangs;
}
