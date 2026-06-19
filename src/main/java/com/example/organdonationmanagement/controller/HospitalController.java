package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.request.HospitalRequest;
import com.example.organdonationmanagement.entity.Hospital;
import com.example.organdonationmanagement.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;


    @GetMapping
    public String showHospitalPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
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


    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("activeMenu", "hospital");
        model.addAttribute("title", "Thêm bệnh viện mới");
        model.addAttribute("hospitalRequest", new HospitalRequest());
        return "hospital/create";
    }


    @PostMapping("/create")
    public String createHospital(@ModelAttribute HospitalRequest request, Model model) {
        try {
            hospitalService.create(request);
            return "redirect:/hospital";
        } catch (Exception e) {

            model.addAttribute("activeMenu", "hospital");
            model.addAttribute("title", "Thêm bệnh viện mới");
            model.addAttribute("hospitalRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "hospital/create";
        }
    }

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

    @PostMapping("/update/{id}")
    public String updateHospital(@PathVariable Long id, @ModelAttribute HospitalRequest request, Model model) {
        try {
            hospitalService.update(id, request);
            return "redirect:/hospital";
        } catch (Exception e) {
            model.addAttribute("activeMenu", "hospital");
            model.addAttribute("title", "Chỉnh sửa thông tin bệnh viện");
            model.addAttribute("hospitalRequest", request);
            model.addAttribute("hospitalId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "hospital/edit";
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_STAFF')")
    public String deleteHospital(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {

            hospitalService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Xóa bệnh viện thành công!"
            );

        } catch (DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Không thể xóa bệnh viện vì đang có dữ liệu liên kết."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Có lỗi xảy ra khi xóa bệnh viện."
            );
        }

        return "redirect:/hospital";
    }
}
