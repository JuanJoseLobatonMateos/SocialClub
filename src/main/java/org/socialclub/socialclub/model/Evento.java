package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Representa un evento en el sistema del club social.
 */
@Entity
@Table(name = "evento", schema = "clubsocial")
public class Evento {
    /**
     * Identificador único del evento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento", nullable = false)
    private Integer id;

    /**
     * Nombre del evento.
     */
    @Size(max = 300)
    @Column(name = "nombre", length = 300)
    private String nombre;

    /**
     * Imagen del evento en formato de bytes.
     */
    @Column(name = "imagen")
    private byte[] imagen;

    /**
     * Fecha del evento.
     */
    @Column(name = "fecha")
    private LocalDate fecha;

    /**
     * Obtiene el identificador del evento.
     *
     * @return el identificador del evento.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del evento.
     *
     * @param id el identificador del evento.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del evento.
     *
     * @return el nombre del evento.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del evento.
     *
     * @param nombre el nombre del evento.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la imagen del evento en formato de bytes.
     *
     * @return la imagen del evento.
     */
    public byte[] getImagen() {
        return imagen;
    }

    /**
     * Establece la imagen del evento en formato de bytes.
     *
     * @param imagen la imagen del evento.
     */
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    /**
     * Obtiene la fecha del evento.
     *
     * @return la fecha del evento.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del evento.
     *
     * @param fecha la fecha del evento.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}