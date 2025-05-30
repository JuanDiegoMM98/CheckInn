package com.dam.checkinn.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="usuarios")
public class UsuarioModel {

    /* DEFINICIÓN DE ENUMERADOS ***************************************************************************************/

    public enum Genero {
        MASCULINO,
        FEMENINO
    }

    public enum Rol {
        CLIENTE,
        PRO
    }

    /* CONSTRUCTOR ****************************************************************************************************/

    public UsuarioModel() {
        this.setRol(Rol.CLIENTE);
    }

    public UsuarioModel(String dni, String nombre, String apellido1, String apellido2, String correo, String contraseña,
                        String tarjetaBancaria, String direccion, LocalDate fechaNacimiento, String telefono,
                        Genero genero) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.contraseña = contraseña;
        this.tarjetaBancaria = tarjetaBancaria;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = Rol.CLIENTE;
        this.telefono = telefono;
        this.genero = genero;
        this.alojamientos = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }

    /* ATRIBUTOS ******************************************************************************************************/

    @Id
    private String dni;     // PK

    @Column(nullable = false, length = 20)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String apellido1;

    @Column(nullable = false, length = 20)
    private String apellido2;

    @Column(nullable = false, length = 50, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contraseña;

    @Column(nullable = false, length = 16)
    private String tarjetaBancaria;

    @Column(nullable = false, length = 30)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 7)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(length = 9)
    private String telefono;

    @Column
    @Enumerated(EnumType.STRING)
    private Genero genero;

    /* RELACIONES *****************************************************************************************************/

    @OneToMany(mappedBy = "usuarioAlojamiento", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "usuario-alojamientos")
    private
    List<AlojamientoModel> alojamientos;

    @OneToMany(mappedBy = "usuarioReserva", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "usuario-reservas")
    private
    List<ReservaModel> reservas;


    /* GETTER Y SETTER ************************************************************************************************/

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getTarjetaBancaria() {
        return tarjetaBancaria;
    }

    public void setTarjetaBancaria(String tarjetaBancaria) {
        this.tarjetaBancaria = tarjetaBancaria;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public List<AlojamientoModel> getAlojamientos() {
        return alojamientos;
    }

    public void setAlojamientos(List<AlojamientoModel> alojamientos) {
        this.alojamientos = alojamientos;
    }

    public List<ReservaModel> getReservas() {
        return reservas;
    }

    public void setReservas(List<ReservaModel> reservas) {
        this.reservas = reservas;
    }
}
