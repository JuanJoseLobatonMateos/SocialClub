package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Evento;
import org.socialclub.socialclub.util.HibernateUtil;

import java.util.List;

/**
 * Data Access Object (DAO) para administrar entidades {@link Evento} en la base de datos.
 */
public class EventoDAO {
    private static final Logger logger = LoggerFactory.getLogger(EventoDAO.class);

    /**
     * Guarda un nuevo {@link Evento} en la base de datos.
     *
     * @param evento El {@link Evento} que se va a guardar.
     */
    public void guardarEvento(Evento evento) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(evento);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al guardar el evento", e);
        }
    }

    /**
     * Obtiene todos los {@link Evento} de la base de datos.
     *
     * @return Una lista de {@link Evento}.
     */
    public List<Evento> obtenerTodosEventos() {
        List<Evento> eventos = null;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Evento> query = session.createQuery("from Evento", Evento.class);
            eventos = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener todos los eventos", e);
        }
        return eventos;
    }
    /**
     * Elimina un {@link Evento} de la base de datos por su ID.
     *
     * @param id El ID del {@link Evento} que se va a eliminar.
     */
    public void eliminarEvento(Integer id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Evento evento = session.get(Evento.class, id);
            if (evento != null) {
                session.remove(evento);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al eliminar el evento", e);
        }
    }
}