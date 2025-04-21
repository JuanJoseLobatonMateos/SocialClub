package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una reserva en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "reserva", schema = "clubsocial")
public class Reserva {
    /**
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva", nullable = false)
    private Integer id;

    /**
     * Fecha de la reserva.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * Hora de la reserva.
     */
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    /**
     * Instalación asociada a la reserva.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_instalacion", nullable = false)
    private Instalacion idInstalacion;

    /**
     * Socio asociado a la reserva.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "numero_socio", nullable = false)
    private Socio numeroSocio;

    /**
     * Empleado asociado a la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_empleado")
    private Empleado idEmpleado;

    /**
     * Obtiene el identificador de la reserva.
     *
     * @return el identificador de la reserva.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador de la reserva.
     *
     * @param id el identificador de la reserva.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene la fecha de la reserva.
     *
     * @return la fecha de la reserva.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la reserva.
     *
     * @param fecha la fecha de la reserva.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la hora de la reserva.
     *
     * @return la hora de la reserva.
     */
    public LocalTime getHora() {
        return hora;
    }

    /**
     * Establece la hora de la reserva.
     *
     * @param hora la hora de la reserva.
     */
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /**
     * Obtiene la instalación asociada a la reserva.
     *
     * @return la instalación asociada a la reserva.
     */
    public Instalacion getIdInstalacion() {
        return idInstalacion;
    }

    /**
     * Establece la instalación asociada a la reserva.
     *
     * @param idInstalacion la instalación asociada a la reserva.
     */
    public void setIdInstalacion(Instalacion idInstalacion) {
        this.idInstalacion = idInstalacion;
    }

    /**
     * Obtiene el socio asociado a la reserva.
     *
     * @return el socio asociado a la reserva.
     */
    public Socio getNumeroSocio() {
        return numeroSocio;
    }

    /**
     * Establece el socio asociado a la reserva.
     *
     * @param numeroSocio el socio asociado a la reserva.
     */
    public void setNumeroSocio(Socio numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    /**
     * Obtiene el empleado asociado a la reserva.
     *
     * @return el empleado asociado a la reserva.
     */
    public Empleado getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Establece el empleado asociado a la reserva.
     *
     * @param idEmpleado el empleado asociado a la reserva.
     */
    public void setIdEmpleado(Empleado idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}