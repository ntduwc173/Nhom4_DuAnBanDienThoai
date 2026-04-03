package com.example.demo.service;

import com.example.demo.entity.DoiTra;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.DoiTraRepository;
import com.example.demo.repository.DonHangRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US09: Gửi form yêu cầu đổi trả
 */
@ExtendWith(MockitoExtension.class)
class DoiTraServiceTest {

    @Mock
    private DoiTraRepository doiTraRepository;

    @Mock
    private DonHangRepository donHangRepository;

    @InjectMocks
    private DoiTraService doiTraService;

    private DonHang donHangHoanThanh;
    private DonHang donHangChoXuLy;
    private DonHang donHangDangShip;

    @BeforeEach
    void setUp() {
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .build();

        donHangHoanThanh = DonHang.builder()
                .maDonHang("DH001")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusDays(10))
                .tongTien(new BigDecimal("34990000"))
                .trangThai("Hoàn thành")
                .build();

        donHangChoXuLy = DonHang.builder()
                .maDonHang("DH002")
                .khachHang(khachHang)
                .trangThai("Chờ xử lý")
                .build();

        donHangDangShip = DonHang.builder()
                .maDonHang("DH003")
                .khachHang(khachHang)
                .trangThai("Đang ship")
                .build();
    }

    @Nested
    @DisplayName("US09 - Gửi form yêu cầu đổi trả")
    class US09_DoiTra {

        @Test
        @DisplayName("TC09_04: Tạo yêu cầu đổi trả thành công - đơn hàng đã hoàn thành")
        void testTaoYeuCauDoiTra_ThanhCong() {
            // Arrange
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHangHoanThanh));
            when(doiTraRepository.save(any(DoiTra.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DoiTra result = doiTraService.taoYeuCauDoiTra("DH001", "Sản phẩm không đúng mô tả");

            // Assert
            assertNotNull(result);
            assertEquals("Sản phẩm không đúng mô tả", result.getLyDo());
            assertEquals("Chờ xử lý", result.getTrangThai());
            assertNotNull(result.getNgayYeuCau());
            assertTrue(result.getMaDoiTra().startsWith("DT"));
        }

        @Test
        @DisplayName("TC09_05: Tạo yêu cầu đổi trả - đơn hàng chưa hoàn thành -> ném exception")
        void testTaoYeuCauDoiTra_ChuaHoanThanh() {
            // Arrange
            when(donHangRepository.findById("DH002")).thenReturn(Optional.of(donHangChoXuLy));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> doiTraService.taoYeuCauDoiTra("DH002", "Đổi ý"));
            assertTrue(ex.getMessage().contains("hoàn thành"));
        }

        @Test
        @DisplayName("TC09_06: Tạo yêu cầu đổi trả - đơn đang ship -> ném exception")
        void testTaoYeuCauDoiTra_DangShip() {
            // Arrange
            when(donHangRepository.findById("DH003")).thenReturn(Optional.of(donHangDangShip));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> doiTraService.taoYeuCauDoiTra("DH003", "Đổi ý"));
            assertTrue(ex.getMessage().contains("hoàn thành"));
        }

        @Test
        @DisplayName("TC09_07: Tạo yêu cầu đổi trả - đơn hàng không tồn tại -> ném exception")
        void testTaoYeuCauDoiTra_DonKhongTonTai() {
            // Arrange
            when(donHangRepository.findById("DH999")).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> doiTraService.taoYeuCauDoiTra("DH999", "Lỗi sản phẩm"));
            assertEquals("Không tìm thấy đơn hàng!", ex.getMessage());
        }

        @Test
        @DisplayName("TC09_08: Đổi trả với các lý do khác nhau từ Dropdown list")
        void testTaoYeuCauDoiTra_CacLyDo() {
            // Arrange
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHangHoanThanh));
            when(doiTraRepository.save(any(DoiTra.class))).thenAnswer(i -> i.getArgument(0));

            // Test với các lý do khác nhau
            String[] lyDos = {
                    "Sản phẩm lỗi từ nhà sản xuất",
                    "Không đúng mô tả",
                    "Đổi size/màu sắc",
                    "Đổi ý, không muốn mua nữa"
            };

            for (String lyDo : lyDos) {
                DoiTra result = doiTraService.taoYeuCauDoiTra("DH001", lyDo);
                assertNotNull(result);
                assertEquals(lyDo, result.getLyDo());
                assertEquals("Chờ xử lý", result.getTrangThai());
            }
        }
    }
}
