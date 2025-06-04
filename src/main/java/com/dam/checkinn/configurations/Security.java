package com.dam.checkinn.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Security {

    @Bean

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

//    @Bean
//    @Order(3)
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests -> {authorizeRequests
//                        .requestMatchers("/api/v1/login").permitAll()
////                        .requestMatchers("/Index.html").permitAll()
////                        .requestMatchers("").permitAll()
//                        .anyRequest().authenticated();
//
//                }).formLogin(form -> {
//                    form.loginPage("/Index")
//                            .defaultSuccessUrl("/Index", true)
//                            .permitAll();
//                });
//                return http.build();
//    }
}

