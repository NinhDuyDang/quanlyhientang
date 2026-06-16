package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.request.HospitalRequest;
import com.example.organdonationmanagement.entity.Hospital;
import com.example.organdonationmanagement.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // 1. TRANG DANH SÁCH BỆNH VIỆN
    @GetMapping
    public String showHospitalPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        model.addAttribute("activeMenu", "hospital");
        model.addAttribute("title", "Quản lý bệnh viện");

        Page<Hospital> hospitalPage = hospitalService.getAll(keyword, page, size);

        model.addAttribute("hospitals", hospitalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", hospitalPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "hospital/list";
    }

    // 2. HIỂN THỊ TRANG THÊM MỚI
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("activeMenu", "hospital");
        model.addAttribute("title", "Thêm bệnh viện mới");
        model.addAttribute("hospitalRequest", new HospitalRequest());
        return "hospital/create";
    }

    // 3. XỬ LÝ LƯU DỮ LIỆU THÊM MỚI (Có bắt lỗi trùng mã)
    @PostMapping("/create")
    public String createHospital(@ModelAttribute HospitalRequest request, Model model) {
        try {
            hospitalService.create(request);
            return "redirect:/hospital";
        } catch (Exception e) {
            // Khi xảy ra lỗi trùng mã hoặc bất kỳ lỗi logic nào từ Service
            model.addAttribute("activeMenu", "hospital");
            model.addAttribute("title", "Thêm bệnh viện mới");
            model.addAttribute("hospitalRequest", request); // Giữ lại toàn bộ dữ liệu user đã nhập trên Form
            model.addAttribute("errorMessage", e.getMessage()); // Gửi thông điệp lỗi: "Mã bệnh viện này đã tồn tại..."
            return "hospital/create"; // Trả lại trang tạo mới kèm thông báo lỗi
        }
    }

    // 4. HIỂN THỊ TRANG CHỈNH SỬA
    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "hospital");
        model.addAttribute("title", "Chỉnh sửa thông tin bệnh viện");

        Hospital hospital = hospitalService.getById(id);

        HospitalRequest hospitalRequest = HospitalRequest.builder()
                .code(hospital.getCode())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .province(hospital.getProvince())
                .phone(hospital.getPhone())
                .email(hospital.getEmail())
                .build();

        model.addAttribute("hospitalRequest", hospitalRequest);
        model.addAttribute("hospitalId", id);
        return "hospital/edit";
    }

    // 5. XỬ LÝ LƯU DỮ LIỆU CẬP NHẬT (Có bắt lỗi trùng lặp khi sửa)
    @PostMapping("/update/{id}")
    public String updateHospital(@PathVariable Long id, @ModelAttribute HospitalRequest request, Model model) {
        try {
            hospitalService.update(id, request);
            return "redirect:/hospital";
        } catch (Exception e) {
            model.addAttribute("activeMenu", "hospital");
            model.addAttribute("title", "Chỉnh sửa thông tin bệnh viện");
            model.addAttribute("hospitalRequest", request); // Giữ lại dữ liệu đang sửa đổi lỗi
            model.addAttribute("hospitalId", id);
            model.addAttribute("errorMessage", e.getMessage()); // Gửi thông điệp lỗi xuống giao diện
            return "hospital/edit"; // Trả lại trang sửa kèm thông báo lỗi
        }
    }

    // 6. XỬ LÝ XÓA BỆNH VIỆN
    @GetMapping("/delete/{id}")
    public String deleteHospital(@PathVariable Long id) {
        hospitalService.delete(id);
        return "redirect:/hospital";
    }
}
