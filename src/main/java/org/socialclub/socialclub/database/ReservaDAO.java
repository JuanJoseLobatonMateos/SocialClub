package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Instalacion;
import org.socialclub.socialclub.model.Reserva;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Esta clase proporciona operaciones de acceso a datos para la entidad Reserva.
 */
public class ReservaDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservaDAO.class);

    /**
     * Guarda una nueva reserva en la base de datos.
     *
     * @param numeroSocio El identificador único del socio que realiza la reserva.
     * @param instalacion La Instalacion donde se realiza la reserva.
     * @param fecha       La fecha de la reserva.
     * @param hora        La hora de la reserva.
     */
    public void guardarReserva(String numeroSocio, Instalacion instalacion, LocalDate fecha, LocalTime hora) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Socio socio = session.get(Socio.class, numeroSocio);
            if (socio == null) {
                throw new IllegalArgumentException("El socio con número " + numeroSocio + " no existe.");
            }

            Reserva reserva = new Reserva();
            reserva.setNumeroSocio(socio);
            reserva.setIdInstalacion(instalacion);
            reserva.setFecha(fecha);
            reserva.setHora(hora);
            session.persist(reserva);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al guardar la reserva", e);
        }
    }

    /**
     * Recupera todas las reservas de la base de datos.
     *
     * @return Una lista de objetos Reserva que representan todas las reservas.
     */
    public List<Reserva> obtenerReservas() {
        List<Reserva> reservas = null;
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Reserva> query = session.createQuery("from Reserva", Reserva.class);
            reservas = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener las reservas", e);
        }
        return reservas;
    }

    /**
     * Elimina una reserva de la base de datos.
     *
     * @param id El identificador único de la reserva que se va a eliminar.
     */
    public void eliminarReserva(int id) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Reserva reserva = session.get(Reserva.class, id);
            if (reserva != null) {
                session.remove(reserva);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al eliminar la reserva", e);
        }
    }

    /**
     * Recupera las horas reservadas para una instalación específica en una fecha dada.
     *
     * @param idInstalacion El identificador único de la instalación.
     * @param fecha         La fecha para la que se recuperarán las horas reservadas.
     * @return Una lista de objetos LocalTime que representan las horas reservadas.
     */
    public List<LocalTime> obtenerHorasReservadas(int idInstalacion, LocalDate fecha) {
        Transaction transaction = null;
        List<LocalTime> horasReservadas = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<LocalTime> query = session.createQuery(
                    "select r.hora from Reserva r where r.idInstalacion.id = :idInstalacion and r.fecha = :fecha",
                    LocalTime.class
            );
            query.setParameter("idInstalacion", idInstalacion);
            query.setParameter("fecha", fecha);
            horasReservadas = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener las horas reservadas", e);
        }
        return horasReservadas;
    }

    /**
     * Obtiene las reservas realizadas en una fecha específica.
     *
     * @param fecha La fecha para la que se desean recuperar las reservas.
     * @return Una lista de objetos Reserva que representan las reservas realizadas en la fecha dada.
     * La lista puede estar vacía si no hay reservas para la fecha especificada.
     * El valor de retorno es null si se produce algún error durante la operación.
     */
    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        Transaction transaction = null;
        List<Reserva> reservas = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Reserva> query = session.createQuery("from Reserva where fecha = :fecha", Reserva.class);
            query.setParameter("fecha", fecha);
            reservas = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener las reservas por fecha", e);
        }
        return reservas;
    }
}