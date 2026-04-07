package com.absensi.sistem_absensi.controller;

import com.absensi.sistem_absensi.model.Karyawan;
import com.absensi.sistem_absensi.service.KaryawanService; // Sesuaikan nama service kamu
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class KaryawanController {

    @Autowired
    private KaryawanService karyawanService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listKaryawan", karyawanService.getAllKaryawan());
        return "index";
    }

    @GetMapping("/showNewKaryawanForm")
    public String showNewKaryawanForm(Model model) {
        Karyawan karyawan = new Karyawan();
        model.addAttribute("karyawan", karyawan);
        return "new_karyawan";
    }

    @PostMapping("/saveKaryawan")
    public String saveKaryawan(@ModelAttribute("karyawan") Karyawan karyawan) {
        karyawanService.saveKaryawan(karyawan);
        return "redirect:/";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {
        Karyawan karyawan = karyawanService.getKaryawanById(id);
        model.addAttribute("karyawan", karyawan);
        return "update_karyawan";
    }

    @GetMapping("/deleteKaryawan/{id}")
    public String deleteKaryawan(@PathVariable(value = "id") long id) {
        this.karyawanService.deleteKaryawanById(id);
        return "redirect:/";
    }
}