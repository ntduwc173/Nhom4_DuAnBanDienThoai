package com.example.demo.controller;

import com.example.demo.entity.ChiTietDonHang;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.ChiTietDonHangRepository;
import com.example.demo.service.DonHangService;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases cho US16: Xem danh sách đơn hàng, US17: Cập nhật trạng thái giao hàng
 */
@ExtendWith(MockitoExtension.class)
class AdminDonHangControllerTest {

    @Mock
    private DonHangService donHangService;

    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminDonHangController controller;

    private KhachHang khachHang;
    private DonHang donHangThuong;
    private DonHang donHangPreOrder;
    private DonHang donHangDangShip;

    @BeforeEach
    void setUp() {
        khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .build();

        donHangThuong = DonHang.builder()
                .maDonHang("DH001")
                .khachHang(khachHang)
                .tongTien(new BigDecimal("34990000"))
                .trangThai("Chờ xử lý")
                .ngayDat(LocalDateTime.now().minusDays(2))
                .build();

        donHangPreOrder = DonHang.builder()
                .maDonHang("PO001")
                .khachHang(khachHang)
                .tongTien(new BigDecimal("39990000"))
                .trangThai("Pre-order")
                .ngayDat(LocalDateTime.now().minusDays(1))
                .build();

        donHangDangShip = DonHang.builder()
                .maDonHang("DH002")
                .khachHang(khachHang)
                .tongTien(new BigDecimal("25990000"))
                .trangThai("Đang ship")
                .ngayDat(LocalDateTime.now().minusDays(3))
                .build();
    }

    // ========== US16: Xem danh sách đơn hàng ==========
    @Nested
    @DisplayName("US16 - Xem danh sách đơn hàng")
    class US16_DanhSachDonHang {

        @Test
        @DisplayName("TC16_04: Hiển thị tất cả đơn hàng (đơn thường + Pre-order)")
        void testDanhSachDonHang_TatCa() {
            // Arrange
            when(donHangService.getAllDonHang())
                    .thenReturn(Arrays.asList(donHangPreOrder, donHangThuong, donHangDangShip));

            // Act
            String result = controller.danhSachDonHang(null, null, model);

            // Assert
            assertEquals("admin/admin-don-hang", result);
            verify(model).addAttribute(eq("donHangs"), anyList());
        }

        @Test
        @DisplayName("TC16_05: Lọc đơn hàng theo trạng thái 'Chờ xử lý'")
        void testDanhSachDonHang_LocTrangThai() {
            // Arrange
            when(donHangService.getAllDonHang())
                    .thenReturn(Arrays.asList(donHangPreOrder, donHangThuong, donHangDangShip));

            // Act
            String result = controller.danhSachDonHang("Chờ xử lý", null, model);

            // Assert
            assertEquals("admin/admin-don-hang", result);
            // Verify that the filtered list is passed to view
            verify(model).addAttribute(eq("donHangs"), argThat(list -> {
                @SuppressWarnings("unchecked")
                List<DonHang> donHangs = (List<DonHang>) list;
                // Chỉ lọc ra được đơn "Chờ xử lý" (donHangThuong có "Chờ xử lý" startsWith "ch")
                return donHangs.stream().allMatch(dh -> 
                        dh.getTrangThai().toLowerCase().contains("chờ xử lý".toLowerCase()));
            }));
        }

        @Test
        @DisplayName("TC16_06: Tìm kiếm đơn hàng theo mã đơn")
        void testDanhSachDonHang_TimTheoMaDon() {
            // Arrange
            when(donHangService.getAllDonHang())
                    .thenReturn(Arrays.asList(donHangPreOrder, donHangThuong, donHangDangShip));

            // Act
            String result = controller.danhSachDonHang(null, "DH001", model);

            // Assert
            assertEquals("admin/admin-don-hang", result);
            verify(model).addAttribute(eq("donHangs"), argThat(list -> {
                @SuppressWarnings("unchecked")
                List<DonHang> donHangs = (List<DonHang>) list;
                return donHangs.size() == 1 && "DH001".equals(donHangs.get(0).getMaDonHang());
            }));
        }

        @Test
        @DisplayName("TC16_07: Tìm kiếm đơn hàng theo tên khách")
        void testDanhSachDonHang_TimTheoTenKhach() {
            // Arrange
            when(donHangService.getAllDonHang())
                    .thenReturn(Arrays.asList(donHangThuong));

            // Act
            String result = controller.danhSachDonHang(null, "Nguyễn Văn A", model);

            // Assert
            assertEquals("admin/admin-don-hang", result);
        }
    }

    // ========== US17: Cập nhật trạng thái giao hàng (Controller layer) ==========
    @Nested
    @DisplayName("US17 - Cập nhật trạng thái giao hàng (Controller)")
    class US17_CapNhatTrangThai {

        @Test
        @DisplayName("TC17_09: Controller - cập nhật trạng thái chuyển sang 'Đang ship'")
        void testCapNhatTrangThai_DangShip() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            DonHang updatedDonHang = DonHang.builder()
                    .maDonHang("DH001")
                    .trangThai("Đang ship")
                    .build();
            when(donHangService.capNhatTrangThai("DH001", "Đang ship"))
                    .thenReturn(updatedDonHang);

            // Act
            String result = controller.capNhatTrangThai("DH001", "Đang ship", redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/don-hang/chi-tiet/DH001", result);
            assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        }

        @Test
        @DisplayName("TC17_10: Controller - đánh dấu đã thanh toán")
        void testDaThanhToan_True() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            DonHang updatedDonHang = DonHang.builder()
                    .maDonHang("DH001")
                    .daThanhToan(true)
                    .build();
            when(donHangService.capNhatDaThanhToan("DH001", true))
                    .thenReturn(updatedDonHang);

            // Act
            String result = controller.daThanhToan("DH001", true, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/don-hang/chi-tiet/DH001", result);
            String successMsg = (String) redirectAttributes.getFlashAttributes().get("success");
            assertTrue(successMsg.contains("ĐÃ THANH TOÁN"));
        }

        @Test
        @DisplayName("TC17_11: Controller - bỏ đánh dấu đã thanh toán")
        void testDaThanhToan_False() {
            // Arrange
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            DonHang updatedDonHang = DonHang.builder()
                    .maDonHang("DH001")
                    .daThanhToan(false)
                    .build();
            when(donHangService.capNhatDaThanhToan("DH001", false))
                    .thenReturn(updatedDonHang);

            // Act
            String result = controller.daThanhToan("DH001", null, redirectAttributes);

            // Assert
            assertEquals("redirect:/admin/don-hang/chi-tiet/DH001", result);
        }

        @Test
        @DisplayName("TC17_12: Controller - chi tiết đơn hàng")
        void testChiTietDonHang() {
            // Arrange
            when(donHangService.getDonHangById("DH001"))
                    .thenReturn(Optional.of(donHangThuong));
            when(chiTietDonHangRepository.findByDonHang_MaDonHang("DH001"))
                    .thenReturn(Arrays.asList());

            // Act
            String result = controller.chiTietDonHang("DH001", model);

            // Assert
            assertEquals("admin/admin-chi-tiet-don-hang", result);
            verify(model).addAttribute("donHang", donHangThuong);
            verify(model).addAttribute(eq("chiTietList"), anyList());
        }

        @Test
        @DisplayName("TC17_13: Controller - chi tiết đơn hàng không tồn tại -> redirect")
        void testChiTietDonHang_KhongTonTai() {
            // Arrange
            when(donHangService.getDonHangById("DH999")).thenReturn(Optional.empty());

            // Act
            String result = controller.chiTietDonHang("DH999", model);

            // Assert
            assertEquals("redirect:/admin/don-hang", result);
        }
    }
}
