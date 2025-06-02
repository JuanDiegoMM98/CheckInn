package com.dam.checkinn.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Login {


    @Bean
    @Order(3)
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

