package com.dam.checkinn.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="alojamientos")
public class AlojamientoModel {

    /* DEFINICIÓN DE ENUMERADOS ***************************************************************************************/

    public enum Servicio {WIFI, PISCINA, BARBACOA}

    /* CONSTRUCTOR ****************************************************************************************************/

    public AlojamientoModel() {
        this.disponible = true;
    }

    public AlojamientoModel(String nombre, String descripcion, String provincia,
                            double precioNoche, int capacidad, byte[] imagen, List<Servicio> servicios,
                            LocalDate inicioBloqueo, LocalDate finBloqueo, UsuarioModel usuarioAlojamiento
                            ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.provincia = provincia;
        this.disponible = true;
        this.precioNoche = precioNoche;
        this.capacidad = capacidad;
        this.imagen = imagen;
        this.servicios = servicios;
        this.inicioBloqueo = inicioBloqueo;
        this.finBloqueo = finBloqueo;
        this.usuarioAlojamiento = usuarioAlojamiento;
        this.reservas = new ArrayList<>();
    }

    /* ATRIBUTOS ******************************************************************************************************/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;     // PK

    @Column(nullable = false, length = 20, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String provincia;

    @Column(nullable = false)
    private boolean disponible;

    @Column(nullable = false)
    private double precioNoche;

    @Column(nullable = false)
    private int capacidad;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMBLOB") //16 MB
    private byte[] imagen;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Servicio> servicios;

    @Column
    private LocalDate inicioBloqueo;

    @Column
    private LocalDate finBloqueo;

    /* RELACIONES *****************************************************************************************************/
    @ManyToOne
    @JsonBackReference(value="usuario-alojamientos")
    private UsuarioModel usuarioAlojamiento;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "reserva-alojamiento")
    private List<ReservaModel> reservas;

    /* GETTER Y SETTER ************************************************************************************************/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public LocalDate getInicioBloqueo() {
        return inicioBloqueo;
    }

    public void setInicioBloqueo(LocalDate inicioBloqueo) {
        this.inicioBloqueo = inicioBloqueo;
    }

    public LocalDate getFinBloqueo() {
        return finBloqueo;
    }

    public void setFinBloqueo(LocalDate finBloqueo) {
        this.finBloqueo = finBloqueo;
    }

    public UsuarioModel getUsuarioAlojamiento() {
        return usuarioAlojamiento;
    }

    public void setUsuarioAlojamiento(UsuarioModel usuarioAlojamiento) {
        this.usuarioAlojamiento = usuarioAlojamiento;
    }

    public List<ReservaModel> getReservas() {
        return reservas;
    }

    public void setReservas(List<ReservaModel> reservas) {
        this.reservas = reservas;
    }
}
