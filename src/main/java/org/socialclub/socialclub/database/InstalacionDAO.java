package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Instalacion;

import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Objeto de Acceso a Datos (DAO) para administrar las entidades {@link Instalacion} en la base de datos.
 * Esta clase proporciona métodos para crear, actualizar, eliminar y recuperar instancias de {@link Instalacion}.
 */
public class InstalacionDAO {
    private static final Logger logger = LoggerFactory.getLogger(InstalacionDAO.class);

    /**
     * Crea una nueva {@link Instalacion} en la base de datos.
     *
     * @param instalacion la {@link Instalacion} que se va a crear.
     */
    public void crearInstalacion(Instalacion instalacion) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(instalacion);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al crear la instalación", e);
        }
    }

    /**
     * Actualiza una {@link Instalacion} existente en la base de datos.
     *
     * @param instalacion la {@link Instalacion} que se va a actualizar.
     */
    public void updateInstalacion(Instalacion instalacion) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(instalacion);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al actualizar la instalación", e);
        }
    }

    /**
     * Elimina una {@link Instalacion} de la base de datos por su identificador único.
     *
     * @param id el identificador único de la {@link Instalacion} que se va a eliminar.
     */
    public void deleteInstalacion(int id) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Instalacion instalacion = session.get(Instalacion.class, id);
            if (instalacion != null) {
                session.remove(instalacion);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al eliminar la instalación", e);
        }
    }

    /**
     * Recupera una lista de todas las instancias de {@link Instalacion} de la base de datos.
     *
     * @return una lista de {@link Instalacion} instances.
     */
    public List<Instalacion> obtenerInstalaciones() {
        Transaction transaction = null;
        List<Instalacion> instalaciones = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Instalacion> query = session.createQuery("from Instalacion", Instalacion.class);
            instalaciones = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener las instalaciones", e);
        }
        return instalaciones;
    }}