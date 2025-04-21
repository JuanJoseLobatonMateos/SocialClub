package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Representa una instalación en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "instalacion", schema = "clubsocial")
public class Instalacion {
    /**
     * Identificador único de la instalación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Tipo de la instalación.
     */
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    /**
     * Nombre de la instalación.
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Capacidad de la instalación.
     */
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    /**
     * Precio de alquiler de la instalación.
     */
    @Column(name = "precio_alquiler", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAlquiler;

    /**
     * Duración de uso de la instalación.
     */
    @Column(name = "duracion", nullable = false)
    private Integer duracion;

    /**
     * Hora de inicio de disponibilidad de la instalación.
     */
    @Column(name = "hora_ini", nullable = false)
    private LocalTime horaIni;

    /**
     * Hora de fin de disponibilidad de la instalación.
     */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * Disponibilidad de la instalación.
     */
    @Column(name = "disponibilidad", nullable = false)
    private Byte disponibilidad;

    /**
     * Empleado asociado a la instalación.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado idEmpleado;

    /**
     * Imagen de la instalación en formato de bytes.
     */
    @Column(name = "imagen")
    private byte[] imagen;

    /**
     * Obtiene la imagen de la instalación en formato de bytes.
     *
     * @return la imagen de la instalación.
     */
    public byte[] getImagen() {
        return imagen;
    }

    /**
     * Establece la imagen de la instalación en formato de bytes.
     *
     * @param imagen la imagen de la instalación.
     */
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    /**
     * Obtiene el identificador de la instalación.
     *
     * @return el identificador de la instalación.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador de la instalación.
     *
     * @param id el identificador de la instalación.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de la instalación.
     *
     * @return el tipo de la instalación.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de la instalación.
     *
     * @param tipo el tipo de la instalación.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el nombre de la instalación.
     *
     * @return el nombre de la instalación.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la instalación.
     *
     * @param nombre el nombre de la instalación.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la capacidad de la instalación.
     *
     * @return la capacidad de la instalación.
     */
    public Integer getCapacidad() {
        return capacidad;
    }

    /**
     * Establece la capacidad de la instalación.
     *
     * @param capacidad la capacidad de la instalación.
     */
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Obtiene el precio de alquiler de la instalación.
     *
     * @return el precio de alquiler de la instalación.
     */
    public BigDecimal getPrecioAlquiler() {
        return precioAlquiler;
    }

    /**
     * Establece el precio de alquiler de la instalación.
     *
     * @param precioAlquiler el precio de alquiler de la instalación.
     */
    public void setPrecioAlquiler(BigDecimal precioAlquiler) {
        this.precioAlquiler = precioAlquiler;
    }

    /**
     * Obtiene la duración de uso de la instalación.
     *
     * @return la duración de uso de la instalación.
     */
    public Integer getDuracion() {
        return duracion;
    }

    /**
     * Establece la duración de uso de la instalación.
     *
     * @param duracion la duración de uso de la instalación.
     */
    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    /**
     * Obtiene la hora de inicio de disponibilidad de la instalación.
     *
     * @return la hora de inicio de disponibilidad de la instalación.
     */
    public LocalTime getHoraIni() {
        return horaIni;
    }

    /**
     * Establece la hora de inicio de disponibilidad de la instalación.
     *
     * @param horaIni la hora de inicio de disponibilidad de la instalación.
     */
    public void setHoraIni(LocalTime horaIni) {
        this.horaIni = horaIni;
    }

    /**
     * Obtiene la hora de fin de disponibilidad de la instalación.
     *
     * @return la hora de fin de disponibilidad de la instalación.
     */
    public LocalTime getHoraFin() {
        return horaFin;
    }

    /**
     * Establece la hora de fin de disponibilidad de la instalación.
     *
     * @param horaFin la hora de fin de disponibilidad de la instalación.
     */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Obtiene la disponibilidad de la instalación.
     *
     * @return la disponibilidad de la instalación.
     */
    public Byte getDisponibilidad() {
        return disponibilidad;
    }

    /**
     * Establece la disponibilidad de la instalación.
     *
     * @param disponibilidad la disponibilidad de la instalación.
     */
    public void setDisponibilidad(Byte disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    /**
     * Obtiene el empleado asociado a la instalación.
     *
     * @return el empleado asociado a la instalación.
     */
    public Empleado getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Establece el empleado asociado a la instalación.
     *
     * @param idEmpleado el empleado asociado a la instalación.
     */
    public void setIdEmpleado(Empleado idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * Obtiene la disponibilidad de la instalación en formato de texto.
     *
     * @return "Disponible" si la instalación está disponible, "No Disponible" en caso contrario.
     */
    public String getDisponibilidadTexto() {
        return (disponibilidad != null && disponibilidad == 0) ? "Disponible" : "No Disponible";
    }

    /**
     * Establece la disponibilidad de la instalación a partir de un texto.
     *
     * @param disponibilidadTexto "Disponible" para disponible, "No Disponible" para no disponible.
     */
    public void setDisponibilidadFromTexto(String disponibilidadTexto) {
        if ("Disponible".equals(disponibilidadTexto)) {
            this.disponibilidad = 0; // 0 para disponible
        } else {
            this.disponibilidad = 1; // 1 para no disponible
        }
    }
}