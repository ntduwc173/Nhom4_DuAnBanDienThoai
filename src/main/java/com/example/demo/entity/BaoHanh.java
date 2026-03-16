package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BAO_HANH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaoHanh {

    @Id
    @Column(name = "MaBaoHanh", length = 50)
    private String maBaoHanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    private DonHang donHang;

    @Column(name = "NgayYeuCau")
    private LocalDateTime ngayYeuCau;

    @Column(name = "MoTaLoi", length = 1000)
    private String moTaLoi;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;
}
