package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOI_TRA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoiTra {

    @Id
    @Column(name = "MaDoiTra", length = 50)
    private String maDoiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    private DonHang donHang;

    @Column(name = "LyDo", length = 1000)
    private String lyDo;

    @Column(name = "NgayYeuCau")
    private LocalDateTime ngayYeuCau;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;
}
