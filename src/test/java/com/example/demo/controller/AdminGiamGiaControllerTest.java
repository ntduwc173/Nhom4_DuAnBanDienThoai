package com.example.demo.controller;

import com.example.demo.entity.MaGiamGia;
import com.example.demo.repository.MaGiamGiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US13: Thêm phiếu giảm giá, US14: Sửa phiếu giảm giá, US15: Switch trạng thái
 */
@ExtendWith(MockitoExtension.class)
class AdminGiamGiaControllerTest {

    @Mock
    private MaGiamGiaRepository maGiamGiaRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminGiamGiaController controller;

    private MaGiamGia giamGiaHoatDong;
    private MaGiamGia giamGiaChuaBatDau;
    private MaGiamGia giamGiaHetHan;

    @BeforeEach
    void setUp() {
        giamGiaHoatDong = MaGiamGia.builder()
                .maGiamGia("SALE10")
                .tenMa("Giảm 10% Tết")
                .phanTramGiam(10.0)
                .ngayBatDau(LocalDateTime.now().minusDays(5))
                .ngayKetThuc(LocalDateTime.now().plusDays(25))
                .trangThai("Hoạt động")
                .tuDong(false)
                .build();

        giamGiaChuaBatDau = MaGiamGia.builder()
                .maGiamGia("NOEL2024")
                .tenMa("Giảm 20% Noel")
                .phanTramGiam(20.0)
                .ngayBatDau(LocalDateTime.now().plusDays(10))
                .ngayKetThuc(LocalDateTime.now().plusDays(40))
                .trangThai("Chưa bắt đầu")
                .tuDong(true)
                .build();

        giamGiaHetHan = MaGiamGia.builder()
                .maGiamGia("SUMMER23")
                .tenMa("Giảm 15% Hè")
                .phanTramGiam(15.0)
                .ngayBatDau(LocalDateTime.now().minusMonths(6))
                .ngayKetThuc(LocalDateTime.now().minusMonths(3))
                .trangThai("Hết hạn")
                .tuDong(false)
                .build();
    }

    // ========== US13: Thêm phiếu giảm giá ==========
    @Nested
    @DisplayName("US13 - Thêm phiếu giảm giá")
    class US13_ThemPhieuGiamGia {

        @Test
        @DisplayName("TC13_01: Tạo phiếu giảm giá mới thành công (Tên, %, Ngày BĐ, Ngày KT)")
        void testThemGiamGia_ThanhCong() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(i -> i.getArgument(0));

            String ngayBatDau = LocalDateTime.now().plusDays(1).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            String ngayKetThuc = LocalDateTime.now().plusDays(30).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Act
            String result = controller.themGiamGia(
                    "TETNGUYEN", "Giảm giá Tết Nguyên Đán",
                    25.0, ngayBatDau, ngayKetThuc,
                    new BigDecimal("5000000"), false, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/giam-gia", result);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
            verify(maGiamGiaRepository).save(any(MaGiamGia.class));
        }

        @Test
        @DisplayName("TC13_02: Tạo phiếu giảm giá tự động (hệ thống tự kích hoạt)")
        void testThemGiamGia_TuDong() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(invocation -> {
                MaGiamGia gg = invocation.getArgument(0);
                assertTrue(gg.getTuDong(), "Phiếu giảm giá phải có flag Tự động = true");
                return gg;
            });

            String ngayBatDau = LocalDateTime.now().plusDays(1).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            String ngayKetThuc = LocalDateTime.now().plusDays(30).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Act
            controller.themGiamGia(
                    null, "Auto Giảm 20%",
                    20.0, ngayBatDau, ngayKetThuc,
                    null, true, redirectAttributes);

            // Assert
            verify(maGiamGiaRepository).save(any(MaGiamGia.class));
        }

        @Test
        @DisplayName("TC13_03: Tạo phiếu - trạng thái 'Chưa bắt đầu' khi ngày BĐ > now")
        void testThemGiamGia_ChuaBatDau() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(invocation -> {
                MaGiamGia gg = invocation.getArgument(0);
                assertEquals("Chưa bắt đầu", gg.getTrangThai());
                return gg;
            });

            String ngayBatDau = LocalDateTime.now().plusDays(10).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            String ngayKetThuc = LocalDateTime.now().plusDays(40).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Act
            controller.themGiamGia(
                    null, "Black Friday",
                    30.0, ngayBatDau, ngayKetThuc,
                    null, false, redirectAttributes);

            // Assert
            verify(maGiamGiaRepository).save(any(MaGiamGia.class));
        }

        @Test
        @DisplayName("TC13_04: Tạo phiếu - trạng thái 'Hoạt động' khi now nằm giữa ngày BĐ và KT")
        void testThemGiamGia_HoatDong() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(invocation -> {
                MaGiamGia gg = invocation.getArgument(0);
                assertEquals("Hoạt động", gg.getTrangThai());
                return gg;
            });

            String ngayBatDau = LocalDateTime.now().minusDays(1).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            String ngayKetThuc = LocalDateTime.now().plusDays(30).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Act
            controller.themGiamGia(
                    "ACTIVENOW", "Đang hoạt động",
                    10.0, ngayBatDau, ngayKetThuc,
                    null, false, redirectAttributes);

            // Assert
            verify(maGiamGiaRepository).save(any(MaGiamGia.class));
        }

        @Test
        @DisplayName("TC13_05: Tạo phiếu - trạng thái 'Hết hạn' khi ngày KT < now")
        void testThemGiamGia_HetHan() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(invocation -> {
                MaGiamGia gg = invocation.getArgument(0);
                assertEquals("Hết hạn", gg.getTrangThai());
                return gg;
            });

            String ngayBatDau = LocalDateTime.now().minusDays(60).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            String ngayKetThuc = LocalDateTime.now().minusDays(1).format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Act
            controller.themGiamGia(
                    "OLDCODE", "Mã cũ",
                    5.0, ngayBatDau, ngayKetThuc,
                    null, false, redirectAttributes);

            // Assert
            verify(maGiamGiaRepository).save(any(MaGiamGia.class));
        }
    }

    // ========== US14: Sửa phiếu giảm giá (Tên, %, Ngày BĐ, Ngày KT) ==========
    // Lưu ý: US14 dùng cùng endpoint /them để lưu (POST) —
    // controller dùng maGiamGia param để phân biệt thêm / sửa

    // ========== US15: Thay đổi trạng thái phiếu giảm giá ==========
    @Nested
    @DisplayName("US15 - Thay đổi trạng thái phiếu giảm giá (nút switch)")
    class US15_SwitchTrangThai {

        @Test
        @DisplayName("TC15_01: Trang quản lý tự động cập nhật trạng thái theo thời gian")
        void testTrangQuanLy_TuDongCapNhatTrangThai() {
            // Arrange
            // giamGiaHoatDong: now nằm giữa ngayBatDau và ngayKetThuc -> "Hoạt động"
            // giamGiaChuaBatDau: now < ngayBatDau -> "Chưa bắt đầu"
            // giamGiaHetHan: now > ngayKetThuc -> "Hết hạn"
            List<MaGiamGia> allGiamGia = Arrays.asList(
                    giamGiaHoatDong, giamGiaChuaBatDau, giamGiaHetHan);
            when(maGiamGiaRepository.findAll()).thenReturn(allGiamGia);

            // Act
            String result = controller.trangQuanLy(model);

            // Assert
            assertEquals("admin/admin-giam-gia", result);
            // Kiểm tra trạng thái đã được tự động cập nhật đúng
            assertEquals("Hoạt động", giamGiaHoatDong.getTrangThai());
            assertEquals("Chưa bắt đầu", giamGiaChuaBatDau.getTrangThai());
            assertEquals("Hết hạn", giamGiaHetHan.getTrangThai());
        }

        @Test
        @DisplayName("TC15_02: Trạng thái tự chuyển từ 'Chưa bắt đầu' sang 'Hoạt động' khi đến ngày")
        void testAutoSwitch_ChuaBatDau_To_HoatDong() {
            // Arrange - giả lập mã giảm giá đã đến ngày bắt đầu
            MaGiamGia maGG = MaGiamGia.builder()
                    .maGiamGia("TEST01")
                    .tenMa("Test")
                    .phanTramGiam(10.0)
                    .ngayBatDau(LocalDateTime.now().minusHours(1)) // đã bắt đầu
                    .ngayKetThuc(LocalDateTime.now().plusDays(30))
                    .trangThai("Chưa bắt đầu") // trạng thái cũ
                    .build();
            when(maGiamGiaRepository.findAll()).thenReturn(Arrays.asList(maGG));

            // Act
            controller.trangQuanLy(model);

            // Assert - trạng thái phải được tự động chuyển
            assertEquals("Hoạt động", maGG.getTrangThai());
            verify(maGiamGiaRepository).save(maGG);
        }

        @Test
        @DisplayName("TC15_03: Trạng thái tự chuyển từ 'Hoạt động' sang 'Hết hạn' khi quá ngày KT")
        void testAutoSwitch_HoatDong_To_HetHan() {
            // Arrange
            MaGiamGia maGG = MaGiamGia.builder()
                    .maGiamGia("TEST02")
                    .tenMa("Test Hết hạn")
                    .phanTramGiam(15.0)
                    .ngayBatDau(LocalDateTime.now().minusDays(30))
                    .ngayKetThuc(LocalDateTime.now().minusHours(1)) // đã hết hạn
                    .trangThai("Hoạt động") // trạng thái cũ 
                    .build();
            when(maGiamGiaRepository.findAll()).thenReturn(Arrays.asList(maGG));

            // Act
            controller.trangQuanLy(model);

            // Assert
            assertEquals("Hết hạn", maGG.getTrangThai());
            verify(maGiamGiaRepository).save(maGG);
        }

        @Test
        @DisplayName("TC15_04: Xóa chương trình giảm giá")
        void testXoaGiamGia() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

            // Act
            String result = controller.xoaGiamGia("SALE10", redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/giam-gia", result);
            verify(maGiamGiaRepository).deleteById("SALE10");
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        }
    }
}
