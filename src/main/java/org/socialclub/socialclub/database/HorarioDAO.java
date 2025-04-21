package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase proporciona métodos para interactuar con la base de datos relacionada con los horarios de instalaciones deportivas.
 */
public class HorarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(HorarioDAO.class);

    /**
     * Recupera las horas de inicio de las instalaciones deportivas de la base de datos.
     *
     * @param idInstalacion El identificador único de la instalación deportiva.
     * @return Una lista de objetos LocalTime que representan las horas de inicio de la instalación deportiva.
     * Si no se encuentran horas de inicio para la instalación dada, se devuelve una lista vacía.
     */
    public List<LocalTime> obtenerHorasPorInstalacion(int idInstalacion) {
        List<LocalTime> horas = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<LocalTime> query = session.createQuery(
                    "select h.horaInicio from Horario h where h.idInstalacion.id = :idInstalacion", LocalTime.class);
            query.setParameter("idInstalacion", idInstalacion);
            horas = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener las horas de la instalación con id {}", idInstalacion, e);
        }
        return horas;
    }
}