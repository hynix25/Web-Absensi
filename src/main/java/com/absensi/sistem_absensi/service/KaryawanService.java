package com.absensi.sistem_absensi.service;

import com.absensi.sistem_absensi.model.Karyawan;
import java.util.List;

public interface KaryawanService {
    List<Karyawan> getAllKaryawan();
    void saveKaryawan(Karyawan karyawan);
    Karyawan getKaryawanById(long id);
    void deleteKaryawanById(long id); // Pastikan pakai 'I' besar di 'ById'
}