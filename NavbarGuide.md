# 📱 Hướng Dẫn Navbar Mới Cho Staff

## ✨ Cập Nhật

Tôi đã thêm **Menu Navbar Chung** cho tất cả pages, đặc biệt là menu **NHÂN VIÊN** cho staff.

---

## 🎯 Navbar Có Gì Mới?

### 1️⃣ **Trang Chủ** (Home)
```
Link: /movies
Hiển thị trên navbar cho tất cả user
```

### 2️⃣ **QUẢN LÝ** (ADMIN Menu)
```
Chỉ hiển thị cho: ROLE_ADMIN, ADMIN
Màu: Vàng (text-warning)
Menu items:
  - Quản lý Phim → /admin/movies
  - Quản lý Suất chiếu → /admin/showtimes
```

### 3️⃣ **👨‍💼 NHÂN VIÊN** (STAFF Menu) - MỚI ⭐
```
Chỉ hiển thị cho: ROLE_STAFF, STAFF
Màu: Xanh (text-success)
Menu items:
  - 📋 Đơn Chờ Xác Nhận → /staff/pending-bookings
  - 🔍 Tìm Kiếm & Xác Nhận → /staff/bookings
  - 🏠 Dashboard Nhân Viên → /staff
```

### 4️⃣ **Hồ Sơ & Lịch Sử** (User Menu)
```
Hiển thị cho: Tất cả user đã login
- Chào: [Tên User] (Role)
- Hồ sơ → /profile
- Lịch sử vé → /bookings/history
- Đăng xuất
```

---

## 📍 Navbar Trên Các Trang

Navbar đã được thêm vào các trang sau:

### Admin Pages:
✅ `/admin/movies` - Quản lý phim  
✅ `/admin/showtimes` - Quản lý suất chiếu  

### Staff Pages:
✅ `/staff` - Dashboard nhân viên  
✅ `/staff/pending-bookings` - Danh sách chờ xác nhận  
✅ `/staff/bookings` - Tìm kiếm đơn  

### User Pages:
✅ `/movies` - Danh sách phim  
✅ `/bookings/history` - Lịch sử vé  
✅ `/profile` - Hồ sơ cá nhân  
✅ `/bookings/select-seats` - Chọn ghế  

---

## 🔐 Quyền Truy Cập Menu

| Menu | Admin | Staff | User |
|------|-------|-------|------|
| **QUẢN LÝ** | ✅ Hiển thị | ❌ Ẩn | ❌ Ẩn |
| **👨‍💼 NHÂN VIÊN** | ⚠️ Hiển thị | ✅ Hiển thị | ❌ Ẩn |
| **Hồ sơ** | ✅ Hiển thị | ✅ Hiển thị | ✅ Hiển thị |
| **Lịch sử vé** | ✅ Hiển thị | ✅ Hiển thị | ✅ Hiển thị |

---

## 🎬 Các Tình Huống

### Tình Huống 1: Khách Hàng (Role: USER)
```
Truy cập: http://localhost:8080/movies
Navbar hiển thị:
  - Trang chủ
  - Hồ sơ ← Cá nhân
  - Lịch sử vé ← Xem vé đã đặt
  - Đăng xuất
```

### Tình Huống 2: Nhân Viên (Role: STAFF)
```
Truy cập: http://localhost:8080/movies
Navbar hiển thị:
  - Trang chủ
  - 👨‍💼 NHÂN VIÊN ← MENU MỚI
      │
      ├─ 📋 Đơn Chờ Xác Nhận
      ├─ 🔍 Tìm Kiếm & Xác Nhận
      └─ 🏠 Dashboard Nhân Viên
  - Hồ sơ
  - Lịch sử vé
  - Đăng xuất

Click "👨‍💼 NHÂN VIÊN" → Menu dropdown hiển thị 3 option
```

### Tình Huống 3: Admin (Role: ADMIN)
```
Truy cập: http://localhost:8080/movies
Navbar hiển thị:
  - Trang chủ
  - QUẢN LÝ (ADMIN)
      │
      ├─ Quản lý Phim
      └─ Quản lý Suất chiếu
  - 👨‍💼 NHÂN VIÊN (vì ADMIN cũng có quyền staff)
      │
      ├─ 📋 Đơn Chờ Xác Nhận
      ├─ 🔍 Tìm Kiếm & Xác Nhận
      └─ 🏠 Dashboard Nhân Viên
  - Hồ sơ
  - Lịch sử vé
  - Đăng xuất
```

---

## 🎨 Màu Sắc Menu

```css
QUẢN LÝ (Admin)  → Vàng (text-warning)    ⚠️
👨‍💼 NHÂN VIÊN → Xanh (text-success)   ✅
```

---

## 🚀 Cách Sử Dụng

### Cho Khách Hàng:
1. Vào trang chủ → Đặt vé
2. Navbar menu → "Lịch sử vé" → Xem vé đã đặt
3. Navbar menu → "Hồ sơ" → Cập nhật thông tin

### Cho Nhân Viên:
1. Login với: **staff@gmail.com / staff123**
2. Navbar menu → "👨‍💼 NHÂN VIÊN"
3. Chọn:
   - **📋 Đơn Chờ Xác Nhận** → Xem tất cả booking PENDING
   - **🔍 Tìm Kiếm & Xác Nhận** → Tìm theo mã
   - **🏠 Dashboard** → Trang chính

### Cho Admin:
1. Login với admin account
2. Navbar menu → "QUẢN LÝ" → Quản lý Phim/Suất chiếu
3. Navbar menu → "👨‍💼 NHÂN VIÊN" → Quản lý vé

---

## ✅ Lợi Ích Của Navbar Mới

| Lợi Ích | Mô Tả |
|--------|-------|
| **Dễ sử dụng** | Một click từ bất kỳ trang nào để truy cập chức năng staff |
| **Consistent** | Navbar giống nhau trên tất cả trang |
| **Secure** | Menu chỉ hiển thị dựa trên quyền hạn |
| **Mobile-friendly** | Menu collapse trên mobile (burger menu) |
| **Professional** | Hiển thị tên user và role hiện tại |

---

## 📝 Kỹ Thuật

### Làm thế nào navbar hiển thị menu dựa trên role?

```html
<!-- Dùng Spring Security Thymeleaf integration -->
<li sec:authorize="hasAnyAuthority('ROLE_STAFF', 'STAFF')">
    <!-- Chỉ hiển thị nếu user có role STAFF -->
    👨‍💼 NHÂN VIÊN
</li>
```

### File Navbar:
- **Navbar fragment**: `/fragments/navbar.html` (có thể tái sử dụng)
- **Navbar embedded**: Có sẵn trên mỗi trang HTML
- **Consistency**: Tất cả trang dùng cùng HTML navbar code

---

## 🎯 Summary

| Thay Đổi | Chi Tiết |
|---------|---------|
| **Thêm Menu NHÂN VIÊN** | Menu dropdown với 3 option cho staff |
| **Navbar Trên Tất Cả Trang** | Consistent design trên movies, bookings, profile, staff |
| **Security-aware** | Menu được ẩn hiện dựa trên role |
| **Mobile-responsive** | Burger menu trên mobile devices |
| **Easy Navigation** | 1 click để truy cập bất kỳ chức năng nào |

---

**✅ Bây giờ Staff có thể dễ dàng truy cập tất cả chức năng từ navbar!**

