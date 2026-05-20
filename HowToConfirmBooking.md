# 🎯 Hướng Dẫn Xác Nhận Booking Để Status Thành CONFIRMED

## 📱 Truy Cập Staff Page

Nhân viên cần có tài khoản STAFF hoặc ADMIN để truy cập.

### Username & Password mặc định:
```
Email: staff@gmail.com
Password: staff123
```

---

## 📋 Cách 1: Xem Danh Sách Tất Cả Đơn Chờ Xác Nhận

### Bước 1: Vào Dashboard
```
URL: http://localhost:8080/staff
```

### Bước 2: Click "📋 Đơn Chờ Xác Nhận"
```
Hoặc trực tiếp vào: http://localhost:8080/staff/pending-bookings
```

### Bước 3: Xem Danh Sách
Trang sẽ hiển thị tất cả booking có **status = PENDING**:
- ✅ Mã đơn (booking code) - Badge màu vàng
- ✅ Email khách hàng
- ✅ Tên phim
- ✅ Suất chiếu
- ✅ Phòng chiếu
- ✅ Danh sách ghế (badge màu xanh)
- ✅ Tổng tiền
- ✅ Thời gian đặt vé

### Bước 4: Click "✓ Xác Nhận"
- Booking sẽ chuyển sang **CONFIRMED status**
- Tất cả Ticket sẽ chuyển sang **ACTIVE status**
- Thông báo: "Xác nhận thanh toán thành công!"

---

## 🔍 Cách 2: Tìm Kiếm Theo Mã Đơn

### Bước 1: Truy Cập Tìm Kiếm
```
URL: http://localhost:8080/staff/bookings
```

Hoặc từ Dashboard → Click **"🔍 Tìm Kiếm & Xác Nhận"**

### Bước 2: Nhập Mã Đơn
```
Ví dụ: ABC12345
(Mã này được tạo ngẫu nhiên khi khách hàng đặt vé)
```

### Bước 3: Click "Tìm"
Hệ thống sẽ hiển thị thông tin chi tiết của đơn:
- Mã vé
- Khách hàng
- Phim & suất chiếu
- Trạng thái hiện tại
- Danh sách ghế
- Ai đã xác nhận (nếu đã xác nhận trước đó)
- Thời gian xác nhận (nếu đã xác nhận)

### Bước 4: Xác Nhận Hoặc In Vé

#### Nếu Status = PENDING:
```
✅ Nút "Xác nhận thanh toán" (màu xanh) - Click để xác nhận
```

#### Nếu Status = CONFIRMED:
```
✓ Đã xác nhận (disabled button) - không thể click
```

#### Nếu Status = CANCELLED:
```
✗ Đã hủy (disabled button) - không thể click
```

### Bước 5: In Vé (Sau Khi Confirm)
```
Click "In vé" → Trang in sẽ mở
→ Ctrl+P hoặc print → In vé cho khách hàng
```

---

## 🎬 Ví Dụ Cụ Thể Chi Tiết

### Scenario: Khách Hàng Đặt 2 Vé

#### 1️⃣ Khách hàng đặt vé
```
- Chọn phim "Inception" lúc 19:00 hôm nay
- Chọn ghế: A1, A2
- Click "Đặt vé"
- Booking Code được tạo: ABC12345
- Status: PENDING (chưa xác nhận)
```

#### 2️⃣ Nhân viên xác nhận

**Cách A: Qua danh sách chờ xác nhận**
```
1. Vào Staff page: /staff
2. Click "📋 Đơn Chờ Xác Nhận"
3. Thấy hàng với:
   - Mã đơn: ABC12345 (badge vàng)
   - Khách: user@email.com
   - Phim: Inception
   - Ghế: A1, A2 (badge xanh)
4. Click "✓ Xác Nhận"
5. → Status: CONFIRMED ✅
```

**Cách B: Qua tìm kiếm theo mã**
```
1. Vào Staff page: /staff
2. Nhập "ABC12345" vào ô tìm kiếm
3. Click "Tìm"
4. Xem chi tiết: Status = PENDING
5. Click "Xác nhận thanh toán"
6. → Status: CONFIRMED ✅
7. Click "In vé" để in vé cho khách
```

#### 3️⃣ Khách hàng kiểm tra
```
Khách vào: /bookings/history
→ Thấy booking: Status = CONFIRMED ✓
→ Có thể hủy nếu còn > 24h trước giờ chiếu
```

---

## 📊 Bảng So Sánh Các Status

| Status | Mô Tả | Hành Động | Nút Xác Nhận |
|--------|-------|----------|------------|
| **PENDING** | Chờ xác nhận | Chi tiết hiển thị | ✅ Hiển thị "Xác nhận thanh toán" |
| **CONFIRMED** | Đã xác nhận | Chi tiết + người xác nhận | ❌ "✓ Đã xác nhận" (disabled) |
| **CANCELLED** | Đã hủy | Chi tiết + lý do hủy | ❌ "✗ Đã hủy" (disabled) |

---

## ❌ Lỗi Thường Gặp

### 1. "Không tìm thấy mã đơn"
```
Nguyên nhân: Mã đơn nhập sai hoặc booking không tồn tại
Giải pháp: Kiểm tra lại mã, dùng danh sách PENDING để xem tất cả
```

### 2. Không thấy nút "Xác nhận thanh toán"
```
Nguyên nhân: Booking đã ở status CONFIRMED hoặc CANCELLED
Giải pháp: Không cần xác nhận lại, nó đã xác nhận rồi
```

### 3. "Xác nhận thanh toán thành công!" nhưng không chuyển trang
```
Nguyên nhân: Trình duyệt cache, cần refresh
Giải pháp: F5 refresh hoặc Ctrl+Shift+R hard refresh
```

---

## 🔐 Quyền Truy Cập

| Tài Khoản | Quyền |
|-----------|------|
| **ADMIN** | ✅ Truy cập tất cả, xác nhận booking |
| **STAFF** | ✅ Tìm kiếm & xác nhận booking |
| **USER** | ❌ Không thể vào /staff |

---

## 🎁 Quick Actions

### Trực Tiếp Nhập URL:

```
# Xem danh sách chờ xác nhận
http://localhost:8080/staff/pending-bookings

# Tìm kiếm theo mã
http://localhost:8080/staff/bookings

# In vé
http://localhost:8080/staff/bookings/print?code=ABC12345
```

---

## 📝 Quy Trình Workflow Đầy Đủ

```
┌─────────────────────────────────────────────────────────────┐
│ 1. KHÁCH HÀNG ĐẶT VÉ                                        │
├─────────────────────────────────────────────────────────────┤
│ /movies → Chọn phim                                          │
│ /movies/{id}/showtime → Chọn suất chiếu                     │
│ /bookings/select-seats/{id} → Chọn ghế                      │
│ POST /bookings/create → Đặt vé                              │
│ → Booking: PENDING, Tickets: PENDING                        │
│ → Hệ thống gửi booking code: ABC12345                       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. NHÂN VIÊN XÁC NHẬN (STAFF)                              │
├─────────────────────────────────────────────────────────────┤
│ /staff → Dashboard                                          │
│ /staff/pending-bookings → Xem danh sách                     │
│ HOẶC /staff/bookings → Tìm kiếm theo mã ABC12345           │
│ POST /staff/bookings/confirm/{id} → Xác nhận               │
│ → Booking: CONFIRMED, Tickets: ACTIVE                       │
│ → confirmedBy: staff_user                                   │
│ → confirmedAt: thời gian xác nhận                           │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. IN VÉ (STAFF)                                            │
├─────────────────────────────────────────────────────────────┤
│ /staff/bookings/print?code=ABC12345 → Trang in vé          │
│ Ctrl+P → In vé cho khách hàng                               │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. KHÁCH HÀNG KIỂM TRA (USER)                              │
├─────────────────────────────────────────────────────────────┤
│ /bookings/history → Xem lịch sử vé                          │
│ → Status: CONFIRMED ✅ = Có hiệu lực                       │
│ → Có thể hủy vé nếu còn > 24h trước giờ chiếu              │
│ → POST /bookings/cancel/{id} → Hủy vé                      │
│ → Booking: CANCELLED, Tickets: DELETED                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 💡 Tips & Tricks

1. **Để nhanh hơn**: Dùng danh sách PENDING thay vì tìm kiếm từng cái
2. **Để in batch**: Mở multiple tabs để in nhiều vé cùng lúc
3. **Để check lại**: Tìm kiếm theo code để verify booking đã confirm chưa
4. **Để urgent**: Nhân viên có thể force confirm nếu khách call điện

---

**✅ Bây giờ bạn đã biết cách confirm booking để status thành CONFIRMED!**

