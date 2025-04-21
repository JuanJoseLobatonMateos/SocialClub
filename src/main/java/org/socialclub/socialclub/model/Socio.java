package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Representa un socio del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "socio", schema = "clubsocial")
public class Socio {
    private static final Logger logger = LoggerFactory.getLogger(Socio.class);

    /**
     * Enum que representa la titularidad del socio.
     */
    public enum Titularidad {
        NORMAL,
        TITULAR,
        COTITULAR
    }

    /**
     * Número de socio, generado automáticamente.
     */
    @Id
    @UuidGenerator
    @Column(name = "numero_socio", nullable = false, length = 200, updatable = false, insertable = false)
    private String numeroSocio;

    /**
     * Identificador del socio.
     */
    @Column(name = "id_socio", nullable = false, updatable = false, insertable = false)
    private Integer idSocio;

    /**
     * Familia a la que pertenece el socio.
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "num_familia", nullable = false)
    private Familia numFamilia;

    /**
     * Nombre del socio.
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Apellidos del socio.
     */
    @Column(name = "apellidos", nullable = false, length = 50)
    private String apellidos;

    /**
     * Teléfono del socio.
     */
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;

    /**
     * DNI del socio.
     */
    @Column(name = "dni", length = 10)
    private String dni;

    /**
     * Email del socio.
     */
    @Column(name = "email", length = 40)
    private String email;

    /**
     * Fecha de nacimiento del socio.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Foto del socio en formato byte array.
     */
    @Column(name = "foto")
    private byte[] foto;

    /**
     * Huella del socio en formato byte array.
     */
    @Column(name = "huella")
    private byte[] huella;

    /**
     * Indica si el socio está dentro de la instalación.
     */
    @ColumnDefault("0")
    @Column(name = "dentro_instalacion", nullable = false)
    private Byte dentroInstalacion;

    /**
     * Fecha de alta del socio.
     */
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    /**
     * Empleado que registró al socio.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado idEmpleado;

    /**
     * Contraseña del socio.
     */
    @Column(name = "contrasenia", length = 250)
    private String contrasenia;

    /**
     * Titularidad del socio.
     */
    @ColumnDefault("'NORMAL'")
    @Enumerated(EnumType.STRING)
    @Lob
    @Column(name = "titularidad", nullable = false)
    private Titularidad titularidad;

    // Getters y setters...

    /**
     * Obtiene la titularidad del socio.
     *
     * @return la titularidad del socio.
     */
    public Titularidad getTitularidad() {
        return titularidad;
    }

    /**
     * Establece la titularidad del socio.
     *
     * @param titularidad la titularidad del socio.
     */
    public void setTitularidad(Titularidad titularidad) {
        this.titularidad = titularidad;
    }

    /**
     * Obtiene la contraseña del socio.
     *
     * @return la contraseña del socio.
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Establece la contraseña del socio.
     *
     * @param contrasenia la contraseña del socio.
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Obtiene el número de socio.
     *
     * @return el número de socio.
     */
    public String getNumeroSocio() {
        return numeroSocio;
    }

    /**
     * Establece el número de socio.
     *
     * @param numeroSocio el número de socio.
     */
    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    /**
     * Obtiene el identificador del socio.
     *
     * @return el identificador del socio.
     */
    public Integer getIdSocio() {
        return idSocio;
    }

    /**
     * Establece el identificador del socio.
     *
     * @param idSocio el identificador del socio.
     */
    public void setIdSocio(Integer idSocio) {
        this.idSocio = idSocio;
    }

    /**
     * Obtiene la familia a la que pertenece el socio.
     *
     * @return la familia a la que pertenece el socio.
     */
    public Familia getNumFamilia() {
        return numFamilia;
    }

    /**
     * Establece la familia a la que pertenece el socio.
     *
     * @param numFamilia la familia a la que pertenece el socio.
     */
    public void setNumFamilia(Familia numFamilia) {
        this.numFamilia = numFamilia;
    }

    /**
     * Obtiene el nombre del socio.
     *
     * @return el nombre del socio.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del socio.
     *
     * @param nombre el nombre del socio.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del socio.
     *
     * @return los apellidos del socio.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del socio.
     *
     * @param apellidos los apellidos del socio.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el teléfono del socio.
     *
     * @return el teléfono del socio.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del socio.
     *
     * @param telefono el teléfono del socio.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el DNI del socio.
     *
     * @return el DNI del socio.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del socio.
     *
     * @param dni el DNI del socio.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el email del socio.
     *
     * @return el email del socio.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del socio.
     *
     * @param email el email del socio.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la fecha de nacimiento del socio.
     *
     * @return la fecha de nacimiento del socio.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Establece la fecha de nacimiento del socio.
     *
     * @param fechaNacimiento la fecha de nacimiento del socio.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Obtiene la foto del socio en formato byte array.
     *
     * @return la foto del socio.
     */
    public byte[] getFoto() {
        return foto;
    }

    /**
     * Establece la foto del socio en formato byte array.
     *
     * @param foto la foto del socio.
     */
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    /**
     * Obtiene la huella del socio en formato byte array.
     *
     * @return la huella del socio.
     */
    public byte[] getHuella() {
        return huella;
    }

    /**
     * Establece la huella del socio en formato byte array.
     *
     * @param huella la huella del socio.
     */
    public void setHuella(byte[] huella) {
        this.huella = huella;
    }

    /**
     * Obtiene si el socio está dentro de la instalación.
     *
     * @return si el socio está dentro de la instalación.
     */
    public Byte getDentroInstalacion() {
        return dentroInstalacion;
    }

    /**
     * Establece si el socio está dentro de la instalación.
     *
     * @param dentroInstalacion si el socio está dentro de la instalación.
     */
    public void setDentroInstalacion(Byte dentroInstalacion) {
        this.dentroInstalacion = dentroInstalacion;
    }

    /**
     * Obtiene la fecha de alta del socio.
     *
     * @return la fecha de alta del socio.
     */
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Establece la fecha de alta del socio.
     *
     * @param fechaAlta la fecha de alta del socio.
     */
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    /**
     * Obtiene el empleado que registró al socio.
     *
     * @return el empleado que registró al socio.
     */
    public Empleado getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Establece el empleado que registró al socio.
     *
     * @param idEmpleado el empleado que registró al socio.
     */
    public void setIdEmpleado(Empleado idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * Obtiene el tipo de carnet del socio basado en su edad.
     *
     * @return el tipo de carnet del socio.
     */
    public String getTipoCarnet() {
        if (fechaNacimiento == null) {
            return "Sin definir";
        }

        int edad = calcularEdad(fechaNacimiento);

        if (edad <= 3) {
            return "Bebé";
        } else if (edad <= 12) {
            return "Infantil";
        } else if (edad <= 18) {
            return "Juvenil";
        } else {
            return "Adulto";
        }
    }

    /**
     * Calcula la edad del socio basado en su fecha de nacimiento.
     *
     * @param fechaNacimiento la fecha de nacimiento del socio.
     * @return la edad del socio.
     */
    private int calcularEdad(LocalDate fechaNacimiento) {
        return LocalDate.now().getYear() - fechaNacimiento.getYear() -
                (LocalDate.now().getDayOfYear() < fechaNacimiento.getDayOfYear() ? 1 : 0);
    }

    /**
     * Imagen de la foto del socio.
     */
    @Transient
    private BufferedImage fotoImagen;

    /**
     * Obtiene la imagen de la foto del socio.
     *
     * @return la imagen de la foto del socio.
     */
    public BufferedImage getFotoImagen() {
        if (foto != null && fotoImagen == null) {
            try {
                fotoImagen = ImageUtils.convertBytesToImage(foto);
            } catch (IOException e) {
                logger.error("Error al convertir bytes a imagen", e);
            }
        }
        return fotoImagen;
    }
}