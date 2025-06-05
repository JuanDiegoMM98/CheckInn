package com.dam.checkinn.configurations.security;


import ch.qos.logback.core.util.StringUtil;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByDniIgnoreCase(dni)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró usuario con ese DNI"));

        return User.builder()
                .username(usuario.getDni())
                .password(usuario.getContraseña())
                .roles("AUTENTICADO")
                .build();
    }
}
