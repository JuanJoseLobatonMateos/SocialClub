package org.socialclub.socialclub.database;

import jakarta.persistence.RollbackException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Rol;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Data Access Object (DAO) para la entidad Empleado. Proporciona métodos para realizar operaciones CRUD en los registros de empleados.
 */
public class EmpleadoDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoDAO.class);
    private static final String EMAIL_PARAM = "email";

    /**
     * Crea un nuevo registro de empleado en la base de datos.
     *
     * @param empleado El objeto Empleado que se va a guardar en la base de datos. No puede ser nulo.
     * @throws HibernateException Si se produce un error al interactuar con la base de datos.
     * @throws RollbackException  Si se revierte una transacción de base de datos debido a un error.
     */
    public void crearEmpleado(Empleado empleado) throws HibernateException, RollbackException {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(empleado);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Error al crear empleado", e);
            }
        }
    }

    /**
     * Guarda un nuevo empleado en la base de datos con la información proporcionada.
     *
     * @param nombre          El nombre del empleado.
     * @param apellidos       Los apellidos del empleado.
     * @param dni             El DNI del empleado.
     * @param domicilio       El domicilio del empleado.
     * @param telefono        El teléfono del empleado.
     * @param fechaNacimiento La fecha de nacimiento del empleado en formato ISO.
     * @param email           El email del empleado.
     * @param password        La contraseña del empleado.
     * @param imagen          La imagen del empleado en formato SerialBlob.
     */
    public void guardarEmpleado(String nombre, String apellidos, String dni, String domicilio, String telefono, String fechaNacimiento, String email, String password, SerialBlob imagen) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setApellidos(apellidos);
        empleado.setDni(dni);
        empleado.setDomicilio(domicilio);
        empleado.setTelefono(telefono);
        empleado.setFechaNacimiento(LocalDate.parse(fechaNacimiento, DateTimeFormatter.ISO_DATE));
        empleado.setEmail(email);
        empleado.setContrasenia(password);

        try {
            InputStream inputStream = imagen.getBinaryStream();
            byte[] imagenBytes = inputStream.readAllBytes();
            empleado.setFoto(imagenBytes);
        } catch (SQLException | IOException e) {
            logger.error("Error al guardar empleado", e);
        }

        Rol rol = new Rol();
        rol.setId(2);
        empleado.setRol(rol);

        crearEmpleado(empleado);
    }

    /**
     * Obtiene un empleado de la base de datos por su identificador único.
     *
     * @param id El identificador único del empleado que se desea obtener. No puede ser nulo.
     * @return El empleado con el identificador especificado, o null si no se encuentra ninguno.
     */
    public Empleado obtenerEmpleadoPorId(Integer id) {
        Session session = getSessionFactory().getCurrentSession();
        Empleado empleado = null;
        try {
            session.beginTransaction();
            empleado = session.get(Empleado.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener empleado por id", e);
        } finally {
            session.close();
        }
        return empleado;
    }

    /**
     * Obtiene una lista de todos los empleados almacenados en la base de datos.
     *
     * @return Una lista de objetos {@link Empleado}, que representan a todos los empleados en la base de datos.
     * La lista puede estar vacía si no hay empleados registrados.
     * El valor de retorno es <code>null</code> en caso de error durante la operación.
     */
    public List<Empleado> obtenerTodosEmpleados() {
        Session session = getSessionFactory().getCurrentSession();
        List<Empleado> empleados = null;
        try {
            session.beginTransaction();
            Query<Empleado> query = session.createQuery("from Empleado", Empleado.class);
            empleados = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener todos los empleados", e);
        } finally {
            session.close();
        }
        return empleados;
    }

    /**
     * Actualiza la información de un empleado en la base de datos.
     *
     * @param empleado El objeto Empleado que contiene la información actualizada. No puede ser nulo.
     *                 El objeto debe tener un identificador único (id) válido para que se pueda encontrar y actualizar el empleado en la base de datos.
     * @throws HibernateException Si se produce un error al interactuar con la base de datos.
     * @throws RollbackException  Si se revierte una transacción de base de datos debido a un error.
     */
    public void actualizarEmpleado(Empleado empleado) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(empleado);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al actualizar empleado", e);
        }
    }

    /**
     * Elimina un empleado de la base de datos por su identificador único.
     *
     * @param id El identificador único del empleado que se desea eliminar. No puede ser nulo.
     *           El empleado se buscará en la base de datos y, si se encuentra, se eliminará.
     *           Si no se encuentra ningún empleado con el identificador especificado, no se realizará ninguna acción.
     * @throws HibernateException Si se produce un error al interactuar con la base de datos.
     * @throws RollbackException  Si se revierte una transacción de base de datos debido a un error.
     *                            Los errores se registrarán en el registro de la aplicación.
     */
    public void eliminarEmpleado(Integer id) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Empleado empleado = session.get(Empleado.class, id);
            if (empleado != null) {
                session.remove(empleado);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al eliminar empleado", e);
        }
    }

    /**
     * Obtiene un empleado de la base de datos por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del empleado que se desea obtener. No puede ser nula ni vacía.
     * @return El empleado con la dirección de correo electrónico especificada, o null si no se encuentra ninguno.
     * La operación se realiza en una transacción de base de datos y se utiliza una consulta HQL para buscar al empleado.
     * Si se produce algún error durante la operación, se registrará un mensaje de error en el registro de la aplicación.
     */
    public Empleado obtenerEmpleadoPorEmail(String email) {
        Session session = getSessionFactory().openSession();
        Empleado empleado = null;
        try {
            session.beginTransaction();
            Query<Empleado> query = session.createQuery("FROM Empleado WHERE email = :email", Empleado.class);
            query.setParameter(EMAIL_PARAM, email);
            empleado = query.uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener empleado por email", e);
        } finally {
            session.close();
        }
        return empleado;
    }

    /**
     * Obtiene el nombre de un empleado de la base de datos por su identificador único.
     *
     * @param id El identificador único del empleado cuyo nombre se desea obtener. No puede ser nulo.
     * @return El nombre del empleado con el identificador especificado, o null si no se encuentra ninguno.
     * La operación se realiza en una transacción de base de datos y utiliza la sesión de Hibernate para interactuar con la base de datos.
     * Si se produce algún error durante la operación, se registrará un mensaje de error en el registro de la aplicación.
     */
    public String obtenerNombreEmpleadoPorId(Integer id) {
        Session session = getSessionFactory().openSession();
        String nombre = null;
        try {
            session.beginTransaction();
            Empleado empleado = session.get(Empleado.class, id);
            if (empleado != null) {
                nombre = empleado.getNombre();
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener nombre de empleado por id", e);
        } finally {
            session.close();
        }
        return nombre;
    }

    /**
     * Verifica si un DNI ya está registrado en la base de datos.
     *
     * @param dni El DNI que se desea verificar. No puede ser nulo ni vacío.
     * @return <code>true</code> si el DNI existe en la base de datos, <code>false</code> en caso contrario.
     * La operación se realiza en una transacción de base de datos y utiliza la sesión de Hibernate para interactuar con la base de datos.
     * Si se produce algún error durante la operación, se registrará un mensaje de error en el registro de la aplicación.
     */
    public boolean existeDni(String dni) {
        Session session = getSessionFactory().openSession();
        boolean existe = false;
        try {
            session.beginTransaction();
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM Empleado e WHERE e.dni = :dni", Long.class);
            query.setParameter("dni", dni);
            Long count = query.uniqueResult();
            existe = count > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al verificar si el DNI existe", e);
        } finally {
            session.close();
        }
        return existe;
    }

    /**
     * Verifica si un email ya está registrado en la base de datos.
     *
     * @param email El email que se desea verificar. No puede ser nulo ni vacío.
     * @return <code>true</code> si el email existe en la base de datos, <code>false</code> en caso contrario.
     * La operación se realiza en una transacción de base de datos y utiliza la sesión de Hibernate para interactuar con la base de datos.
     * Si se produce algún error durante la operación, se registrará un mensaje de error en el registro de la aplicación.
     */
    public boolean existeEmail(String email) {
        Session session = getSessionFactory().openSession();
        boolean existe = false;
        try {
            session.beginTransaction();
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM Empleado e WHERE e.email = :email", Long.class);
            query.setParameter(EMAIL_PARAM, email);
            Long count = query.uniqueResult();
            existe = count > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al verificar si el email existe", e);
        } finally {
            session.close();
        }
        return existe;
    }
}