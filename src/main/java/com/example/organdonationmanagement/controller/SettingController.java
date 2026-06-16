package com.example.organdonationmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
public class SettingController {

    public static class CurrentUser {
        public static String name = "Admin Trung tâm";
        public static int age = 35;
        public static String phone = "024 3869 3731";
        public static String address = "Giải Phóng, Phương Mai, Đống Đa, Hà Nội";
        public static String avatarData = "https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=150";
    }

    // 1. ĐIỀU HƯỚNG ĐẾN TRANG CÀI ĐẶT
    @GetMapping("/setting")
    public String showSettingPage(Model model) {
        model.addAttribute("activeMenu", "setting");
        model.addAttribute("username", CurrentUser.name);

        model.addAttribute("name", CurrentUser.name);
        model.addAttribute("age", CurrentUser.age);
        model.addAttribute("phone", CurrentUser.phone);
        model.addAttribute("address", CurrentUser.address);
        model.addAttribute("avatarUrl", CurrentUser.avatarData);

        return "setting/setting";
    }

    // 2. XỬ LÝ LƯU CẬP NHẬT TỪ FORM CÀI ĐẶT
    @PostMapping("/setting/update")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("avatarFile") MultipartFile avatarFile) throws IOException {

        CurrentUser.name = name;
        CurrentUser.age = age;
        CurrentUser.phone = phone;
        CurrentUser.address = address;

        if (avatarFile != null && !avatarFile.isEmpty()) {
            byte[] bytes = avatarFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            CurrentUser.avatarData = "data:" + avatarFile.getContentType() + ";base64," + base64Image;
        }

        return "redirect:/profile";
    }

    // 3. ĐIỀU HƯỚNG ĐẾN TRANG HỒ SƠ (Đã cập nhật đường dẫn vào gói thư mục con)
    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        model.addAttribute("activeMenu", "none");
        model.addAttribute("username", CurrentUser.name);

        model.addAttribute("name", CurrentUser.name);
        model.addAttribute("age", CurrentUser.age);
        model.addAttribute("phone", CurrentUser.phone);
        model.addAttribute("address", CurrentUser.address);
        model.addAttribute("avatarUrl", CurrentUser.avatarData);

        return "profile/profile"; // <--- ĐÃ SỬA CHUẨN: Tìm đến templates/profile/profile.html
    }
}
