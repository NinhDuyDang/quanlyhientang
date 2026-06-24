package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.request.PatientCaseRequest;
import com.example.organdonationmanagement.entity.PatientCase;
import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.entity.enums.Role;
import com.example.organdonationmanagement.exception.ResourceNotFoundException;
import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.service.PatientCaseService;
import com.example.organdonationmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientCaseController {

    private final PatientCaseService patientCaseService;
    private final HospitalRepository hospitalRepository;
    @Autowired
    private UserService userService;
    @GetMapping
    public String listPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {

        Page<PatientCase> patientPage = patientCaseService.findPatients(hospitalId, status, fromDate, toDate, page - 1, 10);

        model.addAttribute("activeMenu", "patient");
        model.addAttribute("patients", patientPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientPage.getTotalPages());

        model.addAttribute("hospitalId", hospitalId);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("hospitals", hospitalRepository.findAll());

        return "patient/list";
    }
@GetMapping("/create")
public String showCreateForm(Model model, Principal principal) {
    User currentUser = userService.findByUsername(principal.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    model.addAttribute("activeMenu", "patient");
    model.addAttribute("currentUser", currentUser);

    if (currentUser.getRole() == Role.ADMIN) {
        model.addAttribute("hospitals", hospitalRepository.findAll());
    }
    return "patient/create";
}

    @PostMapping("/create")
    public String createPatient(@ModelAttribute PatientCaseRequest request) {
        patientCaseService.create(request);
        return "redirect:/patient";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activeMenu", "patient");
        model.addAttribute("patient", patientCaseService.getById(id));
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "patient/edit";
    }

@PostMapping("/edit/{id}")
public String updatePatient(@PathVariable("id") Long id,
                            @ModelAttribute("patient") PatientCaseRequest request,
                            Principal principal) {
    User currentUser = userService.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user với username: " + principal.getName()));
    patientCaseService.update(id, request, currentUser);

    return "redirect:/patient";
}

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") Long id) {
        patientCaseService.delete(id);
        return "redirect:/patient";
    }
}
