package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

    /**
     * Representa una familia en el sistema del club social.
     */
    @SuppressWarnings("ALL")
    @Entity
    @Table(name = "familia", schema = "clubsocial")
    public class Familia {
        /**
         * Identificador único de la familia.
         */
        @Id
        @Column(name = "num_familia", nullable = false)
        private Integer id;

        /**
         * Nombre del titular de la familia.
         */
        @Column(name = "nombre_titular", nullable = false, length = 50)
        private String nombreTitular;

        /**
         * Apellidos del titular de la familia.
         */
        @Column(name = "apellidos_titular", nullable = false, length = 80)
        private String apellidosTitular;

        /**
         * Empleado asociado a la familia.
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "id_empleado", nullable = false)
        private Empleado idEmpleado;

        /**
         * Número de miembros de la familia.
         */
        @ColumnDefault("0")
        @Column(name = "numero_miembros")
        private Integer numeroMiembros;

        /**
         * Socios asociados a la familia.
         */
        @OneToMany(mappedBy = "numFamilia")
        private Set<Socio> socios = new LinkedHashSet<>();

        /**
         * Obtiene los socios asociados a la familia.
         * @return los socios asociados a la familia.
         */
        public Set<Socio> getSocios() {
            return socios;
        }

        /**
         * Establece los socios asociados a la familia.
         * @param socios los socios asociados a la familia.
         */
        public void setSocios(Set<Socio> socios) {
            this.socios = socios;
        }

        /**
         * Obtiene el número de miembros de la familia.
         * @return el número de miembros de la familia.
         */
        public Integer getNumeroMiembros() {
            return numeroMiembros;
        }

        /**
         * Establece el número de miembros de la familia.
         * @param numeroMiembros el número de miembros de la familia.
         */
        public void setNumeroMiembros(Integer numeroMiembros) {
            this.numeroMiembros = numeroMiembros;
        }

        /**
         * Obtiene el identificador de la familia.
         * @return el identificador de la familia.
         */
        public Integer getId() {
            return id;
        }

        /**
         * Establece el identificador de la familia.
         * @param id el identificador de la familia.
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * Obtiene el nombre del titular de la familia.
         * @return el nombre del titular de la familia.
         */
        public String getNombreTitular() {
            return nombreTitular;
        }

        /**
         * Establece el nombre del titular de la familia.
         * @param nombreTitular el nombre del titular de la familia.
         */
        public void setNombreTitular(String nombreTitular) {
            this.nombreTitular = nombreTitular;
        }

        /**
         * Obtiene los apellidos del titular de la familia.
         * @return los apellidos del titular de la familia.
         */
        public String getApellidosTitular() {
            return apellidosTitular;
        }

        /**
         * Establece los apellidos del titular de la familia.
         * @param apellidosTitular los apellidos del titular de la familia.
         */
        public void setApellidosTitular(String apellidosTitular) {
            this.apellidosTitular = apellidosTitular;
        }

        /**
         * Obtiene el empleado asociado a la familia.
         * @return el empleado asociado a la familia.
         */
        public Empleado getIdEmpleado() {
            return idEmpleado;
        }

        /**
         * Establece el empleado asociado a la familia.
         * @param idEmpleado el empleado asociado a la familia.
         */
        public void setIdEmpleado(Empleado idEmpleado) {
            this.idEmpleado = idEmpleado;
        }
    }