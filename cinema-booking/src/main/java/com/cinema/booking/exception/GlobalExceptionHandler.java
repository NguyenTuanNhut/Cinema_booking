package com.cinema.booking.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Xử lý lỗi (Exception Handler) toàn cục
 * @ControllerAdvice: Áp dụng cho tất cả controllers
 *
 * Khi có lỗi xảy ra, sẽ báo lỗi cho người dùng qua flash message
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý IOException (lỗi runtime)
     * Chạy khi có RuntimeException được ném ra từ code
     * @param ex Exception được ném
     * @param model Model để truyền dữ liệu đến view
     * @param redirectAttributes Flash message khi redirect
     * @return Redirect về trang chủ với thông báo lỗi
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model, RedirectAttributes redirectAttributes) {
        // Lấy message từ exception, nếu null thì dùng message mặc định
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi!";
        // Thêm thông báo lỗi vào flash attribute
        redirectAttributes.addFlashAttribute("error", errorMessage);
        // Redirect về trang chủ
        return "redirect:/";
    }

    /**
     * Xử lý Exception chung (lưới an toàn cuối cùng)
     * Chạy khi có Exception bất kỳ được ném ra từ code
     * @param ex Exception được ném
     * @param model Model để truyền dữ liệu đến view
     * @param redirectAttributes Flash message khi redirect
     * @return Redirect về trang chủ với thông báo lỗi hệ thống
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, RedirectAttributes redirectAttributes) {
        // Thông báo lỗi chung (không tiết lộ thông tin chi tiết lỗi)
        String errorMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.";
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/";
    }
}

