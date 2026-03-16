package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TAI_KHOAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {

    @Id
    @Column(name = "MaTaiKhoan", length = 50)
    private String maTaiKhoan;

    @OneToOne
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang")
    private KhachHang khachHang;

    @Column(name = "TenDangNhap", length = 100, unique = true)
    private String tenDangNhap;

    @Column(name = "MatKhau", length = 255)
    private String matKhau;
}
