package com.dam.checkinn;

import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.ReservaRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class PrecargaDatos {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Bean
    @Order(1)
    public ApplicationRunner precarga() {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {

                // USUARIOS
                UsuarioModel u1 = new UsuarioModel(
                        "12345678A", "Juan", "Pérez", "Gómez", "juan@mail.com",
                        "pass1234", "1111222233334444", "Calle Falsa 123", LocalDate.of(1980, 5, 20),
                        "600123456", UsuarioModel.Genero.MASCULINO, new ArrayList<>(), new ArrayList<>());

                UsuarioModel u2 = new UsuarioModel(
                        "12345678B", "Laura", "Martínez", "Ruiz", "laura@mail.com",
                        "pass1234", "4444333322221111", "Avenida Siempre Viva 742", LocalDate.of(1990, 8, 15),
                        "699987654", UsuarioModel.Genero.FEMENINO, new ArrayList<>(), new ArrayList<>());
                u2.setRol(UsuarioModel.Rol.PRO);

                usuarioRepository.save(u1);
                usuarioRepository.save(u2);

                // ALOJAMIENTOS
                List<AlojamientoModel.Servicio> servicios = new ArrayList<>();
                servicios.add(AlojamientoModel.Servicio.WIFI);

                AlojamientoModel alojamiento = new AlojamientoModel(
                        "Casa Rural El Bosque",
                        "Una casa rural acogedora en plena naturaleza.",
                        "Asturias",
                        120.0,
                        5,
                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
                        servicios,
                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
                        u2,
                        new ArrayList<>() // reservas vacías al inicio
                );

                alojamientoRepository.save(alojamiento);

                // RESERVAS
                ReservaModel reserva = new ReservaModel(
                        240.0,                                // precio
                        LocalDate.of(2025, 7, 10),            // fechaInicio
                        LocalDate.of(2025, 7, 12),            // fechaFin
                        u1,                                                          // usuario que reserva
                        alojamiento                           // alojamiento reservado
                );

                reservaRepository.save(reserva);
            }
        };
    }
}
