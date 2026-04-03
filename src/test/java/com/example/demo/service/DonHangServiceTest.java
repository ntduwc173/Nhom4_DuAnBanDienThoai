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
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US04, US05, US06, US07, US08, US09, US16, US17:
 * DonHangService (Đặt hàng, Thanh toán, Mã giảm giá, Giảm giá tự động, Pre-order, Đổi trả, Quản lý đơn hàng)
 */
@ExtendWith(MockitoExtension.class)
class DonHangServiceTest {

    @Mock
    private DonHangRepository donHangRepository;
    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Mock
    private KhachHangRepository khachHangRepository;
    @Mock
    private SanPhamRepository sanPhamRepository;
    @Mock
    private GioHangService gioHangService;
    @Mock
    private MaGiamGiaRepository maGiamGiaRepository;
    @Mock
    private DonHangGiamGiaRepository donHangGiamGiaRepository;

    @InjectMocks
    private DonHangService donHangService;

    private KhachHang khachHang;
    private SanPham sanPham;
    private ChiTietGioHang chiTietGioHang;
    private DonHang donHangHoanThanh;
    private MaGiamGia maGiamGiaHopLe;
    private MaGiamGia maGiamGiaHetHan;
    private MaGiamGia maGiamGiaTuDong;

    @BeforeEach
    void setUp() {
        khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .diaChi("123 Lê Lợi, Q1, TP.HCM")
                .build();

        sanPham = SanPham.builder()
                .maSanPham("SP001")
                .tenSanPham("iPhone 15 Pro Max")
                .gia(new BigDecimal("34990000"))
                .soLuong(10)
                .preOrder(false)
                .build();

        chiTietGioHang = ChiTietGioHang.builder()
                .maCTGH("CTGH001")
                .sanPham(sanPham)
                .soLuong(1)
                .build();

        donHangHoanThanh = DonHang.builder()
                .maDonHang("DH001")
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now().minusDays(5))
                .tongTien(new BigDecimal("34990000"))
                .trangThai("Hoàn thành")
                .phuongThucThanhToan("COD")
                .diaChiNhanHang("123 Lê Lợi, Q1, TP.HCM")
                .build();

        maGiamGiaHopLe = MaGiamGia.builder()
                .maGiamGia("SALE10")
                .tenMa("Giảm 10%")
                .phanTramGiam(10.0)
                .ngayBatDau(LocalDateTime.now().minusDays(1))
                .ngayKetThuc(LocalDateTime.now().plusDays(30))
                .tuDong(false)
                .trangThai("Hoạt động")
                .build();

        maGiamGiaHetHan = MaGiamGia.builder()
                .maGiamGia("EXPIRED01")
                .tenMa("Mã hết hạn")
                .phanTramGiam(15.0)
                .ngayBatDau(LocalDateTime.now().minusDays(60))
                .ngayKetThuc(LocalDateTime.now().minusDays(1))
                .tuDong(false)
                .trangThai("Hết hạn")
                .build();

        maGiamGiaTuDong = MaGiamGia.builder()
                .maGiamGia("AUTO20")
                .tenMa("Tự động giảm 20%")
                .phanTramGiam(20.0)
                .ngayBatDau(LocalDateTime.now().minusDays(1))
                .ngayKetThuc(LocalDateTime.now().plusDays(30))
                .tuDong(true)
                .trangThai("Hoạt động")
                .build();
    }

    // ========== US04: Điền thông tin giao nhận hàng ==========
    @Nested
    @DisplayName("US04 - Điền thông tin giao nhận hàng")
    class US04_ThongTinGiaoNhan {

        @Test
        @DisplayName("TC04_01: Tạo đơn hàng với đủ thông tin khách hàng (Tên, SĐT, Địa chỉ)")
        void testTaoDonHang_DuThongTin() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi, Q1, TP.HCM",
                    "COD", null, null);

            // Assert
            assertNotNull(result);
            assertEquals("Chờ xử lý", result.getTrangThai());
            assertEquals("123 Lê Lợi, Q1, TP.HCM", result.getDiaChiNhanHang());
            assertNotNull(result.getKhachHang());
        }

        @Test
        @DisplayName("TC04_02: Tạo đơn hàng khi giỏ hàng trống - Validate bắt buộc")
        void testTaoDonHang_GioHangTrong() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.emptyList());
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> donHangService.taoDonHang(
                            "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                            "COD", null, null));
            assertEquals("Giỏ hàng trống!", ex.getMessage());
        }

        @Test
        @DisplayName("TC04_03: Tạo đơn hàng với khách hàng đã đăng nhập")
        void testTaoDonHang_KhachHangDaDangNhap() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                    "COD", null, khachHang);

            // Assert
            assertNotNull(result);
            assertEquals(khachHang, result.getKhachHang());
        }

        @Test
        @DisplayName("TC04_04: Tạo đơn hàng cho khách mới - tạo khách hàng tự động")
        void testTaoDonHang_KhachMoi() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0909999999"))
                    .thenReturn(Optional.empty());
            when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(i -> i.getArgument(0));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Trần Văn B", "0909999999", "456 Nguyễn Huệ",
                    "COD", null, null);

            // Assert
            assertNotNull(result);
            verify(khachHangRepository).save(any(KhachHang.class));
        }

        @Test
        @DisplayName("TC04_05: Đặt hàng sản phẩm hết tồn kho - ném exception")
        void testTaoDonHang_HetTonKho() {
            // Arrange
            SanPham spHetHang = SanPham.builder()
                    .maSanPham("SP002")
                    .tenSanPham("Samsung Galaxy S24")
                    .gia(new BigDecimal("25990000"))
                    .soLuong(0)
                    .preOrder(false)
                    .build();
            ChiTietGioHang cartItem = ChiTietGioHang.builder()
                    .sanPham(spHetHang).soLuong(1).build();

            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(cartItem));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> donHangService.taoDonHang(
                            "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                            "COD", null, null));
            assertTrue(ex.getMessage().contains("hết hàng"));
        }
    }

    // ========== US05: Chọn phương thức thanh toán ==========
    @Nested
    @DisplayName("US05 - Chọn phương thức thanh toán")
    class US05_PhuongThucThanhToan {

        @Test
        @DisplayName("TC05_01: Đặt hàng thanh toán COD")
        void testTaoDonHang_COD() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                    "COD", null, null);

            // Assert
            assertEquals("COD", result.getPhuongThucThanhToan());
        }

        @Test
        @DisplayName("TC05_02: Đặt hàng thanh toán Chuyển khoản")
        void testTaoDonHang_ChuyenKhoan() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                    "Chuyển khoản", null, null);

            // Assert
            assertEquals("Chuyển khoản", result.getPhuongThucThanhToan());
        }
    }

    // ========== US06: Nhập mã giảm giá ==========
    @Nested
    @DisplayName("US06 - Nhập mã giảm giá (PGG)")
    class US06_MaGiamGia {

        @Test
        @DisplayName("TC06_01: Mã giảm giá hợp lệ - trả về MaGiamGia")
        void testKiemTraMaGiamGia_HopLe() {
            // Arrange
            when(maGiamGiaRepository.findByMaGiamGia("SALE10"))
                    .thenReturn(Optional.of(maGiamGiaHopLe));

            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("sale10", new BigDecimal("34990000"));

            // Assert
            assertNotNull(result);
            assertEquals("SALE10", result.getMaGiamGia());
            assertEquals(10.0, result.getPhanTramGiam());
        }

        @Test
        @DisplayName("TC06_02: Mã giảm giá không tồn tại - trả về null (báo lỗi đỏ)")
        void testKiemTraMaGiamGia_KhongTonTai() {
            // Arrange
            when(maGiamGiaRepository.findByMaGiamGia("KHONGTONTAI"))
                    .thenReturn(Optional.empty());

            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("KHONGTONTAI", new BigDecimal("34990000"));

            // Assert
            assertNull(result, "Mã không tồn tại phải trả về null");
        }

        @Test
        @DisplayName("TC06_03: Mã giảm giá đã hết hạn - trả về null (báo lỗi đỏ)")
        void testKiemTraMaGiamGia_HetHan() {
            // Arrange
            when(maGiamGiaRepository.findByMaGiamGia("EXPIRED01"))
                    .thenReturn(Optional.of(maGiamGiaHetHan));

            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("EXPIRED01", new BigDecimal("34990000"));

            // Assert
            assertNull(result, "Mã hết hạn phải trả về null");
        }

        @Test
        @DisplayName("TC06_04: Mã giảm giá rỗng - trả về null")
        void testKiemTraMaGiamGia_Rong() {
            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("", new BigDecimal("34990000"));

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("TC06_05: Mã giảm giá null - trả về null")
        void testKiemTraMaGiamGia_Null() {
            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia(null, new BigDecimal("34990000"));

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("TC06_06: Mã giảm giá chưa đến ngày bắt đầu")
        void testKiemTraMaGiamGia_ChuaDenNgay() {
            // Arrange
            MaGiamGia maGGChuaBatDau = MaGiamGia.builder()
                    .maGiamGia("FUTURE01")
                    .tenMa("Mã tương lai")
                    .phanTramGiam(20.0)
                    .ngayBatDau(LocalDateTime.now().plusDays(10))
                    .ngayKetThuc(LocalDateTime.now().plusDays(30))
                    .build();
            when(maGiamGiaRepository.findByMaGiamGia("FUTURE01"))
                    .thenReturn(Optional.of(maGGChuaBatDau));

            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("FUTURE01", new BigDecimal("34990000"));

            // Assert
            assertNull(result, "Mã chưa đến ngày phải trả về null");
        }

        @Test
        @DisplayName("TC06_07: Mã giảm giá không đạt điều kiện tối thiểu")
        void testKiemTraMaGiamGia_KhongDatDieuKien() {
            // Arrange
            MaGiamGia maGGDieuKien = MaGiamGia.builder()
                    .maGiamGia("MIN50M")
                    .tenMa("Giảm cho đơn 50 triệu+")
                    .phanTramGiam(15.0)
                    .ngayBatDau(LocalDateTime.now().minusDays(1))
                    .ngayKetThuc(LocalDateTime.now().plusDays(30))
                    .dieuKienToiThieu(new BigDecimal("50000000"))
                    .build();
            when(maGiamGiaRepository.findByMaGiamGia("MIN50M"))
                    .thenReturn(Optional.of(maGGDieuKien));

            // Act
            MaGiamGia result = donHangService.kiemTraMaGiamGia("MIN50M", new BigDecimal("34990000"));

            // Assert
            assertNull(result, "Đơn hàng chưa đạt điều kiện 50M phải trả về null");
        }
    }

    // ========== US07: Giảm giá tự động 20% ==========
    @Nested
    @DisplayName("US07 - Giảm giá tự động 20%")
    class US07_GiamGiaTuDong {

        @Test
        @DisplayName("TC07_01: Tự động áp dụng giảm 20% khi thỏa mãn điều kiện")
        void testApDungGiamGiaTuDong() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue())
                    .thenReturn(Collections.singletonList(maGiamGiaTuDong));

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                    "COD", null, null);

            // Assert
            assertNotNull(result);
            // Verify giảm giá tự động được lưu
            verify(donHangGiamGiaRepository).save(any(DonHangGiamGia.class));
        }

        @Test
        @DisplayName("TC07_02: Không có giảm giá tự động nào hoạt động")
        void testApDungGiamGiaTuDong_KhongCo() {
            // Arrange
            when(gioHangService.getChiTietGioHang()).thenReturn(Collections.singletonList(chiTietGioHang));
            when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("34990000"));
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));
            when(maGiamGiaRepository.findByTuDongTrue()).thenReturn(Collections.emptyList());

            // Act
            DonHang result = donHangService.taoDonHang(
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi",
                    "COD", null, null);

            // Assert
            assertNotNull(result);
            assertEquals(new BigDecimal("34990000"), result.getTongTien());
            // Không có giảm giá tự động -> không save DonHangGiamGia
            verify(donHangGiamGiaRepository, never()).save(any(DonHangGiamGia.class));
        }
    }

    // ========== US08: Đặt hàng trước (Pre-order) ==========
    @Nested
    @DisplayName("US08 - Đặt hàng trước (Pre-order)")
    class US08_PreOrder {

        @Test
        @DisplayName("TC08_03: Tạo đơn Pre-order bắt buộc chuyển khoản")
        void testTaoPreOrder_BatBuocChuyenKhoan() {
            // Arrange
            when(khachHangRepository.findFirstBySoDienThoai("0901234567"))
                    .thenReturn(Optional.of(khachHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.taoPreOrder("SP003",
                    "Nguyễn Văn A", "0901234567", "123 Lê Lợi");

            // Assert
            assertNotNull(result);
            assertEquals("Pre-order", result.getTrangThai());
            assertEquals("Chuyển khoản", result.getPhuongThucThanhToan());
            assertTrue(result.getMaDonHang().startsWith("PO"));
        }

        @Test
        @DisplayName("TC08_04: Pre-order tạo khách hàng mới nếu chưa có")
        void testTaoPreOrder_KhachMoi() {
            // Arrange
            when(khachHangRepository.findFirstBySoDienThoai("0909999888"))
                    .thenReturn(Optional.empty());
            when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(i -> i.getArgument(0));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.taoPreOrder("SP003",
                    "Trần Thị C", "0909999888", "789 Trần Hưng Đạo");

            // Assert
            assertNotNull(result);
            verify(khachHangRepository).save(any(KhachHang.class));
        }
    }

    // ========== US09: Gửi form yêu cầu đổi trả ==========
    @Nested
    @DisplayName("US09 - Gửi form yêu cầu đổi trả")
    class US09_DoiTra {

        @Test
        @DisplayName("TC09_01: Tìm đơn hàng hoàn thành để đổi trả - thành công")
        void testTimDonHangDeDoiTra_ThanhCong() {
            // Arrange
            when(donHangRepository.findByMaDonHangAndKhachHang_SoDienThoai("DH001", "0901234567"))
                    .thenReturn(Optional.of(donHangHoanThanh));

            // Act
            Optional<DonHang> result = donHangService.timDonHangDeDoiTra("DH001", "0901234567");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("Hoàn thành", result.get().getTrangThai());
        }

        @Test
        @DisplayName("TC09_02: Tìm đơn hàng chưa hoàn thành - không cho đổi trả")
        void testTimDonHangDeDoiTra_ChuaHoanThanh() {
            // Arrange
            DonHang donHangChoXuLy = DonHang.builder()
                    .maDonHang("DH002")
                    .trangThai("Chờ xử lý")
                    .build();
            when(donHangRepository.findByMaDonHangAndKhachHang_SoDienThoai("DH002", "0901234567"))
                    .thenReturn(Optional.of(donHangChoXuLy));

            // Act
            Optional<DonHang> result = donHangService.timDonHangDeDoiTra("DH002", "0901234567");

            // Assert
            assertFalse(result.isPresent(), "Đơn hàng chưa hoàn thành không được đổi trả");
        }

        @Test
        @DisplayName("TC09_03: Tìm đơn hàng không tồn tại - trả về empty")
        void testTimDonHangDeDoiTra_KhongTonTai() {
            // Arrange
            when(donHangRepository.findByMaDonHangAndKhachHang_SoDienThoai("DH999", "0901234567"))
                    .thenReturn(Optional.empty());

            // Act
            Optional<DonHang> result = donHangService.timDonHangDeDoiTra("DH999", "0901234567");

            // Assert
            assertFalse(result.isPresent());
        }
    }

    // ========== US16: Xem danh sách đơn hàng (Admin) ==========
    @Nested
    @DisplayName("US16 - Xem danh sách đơn hàng (Admin)")
    class US16_DanhSachDonHang {

        @Test
        @DisplayName("TC16_01: Lấy tất cả đơn hàng sắp xếp theo ngày mới nhất")
        void testGetAllDonHang() {
            // Arrange
            DonHang dh1 = DonHang.builder()
                    .maDonHang("DH001").trangThai("Hoàn thành")
                    .ngayDat(LocalDateTime.now().minusDays(5)).build();
            DonHang dh2 = DonHang.builder()
                    .maDonHang("PO001").trangThai("Pre-order")
                    .ngayDat(LocalDateTime.now().minusDays(1)).build();
            when(donHangRepository.findAllByOrderByNgayDatDesc())
                    .thenReturn(Arrays.asList(dh2, dh1));

            // Act
            List<DonHang> result = donHangService.getAllDonHang();

            // Assert
            assertEquals(2, result.size());
            // Có cả đơn thường vs đơn Pre-order
            assertTrue(result.stream().anyMatch(dh -> "Pre-order".equals(dh.getTrangThai())));
            assertTrue(result.stream().anyMatch(dh -> "Hoàn thành".equals(dh.getTrangThai())));
        }

        @Test
        @DisplayName("TC16_02: Thông tin đơn hàng: Mã đơn, Khách, Tổng tiền, Trạng thái")
        void testDonHangCoThongTinDay() {
            // Arrange & Act - verify entity có đầy đủ field
            DonHang dh = donHangHoanThanh;

            // Assert
            assertNotNull(dh.getMaDonHang(), "Phải có Mã đơn");
            assertNotNull(dh.getKhachHang(), "Phải có thông tin Khách");
            assertNotNull(dh.getTongTien(), "Phải có Tổng tiền");
            assertNotNull(dh.getTrangThai(), "Phải có Trạng thái");
        }

        @Test
        @DisplayName("TC16_03: Phân loại đơn thường vs đơn Pre-order")
        void testPhanLoaiDon() {
            DonHang donThuong = DonHang.builder().maDonHang("DH001").trangThai("Chờ xử lý").build();
            DonHang donPreOrder = DonHang.builder().maDonHang("PO001").trangThai("Pre-order").build();

            // Đơn thường: mã bắt đầu bằng "DH"
            assertTrue(donThuong.getMaDonHang().startsWith("DH"));
            assertNotEquals("Pre-order", donThuong.getTrangThai());

            // Đơn Pre-order: mã bắt đầu bằng "PO"
            assertTrue(donPreOrder.getMaDonHang().startsWith("PO"));
            assertEquals("Pre-order", donPreOrder.getTrangThai());
        }
    }

    // ========== US17: Cập nhật trạng thái giao hàng ==========
    @Nested
    @DisplayName("US17 - Cập nhật trạng thái giao hàng")
    class US17_CapNhatTrangThai {

        @Test
        @DisplayName("TC17_01: Cập nhật trạng thái 'Chờ xử lý' -> 'Đang ship'")
        void testCapNhatTrangThai_DangShip() {
            // Arrange
            DonHang donHang = DonHang.builder().maDonHang("DH001").trangThai("Chờ xử lý").build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.capNhatTrangThai("DH001", "Đang ship");

            // Assert
            assertEquals("Đang ship", result.getTrangThai());
        }

        @Test
        @DisplayName("TC17_02: Cập nhật trạng thái 'Đang ship' -> 'Hoàn thành'")
        void testCapNhatTrangThai_HoanThanh() {
            // Arrange
            DonHang donHang = DonHang.builder().maDonHang("DH001").trangThai("Đang ship").build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.capNhatTrangThai("DH001", "Hoàn thành");

            // Assert
            assertEquals("Hoàn thành", result.getTrangThai());
        }

        @Test
        @DisplayName("TC17_03: Cập nhật trạng thái -> 'Hủy'")
        void testCapNhatTrangThai_Huy() {
            // Arrange
            DonHang donHang = DonHang.builder().maDonHang("DH001").trangThai("Chờ xử lý").build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.capNhatTrangThai("DH001", "Hủy");

            // Assert
            assertEquals("Hủy", result.getTrangThai());
        }

        @Test
        @DisplayName("TC17_04: Đánh dấu đã thanh toán (cho đơn chuyển khoản)")
        void testCapNhatDaThanhToan() {
            // Arrange
            DonHang donHang = DonHang.builder()
                    .maDonHang("DH001")
                    .phuongThucThanhToan("Chuyển khoản")
                    .daThanhToan(false)
                    .build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.capNhatDaThanhToan("DH001", true);

            // Assert
            assertTrue(result.getDaThanhToan(), "Checkbox 'Đã nhận tiền' phải được đánh dấu");
        }

        @Test
        @DisplayName("TC17_05: Bỏ đánh dấu đã thanh toán")
        void testCapNhatDaThanhToan_BoCheck() {
            // Arrange
            DonHang donHang = DonHang.builder()
                    .maDonHang("DH001")
                    .daThanhToan(true)
                    .build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.capNhatDaThanhToan("DH001", false);

            // Assert
            assertFalse(result.getDaThanhToan());
        }

        @Test
        @DisplayName("TC17_06: Cập nhật trạng thái đơn không tồn tại - ném exception")
        void testCapNhatTrangThai_KhongTimThay() {
            // Arrange
            when(donHangRepository.findById("DH999")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class,
                    () -> donHangService.capNhatTrangThai("DH999", "Đang ship"));
        }

        @Test
        @DisplayName("TC17_07: Hủy đơn hàng ở trạng thái 'Chờ xử lý' + hoàn trả tồn kho")
        void testHuyDonHang_ThanhCong() {
            // Arrange
            DonHang donHang = DonHang.builder()
                    .maDonHang("DH001").trangThai("Chờ xử lý").build();
            ChiTietDonHang ct = ChiTietDonHang.builder()
                    .sanPham(sanPham).soLuong(2).build();

            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));
            when(chiTietDonHangRepository.findByDonHang_MaDonHang("DH001"))
                    .thenReturn(Collections.singletonList(ct));
            when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            DonHang result = donHangService.huyDonHang("DH001");

            // Assert
            assertEquals("Đã hủy", result.getTrangThai());
            assertEquals(12, sanPham.getSoLuong()); // 10 + 2 hoàn trả
            verify(sanPhamRepository).save(sanPham);
        }

        @Test
        @DisplayName("TC17_08: Hủy đơn hàng không phải 'Chờ xử lý' - ném exception")
        void testHuyDonHang_KhongChoPhep() {
            // Arrange
            DonHang donHang = DonHang.builder()
                    .maDonHang("DH001").trangThai("Đang ship").build();
            when(donHangRepository.findById("DH001")).thenReturn(Optional.of(donHang));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> donHangService.huyDonHang("DH001"));
            assertTrue(ex.getMessage().contains("Chờ xử lý"));
        }
    }
}
