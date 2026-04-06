package com.absensi.sistem_absensi.repository;

import com.absensi.sistem_absensi.model.Attendance;
import com.absensi.sistem_absensi.model.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findFirstByKaryawanAndWaktuMasukBetween(Karyawan karyawan, LocalDateTime start, LocalDateTime end);
    Optional<Attendance> findFirstByKaryawanAndWaktuKeluarIsNullOrderByWaktuMasukDesc(Karyawan karyawan);

    // Method untuk filter rekap
    List<Attendance> findByWaktuMasukBetween(LocalDateTime start, LocalDateTime end);
}