package com.example.organdonationmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    // 1. MÀN HÌNH DANH SÁCH TÀI KHOẢN (USER)
    @GetMapping("/account")
    public String listUsers(Model model) {
        model.addAttribute("activeMenu", "account"); // Sidebar tự sáng nút Tài khoản
        model.addAttribute("username", "Admin Trung tâm");

        // FAKE DATA: Khớp 100% dữ liệu theo ảnh mẫu image_57bf7f.png
        List<Map<String, Object>> users = new ArrayList<>();
        users.add(createUserMap(1L, "admin_trungtam", "12345678", "ADMIN", "Trung tâm Điều phối", true, "10/06/2026 08:30"));
        users.add(createUserMap(2L, "bv_bachmai", "pass@bm123", "HOSPITAL", "Bệnh viện Bạch Mai", true, "11/06/2026 14:15"));
        users.add(createUserMap(3L, "bv_choray", "cr_coord99", "HOSPITAL", "Bệnh viện Chợ Rẫy", false, "12/06/2026 09:00")); // Trạng thái ẩn/khóa
        users.add(createUserMap(4L, "vietduc_admin", "vd_pass2026", "HOSPITAL", "Bệnh viện Việt Đức", true, "13/06/2026 16:45"));

        model.addAttribute("users", users);
        return "account/list";
    }

    // 2. MÀN HÌNH FORM TẠO MỚI TÀI KHOẢN
    @GetMapping("/account/create")
    public String showCreateForm(Model model) {
        model.addAttribute("activeMenu", "account");
        model.addAttribute("username", "Admin Trung tâm");
        return "account/create";
    }

    // 3. MÀN HÌNH FORM CHỈNH SỬA TÀI KHOẢN
    @GetMapping("/account/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activeMenu", "account");
        model.addAttribute("username", "Admin Trung tâm");

        // Giả lập tìm dữ liệu cũ đổ lên form sửa dựa vào ID nhận được
        Map<String, Object> userData = createUserMap(id, "bv_bachmai", "pass@bm123", "HOSPITAL", "Bệnh viện Bạch Mai", true, "11/06/2026 14:15");
        model.addAttribute("user", userData);

        return "account/edit";
    }

    // 4. API BẬT/TẮT ẨN HIỆN TÀI KHOẢN (TOGGLE STATUS ENABLED)
    @GetMapping("/account/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        // Comment: Sau này kết nối database thật gọi service đổi true/false tại đây
        System.out.println("LOG BACKEND: Đã đảo trạng thái ẩn/hiện của User ID: " + id);
        return "redirect:/account";
    }

    // 5. API XỬ LÝ LỆNH XÓA TÀI KHOẢN
    @GetMapping("/account/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        System.out.println("LOG BACKEND: Đã yêu cầu xóa User ID: " + id);
        return "redirect:/account";
    }

    // Hàm bổ trợ đóng gói nhanh dữ liệu Map
    private Map<String, Object> createUserMap(Long id, String username, String password, String role,
                                              String hospitalName, Boolean enabled, String createdAt) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("username", username);
        map.put("password", password);
        map.put("role", role);
        map.put("hospitalName", hospitalName);
        map.put("enabled", enabled);
        map.put("createdAt", createdAt);
        return map;
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Tìm đến file templates/login.html
    }
}
