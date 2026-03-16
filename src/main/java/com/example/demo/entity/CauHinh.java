package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "CAU_HINH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CauHinh {

    @Id
    @Column(name = "MaCauHinh", length = 50)
    private String maCauHinh;

    @Column(name = "CPU", length = 255)
    private String cpu;

    @Column(name = "RAM", length = 100)
    private String ram;

    @Column(name = "BoNhoTrong", length = 100)
    private String boNhoTrong;

    @Column(name = "ManHinh", length = 255)
    private String manHinh;

    @Column(name = "Pin", length = 100)
    private String pin;

    @Column(name = "HeDieuHanh", length = 100)
    private String heDieuHanh;

    @Column(name = "Camera", length = 255)
    private String camera;

    // Một cấu hình có nhiều sản phẩm
    @OneToMany(mappedBy = "cauHinh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;
}
