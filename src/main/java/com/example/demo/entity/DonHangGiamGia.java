package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DON_HANG_GIAM_GIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHangGiamGia {

    @Id
    @Column(name = "Ma", length = 50)
    private String ma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiamGia", referencedColumnName = "MaGiamGia")
    private MaGiamGia maGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    private DonHang donHang;

    @Column(name = "SoTienGiam", precision = 18, scale = 2)
    private BigDecimal soTienGiam;
}
