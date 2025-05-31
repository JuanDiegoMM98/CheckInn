package com.dam.checkinn.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class ReservaModel {

    /* CONSTRUCTOR ****************************************************************************************************/

    public ReservaModel() {
        this.cancelada = false;
    }

    public ReservaModel(double precio, LocalDate fechaInicio, LocalDate fechaFin,
                        UsuarioModel usuarioReserva, AlojamientoModel alojamiento, String motivoCancelacion) {
        this.precio = precio;
        this.cancelada = false;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.usuarioReserva = usuarioReserva;
        this.alojamiento = alojamiento;
        this.motivoCancelacion = motivoCancelacion;
    }

    /* ATRIBUTOS ******************************************************************************************************/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private boolean cancelada;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column
    private String motivoCancelacion;

    /* RELACIONES *****************************************************************************************************/

    @ManyToOne
    @JsonBackReference(value = "usuario-reservas")
    private
    UsuarioModel usuarioReserva;

    @ManyToOne
    @JsonBackReference(value = "reserva-alojamiento")
    private
    AlojamientoModel alojamiento;


    /* GETTER Y SETTER ************************************************************************************************/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public UsuarioModel getUsuarioReserva() {
        return usuarioReserva;
    }

    public void setUsuarioReserva(UsuarioModel usuarioReserva) {
        this.usuarioReserva = usuarioReserva;
    }

    public AlojamientoModel getAlojamiento() {
        return alojamiento;
    }

    public void setAlojamiento(AlojamientoModel alojamiento) {
        this.alojamiento = alojamiento;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
