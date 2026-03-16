package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "MA_GIAM_GIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaGiamGia {

    @Id
    @Column(name = "MaGiamGia", length = 50)
    private String maGiamGia;

    @Column(name = "TenMa", length = 255)
    private String tenMa;

    @Column(name = "PhanTramGiam")
    private Double phanTramGiam;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "DieuKienToiThieu", precision = 18, scale = 2)
    private BigDecimal dieuKienToiThieu;

    @Column(name = "TuDong")
    private Boolean tuDong;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    // Một mã giảm giá áp dụng cho nhiều đơn hàng
    @OneToMany(mappedBy = "maGiamGia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonHangGiamGia> donHangGiamGias;
}
