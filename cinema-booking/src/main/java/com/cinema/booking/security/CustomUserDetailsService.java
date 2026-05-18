package com.cinema.booking.security;

import com.cinema.booking.entity.User;
import com.cinema.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // Đánh dấu class này là Spring Service (Spring sẽ tự tạo bean)
@Primary // Nếu có nhiều UserDetailsService thì ưu tiên dùng class này
@RequiredArgsConstructor // Lombok tự tạo constructor cho field final (dependency injection)
public class CustomUserDetailsService implements UserDetailsService {

    // Inject repository để truy vấn database
    private final UserRepository userRepository;

    /**
     * Method này Spring Security sẽ tự gọi khi user đăng nhập
     * Ví dụ: user nhập email + password → Spring gọi method này để tìm user
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Tìm user trong database theo email
        User user = userRepository.findByEmail(email)
                // Nếu không tìm thấy user thì ném lỗi đăng nhập thất bại
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        /**
         * Trả về UserDetails cho Spring Security
         * Spring cần:
         * - username (ở đây dùng email)
         * - password (đã mã hóa trong DB)
         * - authorities (quyền / role)
         */
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // username (Spring dùng email làm username)
                user.getPassword(), // password đã encode (BCrypt)
                // chuyển role sang dạng Spring Security hiểu được
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                user.getRole().getName() // ví dụ: ROLE_ADMIN
                        )
                )
        );
    }
}