package com.plantrack.config;

import com.plantrack.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /*
     * Codificador de contraseñas.
     * Se utiliza para comparar la contraseña ingresada en el login
     * con el hash almacenado en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Proveedor de autenticación.
     * Indica a Spring Security cómo cargar usuarios desde la base de datos
     * y cómo verificar sus contraseñas.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /*
     * Configuración principal de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Rutas públicas.
                         * No requieren iniciar sesión.
                         */
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/webjars/**"
                        ).permitAll()

                        /*
                         * Dashboard.
                         * Cualquier usuario autenticado puede entrar.
                         */
                        .requestMatchers("/dashboard").authenticated()

                        /*
                         * Gestión de usuarios.
                         * Solo ADMIN.
                         */
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        /*
                         * Gestión de estructura industrial.
                         * ADMIN y DESIGNER pueden administrar estos datos.
                         * OPERATOR no debe modificar estructura.
                         */
                        .requestMatchers("/sectors/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers("/equipments/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers("/parts/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers("/positions/**").hasAnyRole("ADMIN", "DESIGNER")

                        /*
                         * Gestión de planos.
                         * Crear, editar y eliminar solo ADMIN o DESIGNER.
                         */
                        .requestMatchers("/flats/new").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers("/flats/edit/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers("/flats/delete/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers(HttpMethod.POST, "/flats/save").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers(HttpMethod.POST, "/flats/update/**").hasAnyRole("ADMIN", "DESIGNER")

                        /*
                         * Consulta de planos.
                         * ADMIN, DESIGNER y OPERATOR pueden consultar,
                         * visualizar y descargar planos.
                         */
                        .requestMatchers(HttpMethod.GET, "/flats/**").hasAnyRole("ADMIN", "DESIGNER", "OPERATOR")

                        /*
                         * Versiones de planos.
                         * Crear versiones solo ADMIN o DESIGNER.
                         */
                        .requestMatchers("/versions/new/**").hasAnyRole("ADMIN", "DESIGNER")
                        .requestMatchers(HttpMethod.POST, "/versions/save/**").hasAnyRole("ADMIN", "DESIGNER")

                        /*
                         * Consultar versiones.
                         * Todos los roles autenticados pueden ver historial,
                         * visualizar y descargar versiones.
                         */
                        .requestMatchers(HttpMethod.GET, "/versions/**").hasAnyRole("ADMIN", "DESIGNER", "OPERATOR")

                        /*
                         * Códigos QR.
                         * Generar QR solo ADMIN o DESIGNER.
                         */
                        .requestMatchers(HttpMethod.POST, "/qr/generate/**").hasAnyRole("ADMIN", "DESIGNER")

                        /*
                         * Consultar QR.
                         * Todos los roles autenticados pueden ver QR y acceder al plano.
                         */
                        .requestMatchers(HttpMethod.GET, "/qr/**").hasAnyRole("ADMIN", "DESIGNER", "OPERATOR")

                        /*
                         * Cualquier otra ruta requiere autenticación.
                         */
                        .anyRequest().authenticated()
                    )

                    .exceptionHandling(exception -> exception
                            .accessDeniedPage("/access-denied")
                    )

                    .formLogin(form -> form
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/dashboard", true)
                            .failureUrl("/login?error=true")
                            .permitAll()
                    )

                /*
                 * Login personalizado.
                 */
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                /*
                 * Logout.
                 */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}