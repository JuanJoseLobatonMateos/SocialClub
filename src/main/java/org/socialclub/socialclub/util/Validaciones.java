package org.socialclub.socialclub.util;

import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.database.SocioDAO;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Esta clase contiene varios métodos de validación para diferentes tipos de datos utilizados en la aplicación Social Club.
 */
public class Validaciones {

    private static final int MAX_NOMBRE_LENGTH = 50;
    private static final int MAX_APELLIDOS_LENGTH = 50;
    private static final int MAX_DIRECCION_LENGTH = 100;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{8}[A-Za-z]$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    private static final int MAX_CAPACIDAD = 500;
    private static final Pattern PRECIO_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private Validaciones() {
    }

    /**
     * Valida el nombre proporcionado de acuerdo con las reglas especificadas.
     *
     * @param nombre El nombre a validar.
     * @return Una cadena que contiene un mensaje de error si el nombre es inválido, o null si el nombre es válido.
     * Las reglas de validación son las siguientes:
     * - El nombre no puede ser nulo o vacío.
     * - El nombre no puede superar los {@value #MAX_NOMBRE_LENGTH} caracteres.
     * - El nombre solo puede contener caracteres alfabéticos, espacios y los acentos españoles (á, é, í, ó, ú, Á, É, Í, Ó, Ú, ñ, Ñ).
     */
    public static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacío";
        }
        if (nombre.length() > MAX_NOMBRE_LENGTH) {
            return "Nombre demasiado largo";
        }
        if (!nombre.matches("[a-zA-Z\\sáéíóúÁÉÍÓÚñÑ]+")) {
            return "Nombre inválido";
        }
        return null;
    }

    /**
     * Valida los apellidos proporcionados de acuerdo con las reglas especificadas.
     *
     * @param apellidos Los apellidos a validar.
     * @return Una cadena que contiene un mensaje de error si los apellidos son inválidos, o null si los apellidos son válidos.
     * <p>
     * Las reglas de validación son las siguientes:
     * - Los apellidos no pueden ser nulos o vacíos.
     * - Los apellidos no pueden superar los {@value #MAX_APELLIDOS_LENGTH} caracteres.
     * - Los apellidos solo pueden contener caracteres alfabéticos, espacios y los acentos españoles (á, é, í, ó, ú, Á, É, Í, Ó, Ú, ñ, Ñ).
     * - Los apellidos deben ingresarse como dos apellidos separados por un espacio.
     */
    public static String validarApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            return "Los apellidos no pueden estar vacíos";
        }
        if (apellidos.length() > MAX_APELLIDOS_LENGTH) {
            return "Apellidos demasiado largos";
        }
        if (!apellidos.matches("[a-zA-Z\\sáéíóúÁÉÍÓÚñÑ]+")) {
            return "Apellidos inválidos";
        }

        return null;
    }

    /**
     * Valida el DNI proporcionado para un empleado de acuerdo con reglas específicas.
     *
     * @param dni El DNI a validar.
     * @return Una cadena que contiene un mensaje de error si el DNI es inválido, o null si el DNI es válido.
     * Las reglas de validación son las siguientes:
     * - El DNI no puede ser nulo o vacío.
     * - El DNI debe coincidir con el patrón de expresión regular {@link #DNI_PATTERN}.
     * - El DNI no puede estar registrado en la base de datos de empleados.
     */
    public static String validarDniEmpleado(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return "El DNI no puede estar vacío";
        }
        if (!DNI_PATTERN.matcher(dni).matches()) {
            return "El DNI no es válido";
        }
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        if (empleadoDAO.existeDni(dni)) {
            return "El DNI ya está registrado";
        }
        return null;
    }

    /**
     * Valida el DNI proporcionado para un socio de acuerdo con reglas específicas.
     *
     * @param dni           El DNI a validar.
     * @param esMenorDeEdad Un booleano que indica si el socio es menor de edad.
     * @return Una cadena que contiene un mensaje de error si el DNI es inválido, o null si el DNI es válido.
     * Las reglas de validación son las siguientes:
     * - Si el socio es menor de edad, el DNI puede estar vacío y la función devolverá null.
     * - Si el socio es mayor de edad, el DNI no puede estar vacío.
     * - El DNI debe coincidir con el patrón de expresión regular {@link #DNI_PATTERN}.
     * - El DNI no puede estar registrado en la base de datos de socios.
     */
    public static String validarDniSocio(String dni, boolean esMenorDeEdad) {
        if (dni == null || dni.trim().isEmpty()) {
            if (esMenorDeEdad) {
                return null;
            } else {
                return "El DNI no puede estar vacío";
            }
        }
        if (!DNI_PATTERN.matcher(dni).matches()) {
            return "El DNI no es válido";
        }
        SocioDAO socioDAO = new SocioDAO();
        if (socioDAO.existeDni(dni)) {
            return "El DNI ya está registrado";
        }
        return null;
    }

    /**
     * Valida el número de teléfono proporcionado según reglas específicas.
     *
     * @param telefono El número de teléfono que se va a validar.
     * @return Una cadena que contiene un mensaje de error si el número de teléfono no es válido, o null si el número es válido.
     * Las reglas de validación incluyen:
     * - El número de teléfono no puede ser nulo o estar vacío.
     * - El número de teléfono debe coincidir con el patrón de expresión regular {@link #TELEFONO_PATTERN}.
     * - Si el número de teléfono no coincide con el patrón, se devuelve el mensaje "El teléfono no es válido".
     * - Si el número de teléfono es válido, se devuelve null.
     */
    public static String validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "El teléfono no puede estar vacío";
        }
        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            return "El teléfono no es válido";
        }
        return null;
    }

    /**
     * Valida la dirección proporcionada de acuerdo con reglas específicas.
     *
     * @param direccion La dirección a validar.
     * @return Una cadena que contiene un mensaje de error si la dirección es inválida, o null si la dirección es válida.
     * Las reglas de validación son las siguientes:
     * - La dirección no puede ser nula o vacía.
     * - La dirección no puede superar los {@value #MAX_DIRECCION_LENGTH} caracteres.
     * @throws IllegalArgumentException Si la dirección proporcionada es nula.
     */
    public static String validarDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return "La dirección no puede estar vacía";
        }
        if (direccion.length() > MAX_DIRECCION_LENGTH) {
            return "Dirección demasiado larga";
        }
        return null;
    }

    /**
     * Valida la fecha de nacimiento proporcionada para un empleado de acuerdo con reglas específicas.
     *
     * @param fechaNacimiento La fecha de nacimiento a validar.
     * @return Una cadena que contiene un mensaje de error si la fecha de nacimiento es inválida, o null si la fecha es válida.
     * Las reglas de validación son las siguientes:
     * - La fecha de nacimiento no puede ser nula.
     * - La persona debe ser mayor de 18 años.
     * - La fecha de nacimiento no puede ser una fecha futura.
     */
    public static String validarFechaNacimientoEmpleado(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return "Fecha requerida";
        }
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, hoy);
        if (edad.getYears() < 18) {
            return "Debe ser mayor de 18 años";
        }
        return null;
    }

    /**
     * Valida la fecha de nacimiento proporcionada para un socio de acuerdo con reglas específicas.
     *
     * @param fechaNacimiento La fecha de nacimiento a validar.
     * @return Una cadena que contiene un mensaje de error si la fecha de nacimiento es inválida, o null si la fecha es válida.
     * Las reglas de validación incluyen:
     * - La fecha de nacimiento no puede ser nula.
     * - La fecha de nacimiento no puede ser una fecha futura.
     */
    // Validaciones.java
    public static String validarFechaNacimientoSocio(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return "Fecha requerida";
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            return "Fecha futura inválida";
        }
        return null;
    }

    /**
     * Valida la dirección de correo electrónico proporcionada según reglas específicas.
     *
     * @param email La dirección de correo electrónico que se va a validar.
     * @return Una cadena que contiene un mensaje de error si el correo electrónico no es válido, o null si el correo electrónico es válido.
     * Las reglas de validación incluyen:
     * - La dirección de correo electrónico no puede ser nula o estar vacía.
     * - La dirección de correo electrónico debe coincidir con el patrón de expresión regular {@link #EMAIL_PATTERN}.
     * - Si la dirección de correo electrónico no coincide con el patrón, se devuelve el mensaje "El email no es válido".
     * - Si la dirección de correo electrónico es válida, se devuelve null.
     * Además, se verifica si la dirección de correo electrónico ya está registrada en la base de datos de empleados.
     * - Si la dirección de correo electrónico ya está registrada, se devuelve el mensaje "El email ya está registrado".
     */
    public static String validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El email no puede estar vacío";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "El email no es válido";
        }
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        if (empleadoDAO.existeEmail(email)) {
            return "El email ya está registrado";
        }
        return null;
    }

    /**
     * Valida una contraseña proporcionada según reglas específicas.
     *
     * @param password La contraseña a validar.
     * @return Una cadena que contiene un mensaje de error si la contraseña no es válida, o null si la contraseña es válida.
     * Las reglas de validación son las siguientes:
     * - La contraseña no puede ser nula o estar vacía.
     * - La contraseña debe coincidir con el patrón definido por {@link #PASSWORD_PATTERN}.
     */
    public static String validarPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "La contraseña no puede estar vacía";
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Contraseña inválida, debe tener 8 caracteres, mayúsculas, minúsculas y números";
        }
        return null;
    }

    /**
     * Valida el nombre de instalación proporcionado según reglas específicas.
     *
     * @param nombre El nombre de instalación a validar.
     * @return Una cadena que contiene un mensaje de error si el nombre de instalación no es válido, o null si el nombre es válido.
     * Las reglas de validación son las siguientes:
     * - El nombre de instalación no puede ser nulo o estar vacío.
     * - El nombre de instalación no puede superar los {@value #MAX_NOMBRE_LENGTH}caracteres.
     */
    public static String validarNombreInstalacion(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Nombre requerido";
        }
        if (nombre.length() > MAX_NOMBRE_LENGTH) {
            return "Nombre demasiado largo, máximo " + MAX_NOMBRE_LENGTH + " caracteres";
        }
        return null;
    }

    /**
     * Valida la capacidad de la instalación según reglas específicas.
     *
     * @param capacidad La capacidad a validar.
     * @return Una cadena que contiene un mensaje de error si la capacidad no es válida,
     * de lo contrario devuelve null.
     * @throws IllegalArgumentException Si la capacidad proporcionada es nula.
     * @throws NumberFormatException    Si la capacidad no es un número válido.
     * @throws IllegalArgumentException Si la capacidad es un número negativo o mayor que MAX_CAPACIDAD.
     */
    public static String validarCapacidadInstalacion(String capacidad) {
        if (capacidad == null || capacidad.trim().isEmpty()) {
            return "Capacidad requerida";
        }
        try {
            int cap = Integer.parseInt(capacidad);
            if (cap <= 0 || cap > MAX_CAPACIDAD) {
                return "Capacidad inválida, máximo " + MAX_CAPACIDAD;
            }
        } catch (NumberFormatException e) {
            return "Capacidad inválida";
        }
        return null;
    }

    /**
     * Valida el precio de alquiler proporcionado según reglas específicas.
     *
     * @param precio El precio de alquiler a validar.
     * @return Una cadena que contiene un mensaje de error si el precio de alquiler no es válido,
     * o null si el precio es válido.
     * @throws IllegalArgumentException Si el precio de alquiler proporcionado es nulo o está vacío.
     * @throws NumberFormatException    Si el precio de alquiler proporcionado no es un número válido.
     * @throws IllegalArgumentException Si el precio de alquiler es un número negativo o mayor que MAX_PRECIO.
     */
    public static String validarPrecioAlquiler(String precio) {
        if (precio == null || precio.trim().isEmpty()) {
            return "Introduce precio de alquiler";
        }
        if (!PRECIO_PATTERN.matcher(precio).matches()) {
            return "Precio inválido, use hasta dos decimales";
        }
        return null;
    }
}