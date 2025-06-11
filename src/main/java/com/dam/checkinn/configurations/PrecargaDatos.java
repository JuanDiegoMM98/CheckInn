package com.dam.checkinn.configurations;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class PrecargaDatos {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final UsuarioRepository usuarioRepository;

    private final AlojamientoRepository alojamientoRepository;

    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    public PrecargaDatos(UsuarioRepository usuarioRepository, AlojamientoRepository alojamientoRepository,
                         ReservaRepository reservaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* BEAN ***********************************************************************************************************/

//    @Bean
//    @Order(1)
//    public ApplicationRunner precarga() {
//        return new ApplicationRunner() {
//            @Override
//            @Transactional
//            public void run(ApplicationArguments args) throws Exception {
//
//                // USUARIOS
//                UsuarioModel u1 = new UsuarioModel(
//                        "12345678A", "Juan", "Pérez", "Gómez", "juan@mail.com",
//                        passwordEncoder.encode("pass1234"), "1111222233334444", "Calle Falsa 123", LocalDate.of(1980, 5, 20),
//                        "600123456", UsuarioModel.Genero.MASCULINO);
//
//                UsuarioModel u2 = new UsuarioModel(
//                        "12345678B", "Laura", "Martínez", "Ruiz", "laura@mail.com",
//                        passwordEncoder.encode("pass1234"), "4444333322221111", "Avenida Siempre Viva 742", LocalDate.of(1990, 8, 15),
//                        "699987654", UsuarioModel.Genero.FEMENINO);
//                u2.setRol(UsuarioModel.Rol.PRO);
//                UsuarioModel u3 = new UsuarioModel(
//                        "12345678C", "Carlos", "López", "Fernández", "carlos@mail.com",
//                        passwordEncoder.encode("pass1234"), "5555666677778888", "Plaza Mayor 5", LocalDate.of(1985, 3, 10),
//                        "612345678", UsuarioModel.Genero.MASCULINO
//                );
//                u3.setRol(UsuarioModel.Rol.CLIENTE);
//
//                usuarioRepository.save(u1);
//                usuarioRepository.save(u2);
//                usuarioRepository.save(u3);
//
//                // ALOJAMIENTOS
//                List<AlojamientoModel.Servicio> servicios = new ArrayList<>();
//                servicios.add(AlojamientoModel.Servicio.WIFI);
//
//                AlojamientoModel alojamiento = new AlojamientoModel(
//                        "Casa Rural El Bosque",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento);
//
//                AlojamientoModel alojamiento2 = new AlojamientoModel(
//                        "Casa Rural El Bosqu2",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento2);
//
//                AlojamientoModel alojamiento3 = new AlojamientoModel(
//                        "Casa Rural El Bosqu3",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento3);
//
//                AlojamientoModel alojamiento4 = new AlojamientoModel(
//                        "Casa Rural El Bosqu4",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento4);
//
//                AlojamientoModel alojamiento5 = new AlojamientoModel(
//                        "Casa Rural El Bosqu5",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento5);
//
//                AlojamientoModel alojamiento6 = new AlojamientoModel(
//                        "Casa Rural El Bosqu6",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento6);
//
//                AlojamientoModel alojamiento7 = new AlojamientoModel(
//                        "Casa Rural El Bosqu7",
//                        "Una casa rural acogedora en plena naturaleza.",
//                        "Asturias",
//                        "Calle Falsa 123",
//                        120.0,
//                        5,
//                        0,
//                        Files.readAllBytes(Paths.get("src/main/resources/images/ejemplo.jpg")),
//                        servicios,
//                        LocalDate.of(2025, Month.JUNE, 15),   // inicioBloqueo
//                        LocalDate.of(2025, Month.JUNE, 18),   // finBloqueo
//                        u2
//                );
//
//                alojamientoRepository.save(alojamiento7);
//
//                // RESERVAS
//                ReservaModel reserva = new ReservaModel(
//                        240.0,                                // precio
//                        LocalDate.of(2025, 7, 10),            // fechaInicio
//                        LocalDate.of(2025, 7, 12),            // fechaFin
//                        u1,                                                          // usuario que reserva
//                        alojamiento,     // alojamiento reservado
//                        null
//                );
//
//                reservaRepository.save(reserva);
//            }
//        };
//    }
}
