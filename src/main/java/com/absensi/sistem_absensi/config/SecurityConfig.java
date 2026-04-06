package com.absensi.sistem_absensi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Mengambil nilai dari Environment Variable di Railway
    // Jika tidak ada di Railway, pakai default setelah tanda ':'
    @Value("${APP_ADMIN_USER:admin}")
    private String adminUsername;

    @Value("${APP_ADMIN_PASS:admin123}")
    private String adminPassword;

    @Value("${APP_USER_USER:karyawan}")
    private String userUsername;

    @Value("${APP_USER_PASS:user123}")
    private String userPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll() // Izinkan akses login & asset
                        .requestMatchers("/rekap", "/tambah-karyawan", "/hapus/**").hasRole("ADMIN") // Khusus Admin
                        .anyRequest().authenticated() // Sisanya harus login
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/karyawan", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // Nonaktifkan CSRF untuk mempermudah testing awal

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // User Admin
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(adminUsername)
                .password(adminPassword)
                .roles("ADMIN")
                .build();

        // User Karyawan Biasa
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(userUsername)
                .password(userPassword)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}