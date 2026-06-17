package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.request.PatientCaseRequest;
import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.service.PatientCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientCaseController {

    private final PatientCaseService patientCaseService;
    private final HospitalRepository hospitalRepository;

    @GetMapping
    public String listPatients(@RequestParam(required = false) Long hospitalId,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                               Model model) {
        model.addAttribute("activeMenu", "patient");
        model.addAttribute("patients", patientCaseService.search(hospitalId, status, fromDate, toDate));
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "patient/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("activeMenu", "patient");
        model.addAttribute("hospitals", hospitalRepository.findAll());
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
                            @ModelAttribute("patient") PatientCaseRequest request) {
    // Thêm log để kiểm tra trước khi lưu
    System.out.println("Status nhận được từ form: " + request.getStatus());
    
    if (request.getStatus() == null) {
   
        throw new IllegalArgumentException("Trạng thái không được để trống!");
    }
    
    patientCaseService.update(id, request);
    return "redirect:/patient";
}

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") Long id) {
        patientCaseService.delete(id);
        return "redirect:/patient";
    }
}