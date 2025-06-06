package com.dam.checkinn.configurations.security;

import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserManager implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserManager(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        System.out.println("Autenticando: " + dni);
        return usuarioRepository.findByDniIgnoreCase(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + dni));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
