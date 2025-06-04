package com.dam.checkinn.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity
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
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests( request -> {request
//                        .requestMatchers("/Index.html").permitAll()
//                        .requestMatchers("/Registro.html").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
//                        .requestMatchers("app.js", "/images/**").permitAll()
//
//                        .anyRequest().authenticated();
//        }).exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(new Redireccionador("/Index.html"))
//                )
////                .formLogin(form -> form
////                        .loginPage("/Index.html")               // Ruta a tu formulario personalizado
////                        .loginProcessingUrl("/api/v1/login")   // Donde se enviará el formulario (POST)
////                        .defaultSuccessUrl("/Home.html", true)  // Página a la que redirige si el login es exitoso
////                        .failureUrl("/api/v1/login")   // Página si el login falla
////                        .permitAll()
////                )
//                .csrf( csrf -> csrf.disable());
//        return http.build();
//    }
}

