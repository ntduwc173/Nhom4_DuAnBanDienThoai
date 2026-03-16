package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HOA_DON")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @Column(name = "MaHoaDon", length = 50)
    private String maHoaDon;

    @OneToOne
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    private DonHang donHang;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;
}
