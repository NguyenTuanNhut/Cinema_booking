package com.cinema.booking.config;

import com.cinema.booking.entity.*;
import com.cinema.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Lớp khởi tạo dữ liệu ban đầu khi ứng dụng start
 * Tạo tự động các vai trò, thể loại, phòng, ghế, người dùng mặc định
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // Các repository để tương tác với database
    private final RoleRepository roleRepository;              // Quản lý vai trò (Admin, Staff, Customer)
    private final GenreRepository genreRepository;            // Quản lý thể loại phim
    private final RoomRepository roomRepository;              // Quản lý phòng chiếu
    private final UserRepository userRepository;              // Quản lý người dùng
    private final MovieRepository movieRepository;            // Quản lý phim
    private final PasswordEncoder passwordEncoder;            // Mã hóa mật khẩu
    private final ProfileRepository profileRepository;        // Quản lý hồ sơ người dùng
    private final SeatRepository seatRepository;              // Quản lý ghế ngồi
    private final ShowtimeRepository showtimeRepository;      // Quản lý suất chiếu

    /**
     * Phương thức chạy tự động khi ứng dụng khởi động
     * Thứ tự: tạo vai trò → thể loại → phòng/ghế → tài khoản → phim và suất chiếu
     */
    @Override
    public void run(String... args) throws Exception {
        seedRoles();                                          // 1. Tạo vai trò (Admin, Staff, Customer)
        seedGenres();                                         // 2. Tạo thể loại phim
        seedRoomsAndSeats();                                  // 3. Tạo 2 phòng chiếu với 20 ghế mỗi phòng
        forceCreateAdmin("admin@gmail.com", "admin123");      // 4. Tạo tài khoản admin thứ nhất
        forceCreateAdmin("admin2@gmail.com", "admin123");     // 5. Tạo tài khoản admin thứ hai
        forceCreateStaff("staff@gmail.com", "staff123");      // 6. Tạo tài khoản nhân viên
        seedMoviesAndShowtimes();                             // 7. Tạo phim và suất chiếu mẫu
    }

    /**
     * Tạo các vai trò mặc định trong hệ thống
     * - ROLE_ADMIN: Quản trị viên (quản lý toàn bộ hệ thống)
     * - ROLE_STAFF: Nhân viên (quản lý đặt vé, suất chiếu)
     * - ROLE_CUSTOMER: Khách hàng (đặt vé, xem lịch sử)
     */
    private void seedRoles() {
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER");
        // Lặp qua từng vai trò
        for (String name : roleNames) {
            // Kiểm tra vai trò chưa tồn tại thì thêm mới
            if (!roleRepository.findByName(name).isPresent()) {
                Role role = new Role();
                role.setName(name);
                roleRepository.save(role);  // Lưu vào database
            }
        }
    }

    /**
     * Tạo các thể loại phim mặc định
     * Chỉ chạy nếu database chưa có thể loại nào
     */
    private void seedGenres() {
        // Kiểm tra xem database có thể loại nào chưa
        if (genreRepository.count() == 0) {
            // Danh sách thể loại phim
            List<String> genres = Arrays.asList("Action", "Horror", "Comedy", "Drama", "Sci-Fi");
            // Tạo từng thể loại và lưu vào database
            genres.forEach(name -> {
                Genre genre = new Genre();
                genre.setName(name);
                genreRepository.save(genre);
            });
        }
    }

    /**
     * Tạo các phòng chiếu và ghế ngồi mặc định
     * - Tạo 2 phòng chiếu
     * - Mỗi phòng có 20 ghế (chia thành hàng A: 10 ghế, hàng B: 10 ghế)
     */
    private void seedRoomsAndSeats() {
        // Chỉ tạo nếu database chưa có phòng nào
        if (roomRepository.count() == 0) {
            // Tạo 2 phòng chiếu
            for (int i = 1; i <= 2; i++) {
                Room room = new Room();
                room.setName("Room " + i);           // Tên phòng: "Room 1", "Room 2"
                room.setTotalSeats(20);              // Tổng số ghế: 20 ghế
                Room savedRoom = roomRepository.save(room);  // Lưu phòng vào database

                // Tạo 20 ghế cho mỗi phòng
                for (int j = 1; j <= 20; j++) {
                    Seat seat = new Seat();
                    // Quyết định hàng ghế: A1-A10 (10 ghế đầu), B1-B10 (10 ghế sau)
                    seat.setSeatNumber((j <= 10 ? "A" : "B") + (j % 10 == 0 ? 10 : j % 10));
                    seat.setRoom(savedRoom);         // Gán ghế cho phòng
                    seatRepository.save(seat);       // Lưu ghế vào database
                }
            }
        }
    }

    /**
     * Tạo hoặc cập nhật tài khoản admin
     * Nếu admin đã tồn tại, chỉ cập nhật mật khẩu
     * Nếu chưa tồn tại, tạo mới với email là tên người dùng
     */
    private void forceCreateAdmin(String email, String password) {
        // Lấy vai trò ADMIN từ database
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User admin;
        // Kiểm tra admin đã tồn tại chưa
        if (existingUser.isPresent()) {
            // Nếu tồn tại, lấy ra để cập nhật
            admin = existingUser.get();
        } else {
            // Nếu chưa tồn tại, tạo mới
            admin = new User();
            admin.setUsername(email.split("@")[0]);  // Lấy phần trước @ làm tên người dùng
            admin.setEmail(email);
        }
        
        // Cập nhật mật khẩu (mã hóa trước khi lưu)
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(adminRole);                    // Gán vai trò admin
        User savedAdmin = userRepository.save(admin);  // Lưu vào database

        // Tạo hồ sơ người dùng nếu chưa tồn tại
        if (!profileRepository.findByUser(savedAdmin).isPresent()) {
            Profile profile = new Profile();
            profile.setUser(savedAdmin);
            profile.setFullName("Administrator " + admin.getUsername());
            profileRepository.save(profile);
        }
        System.out.println(">>> Force Update Admin: " + email + " / " + password);
    }

    /**
     * Tạo hoặc cập nhật tài khoản nhân viên
     * Tương tự forceCreateAdmin, nhưng với vai trò STAFF
     */
    private void forceCreateStaff(String email, String password) {
        // Lấy vai trò STAFF từ database
        Role staffRole = roleRepository.findByName("ROLE_STAFF").orElseThrow();
        Optional<User> existingUser = userRepository.findByEmail(email);

        User staff;
        // Kiểm tra nhân viên đã tồn tại chưa
        if (existingUser.isPresent()) {
            staff = existingUser.get();
        } else {
            staff = new User();
            staff.setUsername(email.split("@")[0]);
            staff.setEmail(email);
        }

        // Cập nhật mật khẩu và vai trò
        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(staffRole);
        User savedStaff = userRepository.save(staff);

        // Tạo hồ sơ nếu chưa tồn tại
        if (!profileRepository.findByUser(savedStaff).isPresent()) {
            Profile profile = new Profile();
            profile.setUser(savedStaff);
            profile.setFullName("Staff " + staff.getUsername());
            profileRepository.save(profile);
        }
        System.out.println(">>> Force Create Staff: " + email + " / " + password);
    }

    /**
     * Tạo phim mẫu và suất chiếu
     * Chỉ chạy nếu database chưa có phim nào
     */
    private void seedMoviesAndShowtimes() {
        // Chỉ tạo nếu database chưa có phim
        if (movieRepository.count() == 0) {
            Genre action = genreRepository.findAll().get(0);  // Lấy thể loại đầu tiên (Action)
            Room room1 = roomRepository.findAll().get(0);     // Lấy phòng đầu tiên

            // Tạo phim mẫu
            Movie m1 = new Movie();
            m1.setTitle("Avenger: Endgame");
            m1.setDescription("Phim siêu anh hùng Marvel đình đám.");
            m1.setDuration(180);                              // Thời lượng: 180 phút
            m1.setGenre(action);                              // Thể loại: Action
            m1.setReleaseDate(LocalDate.now());               // Ngày phát hành: hôm nay
            m1.setPoster("https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_.jpg");
            Movie savedMovie = movieRepository.save(m1);      // Lưu phim vào database

            // Tạo suất chiếu cho phim
            Showtime st = new Showtime();
            st.setMovie(savedMovie);                          // Liên kết phim
            st.setRoom(room1);                                // Liên kết phòng
            st.setStartTime(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0));  // Ngày mai 19:00
            st.setEndTime(st.getStartTime().plusMinutes(180 + 15));  // Kết thúc sau 195 phút (film + quảng cáo)
            showtimeRepository.save(st);                      // Lưu suất chiếu vào database
        }
    }
}
