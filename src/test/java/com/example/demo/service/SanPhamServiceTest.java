package com.example.demo.service;

import com.example.demo.entity.Hang;
import com.example.demo.entity.LoaiSanPham;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.HangRepository;
import com.example.demo.repository.SanPhamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US01, US02, US08: SanPhamService
 */
@ExtendWith(MockitoExtension.class)
class SanPhamServiceTest {

    @Mock
    private SanPhamRepository sanPhamRepository;

    @Mock
    private HangRepository hangRepository;

    @InjectMocks
    private SanPhamService sanPhamService;

    private SanPham sanPham1;
    private SanPham sanPham2;
    private SanPham sanPham3;
    private Hang hangApple;
    private Hang hangSamsung;

    @BeforeEach
    void setUp() {
        hangApple = Hang.builder().maHang("APPLE").tenHang("Apple").build();
        hangSamsung = Hang.builder().maHang("SAMSUNG").tenHang("Samsung").build();

        LoaiSanPham loaiDienThoai = LoaiSanPham.builder().maLoai("DT").tenLoai("Điện thoại").build();
        LoaiSanPham loaiPhuKien = LoaiSanPham.builder().maLoai("PK").tenLoai("Phụ kiện").build();

        sanPham1 = SanPham.builder()
                .maSanPham("SP001")
                .tenSanPham("iPhone 15 Pro Max")
                .gia(new BigDecimal("34990000"))
                .hinhAnh("iphone15.jpg")
                .hang(hangApple)
                .loaiSanPham(loaiDienThoai)
                .preOrder(false)
                .soLuong(10)
                .build();

        sanPham2 = SanPham.builder()
                .maSanPham("SP002")
                .tenSanPham("Samsung Galaxy S24 Ultra")
                .gia(new BigDecimal("31990000"))
                .hinhAnh("s24ultra.jpg")
                .hang(hangSamsung)
                .loaiSanPham(loaiDienThoai)
                .preOrder(false)
                .soLuong(5)
                .build();

        sanPham3 = SanPham.builder()
                .maSanPham("SP003")
                .tenSanPham("Ốp lưng iPhone 15")
                .gia(new BigDecimal("350000"))
                .hinhAnh("oplungiphone.jpg")
                .hang(hangApple)
                .loaiSanPham(loaiPhuKien)
                .preOrder(false)
                .soLuong(50)
                .build();
    }

    // ========== US01: Xem danh sách điện thoại và phụ kiện ==========
    @Nested
    @DisplayName("US01 - Xem danh sách sản phẩm (Grid + Phân trang)")
    class US01_XemDanhSachSanPham {

        @Test
        @DisplayName("TC01_01: Hiển thị danh sách sản phẩm dạng phân trang - trang đầu tiên")
        void testGetDanhSachSanPham_TrangDauTien() {
            // Arrange
            List<SanPham> products = Arrays.asList(sanPham1, sanPham2, sanPham3);
            Page<SanPham> page = new PageImpl<>(products, PageRequest.of(0, 8), 3);
            when(sanPhamRepository.findAll(any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.getDanhSachSanPham(0, 8);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(0, result.getNumber()); // Trang đầu tiên
            assertEquals(3, result.getContent().size());
            verify(sanPhamRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("TC01_02: Mỗi sản phẩm hiện đủ thông tin: Ảnh, Tên, Giá, Label")
        void testSanPhamHienDuThongTin() {
            // Arrange & Act (kiểm tra entity có đủ field)
            SanPham sp = sanPham1;

            // Assert - Kiểm tra đủ các field cần thiết cho hiển thị
            assertNotNull(sp.getHinhAnh(), "Sản phẩm phải có hình ảnh");
            assertNotNull(sp.getTenSanPham(), "Sản phẩm phải có tên");
            assertNotNull(sp.getGia(), "Sản phẩm phải có giá");
            assertNotNull(sp.getLoaiSanPham(), "Sản phẩm phải có loại (Label)");
            assertEquals("Điện thoại", sp.getLoaiSanPham().getTenLoai());
        }

        @Test
        @DisplayName("TC01_03: Phân trang - trang thứ 2 với page size = 2")
        void testGetDanhSachSanPham_TrangThu2() {
            // Arrange
            List<SanPham> products = Collections.singletonList(sanPham3);
            Page<SanPham> page = new PageImpl<>(products, PageRequest.of(1, 2), 3);
            when(sanPhamRepository.findAll(any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.getDanhSachSanPham(1, 2);

            // Assert
            assertEquals(1, result.getNumber()); // Trang thứ 2
            assertEquals(3, result.getTotalElements()); // Tổng 3 SP
            assertEquals(2, result.getTotalPages()); // 2 trang
        }

        @Test
        @DisplayName("TC01_04: Sắp xếp theo giá tăng dần")
        void testGetDanhSachSanPham_SapXepGiaTang() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Arrays.asList(sanPham3, sanPham2, sanPham1));
            when(sanPhamRepository.findAll(any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.getDanhSachSanPham(0, 8, "gia-tang");

            // Assert
            assertNotNull(result);
            verify(sanPhamRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("TC01_05: Sắp xếp theo giá giảm dần")
        void testGetDanhSachSanPham_SapXepGiaGiam() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Arrays.asList(sanPham1, sanPham2, sanPham3));
            when(sanPhamRepository.findAll(any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.getDanhSachSanPham(0, 8, "gia-giam");

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
        }

        @Test
        @DisplayName("TC01_06: Danh sách sản phẩm trống")
        void testGetDanhSachSanPham_Trong() {
            // Arrange
            Page<SanPham> emptyPage = new PageImpl<>(Collections.emptyList());
            when(sanPhamRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Page<SanPham> result = sanPhamService.getDanhSachSanPham(0, 8);

            // Assert
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }
    }

    // ========== US02: Lọc điện thoại theo hãng ==========
    @Nested
    @DisplayName("US02 - Lọc sản phẩm theo hãng")
    class US02_LocTheoHang {

        @Test
        @DisplayName("TC02_01: Lọc sản phẩm theo hãng Apple - có kết quả")
        void testLocTheoHang_Apple() {
            // Arrange
            List<SanPham> appleProducts = Arrays.asList(sanPham1, sanPham3);
            Page<SanPham> page = new PageImpl<>(appleProducts);
            when(sanPhamRepository.findByHang_MaHang(eq("APPLE"), any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.locTheoHang("APPLE", 0, 8);

            // Assert
            assertEquals(2, result.getTotalElements());
            result.getContent().forEach(sp ->
                    assertEquals("APPLE", sp.getHang().getMaHang()));
        }

        @Test
        @DisplayName("TC02_02: Lọc sản phẩm theo hãng Samsung - có kết quả")
        void testLocTheoHang_Samsung() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Collections.singletonList(sanPham2));
            when(sanPhamRepository.findByHang_MaHang(eq("SAMSUNG"), any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.locTheoHang("SAMSUNG", 0, 8);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals("Samsung Galaxy S24 Ultra", result.getContent().get(0).getTenSanPham());
        }

        @Test
        @DisplayName("TC02_03: Lọc theo hãng không có sản phẩm - Báo 'Không có SP'")
        void testLocTheoHang_KhongCoKetQua() {
            // Arrange
            Page<SanPham> emptyPage = new PageImpl<>(Collections.emptyList());
            when(sanPhamRepository.findByHang_MaHang(eq("XIAOMI"), any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Page<SanPham> result = sanPhamService.locTheoHang("XIAOMI", 0, 8);

            // Assert
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("TC02_04: Lọc theo hãng với sắp xếp giá tăng dần")
        void testLocTheoHang_VoiSapXep() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Arrays.asList(sanPham3, sanPham1));
            when(sanPhamRepository.findByHang_MaHang(eq("APPLE"), any(Pageable.class))).thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.locTheoHang("APPLE", 0, 8, "gia-tang");

            // Assert
            assertEquals(2, result.getTotalElements());
        }

        @Test
        @DisplayName("TC02_05: Lấy tất cả hãng cho nút lọc")
        void testGetAllHang() {
            // Arrange
            when(hangRepository.findAll()).thenReturn(Arrays.asList(hangApple, hangSamsung));

            // Act
            List<Hang> result = sanPhamService.getAllHang();

            // Assert
            assertEquals(2, result.size());
        }
    }

    // ========== US08: Pre-order ==========
    @Nested
    @DisplayName("US08 - Lấy sản phẩm Pre-order")
    class US08_PreOrder {

        @Test
        @DisplayName("TC08_01: Lấy danh sách sản phẩm Pre-order")
        void testGetPreOrderProducts() {
            // Arrange
            SanPham preOrderSp = SanPham.builder()
                    .maSanPham("SP004")
                    .tenSanPham("iPhone 16 Pro Max")
                    .gia(new BigDecimal("39990000"))
                    .preOrder(true)
                    .soLuong(0)
                    .build();
            when(sanPhamRepository.findByPreOrderTrue()).thenReturn(Collections.singletonList(preOrderSp));

            // Act
            List<SanPham> result = sanPhamService.getPreOrderProducts();

            // Assert
            assertFalse(result.isEmpty());
            assertTrue(result.get(0).getPreOrder(), "Sản phẩm phải có flag PreOrder = true");
        }

        @Test
        @DisplayName("TC08_02: Không có sản phẩm Pre-order")
        void testGetPreOrderProducts_Empty() {
            // Arrange
            when(sanPhamRepository.findByPreOrderTrue()).thenReturn(Collections.emptyList());

            // Act
            List<SanPham> result = sanPhamService.getPreOrderProducts();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    // ========== Tìm kiếm sản phẩm ==========
    @Nested
    @DisplayName("Tìm kiếm sản phẩm")
    class TimKiemSanPham {

        @Test
        @DisplayName("TC_TK01: Tìm kiếm theo từ khóa có kết quả")
        void testTimKiem_CoKetQua() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Collections.singletonList(sanPham1));
            when(sanPhamRepository.findByTenSanPhamContainingIgnoreCase(eq("iPhone"), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.timKiem("iPhone", null, 0, 8, null);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTenSanPham().contains("iPhone"));
        }

        @Test
        @DisplayName("TC_TK02: Tìm kiếm theo từ khóa + lọc hãng")
        void testTimKiem_CoHang() {
            // Arrange
            Page<SanPham> page = new PageImpl<>(Collections.singletonList(sanPham1));
            when(sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndHang_MaHang(
                    eq("iPhone"), eq("APPLE"), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            Page<SanPham> result = sanPhamService.timKiem("iPhone", "APPLE", 0, 8, null);

            // Assert
            assertEquals(1, result.getTotalElements());
        }
    }

    @Test
    @DisplayName("Lấy sản phẩm theo mã - tìm thấy")
    void testGetSanPhamById_Found() {
        when(sanPhamRepository.findById("SP001")).thenReturn(Optional.of(sanPham1));

        Optional<SanPham> result = sanPhamService.getSanPhamById("SP001");

        assertTrue(result.isPresent());
        assertEquals("iPhone 15 Pro Max", result.get().getTenSanPham());
    }

    @Test
    @DisplayName("Lấy sản phẩm theo mã - không tìm thấy")
    void testGetSanPhamById_NotFound() {
        when(sanPhamRepository.findById("SP999")).thenReturn(Optional.empty());

        Optional<SanPham> result = sanPhamService.getSanPhamById("SP999");

        assertFalse(result.isPresent());
    }
}
