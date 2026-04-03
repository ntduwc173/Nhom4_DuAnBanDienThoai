package com.example.demo.service;

import com.example.demo.entity.BaoHanh;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.BaoHanhRepository;
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
 * Test cases cho US10: Gửi form yêu cầu bảo hành (12T)
 */
@ExtendWith(MockitoExtension.class)
class BaoHanhServiceTest {

    @Mock
    private BaoHanhRepository baoHanhRepository;

    @Mock
    private DonHangRepository donHangRepository;

    @InjectMocks
    private BaoHanhService baoHanhService;

    private DonHang donHangHoanThanh;
    private DonHang donHangHetHanBH;
    private DonHang donHangChoXuLy;
    private KhachHang khachHang;

    @BeforeEach
    void setUp() {
        khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .build();

        // Đơn hàng hoàn thành cách đây 3 tháng (còn bảo hành)
        donHangHoanThanh = DonHang.builder()
                .maDonHang("DH001")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusMonths(3))
                .tongTien(new BigDecimal("34990000"))
                .trangThai("Hoàn thành")
                .build();

        // Đơn hàng hoàn thành cách đây 15 tháng (hết bảo hành)
        donHangHetHanBH = DonHang.builder()
                .maDonHang("DH002")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusMonths(15))
                .tongTien(new BigDecimal("25990000"))
                .trangThai("Hoàn thành")
                .build();

        // Đơn hàng chưa hoàn thành
        donHangChoXuLy = DonHang.builder()
                .maDonHang("DH003")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusDays(5))
                .trangThai("Chờ xử lý")
                .build();
    }

    // ========== US10: Gửi form yêu cầu bảo hành (12T) ==========
    @Nested
    @DisplayName("US10 - Kiểm tra và tạo yêu cầu bảo hành")
    class US10_BaoHanh {

        @Test
        @DisplayName("TC10_01: Kiểm tra bảo hành bằng mã đơn hàng - còn hạn (<= 12 tháng)")
        void testKiemTraBaoHanh_ConHan() {
            // Arrange
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHangHoanThanh));

            // Act
            DonHang result = baoHanhService.kiemTraBaoHanh("DH001");

            // Assert
            assertNotNull(result, "Đơn hàng còn bảo hành phải trả về kết quả");
            assertEquals("DH001", result.getMaDonHang());
            assertEquals("Hoàn thành", result.getTrangThai());
        }

        @Test
        @DisplayName("TC10_02: Kiểm tra bảo hành bằng SĐT - còn hạn")
        void testKiemTraBaoHanh_BangSoDienThoai() {
            // Arrange
            when(donHangRepository.findById("0901234567")).thenReturn(Optional.empty());
            when(donHangRepository.findTopByKhachHang_SoDienThoaiAndTrangThaiOrderByNgayDatDesc(
                    "0901234567", "Hoàn thành"))
                    .thenReturn(Optional.of(donHangHoanThanh));

            // Act
            DonHang result = baoHanhService.kiemTraBaoHanh("0901234567");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("TC10_03: Kiểm tra bảo hành - hết hạn (> 12 tháng)")
        void testKiemTraBaoHanh_HetHan() {
            // Arrange
            when(donHangRepository.findById("DH002")).thenReturn(Optional.of(donHangHetHanBH));

            // Act
            DonHang result = baoHanhService.kiemTraBaoHanh("DH002");

            // Assert
            assertNull(result, "Đơn hàng hết hạn bảo hành (>12 tháng) phải trả về null");
        }

        @Test
        @DisplayName("TC10_04: Kiểm tra bảo hành - đơn hàng chưa hoàn thành")
        void testKiemTraBaoHanh_ChuaHoanThanh() {
            // Arrange
            when(donHangRepository.findById("DH003")).thenReturn(Optional.of(donHangChoXuLy));

            // Act
            DonHang result = baoHanhService.kiemTraBaoHanh("DH003");

            // Assert
            assertNull(result, "Đơn hàng chưa hoàn thành không được bảo hành");
        }

        @Test
        @DisplayName("TC10_05: Kiểm tra bảo hành - đơn hàng không tồn tại")
        void testKiemTraBaoHanh_KhongTonTai() {
            // Arrange
            when(donHangRepository.findById("DH999")).thenReturn(Optional.empty());
            when(donHangRepository.findTopByKhachHang_SoDienThoaiAndTrangThaiOrderByNgayDatDesc(
                    "DH999", "Hoàn thành"))
                    .thenReturn(Optional.empty());

            // Act
            DonHang result = baoHanhService.kiemTraBaoHanh("DH999");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("TC10_06: Tạo yêu cầu bảo hành thành công - có mô tả lỗi")
        void testTaoYeuCauBaoHanh_ThanhCong() {
            // Arrange
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHangHoanThanh));
            when(baoHanhRepository.save(any(BaoHanh.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            BaoHanh result = baoHanhService.taoYeuCauBaoHanh("DH001", "Màn hình bị sọc, không hiển thị");

            // Assert
            assertNotNull(result);
            assertEquals("Màn hình bị sọc, không hiển thị", result.getMoTaLoi());
            assertEquals("Chờ xử lý", result.getTrangThai());
            assertNotNull(result.getNgayYeuCau());
            assertTrue(result.getMaBaoHanh().startsWith("BH"));
        }

        @Test
        @DisplayName("TC10_07: Tạo yêu cầu bảo hành - đơn hàng không tồn tại -> ném exception")
        void testTaoYeuCauBaoHanh_DonHangKhongCo() {
            // Arrange
            when(donHangRepository.findById("DH999")).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> baoHanhService.taoYeuCauBaoHanh("DH999", "Lỗi pin"));
            assertEquals("Không tìm thấy đơn hàng!", ex.getMessage());
        }

        @Test
        @DisplayName("TC10_08: Tạo yêu cầu bảo hành - hết hạn bảo hành -> ném exception")
        void testTaoYeuCauBaoHanh_HetHanBaoHanh() {
            // Arrange
            when(donHangRepository.findById("DH002")).thenReturn(Optional.of(donHangHetHanBH));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> baoHanhService.taoYeuCauBaoHanh("DH002", "Lỗi wifi"));
            assertTrue(ex.getMessage().contains("hết hạn bảo hành"));
        }

        @Test
        @DisplayName("TC10_09: Tính số tháng bảo hành còn lại")
        void testTinhSoThangConLai() {
            // Act
            long conLai = baoHanhService.tinhSoThangConLai(donHangHoanThanh);

            // Assert - đơn hàng cách đây 3 tháng -> còn 9 tháng
            assertEquals(9, conLai);
        }

        @Test
        @DisplayName("TC10_10: Tính số tháng bảo hành - hết hạn -> trả về 0")
        void testTinhSoThangConLai_HetHan() {
            // Act
            long conLai = baoHanhService.tinhSoThangConLai(donHangHetHanBH);

            // Assert
            assertEquals(0, conLai, "Hết hạn bảo hành phải trả về 0");
        }
    }
}
