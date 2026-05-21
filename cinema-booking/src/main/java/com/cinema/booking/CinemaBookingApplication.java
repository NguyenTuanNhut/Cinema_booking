package com.cinema.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ứng dụng Spring Boot chính cho hệ thống đặt vé rạp chiếu phim
 * - @SpringBootApplication: Đánh dấu class này là ứng dụng Spring Boot
 * - @EnableScheduling: Cho phép chạy các tác vụ định kỳ (scheduled tasks)
 */
@SpringBootApplication
@EnableScheduling
public class CinemaBookingApplication {

    /**
     * Điểm khởi động chương trình
     * Spring sẽ tự động khởi tạo các bean, cấu hình, kết nối DB,...
     */
    public static void main(String[] args) {
        SpringApplication.run(CinemaBookingApplication.class, args);
    }
}