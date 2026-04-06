package com.absensi.sistem_absensi.controller;

import com.absensi.sistem_absensi.model.Attendance;
import com.absensi.sistem_absensi.model.Karyawan;
import com.absensi.sistem_absensi.repository.AttendanceRepository;
import com.absensi.sistem_absensi.repository.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class KaryawanController {

    @Autowired private KaryawanRepository karyawanRepository;
    @Autowired private AttendanceRepository attendanceRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/karyawan")
    public String daftarKaryawan(Model model) {
        try {
            List<Karyawan> listKaryawan = karyawanRepository.findAll();
            int tepat = 0, telat = 0, izinSakit = 0, belum = 0;

            for (Karyawan k : listKaryawan) {
                LocalDateTime start = LocalDate.now().atStartOfDay();
                LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
                Optional<Attendance> o = attendanceRepository.findFirstByKaryawanAndWaktuMasukBetween(k, start, end);

                if (o.isPresent()) {
                    Attendance a = o.get();
                    if ("SAKIT".equals(a.getStatus()) || "IZIN".equals(a.getStatus())) {
                        izinSakit++;
                        k.setStatusTombol("SUDAH_PULANG");
                    } else {
                        if ("Terlambat".equals(a.getCatatan())) telat++; else tepat++;
                        k.setStatusTombol(a.getWaktuKeluar() == null ? "SUDAH_MASUK" : "SUDAH_PULANG");
                    }
                } else {
                    belum++;
                    k.setStatusTombol("BELUM_MASUK");
                }
            }
            model.addAttribute("listKaryawan", listKaryawan);
            model.addAttribute("tepatWaktu", tepat);
            model.addAttribute("terlambat", telat);
            model.addAttribute("izinSakit", izinSakit);
            model.addAttribute("belumAbsen", belum);
            return "halaman_karyawan";
        } catch (Exception e) {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/rekap")
    public String rekap(@RequestParam(required = false) String tanggal, Model model) {
        List<Attendance> data;
        // Logika Reset: Jika parameter kosong/null, tarik semua data
        if (tanggal == null || tanggal.trim().isEmpty() || "null".equals(tanggal)) {
            data = attendanceRepository.findAll();
        } else {
            try {
                LocalDate d = LocalDate.parse(tanggal);
                data = attendanceRepository.findByWaktuMasukBetween(d.atStartOfDay(), d.atTime(LocalTime.MAX));
            } catch (Exception e) {
                data = attendanceRepository.findAll();
            }
        }
        model.addAttribute("listAbsen", data != null ? data : new ArrayList<>());
        return "halaman_rekap";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        String username = principal.getName();

        // Cek Role dari Security Context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Optional<Karyawan> karyawanOpt = karyawanRepository.findAll().stream()
                .filter(k -> k.getNama().equalsIgnoreCase(username))
                .findFirst();

        if (karyawanOpt.isPresent()) {
            Karyawan k = karyawanOpt.get();
            model.addAttribute("user", k);
            // Bedakan ID dan Role Label
            model.addAttribute("userRole", isAdmin ? "ADMINISTRATOR" : "KARYAWAN");
            model.addAttribute("displayId", isAdmin ? "ADM-" + k.getId() : "EMP-" + String.format("%03d", k.getId()));

            List<Attendance> riwayat = attendanceRepository.findAll().stream()
                    .filter(a -> a.getKaryawan() != null && a.getKaryawan().getId().equals(k.getId()))
                    .sorted(Comparator.comparing(Attendance::getWaktuMasuk).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
            model.addAttribute("riwayat", riwayat);
        } else {
            // Default jika user murni Admin (tidak terdaftar di tabel Karyawan)
            Karyawan adminPseudo = new Karyawan();
            adminPseudo.setNama(username);
            adminPseudo.setJabatan("System Administrator");
            model.addAttribute("user", adminPseudo);
            model.addAttribute("userRole", "SUPER ADMIN");
            model.addAttribute("displayId", "ROOT-001");
            model.addAttribute("riwayat", new ArrayList<>());
        }
        return "halaman_profile";
    }

    @PostMapping("/absen/{id}")
    @Transactional
    public String absen(@PathVariable Long id) {
        karyawanRepository.findById(id).ifPresent(k -> {
            Attendance a = new Attendance();
            a.setKaryawan(k);
            a.setWaktuMasuk(LocalDateTime.now());
            a.setStatus("HADIR");
            a.setCatatan(LocalTime.now().isAfter(LocalTime.of(8, 0)) ? "Terlambat" : "Tepat Waktu");
            attendanceRepository.save(a);
        });
        return "redirect:/karyawan";
    }

    @PostMapping("/pulang/{id}")
    @Transactional
    public String pulang(@PathVariable Long id) {
        karyawanRepository.findById(id).ifPresent(k -> {
            attendanceRepository.findFirstByKaryawanAndWaktuKeluarIsNullOrderByWaktuMasukDesc(k).ifPresent(a -> {
                a.setWaktuKeluar(LocalDateTime.now());
                Duration d = Duration.between(a.getWaktuMasuk(), a.getWaktuKeluar());
                a.setDurasiKerja(d.toHours() + "j " + d.toMinutesPart() + "m");
                attendanceRepository.save(a);
            });
        });
        return "redirect:/karyawan";
    }

    @PostMapping("/izin/{id}")
    @Transactional
    public String izin(@PathVariable Long id, @RequestParam String tipe, @RequestParam String alasan) {
        karyawanRepository.findById(id).ifPresent(k -> {
            Attendance a = new Attendance();
            a.setKaryawan(k);
            a.setWaktuMasuk(LocalDateTime.now());
            a.setStatus(tipe);
            a.setAlasan(alasan);
            a.setCatatan("Izin: " + tipe);
            attendanceRepository.save(a);
        });
        return "redirect:/karyawan";
    }

    @PostMapping("/tambah-karyawan")
    public String tambah(@RequestParam String nama, @RequestParam String jabatan) {
        Karyawan k = new Karyawan();
        k.setNama(nama);
        k.setJabatan(jabatan);
        karyawanRepository.save(k);
        return "redirect:/karyawan";
    }

    @GetMapping("/hapus/{id}")
    @Transactional
    public String hapus(@PathVariable Long id) {
        attendanceRepository.findAll().stream()
                .filter(a -> a.getKaryawan() != null && a.getKaryawan().getId().equals(id))
                .forEach(a -> attendanceRepository.delete(a));
        karyawanRepository.deleteById(id);
        return "redirect:/karyawan";
    }
}