package org.socialclub.socialclub.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Familia;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Clase de Objetos de Acceso a Datos (DAO) para administrar entidades Socio en la base de datos.
 * Esta clase proporciona métodos para interactuar con la tabla Socio en la base de datos.
 */
public class SocioDAO {
    private static final Logger logger = LoggerFactory.getLogger(SocioDAO.class);

    /**
     * Maneja el procesamiento de los datos de foto y huella del socio.
     *
     * @param socio  El objeto Socio al que se asociarán los datos de foto y huella.
     * @param foto   El objeto SerialBlob que contiene los datos de foto del socio.
     * @param huella El objeto SerialBlob que contiene los datos de huella del socio.
     * @throws SQLException Si se produce un error al acceder a la base de datos.
     * @throws IOException  Si se produce un error al leer los datos de foto o huella.
     */
    private void manejarImagenYHuella(Socio socio, SerialBlob foto, SerialBlob huella) throws SQLException, IOException {
        if (foto != null) {
            try (InputStream fotoInputStream = foto.getBinaryStream()) {
                byte[] fotoBytes = fotoInputStream.readAllBytes();
                socio.setFoto(fotoBytes); // Asignar los bytes de la foto al socio
            }
        }
        if (huella != null) {
            try (InputStream huellaInputStream = huella.getBinaryStream()) {
                byte[] huellaBytes = huellaInputStream.readAllBytes();
                socio.setHuella(huellaBytes); // Asignar los bytes de la huella al socio
            }
        }
    }

    /**
     * Guarda un nuevo socio en la base de datos.
     *
     * @param numFamilia        La familia a la que pertenece el socio.
     * @param nombre            El nombre del socio.
     * @param apellidos         Los apellidos del socio.
     * @param telefono          El número de teléfono del socio.
     * @param dni               El DNI del socio.
     * @param email             El correo electrónico del socio.
     * @param fechaNacimiento   La fecha de nacimiento del socio en formato ISO 8601 (YYYY-MM-DD).
     * @param foto              La imagen del socio en formato SerialBlob.
     * @param huella            La huella del socio en formato SerialBlob.
     * @param titularidad       La titularidad del socio (titular o familiar).
     * @param dentroInstalacion Indica si el socio está dentro de la instalación (1) o no (0).
     * @param fechaAlta         La fecha de alta del socio en formato ISO 8601 (YYYY-MM-DD).
     * @param idEmpleado        El empleado que registra al socio.
     * @param contrasenia       La contraseña del socio.
     * @throws IllegalArgumentException Si alguno de los campos obligatorios (numFamilia, nombre, apellidos, dni, email) no está presente.
     */
    public void guardarSocio(Familia numFamilia, String nombre, String apellidos, String telefono, String dni, String email, String fechaNacimiento, SerialBlob foto, SerialBlob huella, Socio.Titularidad titularidad, Byte dentroInstalacion, String fechaAlta, Empleado idEmpleado, String contrasenia) {
        if (numFamilia == null || nombre == null || apellidos == null || dni == null || email == null) {
            throw new IllegalArgumentException("Todos los campos obligatorios deben estar presentes");
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Comprobar si la familia existe
            Familia familiaExistente = session.get(Familia.class, numFamilia.getId());
            if (familiaExistente == null) {
                // Si la familia no existe, crear una nueva
                familiaExistente = new Familia();
                familiaExistente.setId(numFamilia.getId());
                familiaExistente.setNombreTitular(nombre);
                familiaExistente.setApellidosTitular(apellidos);
                familiaExistente.setIdEmpleado(idEmpleado);
                session.persist(familiaExistente); // Guardar la nueva familia
            }
            // Crear y guardar el nuevo socio
            Socio socio = new Socio();
            socio.setNumFamilia(familiaExistente);
            socio.setNombre(nombre);
            socio.setApellidos(apellidos);
            socio.setTelefono(telefono);
            socio.setDni(dni);
            socio.setEmail(email);
            socio.setFechaNacimiento(LocalDate.parse(fechaNacimiento, DateTimeFormatter.ISO_DATE));
            socio.setTitularidad(titularidad);
            socio.setDentroInstalacion(dentroInstalacion);
            socio.setFechaAlta(LocalDate.parse(fechaAlta, DateTimeFormatter.ISO_DATE));
            socio.setIdEmpleado(idEmpleado);
            socio.setContrasenia(contrasenia);
            manejarImagenYHuella(socio, foto, huella); // Manejar la imagen y la huella
            session.persist(socio); // Guardar el socio
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback(); // Hacer rollback en caso de error
                } catch (Exception rollbackEx) {
                    logger.error("Error al hacer rollback", rollbackEx);
                }
            }
        }
    }

        /**
         * Obtiene todos los socios registrados en la base de datos.
         *
         * @return Una lista de objetos {@link Socio} que representan a todos los socios registrados.
         * La lista puede estar vacía si no hay socios registrados.
         * La lista no contiene valores nulos.
         */
        public List<Socio> obtenerTodosSocios () {
            Session session = getSessionFactory().getCurrentSession();
            List<Socio> socios = null;
            try {
                session.beginTransaction();
                Query<Socio> query = session.createQuery("from Socio", Socio.class);
                socios = query.getResultList(); // Obtener la lista de socios
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Error al obtener todos los socios", e);
            } finally {
                session.close(); // Cerrar la sesión
            }
            return socios;
        }

        /**
         * Actualiza los datos de un socio en la base de datos.
         *
         * @param socio El objeto {@link Socio} que contiene los nuevos datos a actualizar.
         *              Este objeto debe tener establecido el número de socio (numSocio) para identificar al socio a actualizar.
         *              Los demás atributos del objeto se utilizarán para actualizar los datos del socio en la base de datos.
         */
        public void actualizarSocio (Socio socio){
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.merge(socio); // Actualizar el socio
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback(); // Hacer rollback en caso de error
                }
                logger.error("Error al actualizar el socio", e);
            }
        }

        /**
         * Elimina un socio de la base de datos.
         *
         * @param numSocio El número de socio que se va a eliminar. Este número debe estar presente en la base de datos.
         *                 Se utiliza para identificar al socio a eliminar.
         */
        public void eliminarSocio (String numSocio){
            Transaction transaction = null;
            try (Session session = getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Socio socio = session.get(Socio.class, numSocio);
                if (socio != null) {
                    session.remove(socio); // Eliminar el socio
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback(); // Hacer rollback en caso de error
                }
                logger.error("Error al eliminar el socio", e);
            }
        }

        /**
         * Verifica si un DNI determinado existe en la base de datos.
         *
         * @param dni El DNI que se va a verificar.
         * @return {@code true} si el DNI existe en la base de datos, {@code false} en caso contrario.
         * @throws HibernateException Si se produce un error durante la interacción con la base de datos.
         */
        public boolean existeDni (String dni){
            Session session = getSessionFactory().openSession();
            boolean existe = false;
            try {
                session.beginTransaction();
                Query<Long> query = session.createQuery("SELECT COUNT(e) FROM Socio e WHERE e.dni = :dni", Long.class);
                query.setParameter("dni", dni);
                Long count = query.uniqueResult();
                existe = count > 0; // Verificar si el DNI existe
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Error al verificar si el DNI existe", e);
            } finally {
                session.close(); // Cerrar la sesión
            }
            return existe;
        }

        /**
         * Obtiene una lista de socios que pertenecen a una familia específica.
         *
         * @param idFamilia El identificador único de la familia cuyos socios se desean obtener.
         * @return Una lista de objetos {@link Socio} que representan a los socios pertenecientes a la familia especificada.
         * La lista puede estar vacía si no hay socios registrados para la familia indicada.
         * La lista no contiene valores nulos.
         * @throws HibernateException Si se produce un error durante la interacción con la base de datos.
         */
        public List<Socio> obtenerSociosPorFamilia ( int idFamilia){
            Session session = getSessionFactory().getCurrentSession();
            List<Socio> socios = null;
            try {
                session.beginTransaction();
                Query<Socio> query = session.createQuery("from Socio where numFamilia.id = :idFamilia", Socio.class);
                query.setParameter("idFamilia", idFamilia);
                socios = query.getResultList(); // Obtener la lista de socios por familia
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Error al obtener los socios por familia", e);
            } finally {
                session.close(); // Cerrar la sesión
            }
            return socios;
        }

        /**
         * Obtiene una lista de socios que pertenecen a un rango de edad específico.
         *
         * @param rangoEdad El rango de edad de los socios que se desean obtener.
         *                  Los valores válidos son: "bebe", "niño", "adolescente", "adulto", "abuelo".
         * @return Una lista de objetos {@link Socio} que representan a los socios pertenecientes al rango de edad especificado.
         * La lista puede estar vacía si no hay socios registrados para el rango de edad indicado.
         * La lista no contiene valores nulos.
         * @throws IllegalArgumentException Si el rango de edad proporcionado no es válido.
         * @throws HibernateException       Si se produce un error durante la interacción con la base de datos.
         */
        public List<Socio> obtenerSociosPorRangoEdad (String rangoEdad){
            Session session = getSessionFactory().getCurrentSession();
            List<Socio> socios = null;
            try {
                session.beginTransaction();
                String queryStr;
                switch (rangoEdad) {
                    case "bebe":
                        queryStr = "from Socio where year(current_date()) - year(fechaNacimiento) < 3";
                        break;
                    case "niño":
                        queryStr = "from Socio where year(current_date()) - year(fechaNacimiento) >= 3 and year(current_date()) - year(fechaNacimiento) < 12";
                        break;
                    case "adolescente":
                        queryStr = "from Socio where year(current_date()) - year(fechaNacimiento) >= 12 and year(current_date()) - year(fechaNacimiento) < 18";
                        break;
                    case "adulto":
                        queryStr = "from Socio where year(current_date()) - year(fechaNacimiento) >= 18 and year(current_date()) - year(fechaNacimiento) < 65";
                        break;
                    case "abuelo":
                        queryStr = "from Socio where year(current_date()) - year(fechaNacimiento) >= 65";
                        break;
                    default:
                        throw new IllegalArgumentException("Rango de edad no válido: " + rangoEdad);
                }
                Query<Socio> query = session.createQuery(queryStr, Socio.class);
                socios = query.getResultList(); // Obtener la lista de socios por rango de edad
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Error al obtener los socios por rango de edad", e);
            } finally {
                session.close(); // Cerrar la sesión
            }
            return socios;
        }
    }