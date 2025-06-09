package com.dam.checkinn.configurations.security;

import com.dam.checkinn.models.UsuarioModel;
import jakarta.servlet.http.HttpServletResponse;
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
                        .requestMatchers("app.js", "/images/**", "Registro.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
                        .anyRequest().authenticated();
                })
                .formLogin( form -> form
                        .loginPage("/Login.html")
                        .loginProcessingUrl("/api/v1/login")
                        .usernameParameter("dni")
                        .passwordParameter("contraseña")
                        .defaultSuccessUrl("/Index.html", true)
                        .permitAll()
                )
                .logout(Customizer.withDefaults())
                .build();
    }

}

