package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "LOAI_SAN_PHAM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiSanPham {

    @Id
    @Column(name = "MaLoai", length = 50)
    private String maLoai;

    @Column(name = "TenLoai", length = 255)
    private String tenLoai;

    // Một loại sản phẩm có nhiều sản phẩm
    @OneToMany(mappedBy = "loaiSanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;
}
