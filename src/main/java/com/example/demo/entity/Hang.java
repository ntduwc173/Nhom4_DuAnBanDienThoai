package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hang {

    @Id
    @Column(name = "MaHang", length = 50)
    private String maHang;

    @Column(name = "TenHang", length = 255)
    private String tenHang;

    // Một hãng có nhiều sản phẩm
    @OneToMany(mappedBy = "hang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;
}
