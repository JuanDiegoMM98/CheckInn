package com.dam.checkinn.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {auth
                        .requestMatchers("app.js", "/images", "Registro.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
                        .anyRequest().authenticated();
                })
                .formLogin( form -> form
                        .loginPage("/Login.html")               // URL de la página de login personalizada
                        .loginProcessingUrl("/api/v1/login")
                        .usernameParameter("dni")
                        .passwordParameter("contraseña")// URL a la que se envía el form para autenticación
                        .defaultSuccessUrl("/Index.html", true)  // URL a la que se redirige si login es exitoso
                        .permitAll()
                )
                .logout(Customizer.withDefaults())
                .build();
    }

}

