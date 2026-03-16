package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @Column(name = "MaKhachHang", length = 50)
    private String maKhachHang;

    @Column(name = "Ten", length = 255)
    private String ten;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "DiaChi", length = 500)
    private String diaChi;

    @Column(name = "Email", length = 255)
    private String email;

    // Một khách hàng có nhiều đơn hàng
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonHang> donHangs;

    // Một khách hàng có một giỏ hàng
    @OneToOne(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GioHang gioHang;

    // Một khách hàng có một tài khoản
    @OneToOne(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TaiKhoan taiKhoan;
}
