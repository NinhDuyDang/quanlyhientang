package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.request.ProfileUpdateRequest;
import com.example.organdonationmanagement.dto.request.UserRequest;
import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.service.HospitalService;
import com.example.organdonationmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HospitalService hospitalService;

    // --- CÁC MÀN HÌNH QUẢN LÝ TÀI KHOẢN (ADMIN) ---

    @GetMapping("/account")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("activeMenu", "account");
        model.addAttribute("users", userService.getAll());
        return "account/list";
    }

    @GetMapping("/account/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        model.addAttribute("hospitals", hospitalService.findAll());
        model.addAttribute("activeMenu", "account");
        return "account/create";
    }

    @PostMapping("/account/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String processCreateUser(@ModelAttribute("userRequest") UserRequest request) {
        userService.create(request);
        return "redirect:/account";
    }

    @GetMapping("/account/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        model.addAttribute("hospitals", hospitalService.findAll());
        model.addAttribute("activeMenu", "account");
        return "account/edit";
    }

    @PostMapping("/account/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String processEditUser(@PathVariable("id") Long id, @ModelAttribute("userRequest") UserRequest request) {
        userService.update(id, request);
        return "redirect:/account";
    }

    @PostMapping("/account/toggle-status/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        userService.toggleStatus(id);
        return "redirect:/account";
    }

    @PostMapping("/account/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/account";
    }

    // --- MÀN HÌNH PROFILE (XEM THÔNG TIN) ---

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("activeMenu", "profile");
        return "profile/profile";
    }

    // --- MÀN HÌNH SETTINGS (CHỈNH SỬA) ---

    @GetMapping("/profile/settings")
    public String showSettings(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("activeMenu", "profile");
        return "profile/settings";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Principal principal,
                                @ModelAttribute ProfileUpdateRequest request,
                                RedirectAttributes redirectAttributes) {

        // Gọi Service xử lý cả dữ liệu và file upload
        userService.updateProfile(principal.getName(), request);

        // Flash attribute để thông báo hiện 1 lần rồi mất sau khi reload
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    // --- LOGIN ---

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
