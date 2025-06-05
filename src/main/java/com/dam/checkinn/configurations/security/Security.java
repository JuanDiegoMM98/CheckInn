package com.dam.checkinn.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

//    private final CustomUserDetailsService userDetailsService;
//
//    public Security(CustomUserDetailsService customUserDetailsService) {
//        this.userDetailsService = customUserDetailsService;
//    }
//
    // 1. PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//
//    // 2. AuthenticationManager
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
//        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//        return authBuilder.build();
//    }
//
//    // 3. Configuración de seguridad HTTP
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // deshabilicar csrf
//                .authorizeHttpRequests( request -> {request
//                        .requestMatchers("/Index.html").permitAll()
//                        .requestMatchers("/Registro.html").permitAll()
//                        .requestMatchers("/api/v1/login").permitAll()
//                        .requestMatchers( "/api/v1/usuarios").permitAll()
//                        .requestMatchers("app.js", "/images/**").permitAll()
//                        .anyRequest().authenticated();
//        }).exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(new Redireccionador("/Index.html")) // TODO PONERLO AQUI?
//                )
//                .formLogin(form -> form
//                        .loginPage("/Index.html")               // Ruta a tu formulario personalizado
//                        .loginProcessingUrl("/api/v1/login")   // Donde se enviará el formulario (POST)
//                        .usernameParameter("dni")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/Home.html", true)  // Página a la que redirige si el login es exitoso
//                        .failureUrl("/api/v1/login")   // Página si el login falla  // TODO PONER REDIRECCIONADOR
//                        .permitAll()
//                );
//        return http.build();
//    }




        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())    // Deshabilita CSRF para APIs REST

    // TODO ACTIVAR ESTO DESPUES
    //                .authorizeHttpRequests(auth -> auth
    //                        .requestMatchers(
    //                                "/api/v1/login",
    //                                "/api/v1/usuarios/register").permitAll()  // acceso libre a login
    //                        .anyRequest().authenticated()
    //                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 🔓 Permite TODO el acceso
                )
                .formLogin(form -> form.disable());  // Deshabilita login automático

        return http.build();
    }
}

