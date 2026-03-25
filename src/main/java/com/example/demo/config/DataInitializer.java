package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HangRepository hangRepository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public void run(String... args) {
        if (hangRepository.count() > 0) return;

        // === Tạo Hãng ===
        Hang apple = Hang.builder().maHang("APPLE").tenHang("Apple").build();
        Hang samsung = Hang.builder().maHang("SAMSUNG").tenHang("Samsung").build();
        Hang xiaomi = Hang.builder().maHang("XIAOMI").tenHang("Xiaomi").build();
        Hang oppo = Hang.builder().maHang("OPPO").tenHang("OPPO").build();
        hangRepository.save(apple);
        hangRepository.save(samsung);
        hangRepository.save(xiaomi);
        hangRepository.save(oppo);

        // === Tạo Loại SP ===
        LoaiSanPham dienThoai = LoaiSanPham.builder().maLoai("DT").tenLoai("Điện thoại").build();
        LoaiSanPham phuKien = LoaiSanPham.builder().maLoai("PK").tenLoai("Phụ kiện").build();
        loaiSanPhamRepository.save(dienThoai);
        loaiSanPhamRepository.save(phuKien);

        // === Tạo Sản phẩm ===
        sanPhamRepository.save(SanPham.builder().maSanPham("SP01").tenSanPham("iPhone 16 Pro Max").gia(new BigDecimal("34990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max.png").hang(apple).loaiSanPham(dienThoai).preOrder(false).soLuong(50).moTa("iPhone 16 Pro Max với chip A18 Pro, camera 48MP, màn hình 6.9 inch Super Retina XDR").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP02").tenSanPham("iPhone 16 Pro").gia(new BigDecimal("28990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro.png").hang(apple).loaiSanPham(dienThoai).preOrder(false).soLuong(45).moTa("iPhone 16 Pro với chip A18 Pro, camera 48MP, màn hình 6.3 inch").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP03").tenSanPham("iPhone 16").gia(new BigDecimal("22990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16_1.png").hang(apple).loaiSanPham(dienThoai).preOrder(false).soLuong(60).moTa("iPhone 16 với chip A18, camera 48MP, Action Button").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP04").tenSanPham("Samsung Galaxy S25 Ultra").gia(new BigDecimal("33990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s25-ultra-xam.png").hang(samsung).loaiSanPham(dienThoai).preOrder(false).soLuong(40).moTa("Samsung Galaxy S25 Ultra với Snapdragon 8 Elite, S Pen, camera 200MP").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP05").tenSanPham("Samsung Galaxy S25+").gia(new BigDecimal("25990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s25-plus-xanh.png").hang(samsung).loaiSanPham(dienThoai).preOrder(false).soLuong(35).moTa("Samsung Galaxy S25+ với Snapdragon 8 Elite, màn hình 6.7 inch AMOLED").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP06").tenSanPham("Samsung Galaxy Z Fold6").gia(new BigDecimal("40990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-z-fold6-xam.png").hang(samsung).loaiSanPham(dienThoai).preOrder(false).soLuong(20).moTa("Samsung Galaxy Z Fold6 màn hình gập, Snapdragon 8 Gen 3").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP07").tenSanPham("Xiaomi 14 Ultra").gia(new BigDecimal("23990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-14-ultra_2_.png").hang(xiaomi).loaiSanPham(dienThoai).preOrder(false).soLuong(30).moTa("Xiaomi 14 Ultra với camera Leica, Snapdragon 8 Gen 3").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP08").tenSanPham("OPPO Find X7 Ultra").gia(new BigDecimal("22490000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/o/p/oppo-find-x7-ultra.png").hang(oppo).loaiSanPham(dienThoai).preOrder(false).soLuong(25).moTa("OPPO Find X7 Ultra với camera Hasselblad, Dimensity 9300").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP09").tenSanPham("Ốp lưng iPhone 16 Pro Max").gia(new BigDecimal("350000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/o/p/op-lung-iphone-16-pro-max.png").hang(apple).loaiSanPham(phuKien).preOrder(false).soLuong(200).moTa("Ốp lưng silicon cao cấp cho iPhone 16 Pro Max").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP10").tenSanPham("Tai nghe AirPods Pro 2").gia(new BigDecimal("5990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/a/i/airpods-pro-2.png").hang(apple).loaiSanPham(phuKien).preOrder(false).soLuong(100).moTa("AirPods Pro 2 chống ồn, chip H2, USB-C").build());

        // Pre-order products
        sanPhamRepository.save(SanPham.builder().maSanPham("SP11").tenSanPham("iPhone 17 Pro Max (Pre-order)").gia(new BigDecimal("37990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max.png").hang(apple).loaiSanPham(dienThoai).preOrder(true).soLuong(0).moTa("iPhone 17 Pro Max dự kiến ra mắt - Đặt trước để nhận ưu đãi").build());
        sanPhamRepository.save(SanPham.builder().maSanPham("SP12").tenSanPham("Samsung Galaxy S26 Ultra (Pre-order)").gia(new BigDecimal("35990000")).hinhAnh("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s25-ultra-xam.png").hang(samsung).loaiSanPham(dienThoai).preOrder(true).soLuong(0).moTa("Samsung Galaxy S26 Ultra dự kiến ra mắt - Đặt trước giá ưu đãi").build());

        // === Mã giảm giá ===
        maGiamGiaRepository.save(MaGiamGia.builder()
                .maGiamGia("GIAM10")
                .tenMa("Giảm 10%")
                .phanTramGiam(10.0)
                .ngayBatDau(LocalDateTime.of(2025, 1, 1, 0, 0))
                .ngayKetThuc(LocalDateTime.of(2027, 12, 31, 23, 59))
                .trangThai("Hoạt động")
                .tuDong(false)
                .build());

        maGiamGiaRepository.save(MaGiamGia.builder()
                .maGiamGia("GIAM20")
                .tenMa("Giảm 20%")
                .phanTramGiam(20.0)
                .ngayBatDau(LocalDateTime.of(2025, 1, 1, 0, 0))
                .ngayKetThuc(LocalDateTime.of(2027, 12, 31, 23, 59))
                .trangThai("Hoạt động")
                .tuDong(false)
                .build());

        // US07: Mã giảm giá tự động 20%
        maGiamGiaRepository.save(MaGiamGia.builder()
                .maGiamGia("AUTO20")
                .tenMa("Tự động giảm 20% cho đơn trên 20 triệu")
                .phanTramGiam(20.0)
                .dieuKienToiThieu(new BigDecimal("20000000"))
                .ngayBatDau(LocalDateTime.of(2025, 1, 1, 0, 0))
                .ngayKetThuc(LocalDateTime.of(2027, 12, 31, 23, 59))
                .trangThai("Hoạt động")
                .tuDong(true)
                .build());

        // === Tạo tài khoản Admin ===
        KhachHang adminKH = KhachHang.builder()
                .maKhachHang("KH_ADMIN")
                .ten("Quản trị viên")
                .soDienThoai("0123456789")
                .email("admin@phonestore.vn")
                .diaChi("Hà Nội")
                .build();
        khachHangRepository.save(adminKH);

        taiKhoanRepository.save(TaiKhoan.builder()
                .maTaiKhoan("TK_ADMIN")
                .tenDangNhap("admin")
                .matKhau("admin123")
                .khachHang(adminKH)
                .vaiTro("ADMIN")
                .build());

        // === Tạo tài khoản Khách hàng mẫu ===
        KhachHang sampleKH = KhachHang.builder()
                .maKhachHang("KH_SAMPLE01")
                .ten("Nguyễn Văn A")
                .soDienThoai("0987654321")
                .email("nguyenvana@email.com")
                .diaChi("TP. Hồ Chí Minh")
                .build();
        khachHangRepository.save(sampleKH);

        taiKhoanRepository.save(TaiKhoan.builder()
                .maTaiKhoan("TK_SAMPLE01")
                .tenDangNhap("khachhang")
                .matKhau("123456")
                .khachHang(sampleKH)
                .vaiTro("KHACH_HANG")
                .build());

        System.out.println("=== DỮ LIỆU MẪU ĐÃ ĐƯỢC KHỞI TẠO ===");
        System.out.println("=== Tài khoản Admin: admin / admin123 ===");
        System.out.println("=== Tài khoản Khách hàng: khachhang / 123456 ===");
    }
}
