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
public class PatientCaseController {

    // 1. TRANG DANH SÁCH CA BỆNH
    @GetMapping("/patient")
    public String listPatients(Model model) {
        model.addAttribute("activeMenu", "patient"); // Để sidebar sáng xanh nút Ca bệnh
        model.addAttribute("username", "Admin Trung tâm");

        // FAKE DATA: Khớp dữ liệu theo ảnh mẫu list ca bệnh
        List<Map<String, Object>> patients = new ArrayList<>();
        patients.add(createPatientMap("CA2024060001", "Bệnh viện Bạch Mai", 45, "Nam", "Tai nạn", "Nguy cơ chết não", "Có", "10/06/2024"));
        patients.add(createPatientMap("CA2024060002", "Bệnh viện Chợ Rẫy", 60, "Nữ", "Đột quỵ", "Xác nhận chết não", "Có", "09/06/2024"));
        patients.add(createPatientMap("CA2024060003", "Bệnh viện Việt Đức", 32, "Nam", "Tai nạn", "Không đủ điều kiện", "Không", "08/06/2024"));
        patients.add(createPatientMap("CA2024060004", "Bệnh viện Bạch Mai", 70, "Nữ", "Đột quỵ", "Xác nhận chết não", "Có", "07/06/2024"));

        model.addAttribute("patients", patients);
        return "patient/list";
    }

    // 2. FORM THÊM MỚI CA BỆNH
    @GetMapping("/patient/create")
    public String showCreateForm(Model model) {
        model.addAttribute("activeMenu", "patient");
        model.addAttribute("username", "Admin Trung tâm");
        return "patient/create";
    }

    // 3. FORM CHỈNH SỬA CA BỆNH
    @GetMapping("/patient/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        model.addAttribute("activeMenu", "patient");
        model.addAttribute("username", "Admin Trung tâm");

        // Giả lập lấy dữ liệu cũ đổ lên form sửa dựa theo mã ca bệnh gửi lên
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("maCaBenh", id);
        patientData.put("benhVien", "Bệnh viện Bạch Mai");
        patientData.put("tuoi", 45);
        patientData.put("gioiTinh", "Nam");
        patientData.put("nguyenNhan", "Tai nạn");
        patientData.put("trangThai", "Nguy cơ chết não");
        patientData.put("khaNangHien", "Có khả năng hiến");
        patientData.put("ghiChu", "Bệnh nhân được theo dõi sát sao.");

        model.addAttribute("patient", patientData);
        return "patient/edit";
    }

    // 4. API XỬ LÝ LỆNH XÓA CA BỆNH
    @GetMapping("/patient/delete/{id}")
    public String deletePatient(@PathVariable("id") String id) {
        // Comment: Sau này kết nối database thật gọi service tại đây
        // patientService.deleteById(id);
        System.out.println("Đã yêu cầu xóa ca bệnh có mã: " + id);
        return "redirect:/patient";
    }

    // ==========================================================================
    // HÀM BỔ TRỢ: Tạo nhanh Map dữ liệu ca bệnh (ĐÃ BỔ SUNG ĐỂ SỬA LỖI)
    // ==========================================================================
    private Map<String, Object> createPatientMap(String ma, String bv, int tuoi, String gioiTinh,
                                                 String nguyenNhan, String trangThai, String khaNang, String ngay) {
        Map<String, Object> map = new HashMap<>();
        map.put("maCaBenh", ma);
        map.put("benhVien", bv);
        map.put("tuoi", tuoi);
        map.put("gioiTinh", gioiTinh);
        map.put("nguyenNhan", nguyenNhan);
        map.put("trangThai", trangThai);
        map.put("khaNangHien", khaNang);
        map.put("ngayNhap", ngay);
        return map;
    }
}
