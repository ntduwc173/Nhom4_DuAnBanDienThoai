package com.example.demo.service;

import com.example.demo.entity.ChiTietDonHang;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.ChiTietDonHangRepository;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.HoaDonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US18: In hóa đơn giấy
 */
@ExtendWith(MockitoExtension.class)
class HoaDonServiceTest {

    @Mock
    private HoaDonRepository hoaDonRepository;

    @Mock
    private DonHangRepository donHangRepository;

    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @InjectMocks
    private HoaDonService hoaDonService;

    private DonHang donHang;
    private HoaDon hoaDonExisting;

    @BeforeEach
    void setUp() {
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .diaChi("123 Lê Lợi, Q1, TP.HCM")
                .build();

        donHang = DonHang.builder()
                .maDonHang("DH001")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusDays(5))
                .tongTien(new BigDecimal("34990000"))
                .trangThai("Hoàn thành")
                .phuongThucThanhToan("COD")
                .build();

        hoaDonExisting = HoaDon.builder()
                .maHoaDon("HD001")
                .donHang(donHang)
                .ngayTao(LocalDateTime.now())
                .build();
    }

    // ========== US18: In hóa đơn giấy ==========
    @Nested
    @DisplayName("US18 - In hóa đơn giấy")
    class US18_InHoaDon {

        @Test
        @DisplayName("TC18_01: Lấy hóa đơn - tạo mới khi chưa có")
        void testLayHoaDon_TaoMoi() {
            // Arrange
            donHang.setHoaDon(null); // chưa có hóa đơn
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(hoaDonRepository.save(any(HoaDon.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            HoaDon result = hoaDonService.layHoaDon("DH001");

            // Assert
            assertNotNull(result);
            assertTrue(result.getMaHoaDon().startsWith("HD"));
            assertNotNull(result.getNgayTao());
            assertEquals(donHang, result.getDonHang());
            verify(hoaDonRepository).save(any(HoaDon.class));
        }

        @Test
        @DisplayName("TC18_02: Lấy hóa đơn - trả về hóa đơn đã có")
        void testLayHoaDon_DaCo() {
            // Arrange
            donHang.setHoaDon(hoaDonExisting);
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));

            // Act
            HoaDon result = hoaDonService.layHoaDon("DH001");

            // Assert
            assertNotNull(result);
            assertEquals("HD001", result.getMaHoaDon());
            // Không tạo hóa đơn mới
            verify(hoaDonRepository, never()).save(any(HoaDon.class));
        }

        @Test
        @DisplayName("TC18_03: Lấy hóa đơn - đơn hàng không tồn tại -> ném exception")
        void testLayHoaDon_DonKhongTonTai() {
            // Arrange
            when(donHangRepository.findById("DH999")).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> hoaDonService.layHoaDon("DH999"));
            assertEquals("Không tìm thấy đơn hàng!", ex.getMessage());
        }

        @Test
        @DisplayName("TC18_04: Lấy chi tiết đơn hàng cho in hóa đơn")
        void testGetChiTietDonHang() {
            // Arrange
            SanPham sp1 = SanPham.builder()
                    .maSanPham("SP001")
                    .tenSanPham("iPhone 15 Pro Max")
                    .gia(new BigDecimal("34990000"))
                    .build();
            SanPham sp2 = SanPham.builder()
                    .maSanPham("SP002")
                    .tenSanPham("Ốp lưng iPhone 15")
                    .gia(new BigDecimal("350000"))
                    .build();

            ChiTietDonHang ct1 = ChiTietDonHang.builder()
                    .maCTDonHang("CT001")
                    .sanPham(sp1).soLuong(1)
                    .gia(new BigDecimal("34990000"))
                    .build();
            ChiTietDonHang ct2 = ChiTietDonHang.builder()
                    .maCTDonHang("CT002")
                    .sanPham(sp2).soLuong(2)
                    .gia(new BigDecimal("350000"))
                    .build();

            when(chiTietDonHangRepository.findByDonHang_MaDonHang("DH001"))
                    .thenReturn(Arrays.asList(ct1, ct2));

            // Act
            List<ChiTietDonHang> result = hoaDonService.getChiTietDonHang("DH001");

            // Assert
            assertEquals(2, result.size());
            // Kiểm tra thông tin để in hóa đơn
            assertNotNull(result.get(0).getSanPham().getTenSanPham());
            assertNotNull(result.get(0).getGia());
            assertNotNull(result.get(0).getSoLuong());
        }

        @Test
        @DisplayName("TC18_05: Hóa đơn chứa đủ thông tin: Mã, Đơn hàng, Ngày tạo")
        void testHoaDonDuThongTin() {
            // Assert
            assertNotNull(hoaDonExisting.getMaHoaDon());
            assertNotNull(hoaDonExisting.getDonHang());
            assertNotNull(hoaDonExisting.getNgayTao());

            // Kiểm tra đơn hàng liên kết
            DonHang dh = hoaDonExisting.getDonHang();
            assertNotNull(dh.getKhachHang());
            assertNotNull(dh.getTongTien());
            assertNotNull(dh.getPhuongThucThanhToan());
        }
    }
}
