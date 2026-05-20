package com.cinema.booking.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model, RedirectAttributes redirectAttributes) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi!";
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, RedirectAttributes redirectAttributes) {
        String errorMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.";
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/";
    }
}

