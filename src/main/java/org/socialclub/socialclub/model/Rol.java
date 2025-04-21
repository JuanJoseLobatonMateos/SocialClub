package org.socialclub.socialclub.model;

import jakarta.persistence.*;

/**
 * Representa un rol en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "rol", schema = "clubsocial")
public class Rol {
    /**
     * Identificador único del rol.
     */
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Nombre del rol.
     */
    @Column(name = "nombre_rol", nullable = false, length = 45)
    private String nombreRol;

    /**
     * Descripción del rol.
     */
    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    /**
     * Obtiene el identificador del rol.
     *
     * @return el identificador del rol.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del rol.
     *
     * @param id el identificador del rol.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del rol.
     *
     * @return el nombre del rol.
     */
    public String getNombreRol() {
        return nombreRol;
    }

    /**
     * Establece el nombre del rol.
     *
     * @param nombreRol el nombre del rol.
     */
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    /**
     * Obtiene la descripción del rol.
     *
     * @return la descripción del rol.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del rol.
     *
     * @param descripcion la descripción del rol.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}