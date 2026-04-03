package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
public class TestUS {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private SanPhamService sanPhamService;
    @MockitoBean private GioHangService gioHangService;
    @MockitoBean private DonHangService donHangService;
    @MockitoBean private BaoHanhService baoHanhService;
    @MockitoBean private SanPhamRepository sanPhamRepository;
    @MockitoBean private HangRepository hangRepository;
    @MockitoBean private LoaiSanPhamRepository loaiSanPhamRepository;
    @MockitoBean private MaGiamGiaRepository maGiamGiaRepository;
    @MockitoBean private BaoHanhRepository baoHanhRepository;
    @MockitoBean private ChiTietDonHangRepository chiTietDonHangRepository;

    @BeforeEach
    void setUp() {
        // Dữ liệu chung mặc định để tránh NPE trên UI
        when(sanPhamService.getAllHang()).thenReturn(Collections.emptyList());
        when(gioHangService.getSoLuongTrongGio()).thenReturn(1);
        when(gioHangService.getTongTienGioHang()).thenReturn(new BigDecimal("1000000"));
        
        ChiTietGioHang item = new ChiTietGioHang();
        item.setSanPham(SanPham.builder().tenSanPham("Sản phẩm mẫu").gia(new BigDecimal("1000000")).build());
        item.setSoLuong(1);
        when(gioHangService.getChiTietGioHang()).thenReturn(List.of(item));
    }

    // ====================================================================================
    // US01 - US09: KHÁCH HÀNG (TRANG CHỦ, GIỎ HÀNG, ĐẶT HÀNG)
    // ====================================================================================
    @Nested
    @DisplayName("Khách hàng: US01-US09")
    class CustomerCoreTests {
        @Test @DisplayName("US01: Danh sách SP dạng Grid")
        void testUS01() throws Exception {
            SanPham sp = SanPham.builder().tenSanPham("iPhone 15").preOrder(false).soLuong(5).build();
            when(sanPhamService.getDanhSachSanPham(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of(sp)));
            mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(containsString("iPhone 15")));
        }

        @Test @DisplayName("US02: Lọc theo hãng")
        void testUS02() throws Exception {
            SanPham sp = SanPham.builder().tenSanPham("Samsung S24").preOrder(false).soLuong(5).build();
            when(sanPhamService.locTheoHang(eq("SS"), anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of(sp)));
            mockMvc.perform(get("/").param("hang", "SS")).andExpect(status().isOk());
        }

        @Test @DisplayName("US03: Thêm vào giỏ hàng")
        void testUS03() throws Exception {
            mockMvc.perform(post("/gio-hang/them").param("maSanPham", "SP1").header("referer", "/"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test @DisplayName("US04-US05: Validate thanh toán")
        void testUS04_US05() throws Exception {
            mockMvc.perform(post("/dat-hang/xac-nhan")
                            .param("tenKhach", "An").param("soDienThoai", "0988776655")
                            .param("diaChi", "HN").param("phuongThucThanhToan", "COD"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test @DisplayName("US06: Mã giảm giá")
        void testUS06() throws Exception {
            MaGiamGia mgg = new MaGiamGia(); mgg.setTenMa("KM10"); mgg.setPhanTramGiam(10.0);
            when(donHangService.kiemTraMaGiamGia(eq("KM10"), any())).thenReturn(mgg);
            mockMvc.perform(post("/dat-hang/kiem-tra-ma-giam-gia").param("maGiamGia", "KM10"))
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test @DisplayName("US08: Pre-order page")
        void testUS08() throws Exception {
            SanPham sp = SanPham.builder().tenSanPham("iPhone 16").preOrder(true).build();
            when(sanPhamService.getPreOrderProducts()).thenReturn(List.of(sp));
            mockMvc.perform(get("/pre-order")).andExpect(status().isOk()).andExpect(content().string(containsString("iPhone 16")));
        }

        @Test @DisplayName("US09: Tìm đơn đổi trả")
        void testUS09() throws Exception {
            DonHang dh = new DonHang(); dh.setTrangThai("Hoàn thành");
            when(donHangService.timDonHangDeDoiTra(anyString(), anyString())).thenReturn(Optional.of(dh));
            mockMvc.perform(post("/doi-tra/tim-don").param("maDonHang", "DH1").param("soDienThoai", "0911223344"))
                    .andExpect(status().isOk());
        }
    }

    // ====================================================================================
    // US10: KHÁCH HÀNG - YÊU CẦU BẢO HÀNH
    // ====================================================================================
    @Nested
    @DisplayName("Khách hàng: US10")
    class WarrantyTests {
        @Test @DisplayName("US10: Gửi yêu cầu bảo hành")
        void testUS10() throws Exception {
            BaoHanh bh = new BaoHanh(); bh.setMaBaoHanh("BH01");
            when(baoHanhService.taoYeuCauBaoHanh(anyString(), anyString())).thenReturn(bh);
            mockMvc.perform(post("/bao-hanh/gui-yeu-cau")
                            .param("maDonHang", "DH01")
                            .param("moTaLoi", "Màn hình bị sọc"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // ====================================================================================
    // US11 - US18: QUẢN TRỊ VIÊN (ADMIN)
    // ====================================================================================
    @Nested
    @DisplayName("Admin: US11-US18")
    class AdminTests {
        // Cần sessionattr vaiTro=ADMIN để qua Interceptor
        private final String ROLE_ADMIN = "ADMIN";

        @Test @DisplayName("US11-US12: Admin Thêm SP")
        void testUS11_US12() throws Exception {
            mockMvc.perform(post("/admin/san-pham/them")
                            .sessionAttr("vaiTro", ROLE_ADMIN)
                            .param("tenSanPham", "Oppo Reno 12")
                            .param("maHang", "H01").param("gia", "12000000").param("maLoai", "L01"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test @DisplayName("US13-15: Quản lý PGG")
        void testUS13_15() throws Exception {
            mockMvc.perform(post("/admin/giam-gia/them")
                            .sessionAttr("vaiTro", ROLE_ADMIN)
                            .param("tenMa", "XMAS2025").param("phanTramGiam", "20")
                            .param("ngayBatDau", "2025-12-01T00:00").param("ngayKetThuc", "2025-12-25T23:59"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test @DisplayName("US16: Xem danh sách đơn hàng")
        void testUS16() throws Exception {
            when(donHangService.getAllDonHang()).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/admin/don-hang").sessionAttr("vaiTro", ROLE_ADMIN))
                    .andExpect(status().isOk());
        }

        @Test @DisplayName("US17: Cập nhật trạng thái đơn")
        void testUS17() throws Exception {
            mockMvc.perform(post("/admin/don-hang/cap-nhat-trang-thai")
                            .sessionAttr("vaiTro", ROLE_ADMIN)
                            .param("maDonHang", "DH01").param("trangThai", "Đang Ship"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test @DisplayName("US18: Xem chi tiết (In hóa đơn)")
        void testUS18() throws Exception {
            DonHang dh = new DonHang(); dh.setMaDonHang("DH01");
            when(donHangService.getDonHangById(anyString())).thenReturn(Optional.of(dh));
            mockMvc.perform(get("/admin/don-hang/chi-tiet/DH01").sessionAttr("vaiTro", ROLE_ADMIN))
                    .andExpect(status().isOk());
        }
    }
}
