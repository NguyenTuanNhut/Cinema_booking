# Hướng dẫn Flow Xác nhận Đơn Đặt Vé (Booking Confirmation)

## Tổng Quan

Quy trình đặt vé gồm 2 giai đoạn:
1. **Khách hàng đặt vé** → Booking ở trạng thái **PENDING**
2. **Nhân viên xác nhận** → Booking chuyển sang **CONFIRMED**

---

## Chi Tiết Các Trạng Thái

| Trạng Thái | Mô Tả | Ai Thực Hiện |
|-----------|-------|-------------|
| **PENDING** | Đơn vừa được đặt, chờ xác nhận thanh toán | Khách hàng |
| **CONFIRMED** | Đã xác nhận thanh toán, vé có hiệu lực | Nhân viên (Staff) |
| **CANCELLED** | Đơn bị hủy | Khách hàng hoặc Admin |

---

## Quy Trình Chi Tiết

### 1️⃣ Khách Hàng Đặt Vé

**Endpoint:** `POST /bookings/create`

```
Các bước:
1. Khách hàng chọn phim → click "Đặt vé"
2. Chọn ghế → click "Đặt vé"
3. Hệ thống tạo Booking với trạng thái PENDING
4. Khách hàng nhận được thông báo: "Đặt vé thành công. Vui lòng hoàn tất thanh toán hoặc chờ xác nhận."
```

**Dữ liệu được tạo:**
- Booking entity: status = PENDING
- Ticket entities: status = PENDING
- Total price được tính toán

---

### 2️⃣ Nhân Viên Xác Nhận

**Quy trình:**

```
1. Nhân viên truy cập: /staff/bookings
2. Nhập mã đơn (booking code) → Click "Tìm"
3. Hệ thống hiển thị thông tin đơn
4. Nếu trạng thái = PENDING → Click "Xác nhận thanh toán"
5. Booking chuyển sang CONFIRMED
6. Tất cả Ticket chuyển sang ACTIVE
7. Nhều viên có thể in vé: click "In vé"
```

**Endpoint xác nhận:** `POST /staff/bookings/confirm/{id}`

---

## Dữ Liệu Thay Đổi Khi Confirmed

### Booking
```java
booking.status = BookingStatus.CONFIRMED
booking.confirmedBy = staff_user_object
booking.confirmedAt = current_timestamp
```

### Tickets
```java
ticket.status = TicketStatus.ACTIVE  // Chuyển từ PENDING sang ACTIVE
```

---

## Các Lỗi Có Thể Gặp

### 1. Ghế đã được đặt
```
Lỗi: "Ghế XXX đã được đặt!"
```
**Nguyên nhân:** Ghế được chọn đã có người đặt trước
**Giải pháp:** Chọn ghế khác

### 2. Chỉ có thể hủy vé trước giờ chiếu 24 tiếng
```
Lỗi: "Chỉ có thể hủy vé trước giờ chiếu 24 tiếng"
```
**Nguyên nhân:** Vé không thể hủy nếu còn < 24h đến suất chiếu
**Giải pháp:** Liên hệ nhân viên để yêu cầu hủy

---

## Kiểm Tra Trạng Thái Booking

### Khách hàng xem lịch sử vé
**URL:** `/bookings/history`

**Hiển thị:**
- Mã vé
- Tên phim
- Thời gian chiếu
- Phòng chiếu
- Danh sách ghế
- Tổng tiền
- **Trạng thái (PENDING/CONFIRMED/CANCELLED)**
- Nút hủy vé (nếu CONFIRMED)

---

## Ví Dụ Thực Tế

### Scenario 1: Đặt vé và xác nhận
```
Timeline:
14:00 - Khách hàng A đặt vé cho phim "Inception" lúc 19:00 hôm nay
        → Booking: PENDING, Tickets: PENDING
        
14:05 - Nhân viên B tra cứu đơn theo booking code
        → Thấy status = PENDING, ghế = [A1, A2]
        
14:06 - Nhân viên B click "Xác nhận thanh toán"
        → Booking: CONFIRMED, confirmedBy = B, confirmedAt = 14:06
        → Tickets: ACTIVE
        
14:07 - Khách hàng A vào "Lịch sử vé"
        → Thấy Booking status = CONFIRMED ✓
        → Nhân viên B có thể in vé
```

### Scenario 2: Hủy vé
```
Timeline:
14:00 - Khách hàng A đặt vé cho phim "Inception" lúc 19:00 hôm nay
        → Booking: PENDING
        
14:30 - Khách hàng A vào "Lịch sử vé"
        → Lỗi: Không thể hủy vé (vì chưa CONFIRMED)
        
14:30 - Nhân viên B xác nhận thanh toán
        → Booking: CONFIRMED
        
14:35 - Khách hàng A vào "Lịch sử vé" lại
        → Thấy nút "Hủy vé", click hủy
        → Booking: CANCELLED, Tickets bị xóa
        → Message: "Đã hủy vé thành công"
```

---

## Quyền Truy Cập

| Endpoint | Khách Hàng | Staff | Admin |
|----------|-----------|-------|-------|
| POST /bookings/create | ✅ | ❌ | ✅ |
| GET /bookings/history | ✅ | ⚠️ (riêng của mình) | ✅ |
| POST /bookings/cancel/{id} | ✅ | ❌ | ✅ |
| GET /staff/bookings | ❌ | ✅ | ✅ |
| POST /staff/bookings/confirm/{id} | ❌ | ✅ | ✅ |
| GET /staff/bookings/print | ❌ | ✅ | ✅ |

---

## Code Liên Quan

### BookingStatus Enum
```java
public enum BookingStatus {
    PENDING,      // Chưa xác nhận
    CONFIRMED,    // Đã xác nhận
    CANCELLED     // Đã hủy
}
```

### TicketStatus Enum
```java
public enum TicketStatus {
    PENDING,      // Chưa xác nhận (khi booking PENDING)
    ACTIVE,       // Có hiệu lực (khi booking CONFIRMED)
    CANCELLED     // Đã hủy (khi booking CANCELLED)
}
```

### Service Methods
```java
// Tạo đơn (Khách hàng)
bookingService.createBooking(userId, request)

// Xác nhận thanh toán (Nhân viên)
bookingService.confirmBooking(bookingId, staffUserId)

// Hủy vé (Khách hàng)
bookingService.cancelBooking(bookingId)

// Xem lịch sử (Khách hàng)
bookingService.getBookingHistory(userId)

// Tìm theo mã đơn (Nhân viên)
bookingService.findByBookingCode(bookingCode)
```

---

## Ứng Dụng Tiếp Theo (Có thể cải thiện)

1. **Payment Integration** - Tích hợp thanh toán (Stripe, PayPal)
2. **Expiry Job** - Tự động hủy booking PENDING sau N tiếng
3. **Email Notification** - Gửi email khi booking được xác nhận
4. **QR Code** - In QR code trên vé để kiểm tra
5. **Refund Management** - Quản lý hoàn tiền khi hủy vé

