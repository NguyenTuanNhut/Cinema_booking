# 🗑️ Soft Delete Cho Phim - Hướng Dẫn

## ✅ Những gì đã được thêm

### 1. **Soft Delete Logic**
- Thay vì xóa vật lý phim khỏi database, chỉ set field `active = false`
- Phim đã xóa vẫn còn trong DB nhưng bị ẩn khỏi khách hàng
- Showtimes liên kết vẫn an toàn (không bị FK constraint error)

### 2. **MovieRepository** - Custom Queries
```java
// Lấy chỉ phim còn hoạt động
findAllActive()

// Lấy phim theo ID nếu còn active
findByIdIfActive(Long id)

// Lấy tất cả phim (kể cả đã xóa) - dành cho admin
findAllIncludingDeleted()
```

### 3. **AdminMovieController**
- ✅ **DELETE**: `GET /admin/movies/delete/{id}` → set `active = false`
- ✅ **RESTORE**: `GET /admin/movies/restore/{id}` → set `active = true` (khôi phục)
- ✅ **LIST**: Hiển thị tất cả phim (active + deleted) để admin quản lý
- ✅ **EDIT**: Chỉ cho sửa phim active

### 4. **MovieController** - Khách hàng
- ✅ Chỉ lấy `findAllActive()` - không thấy phim đã xóa
- ✅ Lấy movie by ID dùng `findByIdIfActive()` - nếu xóa sẽ lỗi 404

### 5. **Admin Movies List Template** (`admin/movies/list.html`)
- ✅ Hiển thị phim đã xóa với style **grey out** + label "Đã xóa"
- ✅ Nút **"Xóa"** cho phim active
- ✅ Nút **"Khôi phục"** cho phim đã xóa
- ✅ Nút **"Sửa"** chỉ cho phim active
- ✅ Cột trạng thái (Hoạt động / Đã xóa)

---

## 🚀 Cách Kiểm Tra (Local)

### Bước 1: Khởi động ứng dụng
```powershell
cd D:\JavaApplication\cinema-booking\cinema-booking
.\mvnw.cmd spring-boot:run
```

### Bước 2: Đăng nhập Admin
- http://localhost:8080/login
- Email: `admin@gmail.com`
- Password: `admin123`

### Bước 3: Vào Quản lý Phim
- Click: **QUẢN LÝ** → **Quản lý Phim**

### Bước 4: Test Soft Delete

#### A. Xóa phim
1. Chọn một phim
2. Click nút **"Xóa"**
3. Confirm xóa
4. ✅ Phim sẽ hiển thị với style grey out + label "Đã xóa"
5. ✅ Showtimes liên kết vẫn tồn tại (không lỗi!)

#### B. Khôi phục phim
1. Tìm phim đã xóa (style xám)
2. Click nút **"Khôi phục"**
3. Confirm
4. ✅ Phim sẽ quay lại trạng thái "Hoạt động"

#### C. Kiểm tra khách hàng không thấy phim xóa
1. Logout hoặc mở tab ẩn danh (incognito)
2. Vào http://localhost:8080/movies
3. ✅ Phim đã xóa **không hiển thị** trong danh sách

---

## 📊 Flow Xóa Phim

```
User click "Xóa" phim (Admin)
    ↓
POST /admin/movies/delete/{id}
    ↓
AdminMovieController.delete():
  - Find movie by id
  - Set active = false
  - Save to DB
    ↓
Redirect to /admin/movies
    ↓
Template render:
  - Phim hiển thị với style grey
  - Label "Đã xóa"
  - Nút "Khôi phục" thay "Xóa"
    ↓
✅ Hoàn tất (DB không mất data, Showtimes an toàn)
```

---

## 💾 Dữ liệu Database

Phim không được delete khỏi table, chỉ update cột `active`:

```sql
-- Trước xóa
SELECT * FROM movies WHERE id = 5;
-- | 5 | Avengers | ... | active: TRUE

-- Sau xóa (soft delete)
SELECT * FROM movies WHERE id = 5;
-- | 5 | Avengers | ... | active: FALSE

-- Showtimes vẫn an toàn
SELECT * FROM showtimes WHERE movie_id = 5;
-- | id | movie_id | room_id | ... |
-- | 10 | 5 | 1 | ... | ← FK constraint OK!
```

---

## 🎯 Các Trường Hợp Sử Dụng

| Tình huống | Hành động | Kết quả |
|-----------|----------|--------|
| Admin xóa phim, không còn showtimes | Soft delete (active=false) | ✅ Phim ẩn, data an toàn |
| Admin xóa phim, còn showtimes | Soft delete (active=false) | ✅ Phim ẩn, showtimes vẫn tồn tại |
| Khách xem danh sách phim | Query `findAllActive()` | ✅ Chỉ thấy phim active |
| Khách vào details phim xóa | Query `findByIdIfActive()` | ❌ 404 - Phim không tồn tại |
| Admin muốn khôi phục phim | Click "Khôi phục" | ✅ Set active=true, phim quay lại |

---

## 🛡️ Advantages

✅ **Không mất data**: Phim vẫn trong DB, chỉ ẩn khỏi UI  
✅ **Giải quyết FK constraint**: Showtimes không bị lỗi  
✅ **Audit trail**: Có thể track phim nào đã xóa (nếu thêm `deletedAt` timestamp)  
✅ **Khôi phục dễ**: Chỉ set lại `active = true`  
✅ **Admin control**: Admin thấy tất cả phim, khách chỉ thấy active  

---

## 🐛 Troubleshooting

### ❌ "Khi xóa phim, vẫn lỗi FK constraint"
**Nguyên nhân**: Code chưa update (cũ dùng `deleteById`)  
**Giải pháp**: Xác nhận app chạy version mới (rebuild: `mvn clean package`)

### ❌ "Phim đã xóa vẫn hiển thị ở trang khách"
**Nguyên nhân**: MovieController còn dùng `findAll()` thay vì `findAllActive()`  
**Giải pháp**: Kiểm tra code, đã được update trong phiên bản này

### ❌ "Không có nút 'Khôi phục'"
**Nguyên nhân**: Template chưa update  
**Giải pháp**: Kiểm tra `admin/movies/list.html` có dòng `th:href="@{'/admin/movies/restore/' + ${m.id}}"`

---

## 📁 Files Đã Thay Đổi

| File | Thay đổi |
|------|----------|
| **MovieRepository.java** | ➕ Thêm `findAllActive()`, `findByIdIfActive()`, `findAllIncludingDeleted()` |
| **MovieController.java** | 🔄 Thay `findAll()` → `findAllActive()` |
| **AdminMovieController.java** | 🔄 Delete: set `active=false` thay vì `deleteById()` + ➕ Thêm `restore()` endpoint |
| **admin/movies/list.html** | 🔄 Thêm style deleted, badge status, nút Khôi phục |

---

## ✨ Summary

Soft delete đã triển khai thành công!  
- ✅ Xóa phim an toàn (không lỗi FK)
- ✅ Admin quản lý được phim đã xóa
- ✅ Khách hàng chỉ thấy phim hoạt động
- ✅ Có thể khôi phục phim bất cứ lúc nào

**Test ngay trên localhost:8080! 🚀**

