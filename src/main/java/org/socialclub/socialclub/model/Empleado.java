package org.socialclub.socialclub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.socialclub.socialclub.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Representa un empleado en el sistema del club social.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "empleado", schema = "clubsocial", indexes = {
        @Index(name = "rol", columnList = "rol")
}, uniqueConstraints = {
        @UniqueConstraint(name = "dni", columnNames = {"dni"}),
        @UniqueConstraint(name = "email", columnNames = {"email"})
})
public class Empleado {
    /**
     * Identificador único del empleado.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Nombre del empleado.
     */
    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    /**
     * Apellidos del empleado.
     */
    @Column(name = "apellidos", nullable = false, length = 80)
    private String apellidos;

    /**
     * DNI del empleado.
     */
    @Column(name = "dni", nullable = false, length = 9)
    private String dni;

    /**
     * Teléfono del empleado.
     */
    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    /**
     * Domicilio del empleado.
     */
    @Lob
    @Column(name = "domicilio", nullable = false)
    private String domicilio;

    /**
     * Fecha de nacimiento del empleado.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Email del empleado.
     */
    @Column(name = "email", nullable = false, length = 40)
    private String email;

    /**
     * Contraseña del empleado.
     */
    @Column(name = "contrasenia", length = 250)
    private String contrasenia;

    /**
     * Foto del empleado en formato de bytes.
     */
    @Lob
    @Column(name = "foto")
    private byte[] foto;

    /**
     * Rol del empleado.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "rol", nullable = false)
    private Rol rol;

    /**
     * Familias asociadas al empleado.
     */
    @OneToMany(mappedBy = "idEmpleado")
    private Set<Familia> familias = new LinkedHashSet<>();

    /**
     * Instalaciones asociadas al empleado.
     */
    @OneToMany(mappedBy = "idEmpleado")
    private Set<Instalacion> instalacions = new LinkedHashSet<>();

    /**
     * Registros de entrada asociados al empleado.
     */
    @OneToMany(mappedBy = "idEmpleado")
    private Set<RegistroEntrada> registroEntradas = new LinkedHashSet<>();

    /**
     * Reservas asociadas al empleado.
     */
    @OneToMany(mappedBy = "idEmpleado")
    private Set<Reserva> reservas = new LinkedHashSet<>();

    /**
     * Socios asociados al empleado.
     */
    @OneToMany(mappedBy = "idEmpleado")
    private Set<Socio> socios = new LinkedHashSet<>();

    /**
     * Obtiene el identificador del empleado.
     *
     * @return el identificador del empleado.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador del empleado.
     *
     * @param id el identificador del empleado.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del empleado.
     *
     * @return el nombre del empleado.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del empleado.
     *
     * @param nombre el nombre del empleado.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del empleado.
     *
     * @return los apellidos del empleado.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del empleado.
     *
     * @param apellidos los apellidos del empleado.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el DNI del empleado.
     *
     * @return el DNI del empleado.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del empleado.
     *
     * @param dni el DNI del empleado.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el teléfono del empleado.
     *
     * @return el teléfono del empleado.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del empleado.
     *
     * @param telefono el teléfono del empleado.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el domicilio del empleado.
     *
     * @return el domicilio del empleado.
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Establece el domicilio del empleado.
     *
     * @param domicilio el domicilio del empleado.
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Obtiene la fecha de nacimiento del empleado.
     *
     * @return la fecha de nacimiento del empleado.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Establece la fecha de nacimiento del empleado.
     *
     * @param fechaNacimiento la fecha de nacimiento del empleado.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Obtiene el email del empleado.
     *
     * @return el email del empleado.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del empleado.
     *
     * @param email el email del empleado.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del empleado.
     *
     * @return la contraseña del empleado.
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Establece la contraseña del empleado.
     *
     * @param contrasenia la contraseña del empleado.
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Obtiene la foto del empleado en formato de bytes.
     *
     * @return la foto del empleado.
     */
    public byte[] getFoto() {
        return foto;
    }

    /**
     * Establece la foto del empleado en formato de bytes.
     *
     * @param foto la foto del empleado.
     */
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    /**
     * Obtiene el rol del empleado.
     *
     * @return el rol del empleado.
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Establece el rol del empleado.
     *
     * @param rol el rol del empleado.
     */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Obtiene las familias asociadas al empleado.
     *
     * @return las familias asociadas al empleado.
     */
    public Set<Familia> getFamilias() {
        return familias;
    }

    /**
     * Establece las familias asociadas al empleado.
     *
     * @param familias las familias asociadas al empleado.
     */
    public void setFamilias(Set<Familia> familias) {
        this.familias = familias;
    }

    /**
     * Obtiene las instalaciones asociadas al empleado.
     *
     * @return las instalaciones asociadas al empleado.
     */
    public Set<Instalacion> getInstalacions() {
        return instalacions;
    }

    /**
     * Establece las instalaciones asociadas al empleado.
     *
     * @param instalacions las instalaciones asociadas al empleado.
     */
    public void setInstalacions(Set<Instalacion> instalacions) {
        this.instalacions = instalacions;
    }

    /**
     * Obtiene los registros de entrada asociados al empleado.
     *
     * @return los registros de entrada asociados al empleado.
     */
    public Set<RegistroEntrada> getRegistroEntradas() {
        return registroEntradas;
    }

    /**
     * Establece los registros de entrada asociados al empleado.
     *
     * @param registroEntradas los registros de entrada asociados al empleado.
     */
    public void setRegistroEntradas(Set<RegistroEntrada> registroEntradas) {
        this.registroEntradas = registroEntradas;
    }

    /**
     * Obtiene las reservas asociadas al empleado.
     *
     * @return las reservas asociadas al empleado.
     */
    public Set<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Establece las reservas asociadas al empleado.
     *
     * @param reservas las reservas asociadas al empleado.
     */
    public void setReservas(Set<Reserva> reservas) {
        this.reservas = reservas;
    }

    /**
     * Obtiene los socios asociados al empleado.
     *
     * @return los socios asociados al empleado.
     */
    public Set<Socio> getSocios() {
        return socios;
    }

    /**
     * Establece los socios asociados al empleado.
     *
     * @param socios los socios asociados al empleado.
     */
    public void setSocios(Set<Socio> socios) {
        this.socios = socios;
    }

    /**
     * Imagen de la foto del empleado en formato BufferedImage.
     */
    @Transient
    private BufferedImage fotoImagen;

    /**
     * Obtiene la imagen de la foto del empleado en formato BufferedImage.
     *
     * @return la imagen de la foto del empleado.
     */
    public BufferedImage getFotoImagen() {
        if (foto != null && fotoImagen == null) {
            try {
                fotoImagen = ImageUtils.convertBytesToImage(foto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fotoImagen;
    }
}