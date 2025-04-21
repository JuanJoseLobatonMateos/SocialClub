package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

/**
 * Representa un horario en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "horarios", schema = "clubsocial", indexes = {
        @Index(name = "id_instalacion", columnList = "id_instalacion")
})
public class Horario {
    /**
     * Identificador único del horario.
     */
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Instalación asociada al horario.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_instalacion", nullable = false)
    private Instalacion idInstalacion;

    /**
     * Hora de inicio del horario.
     */
    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de fin del horario.
     */
    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * Indica si el horario está disponible.
     */
    @NotNull
    @ColumnDefault("1")
    @Column(name = "disponible", nullable = false)
    private Byte disponible;

    /**
     * Obtiene el identificador del horario.
     *
     * @return el identificador del horario.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del horario.
     *
     * @param id el identificador del horario.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene la instalación asociada al horario.
     *
     * @return la instalación asociada al horario.
     */
    public Instalacion getIdInstalacion() {
        return idInstalacion;
    }

    /**
     * Establece la instalación asociada al horario.
     *
     * @param idInstalacion la instalación asociada al horario.
     */
    public void setIdInstalacion(Instalacion idInstalacion) {
        this.idInstalacion = idInstalacion;
    }

    /**
     * Obtiene la hora de inicio del horario.
     *
     * @return la hora de inicio del horario.
     */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    /**
     * Establece la hora de inicio del horario.
     *
     * @param horaInicio la hora de inicio del horario.
     */
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    /**
     * Obtiene la hora de fin del horario.
     *
     * @return la hora de fin del horario.
     */
    public LocalTime getHoraFin() {
        return horaFin;
    }

    /**
     * Establece la hora de fin del horario.
     *
     * @param horaFin la hora de fin del horario.
     */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Obtiene la disponibilidad del horario.
     *
     * @return la disponibilidad del horario.
     */
    public Byte getDisponible() {
        return disponible;
    }

    /**
     * Establece la disponibilidad del horario.
     *
     * @param disponible la disponibilidad del horario.
     */
    public void setDisponible(Byte disponible) {
        this.disponible = disponible;
    }
}