# Sơ đồ Use Case: Giỏ hàng (T33)

**Hạng mục:** Vẽ sơ đồ Use case giỏ hàng
**Mã Task:** T33
**Mã US:** US03
**Giai đoạn:** Sprint 1

## Sơ đồ Use Case

```mermaid
useCaseDiagram
    actor "Khách hàng" as Customer
    actor "Khách vãng lai" as Guest
    
    package "Quản lý Giỏ hàng" {
        usecase "Xem giỏ hàng" as UC1
        usecase "Thêm sản phẩm vào giỏ" as UC2
        usecase "Cập nhật số lượng" as UC3
        usecase "Xóa sản phẩm khỏi giỏ" as UC4
        usecase "Làm trống giỏ hàng" as UC5
        usecase "Áp dụng mã giảm giá" as UC6
    }
    
    Customer --> UC1
    Customer --> UC2
    Customer --> UC3
    Customer --> UC4
    Customer --> UC5
    Customer --> UC6
    
    Guest --> UC1
    Guest --> UC2
    Guest --> UC3
    Guest --> UC4
    Guest --> UC5
```

## Mô tả chi tiết (US03)
- **Tên Use Case:** Cập nhật giỏ hàng
- **Tác nhân:** Khách hàng
- **Mô tả:** Cho phép khách hàng thay đổi số lượng sản phẩm hoặc xóa sản phẩm trong quá trình mua sắm.
- **Quy tắc nghiệp vụ (RB05):** Kiểm tra số lượng tồn kho trước khi cập nhật số lượng trong giỏ hàng. Nếu số lượng vượt quá tồn kho, hiển thị thông báo lỗi.

## Trạng thái: New
**Điểm nỗ lực:** 8
