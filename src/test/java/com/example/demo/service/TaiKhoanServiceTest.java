package com.example.demo.service;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.TaiKhoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases cho TaiKhoanService (đăng ký, đăng nhập, phân quyền)
 */
@ExtendWith(MockitoExtension.class)
class TaiKhoanServiceTest {

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private KhachHangRepository khachHangRepository;

    @InjectMocks
    private TaiKhoanService taiKhoanService;

    private TaiKhoan taiKhoanKhachHang;
    private TaiKhoan taiKhoanAdmin;

    @BeforeEach
    void setUp() {
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang("KH001")
                .ten("Nguyễn Văn A")
                .soDienThoai("0901234567")
                .email("nguyenvana@gmail.com")
                .diaChi("123 Lê Lợi, Q1, TP.HCM")
                .build();

        taiKhoanKhachHang = TaiKhoan.builder()
                .maTaiKhoan("TK001")
                .tenDangNhap("nguyenvana")
                .matKhau("password123")
                .khachHang(khachHang)
                .vaiTro("KHACH_HANG")
                .build();

        taiKhoanAdmin = TaiKhoan.builder()
                .maTaiKhoan("TK002")
                .tenDangNhap("admin")
                .matKhau("admin123")
                .vaiTro("ADMIN")
                .build();
    }

    @Nested
    @DisplayName("Đăng ký tài khoản")
    class DangKy {

        @Test
        @DisplayName("Đăng ký thành công với đầy đủ thông tin")
        void testDangKy_ThanhCong() {
            // Arrange
            when(taiKhoanRepository.existsByTenDangNhap("newuser")).thenReturn(false);
            when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(i -> i.getArgument(0));
            when(taiKhoanRepository.save(any(TaiKhoan.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            TaiKhoan result = taiKhoanService.dangKy("newuser", "pass123",
                    "Trần Văn B", "0909888777", "tranvanb@gmail.com", "456 Nguyễn Huệ");

            // Assert
            assertNotNull(result);
            assertEquals("newuser", result.getTenDangNhap());
            assertEquals("KHACH_HANG", result.getVaiTro());
            verify(khachHangRepository).save(any(KhachHang.class));
            verify(taiKhoanRepository).save(any(TaiKhoan.class));
        }

        @Test
        @DisplayName("Đăng ký với tên đăng nhập đã tồn tại - ném exception")
        void testDangKy_TrungTenDangNhap() {
            // Arrange
            when(taiKhoanRepository.existsByTenDangNhap("nguyenvana")).thenReturn(true);

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> taiKhoanService.dangKy("nguyenvana", "pass123",
                            "Nguyễn Văn A", "0901234567", "a@gmail.com", "123 Lê Lợi"));
            assertTrue(ex.getMessage().contains("đã tồn tại"));
        }
    }

    @Nested
    @DisplayName("Đăng nhập")
    class DangNhap {

        @Test
        @DisplayName("Đăng nhập thành công")
        void testDangNhap_ThanhCong() {
            // Arrange
            when(taiKhoanRepository.findByTenDangNhap("nguyenvana"))
                    .thenReturn(Optional.of(taiKhoanKhachHang));

            // Act
            Optional<TaiKhoan> result = taiKhoanService.dangNhap("nguyenvana", "password123");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("nguyenvana", result.get().getTenDangNhap());
        }

        @Test
        @DisplayName("Đăng nhập sai mật khẩu")
        void testDangNhap_SaiMatKhau() {
            // Arrange
            when(taiKhoanRepository.findByTenDangNhap("nguyenvana"))
                    .thenReturn(Optional.of(taiKhoanKhachHang));

            // Act
            Optional<TaiKhoan> result = taiKhoanService.dangNhap("nguyenvana", "wrongpass");

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Đăng nhập với tên đăng nhập không tồn tại")
        void testDangNhap_KhongTonTai() {
            // Arrange
            when(taiKhoanRepository.findByTenDangNhap("khongtontai"))
                    .thenReturn(Optional.empty());

            // Act
            Optional<TaiKhoan> result = taiKhoanService.dangNhap("khongtontai", "pass123");

            // Assert
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Phân quyền")
    class PhanQuyen {

        @Test
        @DisplayName("Kiểm tra tài khoản ADMIN")
        void testIsAdmin_True() {
            assertTrue(taiKhoanService.isAdmin(taiKhoanAdmin));
        }

        @Test
        @DisplayName("Kiểm tra tài khoản KHACH_HANG không phải admin")
        void testIsAdmin_False() {
            assertFalse(taiKhoanService.isAdmin(taiKhoanKhachHang));
        }
    }
}
