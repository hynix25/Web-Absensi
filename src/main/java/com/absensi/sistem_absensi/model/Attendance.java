package com.absensi.sistem_absensi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "karyawan_id")
    private Karyawan karyawan;

    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private String status;
    private String catatan;
    private String durasiKerja;

    @Column(columnDefinition = "TEXT")
    private String alasan;

    // WAJIB: Getter & Setter harus lengkap untuk semua field
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Karyawan getKaryawan() { return karyawan; }
    public void setKaryawan(Karyawan karyawan) { this.karyawan = karyawan; }
    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public void setWaktuMasuk(LocalDateTime waktuMasuk) { this.waktuMasuk = waktuMasuk; }
    public LocalDateTime getWaktuKeluar() { return waktuKeluar; }
    public void setWaktuKeluar(LocalDateTime waktuKeluar) { this.waktuKeluar = waktuKeluar; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    public String getDurasiKerja() { return durasiKerja; }
    public void setDurasiKerja(String durasiKerja) { this.durasiKerja = durasiKerja; }
    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }
}