package com.absensi.sistem_absensi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Karyawan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
    private String jabatan;

    @OneToMany(mappedBy = "karyawan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances;

    // Field tambahan untuk logika UI (tidak masuk database)
    @Transient
    private String statusTombol;
}