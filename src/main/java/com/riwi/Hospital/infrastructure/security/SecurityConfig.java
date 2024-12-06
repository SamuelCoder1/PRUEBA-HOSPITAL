package com.riwi.Hospital.infrastructure.security;

import com.riwi.Hospital.domain.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@AllArgsConstructor
@Configuration
public class SecurityConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:8000");
        corsConfiguration.addAllowedOrigin("http://localhost:8000");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }


    private final JwtFilter jwtFilter; // Filtro para manejar la autenticación JWT

    private final AuthenticationProvider authenticationProvider; // Proveedor de autenticación para manejar las credenciales

    // Definición de los endpoints públicos
    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/register",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/audit/logs"
    };

    // Definición de los endpoints que solo pueden ser accedidos por ADMINISTRADOR
    private final String[] ADMIN_ENDPOINTS = {
            "/patients/",
            "/patients/create",
            "/patients/delete/**",
            "/patients/readByDocument/**",
            "/patients/updatePatientByDocument/**",
            "/doctors/",
            "/doctors/create",
            "/doctors/update/**",
            "/doctors/delete/**",
            "/doctors/readById/**",
            "/appointments/",
            "/appointments/create",
            "/appointments/delete/**",
            "/appointments/readById/**",
            "/appointments/update/**",
            "/appointments/update/status/**",
            "/appointments/readAppointmentsByDoctor/**",
            "/appointments/getAppointmentsByPatient/**",
            "/medical-history/patient/**",
            "/medical-history/add",
            "/appointments/available-slots/**"
    };

    // Definición de los endpoints que solo pueden ser accedidos por DOCTOR
    private final String[] DOCTOR_ENDPOINTS = {
            "/doctors/",
            "/doctors/create",
            "/doctors/update/**",
            "/doctors/delete/**",
            "/doctors/readById/**",
            "/appointments/create",
            "/appointments/delete/**",
            "/appointments/readById/**",
            "/appointments/update/**",
            "/appointments/update/status/**",
            "/appointments/readAppointmentsByDoctor/**",
            "/medical-history/patient/**",
            "/medical-history/add",
            "/appointments/available-slots/**"
    };

    // Definición de los endpoints que solo pueden ser accedidos por PATIENT
    private final String[] PATIENT_ENDPOINTS = {
            "/patients/readByDocument/**",
            "/appointments/getAppointmentsByPatient/**",
            "/appointments/available-slots/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                // Desactiva CSRF ya que se usará JWT
                .addFilterBefore(corsFilter(), CorsFilter.class)
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers(ADMIN_ENDPOINTS).permitAll() // Permite acceso sin restricciones a los ADMIN
                        .requestMatchers(PATIENT_ENDPOINTS).hasRole(Role.PATIENT.name()) // Permite solo a PATIENT acceder a los endpoints de PATIENT
                        .requestMatchers(DOCTOR_ENDPOINTS).hasRole(Role.DOCTOR.name()) // Permite solo a DOCTOR acceder a los endpoints de DOCTOR
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Permite acceso a todos a los endpoints públicos
                        .anyRequest().authenticated()) // Cualquier otra solicitud requiere autenticación
                .authenticationProvider(authenticationProvider) // Establece el proveedor de autenticación
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la sesión para que sea sin estado
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Agrega el filtro JWT antes del filtro de autenticación por nombre de usuario y contraseña
                .build(); // Construye la cadena de seguridad
    }
}
