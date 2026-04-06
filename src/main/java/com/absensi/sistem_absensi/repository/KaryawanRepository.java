package com.absensi.sistem_absensi.repository;

import com.absensi.sistem_absensi.model.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KaryawanRepository extends JpaRepository<Karyawan, Long> {
}