package com.example.demo.controller;

import com.example.demo.entity.Hang;
import com.example.demo.entity.LoaiSanPham;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.HangRepository;
import com.example.demo.repository.LoaiSanPhamRepository;
import com.example.demo.repository.SanPhamRepository;
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
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US11: Sửa sản phẩm, US12: Thêm sản phẩm
 */
@ExtendWith(MockitoExtension.class)
class AdminSanPhamControllerTest {

    @Mock
    private SanPhamRepository sanPhamRepository;

    @Mock
    private HangRepository hangRepository;

    @Mock
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminSanPhamController controller;

    private Hang hangApple;
    private LoaiSanPham loaiDienThoai;
    private SanPham existingSanPham;

    @BeforeEach
    void setUp() {
        hangApple = Hang.builder().maHang("APPLE").tenHang("Apple").build();
        loaiDienThoai = LoaiSanPham.builder().maLoai("DT").tenLoai("Điện thoại").build();

        existingSanPham = SanPham.builder()
                .maSanPham("SP001")
                .tenSanPham("iPhone 15 Pro Max")
                .gia(new BigDecimal("34990000"))
                .hang(hangApple)
                .loaiSanPham(loaiDienThoai)
                .hinhAnh("iphone15.jpg")
                .soLuong(10)
                .preOrder(false)
                .build();
    }

    // ========== US11: Sửa sản phẩm ==========
    @Nested
    @DisplayName("US11 - Sửa sản phẩm")
    class US11_SuaSanPham {

        @Test
        @DisplayName("TC11_01: Cập nhật thông tin sản phẩm thành công (Tên, Hãng, Giá, Loại, Ảnh)")
        void testCapNhatSanPham_ThanhCong() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(existingSanPham));
            when(hangRepository.findById("APPLE")).thenReturn(Optional.of(hangApple));
            when(loaiSanPhamRepository.findById("DT")).thenReturn(Optional.of(loaiDienThoai));
            when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            String result = controller.capNhatSanPham(
                    "SP001", "iPhone 15 Pro Max 256GB", "APPLE",
                    new BigDecimal("36990000"), "DT",
                    "iphone15_new.jpg", "Mô tả mới", 20, false, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/san-pham", result);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
            verify(sanPhamRepository).save(any(SanPham.class));
        }

        @Test
        @DisplayName("TC11_02: Cập nhật sản phẩm không tồn tại - hiện lỗi")
        void testCapNhatSanPham_KhongTonTai() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(sanPhamRepository.findById("SP999")).thenReturn(Optional.empty());

            // Act
            String result = controller.capNhatSanPham(
                    "SP999", "Test", "APPLE",
                    new BigDecimal("1000000"), "DT",
                    null, null, null, null, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/san-pham", result);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("error"));
        }

        @Test
        @DisplayName("TC11_03: Cập nhật sản phẩm giữ ảnh cũ khi không upload ảnh mới")
        void testCapNhatSanPham_GiuAnhCu() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(existingSanPham));
            when(hangRepository.findById("APPLE")).thenReturn(Optional.of(hangApple));
            when(loaiSanPhamRepository.findById("DT")).thenReturn(Optional.of(loaiDienThoai));
            when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

            // Act - hinhAnh = null -> giữ ảnh cũ
            controller.capNhatSanPham(
                    "SP001", "iPhone 15 Pro Max", "APPLE",
                    new BigDecimal("34990000"), "DT",
                    null, null, 10, false, redirectAttributes);

            // Assert
            assertEquals("iphone15.jpg", existingSanPham.getHinhAnh());
        }
    }

    // ========== US12: Thêm sản phẩm ==========
    @Nested
    @DisplayName("US12 - Thêm sản phẩm mới")
    class US12_ThemSanPham {

        @Test
        @DisplayName("TC12_01: Thêm sản phẩm mới thành công (Tên, Hãng, Giá, Loại, Ảnh)")
        void testThemSanPham_ThanhCong() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(hangRepository.findById("APPLE")).thenReturn(Optional.of(hangApple));
            when(loaiSanPhamRepository.findById("DT")).thenReturn(Optional.of(loaiDienThoai));
            when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            String result = controller.themSanPham(
                    "iPhone 16 Pro Max", "APPLE",
                    new BigDecimal("39990000"), "DT",
                    "iphone16.jpg", "iPhone mới nhất", 5, false, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/san-pham", result);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
            verify(sanPhamRepository).save(any(SanPham.class));
        }

        @Test
        @DisplayName("TC12_02: Thêm sản phẩm Pre-order")
        void testThemSanPham_PreOrder() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(hangRepository.findById("APPLE")).thenReturn(Optional.of(hangApple));
            when(loaiSanPhamRepository.findById("DT")).thenReturn(Optional.of(loaiDienThoai));
            when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(invocation -> {
                SanPham sp = invocation.getArgument(0);
                assertTrue(sp.getPreOrder(), "Sản phẩm Pre-order phải có flag True");
                return sp;
            });

            // Act
            controller.themSanPham(
                    "Samsung Galaxy S25 Ultra", "APPLE",
                    new BigDecimal("35990000"), "DT",
                    "s25ultra.jpg", "Máy sắp ra mắt", 0, true, redirectAttributes);

            // Assert
            verify(sanPhamRepository).save(any(SanPham.class));
        }

        @Test
        @DisplayName("TC12_03: Thêm sản phẩm phụ kiện")
        void testThemSanPham_PhuKien() {
            // Arrange
            LoaiSanPham loaiPhuKien = LoaiSanPham.builder().maLoai("PK").tenLoai("Phụ kiện").build();
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(hangRepository.findById("APPLE")).thenReturn(Optional.of(hangApple));
            when(loaiSanPhamRepository.findById("PK")).thenReturn(Optional.of(loaiPhuKien));
            when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            controller.themSanPham(
                    "Ốp lưng iPhone 16", "APPLE",
                    new BigDecimal("350000"), "PK",
                    "oplungiphone16.jpg", "Ốp lưng chính hãng", 100, false, redirectAttributes);

            // Assert
            verify(sanPhamRepository).save(any(SanPham.class));
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        }
    }

    // ========== Trang quản lý sản phẩm ==========
    @Nested
    @DisplayName("Trang quản lý sản phẩm Admin")
    class TrangQuanLy {

        @Test
        @DisplayName("TC_QLP01: Hiển thị trang quản lý sản phẩm")
        void testTrangQuanLy() {
            // Arrange
            when(sanPhamRepository.findAll()).thenReturn(Arrays.asList(existingSanPham));
            when(hangRepository.findAll()).thenReturn(Arrays.asList(hangApple));
            when(loaiSanPhamRepository.findAll()).thenReturn(Arrays.asList(loaiDienThoai));

            // Act
            String result = controller.trangQuanLy(model);

            // Assert
            assertEquals("admin/admin-san-pham", result);
            verify(model).addAttribute("sanPhams", Arrays.asList(existingSanPham));
            verify(model).addAttribute("hangs", Arrays.asList(hangApple));
            verify(model).addAttribute("loais", Arrays.asList(loaiDienThoai));
        }

        @Test
        @DisplayName("TC_QLP02: Xóa sản phẩm thành công")
        void testXoaSanPham() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(existingSanPham));

            // Act
            String result = controller.xoaSanPham("SP001", redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/san-pham", result);
            verify(sanPhamRepository).delete(existingSanPham);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        }
    }
}
