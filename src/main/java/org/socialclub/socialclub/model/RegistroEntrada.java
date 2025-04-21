package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa un registro de entrada en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "registro_entrada", schema = "clubsocial")
public class RegistroEntrada {
    /**
     * Identificador único del registro de entrada.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro", nullable = false)
    private Integer id;

    /**
     * Fecha del registro de entrada.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * Socio asociado al registro de entrada.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "numero_socio", nullable = false)
    private Socio numeroSocio;

    /**
     * Hora de entrada del registro.
     */
    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    /**
     * Hora de salida del registro.
     */
    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    /**
     * Empleado asociado al registro de entrada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_empleado")
    private Empleado idEmpleado;

    /**
     * Obtiene el identificador del registro de entrada.
     *
     * @return el identificador del registro de entrada.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del registro de entrada.
     *
     * @param id el identificador del registro de entrada.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene la fecha del registro de entrada.
     *
     * @return la fecha del registro de entrada.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del registro de entrada.
     *
     * @param fecha la fecha del registro de entrada.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el socio asociado al registro de entrada.
     *
     * @return el socio asociado al registro de entrada.
     */
    public Socio getNumeroSocio() {
        return numeroSocio;
    }

    /**
     * Establece el socio asociado al registro de entrada.
     *
     * @param numeroSocio el socio asociado al registro de entrada.
     */
    public void setNumeroSocio(Socio numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    /**
     * Obtiene la hora de entrada del registro.
     *
     * @return la hora de entrada del registro.
     */
    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    /**
     * Establece la hora de entrada del registro.
     *
     * @param horaEntrada la hora de entrada del registro.
     */
    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    /**
     * Obtiene la hora de salida del registro.
     *
     * @return la hora de salida del registro.
     */
    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    /**
     * Establece la hora de salida del registro.
     *
     * @param horaSalida la hora de salida del registro.
     */
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * Obtiene el empleado asociado al registro de entrada.
     *
     * @return el empleado asociado al registro de entrada.
     */
    public Empleado getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Establece el empleado asociado al registro de entrada.
     *
     * @param idEmpleado el empleado asociado al registro de entrada.
     */
    public void setIdEmpleado(Empleado idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * Obtiene el nombre del socio asociado al registro de entrada.
     *
     * @return el nombre del socio asociado al registro de entrada.
     */
    public String getNombre() {
        return numeroSocio != null ? numeroSocio.getNombre() : null;
    }

    /**
     * Obtiene los apellidos del socio asociado al registro de entrada.
     *
     * @return los apellidos del socio asociado al registro de entrada.
     */
    public String getApellidos() {
        return numeroSocio != null ? numeroSocio.getApellidos() : null;
    }

    /**
     * Obtiene el número de socio en formato de texto.
     *
     * @return el número de socio en formato de texto.
     */
    public String getStringNumeroSocio() {
        return numeroSocio != null ? numeroSocio.getNumeroSocio() : null;
    }
}