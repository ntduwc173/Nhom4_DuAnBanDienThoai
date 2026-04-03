package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US03: Thêm sản phẩm vào giỏ hàng
 */
@ExtendWith(MockitoExtension.class)
class GioHangServiceTest {

    @Mock
    private GioHangRepository gioHangRepository;

    @Mock
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @InjectMocks
    private GioHangService gioHangService;

    private GioHang gioHang;
    private SanPham sanPham;
    private SanPham sanPhamHetHang;
    private SanPham sanPhamPreOrder;

    @BeforeEach
    void setUp() {
        gioHang = GioHang.builder()
                .maGioHang("CART_SESSION_01")
                .build();

        sanPham = SanPham.builder()
                .maSanPham("SP001")
                .tenSanPham("iPhone 15 Pro Max")
                .gia(new BigDecimal("34990000"))
                .soLuong(10)
                .preOrder(false)
                .build();

        sanPhamHetHang = SanPham.builder()
                .maSanPham("SP002")
                .tenSanPham("Samsung Galaxy S24")
                .gia(new BigDecimal("25990000"))
                .soLuong(0)
                .preOrder(false)
                .build();

        sanPhamPreOrder = SanPham.builder()
                .maSanPham("SP003")
                .tenSanPham("iPhone 16 Pro Max")
                .gia(new BigDecimal("39990000"))
                .soLuong(0)
                .preOrder(true)
                .build();
    }

    // ========== US03: Thêm sản phẩm vào giỏ hàng ==========
    @Nested
    @DisplayName("US03 - Thêm sản phẩm vào giỏ hàng")
    class US03_ThemVaoGioHang {

        @Test
        @DisplayName("TC03_01: Thêm sản phẩm mới vào giỏ hàng thành công")
        void testThemVaoGioHang_ThanhCong() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(sanPham));
            when(chiTietGioHangRepository.findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(
                    anyString(), anyString())).thenReturn(Optional.empty());

            // Act
            gioHangService.themVaoGioHang("SP001", 1);

            // Assert
            verify(chiTietGioHangRepository).save(any(ChiTietGioHang.class));
        }

        @Test
        @DisplayName("TC03_02: Thêm sản phẩm đã có trong giỏ - cập nhật số lượng")
        void testThemVaoGioHang_CapNhatSoLuong() {
            // Arrange
            ChiTietGioHang existingItem = ChiTietGioHang.builder()
                    .maCTGH("CTGH001")
                    .gioHang(gioHang)
                    .sanPham(sanPham)
                    .soLuong(2)
                    .build();
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(sanPham));
            when(chiTietGioHangRepository.findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(
                    anyString(), eq("SP001"))).thenReturn(Optional.of(existingItem));

            // Act
            gioHangService.themVaoGioHang("SP001", 1);

            // Assert
            assertEquals(3, existingItem.getSoLuong()); // 2 + 1 = 3
            verify(chiTietGioHangRepository).save(existingItem);
        }

        @Test
        @DisplayName("TC03_03: Thêm sản phẩm không tồn tại - ném exception")
        void testThemVaoGioHang_SanPhamKhongTonTai() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP999")).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> gioHangService.themVaoGioHang("SP999", 1));
            assertEquals("Không tìm thấy sản phẩm!", exception.getMessage());
        }

        @Test
        @DisplayName("TC03_04: Thêm sản phẩm hết hàng - ném exception")
        void testThemVaoGioHang_HetHang() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP002")).thenReturn(Optional.of(sanPhamHetHang));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> gioHangService.themVaoGioHang("SP002", 1));
            assertEquals("Sản phẩm đã hết hàng!", exception.getMessage());
        }

        @Test
        @DisplayName("TC03_05: Thêm sản phẩm Pre-order (hết hàng nhưng vẫn cho thêm)")
        void testThemVaoGioHang_PreOrder() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP003")).thenReturn(Optional.of(sanPhamPreOrder));
            when(chiTietGioHangRepository.findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(
                    anyString(), eq("SP003"))).thenReturn(Optional.empty());

            // Act - Không ném exception dù soLuong = 0 vì là Pre-order
            assertDoesNotThrow(() -> gioHangService.themVaoGioHang("SP003", 1));

            // Assert
            verify(chiTietGioHangRepository).save(any(ChiTietGioHang.class));
        }

        @Test
        @DisplayName("TC03_06: Thêm số lượng vượt quá tồn kho - ném exception")
        void testThemVaoGioHang_VuotTonKho() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(sanPham));
            when(chiTietGioHangRepository.findFirstByGioHang_MaGioHangAndSanPham_MaSanPham(
                    anyString(), eq("SP001"))).thenReturn(Optional.empty());

            // Act & Assert - sanPham có soLuong = 10, cố thêm 15
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> gioHangService.themVaoGioHang("SP001", 15));
            assertTrue(exception.getMessage().contains("vượt quá tồn kho"));
        }
    }

    // ========== Tính năng giỏ hàng bổ sung ==========
    @Nested
    @DisplayName("Quản lý giỏ hàng")
    class QuanLyGioHang {

        @Test
        @DisplayName("TC03_07: Đếm số lượng sản phẩm trong giỏ (hiển thị trên Header icon)")
        void testGetSoLuongTrongGio() {
            // Arrange
            ChiTietGioHang item1 = ChiTietGioHang.builder().soLuong(2).sanPham(sanPham).build();
            ChiTietGioHang item2 = ChiTietGioHang.builder().soLuong(3).sanPham(sanPham).build();

            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(chiTietGioHangRepository.findByGioHang_MaGioHang("CART_SESSION_01"))
                    .thenReturn(Arrays.asList(item1, item2));

            // Act
            int soLuong = gioHangService.getSoLuongTrongGio();

            // Assert
            assertEquals(5, soLuong); // 2 + 3 = 5 hiển thị trên Header icon
        }

        @Test
        @DisplayName("TC03_08: Tính tổng tiền giỏ hàng")
        void testGetTongTienGioHang() {
            // Arrange
            ChiTietGioHang item1 = ChiTietGioHang.builder().soLuong(1).sanPham(sanPham).build();
            // sanPham.gia = 34,990,000

            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.of(gioHang));
            when(chiTietGioHangRepository.findByGioHang_MaGioHang("CART_SESSION_01"))
                    .thenReturn(Collections.singletonList(item1));

            // Act
            BigDecimal tongTien = gioHangService.getTongTienGioHang();

            // Assert
            assertEquals(new BigDecimal("34990000"), tongTien);
        }

        @Test
        @DisplayName("TC03_09: Xóa sản phẩm khỏi giỏ hàng")
        void testXoaKhoiGioHang() {
            // Act
            gioHangService.xoaKhoiGioHang("CTGH001");

            // Assert
            verify(chiTietGioHangRepository).deleteById("CTGH001");
        }

        @Test
        @DisplayName("TC03_10: Cập nhật số lượng sản phẩm trong giỏ")
        void testCapNhatSoLuong() {
            // Arrange
            ChiTietGioHang item = ChiTietGioHang.builder()
                    .maCTGH("CTGH001")
                    .soLuong(1)
                    .sanPham(sanPham)
                    .build();
            when(chiTietGioHangRepository.findById("CTGH001")).thenReturn(Optional.of(item));

            // Act
            gioHangService.capNhatSoLuong("CTGH001", 5);

            // Assert
            assertEquals(5, item.getSoLuong());
            verify(chiTietGioHangRepository).save(item);
        }

        @Test
        @DisplayName("TC03_11: Cập nhật số lượng <= 0 - tự xóa khỏi giỏ")
        void testCapNhatSoLuong_XoaKhiSoLuongKhongHopLe() {
            // Arrange
            ChiTietGioHang item = ChiTietGioHang.builder()
                    .maCTGH("CTGH001")
                    .soLuong(1)
                    .sanPham(sanPham)
                    .build();
            when(chiTietGioHangRepository.findById("CTGH001")).thenReturn(Optional.of(item));

            // Act
            gioHangService.capNhatSoLuong("CTGH001", 0);

            // Assert
            verify(chiTietGioHangRepository).deleteById("CTGH001");
        }

        @Test
        @DisplayName("TC03_12: Xóa tất cả giỏ hàng")
        void testXoaTatCaGioHang() {
            // Act
            gioHangService.xoaTatCaGioHang();

            // Assert
            verify(chiTietGioHangRepository).deleteByGioHang_MaGioHang("CART_SESSION_01");
        }

        @Test
        @DisplayName("TC03_13: Tạo giỏ hàng mới khi chưa có")
        void testGetOrCreateGioHang_TaoMoi() {
            // Arrange
            when(gioHangRepository.findById("CART_SESSION_01")).thenReturn(Optional.empty());
            when(gioHangRepository.save(any(GioHang.class))).thenReturn(gioHang);

            // Act
            GioHang result = gioHangService.getOrCreateGioHang();

            // Assert
            assertNotNull(result);
            verify(gioHangRepository).save(any(GioHang.class));
        }
    }
}
